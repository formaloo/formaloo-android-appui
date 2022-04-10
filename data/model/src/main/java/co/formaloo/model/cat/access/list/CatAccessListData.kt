package co.formaloo.model.cat.access.list

import co.formaloo.model.cat.access.CatAccessProfile
import java.io.Serializable

data class CatAccessListData(
    var profiles: ArrayList<CatAccessProfile>? = null
): Serializable
