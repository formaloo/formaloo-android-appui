package co.formaloo.model.submit

import java.io.Serializable

data class TopFields(
    var title: String? = null,
    var slug: String? = null
): Serializable
