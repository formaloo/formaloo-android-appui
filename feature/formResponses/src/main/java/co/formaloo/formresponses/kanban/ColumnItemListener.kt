package co.formaloo.formresponses.kanban

import co.formaloo.model.submit.Row

interface ColumnItemListener {
    fun openContent(row: Row)
}
