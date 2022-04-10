package co.formaloo.model.form.live

import java.io.Serializable

data class LiveDashboardForm(
    var slug: String? = null,
    var live_dashboard_address: String? = null,
    var address: String? = null,
    var title: String? = null
): Serializable
