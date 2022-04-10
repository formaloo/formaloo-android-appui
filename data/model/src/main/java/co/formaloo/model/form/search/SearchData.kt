package co.formaloo.model.form.search

import co.formaloo.model.form.Form
import co.formaloo.model.submit.Row
import java.io.Serializable

data class SearchData(
    var forms: List<Form>? = null,
    var rows: List<Row>? = null
): Serializable
