package co.formaloo.model.search

import java.io.Serializable

data class SearchData(
    var forms: List<co.formaloo.model.form.Form>? = null,
) : Serializable
