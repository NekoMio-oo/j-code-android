package dev.blamspot.jcode.feature.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import dev.blamspot.jcode.design.AlertDialog
import dev.blamspot.jcode.design.CompactFilledButton
import dev.blamspot.jcode.design.CompactOutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.blamspot.jcode.core.config.ConfigScope
import dev.blamspot.jcode.core.config.EffectiveConfig
import dev.blamspot.jcode.core.config.ProjectConfig
import dev.blamspot.jcode.core.config.WorkspaceConfig
import dev.blamspot.jcode.design.FileIconSet
import dev.blamspot.jcode.design.painter
import dev.blamspot.jcode.design.LocalFileIconSet
import dev.blamspot.jcode.design.FileTypeIcon
import dev.blamspot.jcode.design.FileIconSetRegistry
import dev.blamspot.jcode.design.LocalIconSetSettings
import dev.blamspot.jcode.design.UiIconSet
import dev.blamspot.jcode.design.UiIconSetRegistry
import dev.blamspot.jcode.design.IconSize
import dev.blamspot.jcode.design.JCodeIcon
import dev.blamspot.jcode.design.Radius
import dev.blamspot.jcode.design.SettingsActionRow
import dev.blamspot.jcode.design.Space
import dev.blamspot.jcode.design.StrokeWidth
import dev.blamspot.jcode.design.jcIcon
import dev.blamspot.jcode.design.BottomBarVisibility
import dev.blamspot.jcode.design.ExtraKeysVisibility
import dev.blamspot.jcode.design.LocalBottomBarSetting
import dev.blamspot.jcode.design.LocalFontSettings
import dev.blamspot.jcode.design.LocalEditorDragMovesCursor
import dev.blamspot.jcode.design.LocalDiagnosticsSetting
import dev.blamspot.jcode.design.LocalEditorFontSizeSetting
import dev.blamspot.jcode.design.LocalExtensionFontSizeSetting
import dev.blamspot.jcode.design.LocalTerminalFontSizeSetting
import dev.blamspot.jcode.design.LocalEditorWordWrapSetting
import dev.blamspot.jcode.design.LocalExtraKeysSetting
import dev.blamspot.jcode.design.LocalPerformanceSettings
import dev.blamspot.jcode.design.LocalRightDrawerSetting
import dev.blamspot.jcode.design.ExplorerExcludeEffect
import dev.blamspot.jcode.design.ExplorerHiddenMode
import dev.blamspot.jcode.design.LocalAndroidDevice
import dev.blamspot.jcode.design.LocalAppUpdate
import dev.blamspot.jcode.design.LocalSettingsBackup
import dev.blamspot.jcode.design.EnvVarSettings
import dev.blamspot.jcode.design.LocalEnvVarSettings
import dev.blamspot.jcode.design.LocalEnvironmentBackup
import dev.blamspot.jcode.design.LocalCutoutSetting
import dev.blamspot.jcode.design.LocalExplorerHiddenSetting
import dev.blamspot.jcode.design.LocalTrashSettings
import dev.blamspot.jcode.design.TRASH_RETENTION_CHOICES
import dev.blamspot.jcode.design.trashRetentionLabel
import dev.blamspot.jcode.design.LocalTabColoringSetting
import dev.blamspot.jcode.design.LocalTabMaxSize
import dev.blamspot.jcode.design.TabColoring
import dev.blamspot.jcode.design.TabMaxSize
import dev.blamspot.jcode.design.HeaderActionButton
import dev.blamspot.jcode.design.LocalCommandPaletteSetting
import dev.blamspot.jcode.design.LocalHeaderActionSetting
import dev.blamspot.jcode.design.LocalDeveloperSetting
import dev.blamspot.jcode.design.LocalMarkdownPreviewSetting
import dev.blamspot.jcode.design.LocalVolumeKeysSetting
import dev.blamspot.jcode.design.PaletteCommandCatalog
import dev.blamspot.jcode.design.VolumeKeyAction
import dev.blamspot.jcode.design.LocalRestoreSession
import dev.blamspot.jcode.design.WebPreviewBrowsers
import dev.blamspot.jcode.design.LocalWebPreviewBrowsers
import dev.blamspot.jcode.design.LocalTabCloseButtonSetting
import dev.blamspot.jcode.design.SettingsDefaults
import dev.blamspot.jcode.design.SettingsDropdownRow
import dev.blamspot.jcode.design.SettingsResettableRow
import dev.blamspot.jcode.design.SettingsTextFieldRow
import dev.blamspot.jcode.design.ThemeBundleRegistry
import dev.blamspot.jcode.core.diag.DiagLevel
import dev.blamspot.jcode.core.distro.AppProcesses
import dev.blamspot.jcode.core.distro.DistroEnvironmentState
import dev.blamspot.jcode.design.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

object SettingsFeature {

    /** Group the next composition should jump to, or null. Set by [revealGroup]. */
    private val pendingReveal = mutableStateOf<String?>(null)

    /**
     * Ask the Settings screen to reveal a group by title — switch to the GLOBAL tab, clear any
     * search, expand it, and scroll it into view. Used by the "update available" toast so its Update
     * action lands the user on the progress it starts. Safe to call before Settings is composed: the
     * request is consumed by whichever composition runs next.
     */
    fun revealGroup(title: String) {
        pendingReveal.value = title
    }

