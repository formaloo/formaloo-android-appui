package co.formaloo.model.submitList

import co.formaloo.model.row.RenderedData
import co.formaloo.model.row.Tag
import java.io.Serializable

data class Row(
    var slug: String? = null,
    var form: Any? = null,
    var created_at: String? = null,
    var previous_row: String? = null,
    var next_row: String? = null,
    var submit_code: String? = null,
    var submitter_referer_address: String? = null,
    var row_tags: List<Tag>? = null,
    var id: Int? = null,
    var data: Map<String, Any>? = null,
    var rendered_data: Map<String, RenderedData>? = null,
    var searchable_data: Map<String, Any>? = null,
    var readable_data: Map<String, Any>? = null,
) : Serializable
