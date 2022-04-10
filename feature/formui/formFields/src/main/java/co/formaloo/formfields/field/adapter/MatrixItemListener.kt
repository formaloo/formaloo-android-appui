package co.formaloo.formfields.field.adapter

import co.formaloo.model.form.ChoiceItem

interface MatrixItemListener {
    fun removeItem(pos:Int,item:ChoiceItem)
    fun openEdit(pos:Int,item:ChoiceItem)
}
