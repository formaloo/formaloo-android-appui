package co.formaloo.model.cityChoices

import java.io.Serializable

data class CityChoicesRes(
    var status: Int? = null,
    var data: CityChoicesData? = null
) : Serializable {
    companion object {
        fun empty() = CityChoicesRes(0, null)

    }

    fun toCityChoicesRes() = CityChoicesRes(status, data)
}
