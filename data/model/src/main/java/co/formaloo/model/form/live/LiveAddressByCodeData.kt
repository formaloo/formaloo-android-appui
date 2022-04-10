package co.formaloo.model.form.live

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LiveAddressByCodeData(
    @SerializedName("live-dashboard-code")
    var live_dashboard_code: LiveDashboardCode? = null
) : Serializable
