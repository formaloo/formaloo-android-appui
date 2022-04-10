package co.formaloo.model.submit.stat

import java.io.Serializable

data class Form(
    var stats: Stats? = null,
    var slug: String? = null
): Serializable
