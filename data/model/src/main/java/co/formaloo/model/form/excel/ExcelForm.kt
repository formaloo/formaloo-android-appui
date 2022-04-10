package co.formaloo.model.form.excel

import java.io.Serializable

data class ExcelForm(
    var async_export: Boolean? = null,
    var excel_file: String? = null
): Serializable
