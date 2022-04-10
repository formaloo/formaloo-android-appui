package co.formaloo.model.form.access.list

import java.io.Serializable

data class FormAccessListData(
    var shared_profiles: ArrayList<SharedProfiles>? = null
): Serializable
