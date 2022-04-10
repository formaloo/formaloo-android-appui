package co.formaloo.model.submit.row

import java.io.Serializable

data class CalculationScore(
    var grade: String? = null,
    var currency: String? = null
) : Serializable
