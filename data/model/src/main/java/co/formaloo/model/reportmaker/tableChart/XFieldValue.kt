package co.formaloo.model.reportmaker.tableChart

import java.io.Serializable

data class XFieldValue(
    var slug: String? = null,
    var title: String? = null,
    var description: String? = null
) : Serializable
