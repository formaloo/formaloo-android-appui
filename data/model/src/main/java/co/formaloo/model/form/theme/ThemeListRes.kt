package co.formaloo.model.form.theme

import java.io.Serializable

data class ThemeListRes(
    var status: Int? = null,
    var data: ThemeListData? = null
) : Serializable {
    companion object {
        fun empty() = ThemeListRes(0, null)

    }

    fun toThemeListRes() = ThemeListRes(status, data)
}
