package co.formaloo.model.cityChoices

import java.io.Serializable

data class CityChoicesData(
    var objects: List<CityChoicesObject>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var current_page: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
) : Serializable