    @Composable
    fun Content(
        effectiveConfig: EffectiveConfig,
        workspaceConfig: WorkspaceConfig?,
        projectConfig: ProjectConfig?,
        workspaceError: String?,
        projectError: String?,
        projectOverridesAvailable: Boolean,
        environmentState: DistroEnvironmentState,
        onOpenWorkspaceConfig: () -> Unit,
        onOpenProjectConfig: () -> Unit,
        onOpenEnvironmentWizard: () -> Unit,
        onRefreshEnvironment: () -> Unit,
        // A null value clears the override from the scope's .jcode (see MainViewModel).
        onUpdateFontSize: (ConfigScope, Float?) -> Unit,
        onUpdateTabSize: (ConfigScope, Int?) -> Unit,
        onUpdateTabColoring: (ConfigScope, String?) -> Unit,
        onUpdateLigatures: (ConfigScope, Boolean?) -> Unit,
        onUpdateExplorerViewMode: (ConfigScope, String?) -> Unit,
        themeMode: ThemeMode,
        onUpdateThemeMode: (ThemeMode?) -> Unit,
        themeBundleId: String,
        onUpdateThemeBundle: (String) -> Unit,
        formatterId: String,
        formatterOptions: List<Pair<String, String>>,
        onSelectFormatter: (String) -> Unit,
        isUserWorkspace: Boolean = false,
        modifier: Modifier = Modifier,
    ) {
        val iconSettings = LocalIconSetSettings.current
        val tabCloseSetting = LocalTabCloseButtonSetting.current
        val editorDragSetting = LocalEditorDragMovesCursor.current
        val restoreSessionSetting = LocalRestoreSession.current
        val explorerHiddenSetting = LocalExplorerHiddenSetting.current
        val trashSettings = LocalTrashSettings.current
        val cutoutSetting = LocalCutoutSetting.current
        val volumeKeysSetting = LocalVolumeKeysSetting.current
        val tabColoringSetting = LocalTabColoringSetting.current
        val tabMaxSizeSetting = LocalTabMaxSize.current
        val extraKeysSetting = LocalExtraKeysSetting.current
        val bottomBarSetting = LocalBottomBarSetting.current
        val fontSettings = LocalFontSettings.current
        val perf = LocalPerformanceSettings.current
        val webPreview = LocalWebPreviewBrowsers.current
        // The tab IS the scope — no separate "Edit scope" selector. Index 0 = Global (app-level);
        // each further tab edits one .jcode scope: WORKSPACE appears when a User Workspace is open,
        // PROJECT when a local project is selected, and the Default Workspace's own scope is offered
        // only when there is no project to scope to.
        var selectedTab by rememberSaveable { mutableStateOf(0) }
        val tabScopes: List<ConfigScope?> = buildList {
            add(null)
            if (isUserWorkspace) add(ConfigScope.Workspace)
            if (projectOverridesAvailable) add(ConfigScope.Project)
            if (size == 1) add(ConfigScope.Workspace)
        }
        // The trailing "环境变量" tab lives at index tabScopes.size (it is not a ConfigScope).
        val safeTab = selectedTab.coerceIn(0, tabScopes.size)
        val isEnvVarTab = safeTab == tabScopes.size
        // Scoped cards also render while a search is active (from any tab); they then edit the most
        // specific scope available. getOrNull guards the ENV VAR tab index (out of tabScopes range).
        val selectedScope = tabScopes.getOrNull(safeTab)
            ?: if (projectOverridesAvailable) ConfigScope.Project else ConfigScope.Workspace

        val scopedEditor = when (selectedScope) {
            ConfigScope.Workspace -> workspaceConfig?.editor
            ConfigScope.Project -> projectConfig?.editor
        }

        val fontSize = scopedEditor?.fontSize ?: effectiveConfig.editor.fontSize
        val tabSize = scopedEditor?.tabSize ?: effectiveConfig.editor.tabSize
        val ligatures = scopedEditor?.ligatures ?: effectiveConfig.editor.ligatures

        val scopedExplorer = when (selectedScope) {
            ConfigScope.Workspace -> workspaceConfig?.explorer
            ConfigScope.Project -> projectConfig?.explorer
        }
        val explorerViewMode = scopedExplorer?.viewMode ?: effectiveConfig.explorer.viewMode

        var query by rememberSaveable { mutableStateOf("") }
        val scrollState = rememberScrollState()
        // A reveal request (e.g. the update toast's Update action) puts the named group on screen:
        // the GLOBAL tab because that is where they live, no search because a query bypasses grouping
        // entirely, then expand and scroll. The group's offset is only known once it has been laid
        // out, and Settings may have opened on this very frame, so wait for it rather than guessing.
        val reveal = pendingReveal.value
        LaunchedEffect(reveal) {
            val title = reveal ?: return@LaunchedEffect
            selectedTab = 0
            query = ""
            settingsGroupExpanded.getOrPut(title) { mutableStateOf(false) }.value = true
            var frames = 0
            while (settingsGroupOffsets[title] == null && frames++ < 30) withFrameNanos { }
            settingsGroupOffsets[title]?.let { scrollState.animateScrollTo(it.roundToInt()) }
            pendingReveal.value = null
        }
        // Fresh each composition; cards increment it when they pass the filter, and the trailing
        // empty-state reads it after all cards have composed.
        val matchSink = SettingsMatchSink()
        CompositionLocalProvider(
            LocalSettingsQuery provides query,
            LocalSettingsMatchSink provides matchSink,
        ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                // Less room above the scope tabs than around everything else: they are a header, and
                // the editor's own tab strip is already a horizontal edge directly above them, so a
                // full margin there read as a band of nothing between two rows of tabs.
                .padding(start = Space.md, end = Space.md, top = Space.xs, bottom = Space.md),
            verticalArrangement = Arrangement.spacedBy(Space.ms),
        ) {
            // Material underline tabs, left-packed. ScrollableTabRow's own divider only spans the
            // tab content, so it is suppressed and a full-width one is drawn behind the row.
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.align(Alignment.BottomStart))
                ScrollableTabRow(
                    selectedTabIndex = safeTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                ) {
                    tabScopes.forEachIndexed { index, scope ->
                        Tab(
                            selected = safeTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.height(40.dp),
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = {
                                Text(
                                    text = when (scope) {
                                        null -> "GLOBAL"
                                        ConfigScope.Workspace -> "WORKSPACE"
                                        ConfigScope.Project -> "PROJECT"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                        )
                    }
                    // Trailing content tab (not a scope): the environment-variable editor.
                    Tab(
                        selected = isEnvVarTab,
                        onClick = { selectedTab = tabScopes.size },
                        modifier = Modifier.height(40.dp),
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                text = "环境变量",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                    )
                }
            }
            if (isEnvVarTab) {
                EnvVarEditor(LocalEnvVarSettings.current)
            } else {
            SettingsSearchField(query = query, onQueryChange = { query = it })
            }
            // Search is scoped to the SELECTED tab (like VS Code's User/Workspace split): the GLOBAL
            // tab shows only app-level settings, WORKSPACE/PROJECT only the .jcode-scoped ones — so a
            // search on the Project tab never surfaces global settings that aren't project-overridable.
            val showGlobalTab = safeTab == 0
            val showScopedTab = safeTab in 1 until tabScopes.size
            if (showGlobalTab) {
            SettingsGroup("外观") {
            SettingsCard(
                title = "外观",
                description = "跟随设备的浅色/深色设置。",
                keywords = "外观 主题 深色 浅色 系统 颜色 模式 方案 appearance theme dark light system color mode scheme",
            ) {
                SettingsDropdownRow(
                    label = "模式",
                    options = ThemeMode.entries.map { it.name },
                    selected = themeMode.name,
                    onSelect = { onUpdateThemeMode(ThemeMode.valueOf(it)) },
                    modified = workspaceConfig?.theme?.id != null || projectConfig?.theme?.id != null,
                    onReset = { onUpdateThemeMode(null) },
                )
            }

            SettingsCard(
                title = "主题包",
                description = "应用于整个应用的调色板。",
                keywords = "主题包 调色板 配色 catppuccin dracula midnight oled 黑色 方案 外观 theme bundle color palette catppuccin dracula midnight oled black scheme appearance",
            ) {
                val activeBundle = themeBundleId.ifEmpty { ThemeBundleRegistry.default.id }
                ThemeBundleRegistry.builtIns.forEach { bundle ->
                    BundleRow(
                        name = bundle.name,
                        description = bundle.description,
                        selected = activeBundle == bundle.id,
                        swatch = listOf(
                            bundle.dark.primary,
                            bundle.dark.secondary,
                            bundle.dark.tertiary,
                            bundle.dark.surface,
                        ),
                        onClick = { onUpdateThemeBundle(bundle.id) },
                    )
                }
            }

            SettingsCard(
                title = "界面图标",
                description = "用于应用自身工具栏、选项卡和菜单的图标集。",
                keywords = "图标包 图标集 界面 material 圆角 jcode 线条 外观 icon bundle icons set ui material rounded jcode line appearance" +
                    iconSettings.uiSets.joinToString(" ") { it.name },
            ) {
                val activeUi = iconSettings.activeUiSetId
                iconSettings.uiSets.forEach { set ->
                    UiIconSetRow(
                        set = set,
                        selected = activeUi == set.id,
                        onClick = { iconSettings.onSelectUiSet(set.id) },
                    )
                }
            }

            // Hidden until something can fill it: JCode ships no file icon set, so with no icon-pack
            // extension installed this card would offer exactly one choice — the one already in use.
            if (iconSettings.fileSets.isNotEmpty()) {
                SettingsCard(
                    title = "文件图标",
                    description = "用于资源管理器、选项卡和搜索结果中文件与文件夹的图标集。",
                    keywords = "图标包 图标集 文件 文件夹 资源管理器 外观 icon bundle icons set file files folder explorer appearance" +
                        iconSettings.fileSets.joinToString(" ") { it.name },
                ) {
                    val activeFiles = iconSettings.activeFileSetId
                    FileIconSetRow(
                        name = "无",
                        description = "JCode 自带的文件夹与文件字形，取自界面图标集。",
                        detail = null,
                        selected = activeFiles == FileIconSetRegistry.NONE_ID,
                        onClick = { iconSettings.onSelectFileSet(FileIconSetRegistry.NONE_ID) },
                    )
                    iconSettings.fileSets.forEach { set ->
                        FileIconSetRow(
                            name = set.name,
                            description = set.description,
                            detail = set,
                            selected = activeFiles == set.id,
                            onClick = { iconSettings.onSelectFileSet(set.id) },
                        )
                    }
                }
            }

            SettingsCard(
                title = "字体",
                description = "用于代码编辑器和终端的等宽字体。更多字体" +
                    "可由扩展添加。",
                keywords = "字体 字形 等宽 编辑器 终端 jetbrains mono 系统 代码 外观 font fonts family typeface monospace editor terminal jetbrains mono system code appearance" +
                    fontSettings.options.joinToString(" ") { it.name },
            ) {
                // Re-scan the environment's installed fonts each time this card is shown, so fonts the
                // user apt-installed since launch appear without a restart.
                LaunchedEffect(Unit) { fontSettings.onScanFonts() }
                val fontOptionIds = fontSettings.options.map { it.id }
                val fontLabel: (String) -> String =
                    { id -> fontSettings.options.firstOrNull { it.id == id }?.name ?: id }
                SettingsDropdownRow(
                    label = "编辑器字体",
                    options = fontOptionIds,
                    selected = fontSettings.editorFontId,
                    onSelect = fontSettings.onSelectEditorFont,
                    optionLabel = fontLabel,
                    modified = fontSettings.editorFontId != fontSettings.editorDefaultId,
                    onReset = { fontSettings.onSelectEditorFont(fontSettings.editorDefaultId) },
                )
                SettingsDropdownRow(
                    label = "终端字体",
                    options = fontOptionIds,
                    selected = fontSettings.terminalFontId,
                    onSelect = fontSettings.onSelectTerminalFont,
                    optionLabel = fontLabel,
                    modified = fontSettings.terminalFontId != fontSettings.terminalDefaultId,
                    onReset = { fontSettings.onSelectTerminalFont(fontSettings.terminalDefaultId) },
                )
            }

            SettingsCard(
                title = "终端",
                description = "终端会话的文字大小。应用于所有已打开的终端。",
                keywords = "终端 字体大小 文字 缩放 放大 缩小 控制台 shell tty 可读 terminal font size text scale sp bigger smaller zoom console shell tty readable",
            ) {
                val terminalFontSizeSetting = LocalTerminalFontSizeSetting.current
                StepperRow(
                    label = "字号",
                    value = "${terminalFontSizeSetting.value.toInt()} sp",
                    onDecrease = { terminalFontSizeSetting.onChange((terminalFontSizeSetting.value - 1f).coerceAtLeast(6f)) },
                    onIncrease = { terminalFontSizeSetting.onChange((terminalFontSizeSetting.value + 1f).coerceAtMost(40f)) },
                    modified = terminalFontSizeSetting.value != SettingsDefaults.TERMINAL_FONT_SIZE,
                    onReset = { terminalFontSizeSetting.onChange(SettingsDefaults.TERMINAL_FONT_SIZE) },
                )
            }

            SettingsCard(
                title = "扩展",
                description = "导入的 .vsix 扩展内的文字大小。是缩放比例而非" +
                    "绝对尺寸，因为每个扩展的页面样式各异。",
                keywords = "扩展 vsix 字体大小 文字 缩放 放大 缩小 extension extensions vsix font size text scale zoom bigger smaller" +
                    " 可读 网页视图 扩展市场 已导入 readable webview marketplace imported",
            ) {
                val extensionFontSize = LocalExtensionFontSizeSetting.current
                StepperRow(
                    label = "字号",
                    value = "${extensionFontSize.percent}%",
                    onDecrease = { extensionFontSize.onChange((extensionFontSize.percent - 10).coerceAtLeast(50)) },
                    onIncrease = { extensionFontSize.onChange((extensionFontSize.percent + 10).coerceAtMost(300)) },
                    modified = extensionFontSize.percent != SettingsDefaults.EXTENSION_FONT_SCALE,
                    onReset = { extensionFontSize.onChange(SettingsDefaults.EXTENSION_FONT_SCALE) },
                )
            }

            // Hidden on displays without a cutout (desktop mode, external display, notchless devices).
            if (cutoutSetting.hasCutout) {
                SettingsCard(
                    title = "屏幕挖孔",
                    description = "让应用避开摄像头刘海或挖孔。关闭后，" +
                        "应用将绘制到挖孔区域以实现全屏布局。",
                    keywords = "挖孔 刘海 打孔 摄像头 显示 安全区域 全屏 屏幕边缘 边衬区 cutout notch punch hole camera display safe area letterbox fullscreen screen edge insets",
                ) {
                    ToggleRow(
                        label = "适配设备挖孔",
                        supporting = "将应用布局在刘海屏安全区域内，而不是绘制在其后方。",
                        checked = cutoutSetting.respect,
                        onCheckedChange = cutoutSetting.onChange,
                        modified = cutoutSetting.respect != SettingsDefaults.RESPECT_DEVICE_CUTOUT,
                        onReset = { cutoutSetting.onChange(SettingsDefaults.RESPECT_DEVICE_CUTOUT) },
                    )
                }
            }

            SettingsCard(
                title = "右侧抽屉",
                description = "容纳终端、输出、问题和扩展视图的面板。",
                keywords = "右侧 抽屉 面板 侧边栏 持久 停靠 分屏 半宽 横屏 终端 检查器 right drawer panel sidebar persistent dock split half width landscape terminal inspector",
            ) {
                val rightDrawerSetting = LocalRightDrawerSetting.current
                ToggleRow(
                    label = "横屏时停靠",
                    supporting = "横屏时，与右侧抽屉分屏显示，而不是" +
                        "将其滑到编辑器上方，从而两者都保持可用。竖屏不受影响。",
                    checked = rightDrawerSetting.enabled,
                    onCheckedChange = rightDrawerSetting.onSetEnabled,
                    modified = rightDrawerSetting.enabled != SettingsDefaults.RIGHT_DRAWER_PERSISTENT,
                    onReset = { rightDrawerSetting.onSetEnabled(SettingsDefaults.RIGHT_DRAWER_PERSISTENT) },
                )
            }

            SettingsCard(
                title = "标题栏",
                description = "工作区顶部的横条，显示项目名称和" +
                    "快捷操作。",
                keywords = "标题栏 顶栏 应用栏 终端 命令面板 按钮 操作 隐藏 禁用 移除 header top bar app bar terminal command palette button action hide disable remove",
            ) {
                val headerActionSetting = LocalHeaderActionSetting.current
                SettingsDropdownRow(
                    label = "操作按钮",
                    supporting = "运行按钮旁边的按钮。隐藏它后，仍可通过" +
                        "右侧抽屉访问终端。",
                    options = HeaderActionButton.entries.map { it.name },
                    selected = headerActionSetting.button.name,
                    onSelect = { headerActionSetting.onChange(HeaderActionButton.valueOf(it)) },
                    optionLabel = { headerActionButtonLabel(HeaderActionButton.valueOf(it)) },
                    modified = headerActionSetting.button != SettingsDefaults.HEADER_ACTION_BUTTON,
                    onReset = { headerActionSetting.onChange(SettingsDefaults.HEADER_ACTION_BUTTON) },
                )
            }

            SettingsCard(
                title = "底部状态栏",
                description = "工作区底部的横条，显示分支、发行版和" +
                    "光标位置。",
                keywords = "底部 状态栏 分支 发行版 光标位置 隐藏 始终显示 软键盘 bottom status bar branch distro cursor position hide always show soft keyboard chrome space",
            ) {
                SettingsDropdownRow(
                    label = "显示",
                    options = BottomBarVisibility.entries.map { it.name },
                    selected = bottomBarSetting.visibility.name,
                    onSelect = { bottomBarSetting.onChange(BottomBarVisibility.valueOf(it)) },
                    optionLabel = { bottomBarVisibilityLabel(BottomBarVisibility.valueOf(it)) },
                    modified = bottomBarSetting.visibility != SettingsDefaults.BOTTOM_STATUS_BAR,
                    onReset = { bottomBarSetting.onChange(SettingsDefaults.BOTTOM_STATUS_BAR) },
                )
            }

            SettingsCard(
                title = "扩展按键行",
                description = "Termux 风格的按键行（Esc、Tab、Ctrl、方向键等），显示在" +
                    "终端或编辑器中输入时的键盘上方。选择它在 " +
                    "横竖屏方向下的显示时机。",
                keywords = "扩展按键 软键盘 功能键 方向键 横竖屏 extra keys row esc ctrl alt tab arrows home end pgup pgdn page terminal editor keyboard termux orientation portrait landscape hidden always with soft keyboard function keys f1 f2 f3 f4 f5 f6 f7 f8 f9 f10 f11 f12 fn htop midnight commander",
            ) {
                SettingsDropdownRow(
                    label = "竖屏",
                    options = ExtraKeysVisibility.entries.map { it.name },
                    selected = extraKeysSetting.portrait.name,
                    onSelect = { extraKeysSetting.onChangePortrait(ExtraKeysVisibility.valueOf(it)) },
                    optionLabel = { extraKeysVisibilityLabel(ExtraKeysVisibility.valueOf(it)) },
                    modified = extraKeysSetting.portrait != SettingsDefaults.EXTRA_KEYS_PORTRAIT,
                    onReset = { extraKeysSetting.onChangePortrait(SettingsDefaults.EXTRA_KEYS_PORTRAIT) },
                )
                SettingsDropdownRow(
                    label = "横屏",
                    options = ExtraKeysVisibility.entries.map { it.name },
                    selected = extraKeysSetting.landscape.name,
                    onSelect = { extraKeysSetting.onChangeLandscape(ExtraKeysVisibility.valueOf(it)) },
                    optionLabel = { extraKeysVisibilityLabel(ExtraKeysVisibility.valueOf(it)) },
                    modified = extraKeysSetting.landscape != SettingsDefaults.EXTRA_KEYS_LANDSCAPE,
                    onReset = { extraKeysSetting.onChangeLandscape(SettingsDefaults.EXTRA_KEYS_LANDSCAPE) },
                )
                ToggleRow(
                    label = "功能键",
                    supporting = "在终端获得焦点时，向该行追加 F1–F12 快捷键（htop、" +
                        "midnight commander 及其他 TUI 程序会使用它们）。",
                    checked = extraKeysSetting.functionKeys,
                    onCheckedChange = { extraKeysSetting.onChangeFunctionKeys(it) },
                    modified = extraKeysSetting.functionKeys != SettingsDefaults.EXTRA_KEYS_FUNCTION_KEYS,
                    onReset = { extraKeysSetting.onChangeFunctionKeys(SettingsDefaults.EXTRA_KEYS_FUNCTION_KEYS) },
                )
            }

            } // end Appearance

            SettingsGroup("Input") {
            SettingsCard(
                title = "音量键",
                description = "将硬件音量键重映射为编辑器/终端操作。" +
                    "“系统默认”保持常规音量控制。窗格操作（方向键、滚动）作用于" +
                    "当前聚焦的编辑器或终端；按住可重复方向键和滚动操作。",
                keywords = "音量键 硬件 重映射 绑定 快捷键 撤销 重做 滚动 volume keys button hardware remap bind binding shortcut undo redo arrow scroll " +
                    "命令面板 输入 上 下 翻页 音量键 媒体",
            ) {
                SettingsDropdownRow(
                    label = "音量加",
                    options = VolumeKeyAction.entries.map { it.name },
                    selected = volumeKeysSetting.up.name,
                    onSelect = { volumeKeysSetting.onChangeUp(VolumeKeyAction.valueOf(it)) },
                    optionLabel = { volumeKeyActionLabel(VolumeKeyAction.valueOf(it), "音量加") },
                    modified = volumeKeysSetting.up != SettingsDefaults.VOLUME_UP_ACTION,
                    onReset = { volumeKeysSetting.onChangeUp(SettingsDefaults.VOLUME_UP_ACTION) },
                )
                SettingsDropdownRow(
                    label = "音量减",
                    options = VolumeKeyAction.entries.map { it.name },
                    selected = volumeKeysSetting.down.name,
                    onSelect = { volumeKeysSetting.onChangeDown(VolumeKeyAction.valueOf(it)) },
                    optionLabel = { volumeKeyActionLabel(VolumeKeyAction.valueOf(it), "音量减") },
                    modified = volumeKeysSetting.down != SettingsDefaults.VOLUME_DOWN_ACTION,
                    onReset = { volumeKeysSetting.onChangeDown(SettingsDefaults.VOLUME_DOWN_ACTION) },
                )
            }

            SettingsCard(
                title = "命令面板",
                description = "选择命令面板要提供的内置命令。与上下文相关的 " +
                    "命令仅在其视图聚焦时显示（例如「转到行」需要打开编辑器）。",
                keywords = "命令面板 命令 方向锁定 全屏 保持唤醒 屏幕常亮 " +
                    "隐藏 顶栏 选项卡 禅定模式 转到行 颜色 搜索 选择器 取色器 格式化文档",
            ) {
                val paletteSetting = LocalCommandPaletteSetting.current
                PaletteCommandCatalog.forEach { command ->
                    val enabled = command.id !in paletteSetting.disabledIds
                    ToggleRow(
                        label = command.label,
                        supporting = command.description,
                        checked = enabled,
                        onCheckedChange = { paletteSetting.onSetEnabled(command.id, it) },
                        modified = !enabled,
                        onReset = { paletteSetting.onSetEnabled(command.id, true) },
                    )
                }
            }

            } // end Input

            SettingsGroup("Startup") {
            SettingsCard(
                title = "恢复上次会话",
                description = "关闭应用后，从上次离开的位置继续。",
                keywords = "恢复 会话 重新打开 选项卡 工作区 项目 未保存 找回 启动",
            ) {
                ToggleRow(
                    label = "启动时恢复上次会话",
                    supporting = "JCode 启动时重新打开上次的工作区、项目和编辑器选项卡（包括未保存的更改）。缺失的文件将被跳过。",
                    checked = restoreSessionSetting.enabled,
                    onCheckedChange = restoreSessionSetting.onChange,
                    modified = restoreSessionSetting.enabled != SettingsDefaults.RESTORE_LAST_SESSION,
                    onReset = { restoreSessionSetting.onChange(SettingsDefaults.RESTORE_LAST_SESSION) },
                )
            }

            } // end Startup

            // Per-extension settings now live on the Extension Settings screen (Extensions list → gear),
            // alongside each extension's permissions — not here in App Settings.

            SettingsGroup("Performance") {
            SettingsCard(
                title = "渲染",
                description = "JCode 绘制界面、编辑器和终端的方式。",
                keywords = "性能 渲染 硬件加速 gpu 软件 绘制 图形 卡顿 流畅",
            ) {
                ToggleRow(
                    label = "硬件加速",
                    supporting = "在 GPU 上渲染界面、编辑器和终端。仅在" +
                        "排查此设备上的渲染故障时才将其关闭——软件渲染" +
                        "更慢，重启应用后生效。",
                    checked = perf.hardwareAcceleration,
                    onCheckedChange = perf.onSetHardwareAcceleration,
                    modified = perf.hardwareAcceleration != SettingsDefaults.HARDWARE_ACCELERATION,
                    onReset = { perf.onSetHardwareAcceleration(SettingsDefaults.HARDWARE_ACCELERATION) },
                )
            }
            SettingsCard(
                title = "资源管理",
                description = "通过停止已完成的工作，让 Linux 运行时保持轻量。每个终端、 " +
                    "运行和调试会话都会在内存中持有一棵 proot 进程树。",
                keywords = "性能 内存 电池 进程 后台 资源 优化 超时 自动关闭 performance memory cpu battery proot process terminal kill close idle background resource optimize swipe away warn running max instances timeout auto-close nested sub-shell subshell relocate tab bash zsh install toolchain sdk download timeout minutes android",
            ) {
                ToggleRow(
                    label = "关闭运行中的进程前发出警告",
                    supporting = "关闭包含正在运行的终端命令的项目或工作区时，若存在处于活动状态的" +
                        "构建与运行，或实时调试会话，请在停止前先询问。",
                    checked = perf.confirmCloseRunning,
                    onCheckedChange = perf.onSetConfirmCloseRunning,
                    modified = perf.confirmCloseRunning != SettingsDefaults.CONFIRM_CLOSE_RUNNING,
                    onReset = { perf.onSetConfirmCloseRunning(SettingsDefaults.CONFIRM_CLOSE_RUNNING) },
                )
                ToggleRow(
                    label = "划掉后完全关闭应用",
                    supporting = "当你将 JCode 从 Android 最近任务屏幕划掉时，停止 Linux 运行时" +
                        "(terminals, runs, VMs) and exit completely instead of leaving it running in the background.",
                    checked = perf.exitOnSwipeAway,
                    onCheckedChange = perf.onSetExitOnSwipeAway,
                    modified = perf.exitOnSwipeAway != SettingsDefaults.EXIT_ON_SWIPE_AWAY,
                    onReset = { perf.onSetExitOnSwipeAway(SettingsDefaults.EXIT_ON_SWIPE_AWAY) },
                )
                ToggleRow(
                    label = "自动关闭空闲终端",
                    supporting = "自动关闭在提示符处空闲（无正在运行的程序）的终端，以" +
                        "释放它们的进程树和内存。正在运行命令的终端永远不会被自动关闭。",
                    checked = perf.autoCloseIdleTerminals,
                    onCheckedChange = perf.onSetAutoCloseIdleTerminals,
                    modified = perf.autoCloseIdleTerminals != SettingsDefaults.AUTO_CLOSE_IDLE_TERMINALS,
                    onReset = { perf.onSetAutoCloseIdleTerminals(SettingsDefaults.AUTO_CLOSE_IDLE_TERMINALS) },
                )
                if (perf.autoCloseIdleTerminals) {
                    StepperRow(
                        label = "空闲超时",
                        value = "${perf.idleTimeoutMinutes} min",
                        onDecrease = { perf.onSetIdleTimeoutMinutes(perf.idleTimeoutMinutes - 5) },
                        onIncrease = { perf.onSetIdleTimeoutMinutes(perf.idleTimeoutMinutes + 5) },
                        modified = perf.idleTimeoutMinutes != SettingsDefaults.IDLE_TIMEOUT_MINUTES,
                        onReset = { perf.onSetIdleTimeoutMinutes(SettingsDefaults.IDLE_TIMEOUT_MINUTES) },
                    )
                }
                StepperRow(
                    label = "终端实例数上限",
                    value = "${perf.maxTerminalSessions}",
                    onDecrease = { perf.onSetMaxTerminalSessions((perf.maxTerminalSessions - 1).coerceAtLeast(1)) },
                    onIncrease = { perf.onSetMaxTerminalSessions((perf.maxTerminalSessions + 1).coerceAtMost(24)) },
                    modified = perf.maxTerminalSessions != SettingsDefaults.MAX_TERMINAL_SESSIONS,
                    onReset = { perf.onSetMaxTerminalSessions(SettingsDefaults.MAX_TERMINAL_SESSIONS) },
                )
                StepperRow(
                    label = "工具链安装超时",
                    supporting = "工具链安装（SDK、语言服务器、调试器）可运行的最长" +
                        "时长，超时后将被取消。在连接速度较慢时安装 Android SDK 等大型 SDK，可调大此值。",
                    value = "${perf.installTimeoutMinutes} min",
                    onDecrease = { perf.onSetInstallTimeoutMinutes((perf.installTimeoutMinutes - 5).coerceAtLeast(5)) },
                    onIncrease = { perf.onSetInstallTimeoutMinutes((perf.installTimeoutMinutes + 5).coerceAtMost(180)) },
                    modified = perf.installTimeoutMinutes != SettingsDefaults.INSTALL_TIMEOUT_MINUTES,
                    onReset = { perf.onSetInstallTimeoutMinutes(SettingsDefaults.INSTALL_TIMEOUT_MINUTES) },
                )
                ToggleRow(
                    label = "子 shell 在独立选项卡中打开",
                    supporting = "在终端内启动交互式 shell（bash、zsh 等）时，在" +
                        "临时选项卡中打开，子 shell 退出时该选项卡关闭并返回父级——就像新的" +
                        "控制台窗口一样。脚本和管道 shell 保留在当前选项卡中。",
                    checked = perf.nestedShellTabs,
                    onCheckedChange = perf.onSetNestedShellTabs,
                    modified = perf.nestedShellTabs != SettingsDefaults.NESTED_SHELL_TABS,
                    onReset = { perf.onSetNestedShellTabs(SettingsDefaults.NESTED_SHELL_TABS) },
                )
            }
            } // end Performance

            } // end Global-only cards; the Web preview card below renders on every scope tab.

            // "Web 预览打开位置" edits the app-wide default on the GLOBAL tab and a per-project
            // override on the PROJECT tab (INHERIT = fall back to that default). It renders on every
            // tab; the raw selected tab (not [selectedScope], which coalesces GLOBAL into Project when
            // a project is open) decides which it edits, so the GLOBAL tab always edits the default.
            // Web preview renders on every scope tab, but not on the ENV VAR content tab.
            if (!isEnvVarTab) {
            val projectBrowserScope =
                tabScopes.getOrNull(safeTab) == ConfigScope.Project && webPreview.currentProjectKey.isNotBlank()
            SettingsGroup("网页预览") {
            SettingsCard(
                title = "Web 预览打开位置",
                description = if (projectBrowserScope) {
                    "打开运行中的开发服务器（构建并运行）或 " +
                        "点按终端中的网址时此项目使用的浏览器。「使用全局默认」将沿用应用级设置。"
                } else {
                    "打开运行中的开发服务器（构建与运行）或点按终端中的网址时使用的浏览器" +
                        "。本地项目可在其「项目」设置选项卡中覆盖此设置。"
                },
                keywords = "浏览器 网页预览 默认 内置 browser web preview open url chrome firefox default run dev server " +
                    "系统 始终 询问 选择器 内置 内置 继承 全局 项目 覆盖 " +
                    webPreview.available.joinToString(" ") { it.label },
            ) {
                val options = buildList {
                    if (projectBrowserScope) add(WebPreviewBrowsers.INHERIT)
                    add(WebPreviewBrowsers.SYSTEM)
                    add(WebPreviewBrowsers.ASK)
                    add(WebPreviewBrowsers.BUILTIN)
                    webPreview.available.forEach { add(it.packageName) }
                }
                val selectedChoice = if (projectBrowserScope) {
                    webPreview.projectChoice(webPreview.currentProjectKey)
                } else {
                    webPreview.globalChoice
                }
                options.forEach { choice ->
                    BundleRow(
                        name = webPreview.label(choice),
                        description = when (choice) {
                            WebPreviewBrowsers.INHERIT -> "回退到应用级默认设置"
                            WebPreviewBrowsers.SYSTEM -> "设备的默认浏览器应用"
                            WebPreviewBrowsers.ASK -> "每次显示 Android 应用选择器"
                            WebPreviewBrowsers.BUILTIN -> "JCode 内置的编辑器内浏览器，带开发者工具"
                            else -> choice
                        },
                        selected = selectedChoice == choice,
                        swatch = emptyList(),
                        onClick = {
                            if (projectBrowserScope) {
                                webPreview.onSetProject(webPreview.currentProjectKey, choice)
                            } else {
                                webPreview.onSetGlobal(choice)
                            }
                        },
                    )
                }
            }

            SettingsCard(
                title = "网页引擎",
                description = "JCode 内置浏览器和网页预览背后的 Chromium 引擎。 " +
                    "它是设备的 WebView 提供方——一个 JCode 可读取但无法选择 " +
                    "的系统组件；当它无法更新时，JCode 会回退到 ROM 自带的引擎。",
                keywords = "网页引擎 webview chromium 版本 提供方 更新 渲染 开发者选项 web engine webview chromium version provider outdated update play store " +
                    "浏览器 渲染 空白 dvh 现代 开发者选项 实现",
            ) {
                val ctx = LocalContext.current
                // Re-read on each composition of the card: the user may return from Play or the
                // provider picker with the engine changed, and a stale number here would claim the
                // trip changed nothing.
                val enginePackage = remember { runCatching { WebView.getCurrentWebViewPackage() }.getOrNull() }
                val engineVersion = enginePackage?.versionName ?: "unknown"
                val engineMajor = engineVersion.substringBefore('.').toIntOrNull() ?: 0
                // Chromium 108 shipped dynamic viewport units (dvh) — the line below which modern
                // sites visibly break. A margin above it counts as "current enough".
                val outdated = engineMajor in 1 until WEBVIEW_MODERN_MAJOR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Chromium $engineVersion",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (outdated) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = "已过期",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = Space.ms, vertical = Space.xs),
                            )
                        }
                    } else if (engineMajor > 0) {
                        Text(
                            text = "当前",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                enginePackage?.packageName?.let { SummaryRow(label = "提供方", value = it) }
                if (outdated) {
                    Text(
                        text = "现代网站在此引擎上可能渲染为空白或错乱。请安装 " +
                            "最新版 Android System WebView，然后在「开发者选项」→ 下选择它。 " +
                            "WebView 实现。某些设备会锁定提供方；此时 JCode 将继续" +
                            "使用 ROM 自带的引擎。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                CompactFilledButton(
                    text = "获取最新 WebView",
                    onClick = {
                        val play = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$GOOGLE_WEBVIEW_PACKAGE"),
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        runCatching { ctx.startActivity(play) }.onFailure {
                            runCatching {
                                ctx.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$GOOGLE_WEBVIEW_PACKAGE"),
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                )
                            }
                        }
                    },
                )
                CompactOutlinedButton(
                    text = "选择提供方…",
                    onClick = {
                        runCatching {
                            ctx.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                            )
                        }
                    },
                )
                }
            }
            } // end Web preview

            } // end web-preview (hidden on the ENV VAR tab)

