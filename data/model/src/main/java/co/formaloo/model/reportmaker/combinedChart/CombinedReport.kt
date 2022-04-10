package co.formaloo.model.reportmaker.combinedChart

import co.formaloo.model.form.Fields
import co.formaloo.model.form.Form
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CombinedReport (
    @SerializedName("type")
    @Expose
     val type: String? = null,

    @SerializedName("primary_form")
    @Expose
     val primaryForm: Form? = null,

    @SerializedName("x_field")
    @Expose
     val xField: Fields? = null,

    @SerializedName("y_fields")
    @Expose
     val yFields: List<Fields>? = null,

    @SerializedName("chart_data")
    @Expose
     val chartData: List<CombinedChartDatum>? = null,

    @SerializedName("created_at")
    @Expose
     val createdAt: String? = null,

    @SerializedName("updated_at")
    @Expose
     val updatedAt: String? = null,

    @SerializedName("slug")
    @Expose
     val slug: String? = null,

    @SerializedName("title")
    @Expose
     val title: String? = null,

    @SerializedName("description")
    @Expose
     val description: String? = null,

    @SerializedName("chart_type")
    @Expose
     val chartType: String? = null,

    @SerializedName("rows_y_fields")
    @Expose
     val rowsYFields: List<String>? = null,

    ): Serializable
