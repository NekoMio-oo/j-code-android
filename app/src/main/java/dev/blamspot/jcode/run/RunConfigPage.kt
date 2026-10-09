package dev.blamspot.jcode.run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dev.blamspot.jcode.core.config.RunConfig
import dev.blamspot.jcode.core.config.RunConfigTerminal
import dev.blamspot.jcode.design.CompactFilledButton
import dev.blamspot.jcode.design.SettingsTextFieldRow
import dev.blamspot.jcode.design.Space

/**
 * Structured editor for a project's run configuration (`.jcode/run.yaml`), opened as an in-editor
 * page. Edits a [RunConfig]: a display name, the port to open in the browser, and a list of
 * terminals (label + bash command). [onSave] persists the form to `run.yaml`. Preset/trigger
 * selection happens up front via the "添加运行配置" dialog, not here. Fields use the app's compact
 * [SettingsTextFieldRow] so the page matches the rest of JCode.
 */
@Composable
fun RunConfigPage(
    initial: RunConfig,
    onSave: (RunConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf(initial.name) }
    var port by remember { mutableStateOf(initial.readyPort.takeIf { it > 0 }?.toString().orEmpty()) }
    // A run config is a single command. (Legacy multi-terminal configs seed from the first command;
    // the auto-detected full-stack recipes keep their side-by-side terminals — they aren't edited here.)
    var command by remember { mutableStateOf(initial.terminals.firstOrNull()?.command.orEmpty()) }
    var dirty by remember { mutableStateOf(false) }
    var savedOnce by remember { mutableStateOf(false) }

    fun buildConfig() = RunConfig(
        name = name.ifBlank { "运行" },
        readyPort = port.trim().toIntOrNull() ?: 0,
        debugEntry = initial.debugEntry,
        // One command → one guest process whose PID the run binds to for running/done/killed status.
        terminals = listOf(RunConfigTerminal(label = name.ifBlank { "运行" }, command = command.trim())),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Space.lg),
        verticalArrangement = Arrangement.spacedBy(Space.lg),
    ) {
        Text("构建与运行配置", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(
            "存储在此项目的 .jcode/run.yaml 中。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        SettingsTextFieldRow(
            label = "名称",
            value = name,
            onValueChange = { name = it; dirty = true },
        )
        SettingsTextFieldRow(
            label = "就绪端口",
            supporting = "运行就绪时在浏览器中打开——留空表示不打开。",
            value = port,
            onValueChange = { port = it.filter(Char::isDigit); dirty = true },
            placeholder = "e.g. 5173",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        SettingsTextFieldRow(
            label = "命令 (bash)",
            supporting = "此配置运行的唯一命令。它在终端中详细执行；运行会" +
                "跟踪其进程——运行直到其退出（完成）或被你停止（已终止）。",
            value = command,
            onValueChange = { command = it; dirty = true },
            placeholder = "e.g. dotnet run",
            singleLine = false,
            minLines = 4,
            monospace = true,
        )

        CompactFilledButton(
            text = if (savedOnce && !dirty) "已保存" else "保存",
            onClick = { onSave(buildConfig()); savedOnce = true; dirty = false },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
