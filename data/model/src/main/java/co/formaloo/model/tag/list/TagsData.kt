package co.formaloo.model.tag.list

import co.formaloo.model.form.tags.Tag
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TagsData(
    @SerializedName("objects")
    var tags: ArrayList<Tag>? = null,
    var count:Int? = null,
    var page_size:Int? = null,
    var page_count:Int? = null,
    var current_page:Int? = null,
    var next:String? = null,
    var previous:String? = null

): Serializable
