package co.formaloo.model.account.apiKey

import java.io.Serializable

data class ApiKeyRes(
        var status: Int? = null,
        var data: ApiKeyData? = null
): Serializable {
    companion object {
        fun empty() = ApiKeyRes(0, null)

    }

    fun toApiKeyRes() = ApiKeyRes(status, data)
}
