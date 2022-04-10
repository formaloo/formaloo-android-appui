package co.formaloo.model.stat

import java.io.Serializable

data class TrackData(
    var total: List<SubmitsStats>? = null
): Serializable
