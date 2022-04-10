package co.formaloo.model.account.profile

import java.io.Serializable

data class ProfileRes(
        var status: Int? = null,
        var data: FormProfileData? = null
): Serializable {
    companion object {
        fun empty() = ProfileRes(0, null)

    }

    fun toProfileRes() = ProfileRes(status, data)
}
