package co.formaloo.model.reportmaker.combinedChart

import co.formaloo.model.form.Fields
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CombinedChartDatum(
    @SerializedName("x_field")
    @Expose
     val xField: Fields? = null,

    @SerializedName("y_field")
    @Expose
     val yField: Fields? = null,

    @SerializedName("x_value")
    @Expose
     val xValue: String? = null,

    @SerializedName("y_value")
    @Expose
     val yValue: String? = null,

    @SerializedName("count")
    @Expose
     val count: Int? = null,
):Serializable
