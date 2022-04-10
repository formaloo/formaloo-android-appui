package co.formaloo.model.reportmaker.combinedChart

import co.formaloo.model.form.Fields
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import co.formaloo.model.form.Form
import java.io.Serializable

data class CombinedFieldsChart(
    @SerializedName("type")
    @Expose
     var type: String? = null,

    @SerializedName("primary_form")
    @Expose
     var primaryForm: Form? = null,

    @SerializedName("x_field")
    @Expose
     var xField: Fields? = null,

    @SerializedName("y_fields")
    @Expose
     var yFields: List<Fields?>? = null,

    @SerializedName("chart_data")
    @Expose
     var chartData: List<CombinedChartDatum?>? = null,

    @SerializedName("created_at")
    @Expose
     var createdAt: String? = null,

    @SerializedName("updated_at")
    @Expose
     var updatedAt: String? = null,

    @SerializedName("slug")
    @Expose
     var slug: String? = null,

    @SerializedName("title")
    @Expose
     var title: String? = null,

    @SerializedName("description")
    @Expose
     var description: String? = null,

    @SerializedName("chart_type")
    @Expose
     var chartType: String? = null,

    @SerializedName("rows_y_fields")
    @Expose
     var rowsYFields: List<String?>? = null,

    ): Serializable

