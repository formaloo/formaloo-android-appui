package co.formaloo.model.submitList

import co.formaloo.model.row.TopFields
import java.io.Serializable

data class SubmitListData(
    var rows: List<Row>? = null,
    var top_fields: List<TopFields>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
): Serializable
