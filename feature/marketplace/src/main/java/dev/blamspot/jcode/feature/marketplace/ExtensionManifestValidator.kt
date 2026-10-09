package dev.blamspot.jcode.feature.marketplace

import java.io.File

/** One problem found in an extension's manifest, ranked by [severity]. */
data class ManifestIssue(
    val severity: Severity,
    val message: String,
    /** Optional dotted path into the manifest, e.g. "contributes.runConfigPresets[0].requires". */
    val path: String? = null,
) {
    enum class Severity { Error, Warning, Info }
}

/**
 * Best-effort linter for a sideloaded extension's `extension.yaml`, surfaced in the Extension Dev
 * tools. It reconciles the already-parsed [InstalledExtension] against the raw manifest (to catch
 * typo'd top-level keys the parser silently drops) and checks the fields extension authors most
 * often get wrong. Not a schema validator — it errs toward actionable warnings over completeness.
 */
object ExtensionManifestValidator {

    private val KNOWN_TOP_LEVEL = setOf(
        "id", "name", "publisher", "author", "authors", "type", "version", "description",
        "longDescription", "shortDescription", "samples", "templates", "language", "languages",
        "settings", "api", "requires", "suggests", "contributes", "entry", "images",
        "minJCodeVersion", "targetJCodeVersion", "maxJCodeVersion", "category", "subcategory",
    )
    private val KNOWN_CAPABILITIES = setOf("api", "exec", "fs", "config", "workbench", "service")

    fun validate(ext: InstalledExtension, hostApiVersion: Int): List<ManifestIssue> {
        val issues = mutableListOf<ManifestIssue>()
        fun err(msg: String, path: String? = null) = issues.add(ManifestIssue(ManifestIssue.Severity.Error, msg, path))
        fun warn(msg: String, path: String? = null) = issues.add(ManifestIssue(ManifestIssue.Severity.Warning, msg, path))
        fun info(msg: String, path: String? = null) = issues.add(ManifestIssue(ManifestIssue.Severity.Info, msg, path))

        // --- identity ---
        if (ext.version.isNullOrBlank()) warn("未提供 `version` —— 更新和扩展市场发布都需要它。", "version")
        if (ext.name == ext.id) warn("`name` equals `id` — set a human-readable display name.", "name")
        if (ext.type == ExtensionType.Unknown) {
            warn("无法识别的 `type` —— 将回退为通用扩展（无特定类型界面）。", "type")
        }

        // --- unknown top-level keys (typos that silently drop a whole section) ---
        val rawMap = runCatching { parseYamlMapping(File(ext.dir, "extension.yaml").readText()) }.getOrNull()
        rawMap?.keys?.forEach { key ->
            if (key !in KNOWN_TOP_LEVEL) warn("未知的顶层键 `$key` —— 已忽略（拼写错误？）。", key)
        }
        // A typo'd entry.ui resolves to webUiEntry=null (the installer only sets it when the file
        // exists), so check the RAW value against disk to actually catch the broken path.
        val rawUi = ((rawMap?.get("entry") as? Map<*, *>)?.get("ui") as? String)?.takeIf { it.isNotBlank() }
        if (rawUi != null && !File(ext.dir, rawUi).isFile) {
            err("`entry.ui` points at `$rawUi` which doesn't exist in the package.", "entry.ui")
        }

        // --- API ---
        if (ext.apiMinVersion > hostApiVersion) {
            err("`api.minApiVersion` ${ext.apiMinVersion} > this JCode's v$hostApiVersion — won't run here.", "api.minApiVersion")
        }
        ext.apiCapabilities.forEach { cap ->
            if (cap !in KNOWN_CAPABILITIES) {
                warn("未知的 API 能力 `$cap`（已知：${KNOWN_CAPABILITIES.joinToString(", ")}）。", "api.capabilities")
            }
        }

        // --- languages ---
        ext.languages.forEachIndexed { i, lang ->
            if (lang.fileExtensions.isEmpty()) {
                warn("语言 `${lang.languageId}` 未声明文件 `extensions` —— 无法匹配任何文件。", "languages[$i].extensions")
            }
        }

        // --- icon sets ---
        validateIconSets(ext, ::err, ::info)

        // --- run presets ---
        ext.contributes.runConfigPresets.forEachIndexed { i, preset ->
            val base = "contributes.runConfigPresets[$i]"
            if (preset.requires.isEmpty()) err("预设 `${preset.id}` 缺少 `requires` 通配模式，因此不会被展示。", "$base.requires")
            if (preset.terminals.isEmpty()) err("预设 `${preset.id}` 缺少 `terminals`。", "$base.terminals")
            // A build task is one command with nothing to poll, so both extras are dropped rather than
            // honoured — worth saying, since the manifest gives no other sign of it.
            if (preset.kind == RunPresetKind.Build) {
                if (preset.terminals.size > 1) {
                    warn("预设 `${preset.id}` 是构建任务，因此只有其第一个终端会运行。", "$base.terminals")
                }
                if (preset.readyPort > 0) warn("预设 `${preset.id}` 是构建任务；`readyPort` 会被忽略。", "$base.readyPort")
            }
            preset.requires.forEach { glob ->
                if (runCatching { globToRegexOrNull(glob) }.getOrNull() == null) {
                    warn("预设 `${preset.id}`：通配模式 `$glob` 无效。", "$base.requires")
                }
            }
            // {{fileN}}/{{dirN}} beyond the number of required files never resolves. Scan for the
            // {{…}} tokens by hand and match the inner name with a BRACE-FREE regex — a literal
            // brace in the pattern throws PatternSyntaxException on Android's regex engine.
            val maxIndex = preset.requires.size
            val innerRe = Regex("^(?:file|dir)(\\d+)$")
            preset.terminals.forEach { term ->
                val cmd = term.command
                var i = cmd.indexOf("{{")
                while (i >= 0) {
                    val end = cmd.indexOf("}}", i + 2)
                    if (end < 0) break
                    val inner = cmd.substring(i + 2, end)
                    innerRe.matchEntire(inner)?.groupValues?.get(1)?.toIntOrNull()?.let { n ->
                        if (n < 1 || n > maxIndex) {
                            warn("预设 `${preset.id}`：`{{$inner}}` 没有匹配的 require（共 $maxIndex 个）。", "$base.terminals")
                        }
                    }
                    i = cmd.indexOf("{{", end + 2)
                }
            }
        }

        // --- deps (informational) ---
        val allDeps = ext.requires.sdks + ext.requires.lsps + ext.requires.dbg
        if (allDeps.isNotEmpty()) info("需要工具链：${allDeps.joinToString(", ")}（随扩展一同安装）。", "requires")

        return issues.sortedBy { it.severity.ordinal }
    }

