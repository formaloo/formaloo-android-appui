package co.formaloo.formresponses.kanban

import co.formaloo.model.form.tags.Tag


interface TagsListener {
    fun tagCLicked(it: Tag)
}
