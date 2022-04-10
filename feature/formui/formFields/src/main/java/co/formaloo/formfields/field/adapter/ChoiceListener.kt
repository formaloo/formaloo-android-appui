package co.formaloo.formfields.field.adapter

import co.formaloo.model.form.ChoiceItem

interface ChoiceListener {
    fun selectImage(holder: ChoicesAdapter.ViewHolder, adapter: ChoicesAdapter)
    fun itemsChanged(itemList: List<Pair<Long, ChoiceItem>>)
}