            if (showGlobalTab) {
            SettingsGroup("环境") {
            SettingsCard(
                title = "环境",
                description = "环境设置：proot、发行版引导，以及最后的冒烟测试。 " +
                    "从设置页面安装、切换或移除环境。",
                keywords = "环境 proot 发行版 工具链 冒烟测试 绑定 运行时 设置 管理 刷新 安装 " +
                    "就绪 通过 失败 未安装 未运行 未知 更新 升级 软件包 apt 系统 " +
                    environmentState.runtime.selectedDistro.label,
            ) {
                SummaryRow(
                    label = "proot",
                    value = if (environmentState.prootInstalled) "就绪" else "未安装",
                )
                SummaryRow(
                    label = "发行版",
                    value = when (environmentState.distroInstalled) {
                        true -> environmentState.runtime.selectedDistro.label
                        false -> "未安装"
                        null -> "未知"
                    },
                )
                SummaryRow(
                    label = "工具链",
                    value = when (environmentState.toolchainReady) {
                        true -> "就绪"
                        false -> "未就绪"
                        null -> "未知"
                    },
                )
                SummaryRow(
                    label = "冒烟测试",
                    value = when (environmentState.smokeTestPassed) {
                        true -> "通过"
                        false -> "失败"
                        null -> "未运行"
                    },
                )
                SummaryRow(
                    "主绑定",
                    environmentState.runtime.binds.firstOrNull()?.target ?: "/workspace",
                )
                environmentState.runningStep?.let { runningStep ->
                    Text(
                        text = "正在运行：${runningStep.key}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                environmentState.activityLog.takeLast(3).forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                    CompactFilledButton(text = "管理环境", onClick = onOpenEnvironmentWizard)
                    CompactOutlinedButton(text = "刷新检查", onClick = onRefreshEnvironment)
                }
                LocalEnvironmentBackup.current.migrationSummary?.let { summary ->
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    CompactFilledButton(
                        text = "从旧版本安装导入",
                        onClick = LocalEnvironmentBackup.current.onImportMigration,
                    )
                }
                if (environmentState.distroInstalled == true) {
                    val envBackup = LocalEnvironmentBackup.current
                    Text(
                        text = "将整个 Linux 环境（约 2.5 GB）备份为 .tar.gz 文件，以便 " +
                            "在此处或其他设备上恢复。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                        CompactFilledButton(text = "备份（.tar.gz）", onClick = envBackup.onBackup)
                        CompactOutlinedButton(text = "恢复…", onClick = envBackup.onRestore)
                    }
                    // Moving to an install with a different package name. Android gives that install
                    // its own data directory and no way to read this one's, so everything has to go
                    // out through shared storage first — see MigrationBundle.
                    Text(
                        text = "要迁移到名称不同的构建版本？先将环境、项目、 " +
                            "扩展和设置写入共享的 JCode 文件夹，然后" +
                            "从新安装的版本中将其导入。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    CompactOutlinedButton(
                        text = "导出以供迁移",
                        onClick = envBackup.onExportMigration,
                    )
                    Text(
                        text = "刷新软件包列表并升级已安装的软件包 " +
                            "（apt-get update && upgrade）。在「设置」终端中运行——可能较慢且会消耗流量。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    CompactOutlinedButton(
                        text = if (envBackup.updatingPackages) "正在更新软件包…" else "更新系统软件包",
                        onClick = envBackup.onUpdatePackages,
                        enabled = !envBackup.updatingPackages,
                    )
                }
            }

            SettingsCard(
                title = "后台进程数上限",
                description = "Android 会限制应用可 fork 的进程数量，并终止其余进程—— " +
                    "这会导致整个 Linux 环境在命令执行中途崩溃。提高上限需要 " +
                    "一条 adb 命令；JCode 无法自行设置。",
                keywords = "幽灵进程 崩溃 终端关闭 后台 phantom process limit killed died crashed dies terminal closes distro proot stopped " +
                    "background max_phantom_processes device_config adb activity manager trimming long session " +
                    "claude agent build gradle npm disappears exits by itself",
            ) {
                val clipboard = LocalClipboardManager.current
                var processCount by remember { mutableStateOf<Int?>(null) }
                // Only polls while this card is actually on screen (the group is collapsed by default).
                LaunchedEffect(Unit) {
                    while (true) {
                        processCount = withContext(Dispatchers.IO) { AppProcesses.count() }
                        delay(3_000L)
                    }
                }
                SummaryRow(
                    label = "Linux 进程",
                    value = processCount?.let { "$it / ${AppProcesses.DEFAULT_PHANTOM_LIMIT}（默认上限）" }
                        ?: "未知",
                )
                Text(
                    text = "Android 12+ 会在应用 fork 的进程超过上限（默认为 32 个）时终止它们—— " +
                        "proot、shell 及其下所有进程都计入，因此长时间的构建或 " +
                        "coding-agent 会话超出上限后，终端会终止而 JCode 继续" +
                        "运行。请通过已连接此设备的电脑运行以下命令（或通过本 " +
                        "设备自带的 adb）来提高上限；该设置在重启后保留，但" +
                        "恢复出厂设置后需要重新执行。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = AppProcesses.RAISE_LIMIT_COMMANDS,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                CompactOutlinedButton(
                    text = "复制命令",
                    onClick = { clipboard.setText(AnnotatedString(AppProcesses.RAISE_LIMIT_COMMANDS)) },
                )
            }

            SettingsCard(
                title = "Android 设备",
                description = "将 JCode 与本机的 adb 配对，以便构建产物能安装并启动到本机。",
                keywords = "android 设备 adb 桥接 无线调试 配对 配对码 中继 序列号 apk " +
                    "安装 启动 logcat gradle installdebug flutter 运行",
            ) {
                val androidDevice = LocalAndroidDevice.current
                SummaryRow(label = "ADB 桥接", value = androidDevice.status)
                androidDevice.serial?.let { SummaryRow(label = "序列号", value = it) }
                CompactFilledButton(
                    text = if (androidDevice.ready) "管理设备" else "设置 ADB",
                    onClick = androidDevice.onOpenPage,
                )
            }


            } // end Environment

            SettingsGroup("关于") {
            SettingsCard(
                title = "JCode",
                description = "应用版本，以及来自 GitHub releases 的更新。",
                keywords = "关于 版本 更新 检查 发布 github 更新日志 构建 应用",
            ) {
                val appUpdate = LocalAppUpdate.current
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "版本 ${appUpdate.currentVersion}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (appUpdate.updateAvailable) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = "更新：v${appUpdate.latestVersion}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = Space.ms, vertical = Space.xs),
                            )
                        }
                    } else if (appUpdate.latestVersion != null) {
                        Text(
                            text = "已是最新版本",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // A single button: "安装更新" when a newer release is available (its label shows
                // download/install progress while running), otherwise "检查更新".
                if (appUpdate.updateAvailable) {
                    CompactFilledButton(
                        text = when {
                            !appUpdate.installing -> "安装更新"
                            appUpdate.installProgress in 1..99 -> "下载中…${appUpdate.installProgress}%"
                            else -> "Installing…"
                        },
                        onClick = appUpdate.onInstallUpdate,
                        enabled = !appUpdate.installing,
                    )
                } else {
                    CompactOutlinedButton(
                        text = if (appUpdate.checking) "正在检查…" else "检查更新",
                        onClick = appUpdate.onCheck,
                        enabled = !appUpdate.checking,
                    )
                }
            }

            SettingsCard(
                title = "备份与恢复",
                description = "将应用偏好设置保存到文件，然后在此处或 " +
                    "其他设备上恢复。（主题和编辑器设置保存在工作区配置中。）",
                keywords = "备份 恢复 导出 导入 设置 偏好设置 文件 保存 加载 传输 迁移 json 设备",
            ) {
                val backup = LocalSettingsBackup.current
                Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                    CompactFilledButton(text = "导出设置…", onClick = backup.onExport)
                    CompactOutlinedButton(text = "导入设置…", onClick = backup.onImport)
                }
            }

            } // end About

            SettingsGroup("Diagnostics") {
            SettingsCard(
                title = "诊断日志记录",
                description = "默认关闭，需手动开启。当应用行为异常时，将其 " +
                    "操作记录到文件，可附在问题报告中，然后再将其关闭。",
                keywords = "诊断 诊断信息 日志 日志记录 logcat 调试 跟踪 记录 捕获 崩溃 报告 " +
                    "错误 问题 排查 导出 分享 详细 文件",
            ) {
                val diagnostics = LocalDiagnosticsSetting.current
                var showLog by remember { mutableStateOf(false) }
                // Size/location only move while recording, and only matter while this card is open.
                LaunchedEffect(diagnostics.enabled) {
                    while (diagnostics.enabled) {
                        diagnostics.onRefresh()
                        delay(2_000L)
                    }
                }
                ToggleRow(
                    label = "记录诊断信息",
                    supporting = "将应用事件写入本设备上的日志文件。不会向任何地方发送" +
                        "— you choose when to export it. File paths are replaced with placeholders so the " +
                        "log is safe to share.",
                    checked = diagnostics.enabled,
                    onCheckedChange = diagnostics.onSetEnabled,
                    modified = diagnostics.enabled != SettingsDefaults.DIAGNOSTIC_LOGGING,
                    onReset = { diagnostics.onSetEnabled(SettingsDefaults.DIAGNOSTIC_LOGGING) },
                )
                if (diagnostics.enabled) {
                    SettingsDropdownRow(
                        label = "详情",
                        options = DiagLevel.entries.map { it.name },
                        selected = diagnostics.level.name,
                        onSelect = { diagnostics.onSetLevel(DiagLevel.valueOf(it)) },
                        optionLabel = { DiagLevel.valueOf(it).label },
                        modified = diagnostics.level != SettingsDefaults.DIAGNOSTIC_LEVEL,
                        onReset = { diagnostics.onSetLevel(SettingsDefaults.DIAGNOSTIC_LEVEL) },
                    )
                    ToggleRow(
                        label = "包含系统日志",
                        supporting = "附加 JCode 的 logcat 输出 —— 包括 proot 和 Linux" +
                            "环境，工具链、扩展和语言服务器的大部分详细信息都记录在其中；" +
                            "只有 JCode 自身" +
                            "的条目可读；其他应用的条目一律不可读。",
                        checked = diagnostics.captureSystemLog,
                        onCheckedChange = diagnostics.onSetCaptureSystemLog,
                        modified = diagnostics.captureSystemLog != SettingsDefaults.DIAGNOSTIC_SYSTEM_LOG,
                        onReset = { diagnostics.onSetCaptureSystemLog(SettingsDefaults.DIAGNOSTIC_SYSTEM_LOG) },
                    )
                    ToggleRow(
                        label = "记录崩溃",
                        supporting = "在应用崩溃时附加堆栈跟踪，以便日志覆盖" +
                            "故障本身，而不仅仅是导致故障的过程。",
                        checked = diagnostics.captureCrashes,
                        onCheckedChange = diagnostics.onSetCaptureCrashes,
                        modified = diagnostics.captureCrashes != SettingsDefaults.DIAGNOSTIC_CRASHES,
                        onReset = { diagnostics.onSetCaptureCrashes(SettingsDefaults.DIAGNOSTIC_CRASHES) },
                    )
                    SummaryRow(label = "已记录", value = formatLogSize(diagnostics.sizeBytes))
                    SummaryRow(label = "位置", value = diagnostics.location.ifBlank { "正在启动…" })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                    CompactFilledButton(
                        text = "查看",
                        onClick = { showLog = true },
                        enabled = diagnostics.sizeBytes > 0L,
                    )
                    CompactOutlinedButton(
                        text = "导出…",
                        onClick = diagnostics.onExport,
                        enabled = diagnostics.sizeBytes > 0L,
                    )
                    CompactOutlinedButton(
                        text = "清除",
                        onClick = diagnostics.onClear,
                        enabled = diagnostics.sizeBytes > 0L,
                    )
                }
                if (showLog) {
                    DiagnosticLogDialog(lines = diagnostics.recentLines(), onDismiss = { showLog = false })
                }
            }

            } // end Diagnostics

            SettingsGroup("编辑器") {
            SettingsCard(
                title = "编辑器默认值",
                description = "代码编辑器的默认字号和自动换行。工作区或 " +
                    "项目可在其自身的设置选项卡中覆盖字号。",
                keywords = "编辑器 字号 文本 缩放 sp 自动换行 软换行 行 长行 默认 全局",
            ) {
                val editorFontSizeSetting = LocalEditorFontSizeSetting.current
                val editorWordWrapSetting = LocalEditorWordWrapSetting.current
                StepperRow(
                    label = "字号",
                    value = "${editorFontSizeSetting.value.toInt()} sp",
                    onDecrease = { editorFontSizeSetting.onChange((editorFontSizeSetting.value - 1f).coerceAtLeast(8f)) },
                    onIncrease = { editorFontSizeSetting.onChange((editorFontSizeSetting.value + 1f).coerceAtMost(72f)) },
                    modified = editorFontSizeSetting.value != SettingsDefaults.EDITOR_FONT_SIZE,
                    onReset = { editorFontSizeSetting.onChange(SettingsDefaults.EDITOR_FONT_SIZE) },
                )
                ToggleRow(
                    label = "自动换行",
                    supporting = "将长行按编辑器宽度换行，而不是水平滚动。",
                    checked = editorWordWrapSetting.enabled,
                    onCheckedChange = { editorWordWrapSetting.onChange(it) },
                    modified = editorWordWrapSetting.enabled != SettingsDefaults.EDITOR_WORD_WRAP,
                    onReset = { editorWordWrapSetting.onChange(SettingsDefaults.EDITOR_WORD_WRAP) },
                )
            }
            SettingsCard(
                title = "编辑器手势",
                description = "触摸输入在编辑器中的行为方式。适用于整个应用。",
                keywords = "编辑器 手势 拖动 移动光标 速度 垂直 水平 触摸 滚动",
            ) {
                ToggleRow(
                    label = "拖动以移动光标",
                    supporting = "在编辑器上拖动手指以移动文本光标（视图随之滚动）而不是滚动。长按仍可选择文本。全局生效。",
                    checked = editorDragSetting.enabled,
                    onCheckedChange = editorDragSetting.onChange,
                    modified = editorDragSetting.enabled != SettingsDefaults.EDITOR_DRAG_MOVES_CURSOR,
                    onReset = { editorDragSetting.onChange(SettingsDefaults.EDITOR_DRAG_MOVES_CURSOR) },
                )
                if (editorDragSetting.enabled) {
                    StepperRow(
                        label = "光标拖动速度 —— 垂直",
                        value = "${editorDragSetting.verticalLevel} / 5",
                        onDecrease = { editorDragSetting.onVerticalLevelChange((editorDragSetting.verticalLevel - 1).coerceAtLeast(1)) },
                        onIncrease = { editorDragSetting.onVerticalLevelChange((editorDragSetting.verticalLevel + 1).coerceAtMost(5)) },
                        modified = editorDragSetting.verticalLevel != SettingsDefaults.CURSOR_DRAG_LEVEL,
                        onReset = { editorDragSetting.onVerticalLevelChange(SettingsDefaults.CURSOR_DRAG_LEVEL) },
                    )
                    StepperRow(
                        label = "光标拖动速度 —— 水平",
                        value = "${editorDragSetting.horizontalLevel} / 5",
                        onDecrease = { editorDragSetting.onHorizontalLevelChange((editorDragSetting.horizontalLevel - 1).coerceAtLeast(1)) },
                        onIncrease = { editorDragSetting.onHorizontalLevelChange((editorDragSetting.horizontalLevel + 1).coerceAtMost(5)) },
                        modified = editorDragSetting.horizontalLevel != SettingsDefaults.CURSOR_DRAG_LEVEL,
                        onReset = { editorDragSetting.onHorizontalLevelChange(SettingsDefaults.CURSOR_DRAG_LEVEL) },
                    )
                }
            }

            SettingsCard(
                title = "选项卡",
                description = "编辑器和终端选项卡的行为方式。适用于整个应用。",
                keywords = "选项卡 关闭按钮 隐藏 编辑器 终端 误触 着色 颜色 强调色 随机 目录 宽度 尺寸 小 中 大 缩短 省略号 截断",
            ) {
                ToggleRow(
                    label = "隐藏选项卡关闭按钮",
                    supporting = "移除编辑器和终端选项卡上的 ×，以免误关。请从选项卡的长按菜单关闭选项卡。",
                    checked = tabCloseSetting.hidden,
                    onCheckedChange = tabCloseSetting.onChange,
                    modified = tabCloseSetting.hidden != SettingsDefaults.HIDE_TAB_CLOSE_BUTTON,
                    onReset = { tabCloseSetting.onChange(SettingsDefaults.HIDE_TAB_CLOSE_BUTTON) },
                )
                SettingsDropdownRow(
                    label = "选项卡宽度",
                    supporting = "编辑器或终端选项卡在名称被截断前能达到的最大" +
                        "宽度，名称过长时从中间截断（例如“build.gradle.kts”→“build.g…kts”）。",
                    options = TabMaxSize.entries.map { it.name },
                    selected = tabMaxSizeSetting.size.name,
                    onSelect = { tabMaxSizeSetting.onChange(TabMaxSize.valueOf(it)) },
                    modified = tabMaxSizeSetting.size != SettingsDefaults.TAB_MAX_SIZE,
                    onReset = { tabMaxSizeSetting.onChange(SettingsDefaults.TAB_MAX_SIZE) },
                )
                SettingsDropdownRow(
                    label = "选项卡着色",
                    supporting = "为编辑器文件选项卡按颜色编码。长按文件选项卡可手动设置颜色；" +
                        "颜色会记录在项目的 .jcode 中。项目可覆盖此默认值。",
                    options = TabColoring.entries.map { it.name },
                    selected = tabColoringSetting.mode.name,
                    onSelect = { tabColoringSetting.onChange(TabColoring.valueOf(it)) },
                    optionLabel = { tabColoringLabel(TabColoring.valueOf(it)) },
                    modified = tabColoringSetting.mode != SettingsDefaults.TAB_COLORING,
                    onReset = { tabColoringSetting.onChange(SettingsDefaults.TAB_COLORING) },
                )
            }

            SettingsCard(
                title = "格式化工具",
                description = "编辑器使用的格式化工具。内置格式化工具基于规则；安装格式化扩展后会显示在这里。",
                keywords = "格式化工具 格式化 prettier 缩进 保存时格式化 空白 内置 " +
                    formatterOptions.joinToString(" ") { it.second },
            ) {
                formatterOptions.forEach { (id, label) ->
                    BundleRow(
                        name = label,
                        description = if (id == "builtin") "内置的基于规则的格式化工具" else "格式化扩展",
                        selected = formatterId == id,
                        swatch = emptyList(),
                        onClick = { onSelectFormatter(id) },
                    )
                }
            }

            SettingsCard(
                title = "Markdown 预览",
                description = "渲染后的 Markdown 预览的布局方式。",
                keywords = "markdown 预览 自动换行 竖屏 横屏 宽度 水平滚动 平移 宽表格 代码",
            ) {
                val markdownPreviewSetting = LocalMarkdownPreviewSetting.current
                ToggleRow(
                    label = "竖屏时自动换行",
                    supporting = "关闭：竖屏预览按横屏宽度（即屏幕高度，" +
                        "遵循刘海屏设置）并可横向平移——宽表格和代码保持不换行。",
                    checked = markdownPreviewSetting.wrapInPortrait,
                    onCheckedChange = { markdownPreviewSetting.onSetWrapInPortrait(it) },
                    modified = markdownPreviewSetting.wrapInPortrait != SettingsDefaults.MARKDOWN_WRAP_PORTRAIT,
                    onReset = { markdownPreviewSetting.onSetWrapInPortrait(SettingsDefaults.MARKDOWN_WRAP_PORTRAIT) },
                )
            }

            } // end Editor

            SettingsGroup("资源管理器") {
            SettingsCard(
                title = "排除文件/文件夹",
                description = "在资源管理器中排除项目根目录下的文件和文件夹。“注入模式” " +
                    "来自各项目的 .gitignore，由「源代码管理」扩展保持同步。 " +
                    "被排除的条目默认显示为灰色，或从树中完全隐藏。",
                keywords = "资源管理器 文件 文件夹 排除 隐藏 变灰 gitignore explorer files folder exclude hide hidden grey greyed grey-out dim de-emphasize project root gitignore jcode ignore injected specified show reveal by-line effect",
            ) {
                SettingsDropdownRow(
                    label = "模式",
                    options = ExplorerHiddenMode.entries.map { it.name },
                    selected = explorerHiddenSetting.mode.name,
                    onSelect = { explorerHiddenSetting.onSetMode(ExplorerHiddenMode.valueOf(it)) },
                    optionLabel = { explorerHiddenModeLabel(ExplorerHiddenMode.valueOf(it)) },
                    modified = explorerHiddenSetting.mode != SettingsDefaults.HIDDEN_ROOT_MODE,
                    onReset = { explorerHiddenSetting.onSetMode(SettingsDefaults.HIDDEN_ROOT_MODE) },
                )
                SettingsDropdownRow(
                    label = "排除时",
                    options = ExplorerExcludeEffect.entries.map { it.name },
                    selected = explorerHiddenSetting.effect.name,
                    onSelect = { explorerHiddenSetting.onSetEffect(ExplorerExcludeEffect.valueOf(it)) },
                    optionLabel = { explorerExcludeEffectLabel(ExplorerExcludeEffect.valueOf(it)) },
                    modified = explorerHiddenSetting.effect != SettingsDefaults.EXCLUDE_EFFECT,
                    onReset = { explorerHiddenSetting.onSetEffect(SettingsDefaults.EXCLUDE_EFFECT) },
                )
                var hidePatterns by remember(explorerHiddenSetting.specifiedRaw) {
                    mutableStateOf(explorerHiddenSetting.specifiedRaw)
                }
                SettingsTextFieldRow(
                    label = "指定 —— 每行一个模式",
                    value = hidePatterns,
                    onValueChange = { hidePatterns = it },
                    onCommit = { explorerHiddenSetting.onSetSpecifiedRaw(hidePatterns) },
                    placeholder = ".jcode",
                    singleLine = false,
                    minLines = 3,
                )
            }
            SettingsCard(
                title = "回收站",
                description = "文件在彻底删除前存放的位置。涵盖资源管理器中的「删除」 " +
                    "和「源代码管理」中的「放弃更改」；回收站本身可从资源管理器工具栏打开。",
                keywords = "回收站 回收站 循环 删除 已删除 移除 恢复 恢复 取消删除 放弃更改 scm 源代码管理 保留 保留 天数 清空 永久",
            ) {
                ToggleRow(
                    label = "将删除的文件移至回收站",
                    supporting = "删除文件或文件夹，或在源代码管理中放弃更改时，会保留" +
                        "可恢复的副本。关闭此选项可立即永久删除。",
                    checked = trashSettings.enabled,
                    onCheckedChange = trashSettings.onSetEnabled,
                    modified = trashSettings.enabled != SettingsDefaults.TRASH_ENABLED,
                    onReset = { trashSettings.onSetEnabled(SettingsDefaults.TRASH_ENABLED) },
                )
                if (trashSettings.enabled) {
                    SettingsDropdownRow(
                        label = "保留已删除文件",
                        supporting = "较早的项目会在 JCode 启动时和打开回收站时被移除。" +
                            "回收站是应用私有存储空间，因此其中的内容会计入应用体积。",
                        options = TRASH_RETENTION_CHOICES.map { it.toString() },
                        selected = trashSettings.retentionDays.toString(),
                        onSelect = { trashSettings.onSetRetentionDays(it.toInt()) },
                        optionLabel = { trashRetentionLabel(it.toInt()) },
                        modified = trashSettings.retentionDays != SettingsDefaults.TRASH_RETENTION_DAYS,
                        onReset = { trashSettings.onSetRetentionDays(SettingsDefaults.TRASH_RETENTION_DAYS) },
                    )
                }
            }

            } // end Explorer

            SettingsGroup("Developer") {
            SettingsCard(
                title = "开发者选项",
                description = "用于构建和测试 JCode 扩展的工具。",
                keywords = "开发者选项 扩展 侧载 未签名 jext 调试 dev 工具 检查器 验证器 日志 控制台 重新加载 make 工具 第三方",
            ) {
                val developerSetting = LocalDeveloperSetting.current
                ToggleRow(
                    label = "启用开发者选项",
                    supporting = "在右侧面板添加“扩展开发”选项卡（检查器、清单验证器、" +
                        "实时日志），用于调试未签名的 .jext 或 .vsix。导入扩展不需要此选项； " +
                        "已签名的扩展市场扩展不受影响。",
                    checked = developerSetting.enabled,
                    onCheckedChange = { developerSetting.onSetEnabled(it) },
                    modified = developerSetting.enabled != SettingsDefaults.DEVELOPER_OPTIONS,
                    onReset = { developerSetting.onSetEnabled(SettingsDefaults.DEVELOPER_OPTIONS) },
                )
                if (developerSetting.enabled) {
                    Text(
                        "使用 JCode 扩展制作工具编译并打包扩展，然后导入" +
                            "未签名的 .jext（从「扩展」面板导入）——「扩展开发」选项卡会在每次 " +
                            "重新构建。只有经过签名（由 JCode 维护者私下签名）的软件包才能进入" +
                            "扩展市场。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            } // end Developer

            } // end Global tab

            if (showScopedTab) {
            // The active tab already names the scope; this caption just states its reach.
            if (query.isBlank()) {
                Text(
                    text = when (selectedScope) {
                        ConfigScope.Workspace -> "这些设置保存到工作区的 .jcode 目录，并应用于其下所有项目，除非项目存在覆盖设置。"
                        ConfigScope.Project -> "这些设置保存到项目的 .jcode 中，仅影响所选的本地项目。"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = Space.xxs),
                )
            }

            workspaceError?.let { message ->
                WarningCard(title = "工作区 YAML 警告", message = message)
            }

            if (projectOverridesAvailable) {
                projectError?.let { message ->
                    WarningCard(title = "项目 YAML 警告", message = message)
                }
            }

            environmentState.errorMessage?.let { message ->
                WarningCard(title = "环境警告", message = message)
            }

            SettingsGroup("编辑器", stateKey = "scoped.Editor") {
            SettingsCard(
                title = "编辑器行为",
                description = "这些控件会回写到 YAML，并立即更新已打开的编辑器。",
                keywords = "编辑器 行为 字号 制表符宽度 连字 缩进 选项卡 着色 颜色 强调色",
            ) {
                StepperRow(
                    label = "字号",
                    value = "${fontSize.toInt()} sp",
                    onDecrease = { onUpdateFontSize(selectedScope, (fontSize - 1f).coerceAtLeast(8f)) },
                    onIncrease = { onUpdateFontSize(selectedScope, (fontSize + 1f).coerceAtMost(72f)) },
                    modified = scopedEditor?.fontSize != null,
                    onReset = { onUpdateFontSize(selectedScope, null) },
                )
                SettingsDropdownRow(
                    label = "制表符宽度",
                    supporting = "根据项目不同，2、4 或 8 个空格都是不错的默认值。",
                    options = listOf("2", "4", "8"),
                    selected = tabSize.toString(),
                    onSelect = { onUpdateTabSize(selectedScope, it.toInt()) },
                    optionLabel = { "$it spaces" },
                    modified = scopedEditor?.tabSize != null,
                    onReset = { onUpdateTabSize(selectedScope, null) },
                )
                ToggleRow(
                    label = "连字",
                    supporting = "建议为编辑器界面保持启用，但允许用户在长时间编码时禁用。",
                    checked = ligatures,
                    onCheckedChange = { onUpdateLigatures(selectedScope, it) },
                    modified = scopedEditor?.ligatures != null,
                    onReset = { onUpdateLigatures(selectedScope, null) },
                )
                // Sanitize: a hand-edited .jcode may hold an unknown enum name; fall back to the
                // app default rather than crashing composition on TabColoring.valueOf.
                val tabColoring = (scopedEditor?.tabColoring ?: effectiveConfig.editor.tabColoring)
                    ?.let { runCatching { TabColoring.valueOf(it) }.getOrNull() }
                    ?.name
                    ?: tabColoringSetting.mode.name
                SettingsDropdownRow(
                    label = "选项卡着色",
                    supporting = "覆盖此范围的应用级默认值。",
                    options = TabColoring.entries.map { it.name },
                    selected = tabColoring,
                    onSelect = { onUpdateTabColoring(selectedScope, it) },
                    optionLabel = { runCatching { tabColoringLabel(TabColoring.valueOf(it)) }.getOrDefault(it) },
                    modified = scopedEditor?.tabColoring != null,
                    onReset = { onUpdateTabColoring(selectedScope, null) },
                )
            }

            SettingsCard(
                title = "资源管理器",
                description = "选择文件资源管理器的布局方式。适用于当前编辑范围。",
                keywords = "资源管理器 视图模式 树形 列表 文件管理器 布局 面包屑导航",
            ) {
                OptionRow(
                    label = "视图模式",
                    supporting = "树形显示整个项目层级；列表是带面包屑导航的单文件夹文件管理器。",
                    modified = scopedExplorer?.viewMode != null,
                    onReset = { onUpdateExplorerViewMode(selectedScope, null) },
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                        listOf("Tree", "List").forEach { option ->
                            val selected = explorerViewMode == option
                            if (selected) {
                                CompactFilledButton(
                                    text = option,
                                    onClick = { onUpdateExplorerViewMode(selectedScope, option) },
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                CompactOutlinedButton(
                                    text = option,
                                    onClick = { onUpdateExplorerViewMode(selectedScope, option) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            } // end Editor (scoped)

            SettingsGroup("Files") {
            SettingsCard(
                title = "YAML 文件",
                description = "如需完全掌控，可直接打开底层的配置文件。",
                keywords = "yaml 文件 配置 工作区 项目 打开 底层 编辑",
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
                    CompactFilledButton(text = "打开工作区 YAML", onClick = onOpenWorkspaceConfig)
                    CompactOutlinedButton(
                        text = "打开项目 YAML",
                        onClick = onOpenProjectConfig,
                        enabled = projectOverridesAvailable,
                    )
                }
            }
            } // end Files

            } // end Project/Workspace tab

            // Composed after every card, so matchSink.count reflects the whole page.
            if (!isEnvVarTab && query.isNotBlank() && matchSink.count == 0) {
                SettingsNoResults(query)
            }
        }
        }
    }
}

/** Human-readable labels for the exclude "Mode" dropdown — WHICH entries are excluded. */
/**
 * The floor below which the device's Chromium counts as outdated in the Web engine card.
 *
 * Chromium 108 shipped dynamic viewport units (`dvh`), the first modern-CSS line whose absence
 * makes whole sites render blank rather than merely imperfect; a small margin above it counts as
 * current enough. Deliberately far below the actual current release: the card exists to flag
 * engines that *break* pages, not to nag every device that trails by a few versions.
 */
private const val WEBVIEW_MODERN_MAJOR = 110

/** Google's updatable WebView provider on Play — the install target for outdated engines. */
private const val GOOGLE_WEBVIEW_PACKAGE = "com.google.android.webview"

private fun explorerHiddenModeLabel(mode: ExplorerHiddenMode): String = when (mode) {
    ExplorerHiddenMode.HideSpecifiedAndInjected -> "指定项 + 注入项"
    ExplorerHiddenMode.HideInjected -> "仅注入项"
    ExplorerHiddenMode.None -> "关"
}

/** Human-readable labels for the "排除时" dropdown — HOW excluded entries appear. */
private fun explorerExcludeEffectLabel(effect: ExplorerExcludeEffect): String = when (effect) {
    ExplorerExcludeEffect.GreyOut -> "置灰"
    ExplorerExcludeEffect.Hide -> "隐藏"
}

/** Human-readable label for an [ExtraKeysVisibility] dropdown option. */
private fun extraKeysVisibilityLabel(mode: ExtraKeysVisibility): String = when (mode) {
    ExtraKeysVisibility.Hidden -> "Hidden"
    ExtraKeysVisibility.WithKeyboard -> "随键盘显示"
    ExtraKeysVisibility.Always -> "Always"
}

/** Human-readable label for a [BottomBarVisibility] dropdown option. */
private fun bottomBarVisibilityLabel(mode: BottomBarVisibility): String = when (mode) {
    BottomBarVisibility.Hidden -> "Hidden"
    BottomBarVisibility.HideOnKeyboard -> "软键盘弹出时隐藏"
    BottomBarVisibility.AlwaysShow -> "始终显示"
}

private fun headerActionButtonLabel(button: HeaderActionButton): String = when (button) {
    HeaderActionButton.Terminal -> "终端"
    HeaderActionButton.CommandPalette -> "命令面板"
    HeaderActionButton.Hidden -> "Hidden"
}

private fun tabColoringLabel(mode: TabColoring): String = when (mode) {
    TabColoring.RandomRemember -> "随机（不存在时则记住）"
    TabColoring.Random -> "随机"
    TabColoring.DirectoryBased -> "按目录（随后记住）"
    TabColoring.Disabled -> "Disabled"
}

/** [defaultSuffix] disambiguates the per-button System Default label, e.g. "System Default (Vol Up)". */
private fun volumeKeyActionLabel(action: VolumeKeyAction, defaultSuffix: String): String = when (action) {
    VolumeKeyAction.SystemDefault -> "系统默认（$defaultSuffix）"
    VolumeKeyAction.Undo -> "Undo"
    VolumeKeyAction.Redo -> "Redo"
    VolumeKeyAction.KeyLeft -> "左方向键"
    VolumeKeyAction.KeyRight -> "右方向键"
    VolumeKeyAction.KeyUp -> "上方向键"
    VolumeKeyAction.KeyDown -> "下方向键"
    VolumeKeyAction.ScrollUp -> "向上滚动"
    VolumeKeyAction.ScrollDown -> "向下滚动"
    VolumeKeyAction.CommandPalette -> "命令面板"
}

/** Current Settings search query; cards/headers self-filter on it. */
val LocalSettingsQuery = compositionLocalOf { "" }

/** Counts how many cards passed the search filter this composition, so a no-match query can show an
 *  empty state. A fresh instance is provided each composition (see Content), so every card recomposes
 *  and re-counts on any page change — the count read by the trailing empty-state is always accurate. */
private class SettingsMatchSink { var count = 0 }
private val LocalSettingsMatchSink = compositionLocalOf { SettingsMatchSink() }

/** True when EVERY whitespace-separated term in [query] appears (case-insensitive) somewhere in the
 *  card's searchable text ([haystacks] = title + description + keywords). Term-wise AND matching lets
 *  "tab close" find the Tabs card, where a single-substring match would not. */
private fun matchesSettingsQuery(query: String, vararg haystacks: String): Boolean {
    val terms = query.split(' ', '\t', '\n', '-').filter { it.isNotBlank() }
    if (terms.isEmpty()) return true
    val hay = haystacks.joinToString(" ").lowercase()
    return terms.all { hay.contains(it.lowercase()) }
}

/** Compact, single-line search field (smaller than a default OutlinedTextField). */
@Composable
private fun SettingsSearchField(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(horizontal = Space.ms),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.sm),
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(IconSize.md),
            )
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "搜索设置",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "清除搜索",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onQueryChange("") },
                )
            }
        }
    }
}

