package co.formaloo.model.form.excel

import java.io.Serializable

data class ExcelRes(
    var status: Int? = null,
    var data: ExcelData? = null
) : Serializable {
    companion object {
        fun empty() = ExcelRes(0, null)

    }

    fun toExcelRes() = ExcelRes(status, data)
}
