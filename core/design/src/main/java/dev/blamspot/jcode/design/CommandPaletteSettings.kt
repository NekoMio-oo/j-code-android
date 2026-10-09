package dev.blamspot.jcode.design

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf

/**
 * A configurable built-in Command Palette command: [id] is the stable registry id, [label]/
 * [description] feed the Settings toggle row. The palette hides commands whose id the user disabled;
 * context predicates (active view, focused screen) further gate visibility at registration.
 */
@Immutable
data class PaletteCommandInfo(
    val id: String,
    val label: String,
    val description: String,
)

/** The user-toggleable built-in palette commands (context-dependent ones note their surface). */
val PaletteCommandCatalog: List<PaletteCommandInfo> = listOf(
    PaletteCommandInfo("view.orientationLock", "锁定/解锁屏幕方向", "将屏幕固定为当前方向。"),
    PaletteCommandInfo("view.hideChrome", "隐藏标题栏和选项卡", "无干扰的编辑/预览；悬浮胶囊可恢复界面。"),
    PaletteCommandInfo("view.fullscreen", "Fullscreen", "隐藏系统状态栏和导航栏；从屏幕边缘滑动可临时查看。"),
    PaletteCommandInfo("view.keepAwake", "保持唤醒", "在应用打开时防止屏幕休眠。"),
    PaletteCommandInfo("editor.goToLine", "转到行", "将活动编辑器跳转到指定行（或行:列）。"),
    PaletteCommandInfo("tools.colorSearch", "颜色搜索", "点击屏幕任意位置，取样像素并复制为 HEX/RGB(A)。"),
    PaletteCommandInfo("tools.virtualDevice", "打开虚拟设备", "在选项卡中打开虚拟设备的屏幕及其启动器。"),
    PaletteCommandInfo("browser.open", "打开浏览器", "打开内置浏览器，保持当前页面不变。"),
    PaletteCommandInfo("browser.back", "浏览器：后退", "返回上一页。仅在浏览器选项卡已打开且有可返回的页面时可用。"),
    PaletteCommandInfo("browser.forward", "浏览器：前进", "前往下一页。仅在浏览器选项卡已打开且有可前往的页面时可用。"),
    PaletteCommandInfo("browser.reload", "浏览器：重新加载/停止", "重新加载页面，或在页面仍在加载时停止加载。"),
    PaletteCommandInfo("editor.formatDocument", "格式化文档", "在识别出活动文件的语言时对其进行格式化。"),
    PaletteCommandInfo("editor.fontSizeIncrease", "增大编辑器字号", "将编辑器字号增大 1 磅（8–72）。"),
    PaletteCommandInfo("editor.fontSizeDecrease", "减小编辑器字号", "将编辑器字号减小 1 磅（8–72）。"),
)

/** Which built-in palette commands the user disabled, plus the Settings toggle writer. */
@Immutable
class CommandPaletteSetting(
    val disabledIds: Set<String> = emptySet(),
    val onSetEnabled: (String, Boolean) -> Unit = { _, _ -> },
)

val LocalCommandPaletteSetting = compositionLocalOf { CommandPaletteSetting() }

/**
 * Opens the Command Palette from chrome composed outside the shell composable that owns its
 * visibility — the header's action button, when Settings points it there.
 */
@Immutable
class CommandPaletteLauncher(val onOpen: () -> Unit = {})

val LocalCommandPaletteLauncher = compositionLocalOf { CommandPaletteLauncher() }

/**
 * Workbench chrome state for the palette's "隐藏标题栏和选项卡" mode. A CompositionLocal (not
 * params) because the shell composables sit at the ART verifier register limit.
 */
@Immutable
class ChromeControls(
    val chromeHidden: Boolean = false,
    val onSetChromeHidden: (Boolean) -> Unit = {},
)

val LocalChromeControls = compositionLocalOf { ChromeControls() }