    /**
     * Reports an icon pack whose indexes were not found.
     *
     * This is the failure an author cannot see from the app: the pack installs, `type: iconpack`
     * reads as correct, and Settings simply never offers the set. The paths themselves were already
     * resolved by [IconPackLayout] at install time — this only says what came back empty.
     */
    private fun validateIconSets(
        ext: InstalledExtension,
        err: (String, String?) -> Unit,
        info: (String, String?) -> Unit,
    ) {
        val sets = ext.contributes.iconSets
        sets.unresolved.forEach { declaration ->
            val (path, value) = declaration.split(": ", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
            err("`$path` points at `$value`, which holds no index.yaml.", path)
        }
        if (ext.type == ExtensionType.IconPack && sets.isEmpty) {
            err(
                "`type: iconpack` but no icon index found — add `ui-icons/index.yaml`, " +
                    "`files-icons/index.yaml`, or `contributes.iconSets`.",
                "type",
            )
        }
        if (ext.type != ExtensionType.IconPack && !sets.isEmpty) {
            info("提供图标集；`type: iconpack` 会将其在扩展市场中归类为图标包。", "type")
        }
        if (sets.filesIndexes.isNotEmpty() && sets.uiIndexes.isEmpty()) {
            info("仅提供文件图标；界面图标保持当前选择。", "contributes.iconSets")
        }
        if (sets.uiIndexes.isNotEmpty() && sets.filesIndexes.isEmpty()) {
            info("仅提供界面图标；文件图标保持当前选择。", "contributes.iconSets")
        }
        if (sets.uiIndexes.size + sets.filesIndexes.size > 1) {
            info(
                "提供 ${sets.uiIndexes.size} 个界面图标和 ${sets.filesIndexes.size} 个文件图标 " +
                    "集；每个图标集都会在「设置」中单独提供。",
                "contributes.iconSets",
            )
        }
    }

    // A globstar-aware glob compile that returns null on failure (mirrors ProjectRunner's globToRegex
    // rules: `**`→any, `*`→within-segment, `?`→one char). Used only to flag un-compilable globs.
    private fun globToRegexOrNull(glob: String): Regex? = runCatching {
        val sb = StringBuilder()
        var i = 0
        while (i < glob.length) {
            when {
                glob.startsWith("**", i) -> { sb.append(".*"); i += if (glob.getOrNull(i + 2) == '/') 3 else 2 }
                glob[i] == '*' -> { sb.append("[^/]*"); i++ }
                glob[i] == '?' -> { sb.append("[^/]"); i++ }
                else -> { sb.append(Regex.escape(glob[i].toString())); i++ }
            }
        }
        Regex(sb.toString())
    }.getOrNull()
}
