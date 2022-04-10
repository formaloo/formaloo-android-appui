package co.formaloo.model.tag

import java.io.Serializable

data class TagReq(
    var title: String? = null,
    var color: String? = null,
) : Serializable
