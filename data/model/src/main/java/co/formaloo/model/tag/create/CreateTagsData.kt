package co.formaloo.model.tag.create

import co.formaloo.model.form.tags.Tag
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateTagsData(
    @SerializedName("object")
    var tag: Tag? = null
): Serializable
