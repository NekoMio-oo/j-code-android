package dev.blamspot.jcode.core.editor.completion

/**
 * Types of completion items.
 */
enum class CompletionItemKind(val displayName: String) {
    TEXT("文本"),
    METHOD("方法"),
    FUNCTION("函数"),
    CONSTRUCTOR("构造"),
    FIELD("字段"),
    VARIABLE("变量"),
    CLASS("类"),
    STRUCT("结构"),
    INTERFACE("接口"),
    MODULE("模块"),
    PROPERTY("属性"),
    EVENT("事件"),
    OPERATOR("运算"),
    UNIT("单元"),
    VALUE("值"),
    CONSTANT("常量"),
    ENUM("枚举"),
    ENUM_MEMBER("成员"),
    KEYWORD("关键字"),
    SNIPPET("片段"),
    COLOR("颜色"),
    FILE("文件"),
    REFERENCE("引用"),
    FOLDER("文件夹"),
    TYPE_PARAMETER("类型"),
}

/**
 * A single completion item.
 */
data class CompletionItem(
    /** Display label shown in the completion list */
    val label: String,
    /** Kind of completion item */
    val kind: CompletionItemKind = CompletionItemKind.TEXT,
    /** Additional detail text (e.g., type signature) */
    val detail: String? = null,
    /** Documentation string */
    val documentation: String? = null,
    /** Text to insert (defaults to label if null) */
    val insertText: String? = null,
    /** Snippet text (LSP snippet syntax, takes precedence over insertText) */
    val snippetText: String? = null,
    /** Whether this item is deprecated */
    val deprecated: Boolean = false,
    /** Sort text for ordering (defaults to label) */
    val sortText: String = label,
    /** Filter text for matching (defaults to label) */
    val filterText: String = label,
    /** Source provider identifier */
    val source: String? = null,
)

/**
 * Context for the current completion session.
 */
data class CompletionContext(
    val items: List<CompletionItem>,
    val triggerOffset: Int,
    val triggerChar: Char?,
)
