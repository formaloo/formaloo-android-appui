package co.formaloo.model.reportmaker.tableChart

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableChartDatum(
    @SerializedName("field_slug")
    @Expose
     val fieldSlug: String? = null,

    @SerializedName("field_title")
    @Expose
     val fieldTitle: String? = null,

    @SerializedName("field_description")
    @Expose
     val fieldDescription: String? = null,

    @SerializedName("field_type")
    @Expose
     val fieldType: String? = null,

    @SerializedName("type")
    @Expose
     val type: String? = null,

    @SerializedName("value")
    @Expose
     val value: Any? = null,
) : Serializable
