package co.formaloo.model.form.tags

import java.io.Serializable

data class FormTagsData(
    var form_tags: ArrayList<Tag>? = null
): Serializable
