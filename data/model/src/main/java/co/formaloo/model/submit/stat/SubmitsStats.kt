package co.formaloo.model.submit.stat

import java.io.Serializable

data class SubmitsStats(
    var date: String? = null,
    var count: Int =0
): Serializable