/** Shown when a search query matches no card, so an empty page reads as "no results" not "broken". */
@Composable
private fun SettingsNoResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Space.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.xs),
    ) {
        Text(
            text = "没有与“$query”匹配的设置",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "试试更短或不同的关键词，例如“font”或“theme”。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Which settings groups are open, held outside the composition.
 *
 * `rememberSaveable` is not enough here: rotating the device makes the workbench swap between its
 * modal and docked layouts, which disposes this whole subtree along with its saveable registry, so
 * every group would snap shut on rotation. Session-scoped by design — a fresh launch starts
 * collapsed. Each group owns its own [MutableState] so toggling one doesn't invalidate the rest.
 */
private val settingsGroupExpanded = mutableMapOf<String, MutableState<Boolean>>()

/** Each group's y offset inside the scrolling column, published as it is laid out, so
 *  [SettingsFeature.revealGroup] can scroll to one. */
private val settingsGroupOffsets = mutableMapOf<String, Float>()

/**
 * A run of [SettingsCard]s under one heading, collapsed by default so the page opens as a short list
 * of headings instead of one long scroll.
 *
 * While a search is running the heading and the collapse are bypassed entirely and [content] is
 * emitted straight into the caller's Column — not merely un-collapsed. Cards filter themselves and
 * count themselves into [LocalSettingsMatchSink], so wrapping them at all would both hide matches
 * inside collapsed groups and, for a group whose cards all filtered out, leave an empty child behind
 * that the parent's `spacedBy` would still pad around.
 *
 * [stateKey] separates groups that share a title — "编辑器" is a heading on both the global and the
 * scoped tab.
 */
@Composable
private fun ColumnScope.SettingsGroup(
    title: String,
    stateKey: String = title,
    content: @Composable () -> Unit,
) {
    if (LocalSettingsQuery.current.isNotBlank()) {
        content()
        return
    }
    var expanded by remember(stateKey) { settingsGroupExpanded.getOrPut(stateKey) { mutableStateOf(false) } }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { settingsGroupOffsets[stateKey] = it.positionInParent().y }
            .clickable { expanded = !expanded }
            .padding(top = Space.s, start = Space.xxs, end = Space.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = jcIcon(if (expanded) JCodeIcon.ChevronUp else JCodeIcon.ChevronDown),
            contentDescription = if (expanded) "折叠 $title" else "展开 $title",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.md),
        )
    }
    // AnimatedVisibility stacks its children like a Box, so the cards need their own Column to keep
    // the page's 10dp rhythm instead of drawing on top of each other.
    AnimatedVisibility(visible = expanded) {
        Column(verticalArrangement = Arrangement.spacedBy(Space.ms)) { content() }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    description: String,
    keywords: String = "",
    content: @Composable () -> Unit,
) {
    val query = LocalSettingsQuery.current.trim()
    if (query.isNotEmpty() && !matchesSettingsQuery(query, title, description, keywords)) return
    LocalSettingsMatchSink.current.count++
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.16f),
        shape = RoundedCornerShape(Radius.xxl),
        border = BorderStroke(StrokeWidth.hairline, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier.padding(Space.md),
            verticalArrangement = Arrangement.spacedBy(Space.ms),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            content()
        }
    }
}

