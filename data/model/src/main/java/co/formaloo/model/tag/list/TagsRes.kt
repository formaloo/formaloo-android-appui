package co.formaloo.model.tag.list

import java.io.Serializable

data class TagsRes(
    var status: Int? = null,
    var data: TagsData? = null
):Serializable{
    companion object{
        fun empty()= TagsRes(0, null)
    }

    fun toTagsRes()= TagsRes(status, data)
}
