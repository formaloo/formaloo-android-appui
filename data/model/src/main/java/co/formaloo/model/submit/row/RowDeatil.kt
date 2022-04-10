package co.formaloo.model.submit.row

import co.formaloo.model.form.tags.Tag
import co.formaloo.model.submit.RenderedData
import java.io.Serializable

data class RowDeatil(
    var form: String? = null,
    var slug: String? = null,
    var created_at: String? = null,
    var row_tags: List<Tag>? = null,
    var user: Any? = null,
    var id: Int? = null,
    var previous_row: String? = null,
    var next_row: String? = null,
    var submit_code: String? = null,
    var submitter_referer_address: String? = null,
    var data: Map<String, Any>? = null,
    var readable_data: Map<String, Any>? = null,
    var rendered_data: List<RenderedData>? = null,
    var calculation_score: CalculationScore? = null
) : Serializable