@Composable
private fun WarningCard(
    title: String,
    message: String,
) {
    // Participate in the search filter/count like SettingsCard, so a warning neither leaks into
    // unrelated results nor sits above a "无结果" empty state.
    val query = LocalSettingsQuery.current.trim()
    if (query.isNotEmpty() && !matchesSettingsQuery(query, title, message, "warning error yaml")) return
    LocalSettingsMatchSink.current.count++
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
        shape = RoundedCornerShape(Radius.xxl),
        border = BorderStroke(StrokeWidth.hairline, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier.padding(Space.md),
            verticalArrangement = Arrangement.spacedBy(Space.s),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@Composable
private fun BundleRow(
    name: String,
    description: String,
    selected: Boolean,
    swatch: List<Color>,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(vertical = Space.s, horizontal = Space.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.ms),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Space.xxs)) {
            swatch.take(4).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(color),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "已选中",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSize.md),
            )
        }
    }
}

@Composable
private fun UiIconSetRow(
    set: UiIconSet,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // Five slots a set is most likely to have restyled, so two packs are told apart at a glance
    // rather than by their names.
    val sample = listOf(JCodeIcon.Files, JCodeIcon.Run, JCodeIcon.Terminal, JCodeIcon.Search, JCodeIcon.Settings)
    IconSetRow(
        name = set.name,
        description = set.description.ifBlank { "${set.filledSlots} icons" },
        selected = selected,
        onClick = onClick,
        preview = {
            sample.forEach { slot ->
                Icon(
                    painter = set.art(slot).painter(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(IconSize.sm),
                )
            }
        },
    )
}

@Composable
private fun FileIconSetRow(
    name: String,
    description: String,
    detail: FileIconSet?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // Previewed through the same resolver the Explorer uses, on names a pack of any language is
    // likely to answer — so the row shows what the set will actually draw, not a curated sample.
    val sample = listOf("src" to true, "index.ts" to false, "app.py" to false, "README.md" to false)
    IconSetRow(
        name = name,
        description = description.ifBlank { detail?.let { "${it.iconCount} icons" } ?: "" },
        selected = selected,
        onClick = onClick,
        preview = {
            CompositionLocalProvider(LocalFileIconSet provides detail) {
                sample.forEach { (fileName, isDirectory) ->
                    FileTypeIcon(
                        name = fileName,
                        isDirectory = isDirectory,
                        size = IconSize.sm,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}

@Composable
private fun IconSetRow(
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    preview: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(vertical = Space.s, horizontal = Space.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.ms),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Space.xs),
            verticalAlignment = Alignment.CenterVertically,
            content = { preview() },
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "已选中",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSize.md),
            )
        }
    }
}

/** Human-readable size for the Diagnostics card's "Recorded" row. */
private fun formatLogSize(bytes: Long): String = when {
    bytes <= 0L -> "暂无"
    bytes < 1024L -> "$bytes B"
    bytes < 1024L * 1024L -> "${bytes / 1024L} KB"
    else -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
}

/**
 * The tail of the current diagnostic session. Shown so a user can see exactly what is being recorded
 * before deciding to share it — opting in should not mean opting in blind.
 */
@Composable
private fun DiagnosticLogDialog(lines: List<String>, onDismiss: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("最近的诊断信息") },
        text = {
            if (lines.isEmpty()) {
                Text("尚未记录任何内容。", style = MaterialTheme.typography.bodySmall)
            } else {
                // Newest last, scrolled to the bottom: the end of the log is what a report is about.
                val scroll = rememberScrollState()
                LaunchedEffect(lines.size) { scroll.scrollTo(scroll.maxValue) }
                Column(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .verticalScroll(scroll)
                        .horizontalScroll(rememberScrollState()),
                ) {
                    lines.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                        )
                    }
                }
            }
        },
        confirmButton = { CompactFilledButton(text = "关闭", onClick = onDismiss) },
        dismissButton = {
            CompactOutlinedButton(
                text = "复制",
                onClick = {
                    clipboard.setText(AnnotatedString(buildString { lines.forEach { appendLine(it) } }))
                },
                enabled = lines.isNotEmpty(),
            )
        },
    )
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EnvVarEditor(settings: EnvVarSettings) {
    // Dialog state: null = closed; [adding] distinguishes a brand-new variable from editing [editTarget].
    var editTarget by remember { mutableStateOf<String?>(null) }
    var adding by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(Space.ms)) {
        // A plain heading, not a SettingsGroup: this tab is one section and has no search field.
        Text(
            text = "环境变量",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = Space.s, start = Space.xxs),
        )
        Text(
            text = "导出到每个终端和「构建并运行」会话中（例如 API 密钥、GOPRIVATE、 " +
                "JAVA_OPTS）。应用于新打开的终端。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val entries = settings.vars.entries.sortedBy { it.key.lowercase() }
        if (entries.isEmpty()) {
            Text(
                text = "还没有变量。点按“添加变量”创建一个。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = Space.sm),
            )
        } else {
            entries.forEach { (name, value) ->
                SettingsResettableRow(modified = false, onReset = null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Space.sm),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = value.ifEmpty { "（空）" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        CompactOutlinedButton(text = "编辑", onClick = { editTarget = name; adding = false })
                        CompactOutlinedButton(text = "删除", onClick = { settings.onRemove(name) })
                    }
                }
            }
        }
        CompactFilledButton(text = "添加变量", onClick = { editTarget = ""; adding = true })
    }

    val target = editTarget
    if (target != null) {
        EnvVarDialog(
            initialName = if (adding) "" else target,
            initialValue = if (adding) "" else (settings.vars[target] ?: ""),
            existingNames = settings.vars.keys,
            editingName = if (adding) null else target,
            onDismiss = { editTarget = null },
            onSave = { name, value ->
                settings.onSet(name, value, if (adding) null else target)
                editTarget = null
            },
        )
    }
}

