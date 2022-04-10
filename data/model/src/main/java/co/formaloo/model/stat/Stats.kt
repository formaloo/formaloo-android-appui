package co.formaloo.model.stat

import java.io.Serializable

data class Stats(
    var total: Int? = null,
    var fields: Map<String, Any>? = null,
    var readable: Map<String, Any>? = null,
    var track_data: TrackData?  = null,
    var submits: List<SubmitsStats>?  = null
): Serializable
