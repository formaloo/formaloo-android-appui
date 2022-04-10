package co.formaloo.model.form.theme

import java.io.Serializable

data class ThemeListData(
    var themes: ArrayList<Theme>? = null
): Serializable