@Composable
private fun EnvVarDialog(
    initialName: String,
    initialValue: String,
    existingNames: Set<String>,
    editingName: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, value: String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var value by remember { mutableStateOf(initialValue) }
    val nameValid = name.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))
    val duplicate = name != editingName && name in existingNames
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingName == null) "添加变量" else "编辑变量") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.trim() },
                    label = { Text("名称") },
                    singleLine = true,
                    isError = name.isNotEmpty() && (!nameValid || duplicate),
                    supportingText = {
                        if (duplicate) {
                            Text("已存在名为“$name”的变量")
                        } else if (name.isNotEmpty() && !nameValid) {
                            Text("仅允许字母、数字和下划线；不能以数字开头")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("值") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            CompactFilledButton(
                text = "保存",
                onClick = { onSave(name, value) },
                enabled = nameValid && !duplicate,
            )
        },
        dismissButton = { CompactOutlinedButton(text = "取消", onClick = onDismiss) },
    )
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modified: Boolean = false,
    onReset: (() -> Unit)? = null,
    supporting: String? = null,
) {
    SettingsResettableRow(modified = modified, onReset = onReset) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.sm),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                if (supporting != null) {
                    Text(
                        text = supporting,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Space.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepperButton(JCodeIcon.Minus, "减小 $label", filled = false, onClick = onDecrease)
                StepperButton(JCodeIcon.Add, "增大 $label", filled = true, onClick = onIncrease)
            }
        }
    }
}

