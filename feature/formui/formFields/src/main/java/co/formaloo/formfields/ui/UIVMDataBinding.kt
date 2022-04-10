package co.formaloo.formfields.ui

import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields


fun checkFieldIsRequired(field:Fields,viewmodel: UIViewModel){
    if (field.required == true) {
        viewmodel.reuiredField(field)
    }
}
