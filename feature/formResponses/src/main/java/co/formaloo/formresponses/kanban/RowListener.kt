package co.formaloo.formresponses.kanban

interface RowListener {

    fun downloadFile(_renderedData: String)
    fun copySubmitToClipBoard(submit: String)
}