/**
 * One half of a stepper.
 *
 * An icon rather than a typed "-" and "+", which are a hyphen and a plus sign set at text size:
 * different weights, different widths, and sitting on a text baseline inside a button that holds no
 * text. Two glyphs meant to be a matched pair looked like neither.
 *
 * Round, because that is the shape of a button holding one glyph and nothing else: a pill is a shape
 * that expects a word in it, and reads as a button whose label failed to load. The emphasis pairing
 * stays — outlined to step down, tonal to step up, as elsewhere on the page.
 *
 * They are also the only controls on this page a screen reader could not name: "-" reads as a
 * hyphen and says nothing about what it steps. Each now says which setting it moves.
 */
@Composable
private fun StepperButton(
    icon: JCodeIcon,
    contentDescription: String,
    filled: Boolean,
    onClick: () -> Unit,
) {
    // Sized down from the 40dp default, and the interactive minimum relaxed with it. That minimum
    // is there for good reason and is not worth keeping here: it pads each button out to 48dp of
    // layout, which is most of the gap between the two, and a settings row is not a place anyone
    // taps in a hurry.
    val sizing = Modifier.size(34.dp)
    val glyph: @Composable () -> Unit = {
        Icon(jcIcon(icon), contentDescription, modifier = Modifier.size(IconSize.md))
    }
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        if (filled) {
            FilledTonalIconButton(onClick = onClick, modifier = sizing) { glyph() }
        } else {
            OutlinedIconButton(
                onClick = onClick,
                modifier = sizing,
                // Stated rather than defaulted. An outlined icon button draws its border from the
                // content colour, which in a dark theme is near-white and shouts across a page of
                // quiet rows. `outline` is what every switch on this page already draws its own
                // border with, and these sit in the same column as those switches.
                border = BorderStroke(StrokeWidth.thin, MaterialTheme.colorScheme.outline),
            ) { glyph() }
        }
    }
}

@Composable
private fun OptionRow(
    label: String,
    supporting: String,
    modified: Boolean = false,
    onReset: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    SettingsResettableRow(modified = modified, onReset = onReset) {
        Column(verticalArrangement = Arrangement.spacedBy(Space.sm)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    supporting: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modified: Boolean = false,
    onReset: (() -> Unit)? = null,
) {
    SettingsResettableRow(modified = modified, onReset = onReset) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.md),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
