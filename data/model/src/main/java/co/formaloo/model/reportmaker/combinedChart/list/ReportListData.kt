package co.formaloo.model.reportmaker.combinedChart.list

import co.formaloo.model.reportmaker.combinedChart.CombinedFieldsChart
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReportListData(
    @SerializedName("combined_fields_chartss")
    @Expose
    private val report: List<CombinedFieldsChart>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var current_page: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
): Serializable
