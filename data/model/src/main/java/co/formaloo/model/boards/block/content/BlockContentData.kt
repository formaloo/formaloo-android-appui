package co.formaloo.model.boards.block.content

import co.formaloo.model.submit.TopFields
import co.formaloo.model.submit.Row
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BlockContentData(
    @SerializedName("objects")
    var rows: ArrayList<Row>? = null,
    var columns: List<TopFields>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var current_page: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
): Serializable
