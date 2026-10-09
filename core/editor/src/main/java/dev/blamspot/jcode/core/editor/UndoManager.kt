package dev.blamspot.jcode.core.editor

import dev.blamspot.jcode.core.buffer.EditTx
import dev.blamspot.jcode.core.buffer.Snapshot
import kotlinx.coroutines.runBlocking

/**
 * Linear-history undo manager. Groups adjacent edits within a single IME
 * composing session, a 500 ms typing burst, or until selection moves.
 */
class UndoManager(
    private val state: EditorState,
    private val maxGroups: Int = 500,
    private val maxInvertedBytes: Int = 50 * 1024 * 1024,
) {
    private data class UndoEntry(
        /** The edit the user made — applied verbatim by redo. */
        val tx: EditTx,
        /** The inverse of [tx], built against the pre-edit snapshot — applied by undo. */
        val invertedTx: EditTx,
        val selectionBefore: List<Caret>,
        val selectionAfter: List<Caret>,
        val timestamp: Long,
    )

    private val history = ArrayDeque<UndoEntry>()
    private val redoStack = ArrayDeque<UndoEntry>()
    private var totalInvertedBytes = 0L

    private var currentGroupStart: Long = 0L
    private var currentGroupSelection: List<Caret>? = null
    private var isComposing = false

    /** Record an edit for potential undo. */
    fun recordEdit(tx: EditTx, selectionBefore: List<Caret>, oldSnapshot: Snapshot) {
        val inverted = invertEdit(tx, oldSnapshot)
        val now = System.currentTimeMillis()

        // Decide whether to start a new group
        val shouldNewGroup = when {
            isComposing -> false // composing stays in same group
            history.isEmpty() -> true
            now - currentGroupStart > 500 -> true
            currentGroupSelection != null && selectionMoved(currentGroupSelection!!, selectionBefore) -> true
            else -> false
        }

        if (shouldNewGroup) {
            flushGroup()
            currentGroupStart = now
        }
        currentGroupSelection = selectionBefore

        val entry = UndoEntry(tx, inverted, selectionBefore, selectionBefore, now)
        history.addLast(entry)
        totalInvertedBytes += estimateByteSize(inverted)
        redoStack.clear()

        evictOldestIfNeeded()
    }

    /** Mark the start of an IME composing session. */
    fun beginComposing() {
        isComposing = true
        flushGroup()
        currentGroupStart = System.currentTimeMillis()
    }

    /** Mark the end of an IME composing session. */
    fun endComposing() {
        isComposing = false
        flushGroup()
    }

    /** Flush the current group boundary. */
    fun flushGroup() {
        currentGroupStart = 0L
        currentGroupSelection = null
    }

    /** Perform undo. Returns true if an undo was applied. */
    fun undo(): Boolean {
        if (history.isEmpty()) return false
        flushGroup()

        val entry = history.removeLast()
        totalInvertedBytes -= estimateByteSize(entry.invertedTx)

        runBlocking {
            // recordInUndo = false: an undo is a history traversal, not a new edit — recording the
            // inverse would append it to the history and clear the redo stack underneath us.
            state.applyEdit(entry.invertedTx, recordInUndo = false)
            state.setSelection(entry.selectionBefore)
        }

        redoStack.addLast(entry)
        return true
    }

    /** Perform redo. Returns true if a redo was applied. */
    fun redo(): Boolean {
        if (redoStack.isEmpty()) return false

        val entry = redoStack.removeLast()
        runBlocking {
            // Reapply the original edit verbatim. Inverting the inverse instead would need the
            // pre-undo snapshot — the post-undo one is all that exists here, and reading the
            // deleted text out of it restores the wrong bytes.
            state.applyEdit(entry.tx, recordInUndo = false)
            state.setSelection(entry.selectionAfter)
        }

        history.addLast(entry)
        totalInvertedBytes += estimateByteSize(entry.invertedTx)
        return true
    }

    /** Clear all undo/redo history. */
    fun clear() {
        history.clear()
        redoStack.clear()
        totalInvertedBytes = 0L
        flushGroup()
    }

    private fun invertEdit(tx: EditTx, snapshot: Snapshot): EditTx {
        val builder = EditTx.builder()
        // Invert ops in reverse order
        for (op in tx.ops.asReversed()) {
            when (op) {
                is dev.blamspot.jcode.core.buffer.EditOp.Insert -> {
                    val end = op.offset + op.text.toByteArray(Charsets.UTF_8).size
                    builder.delete(op.offset, end)
                }
                is dev.blamspot.jcode.core.buffer.EditOp.Delete -> {
                    val deletedBytes = snapshot.readRange(op.start, op.end)
                    val deletedText = String(deletedBytes, Charsets.UTF_8)
                    builder.insert(op.start, deletedText)
                }
            }
        }
        return builder.build()
    }

    private fun selectionMoved(before: List<Caret>, after: List<Caret>): Boolean {
        if (before.size != after.size) return true
        return before.zip(after).any { (a, b) -> a.head != b.head }
    }

    private fun estimateByteSize(tx: EditTx): Int {
        return tx.ops.sumOf { op ->
            when (op) {
                is dev.blamspot.jcode.core.buffer.EditOp.Insert -> op.text.toByteArray(Charsets.UTF_8).size
                is dev.blamspot.jcode.core.buffer.EditOp.Delete -> op.end - op.start
            }
        }
    }

    private fun evictOldestIfNeeded() {
        while (history.size > maxGroups || totalInvertedBytes > maxInvertedBytes) {
            val removed = history.removeFirst()
            totalInvertedBytes -= estimateByteSize(removed.invertedTx)
        }
    }
}
