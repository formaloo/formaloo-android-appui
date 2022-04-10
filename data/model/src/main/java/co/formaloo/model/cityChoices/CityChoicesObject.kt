package co.formaloo.model.cityChoices

import java.io.Serializable

data class CityChoicesObject(
    var title: String? = null,
    var slug: String? = null
) : Serializable
