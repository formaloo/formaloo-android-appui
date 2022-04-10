package co.formaloo.model.reportmaker.tableChart.list

import co.formaloo.model.reportmaker.tableChart.TableChart
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableReportListData(
    @SerializedName("table_charts")
    @Expose
    private val report: List<TableChart>? = null,
    var previous: String? = null,
    var next: String? = null,
    var count: Int? = null,
    var current_page: Int? = null,
    var page_count: Int? = null,
    var page_size: Int? = null
): Serializable
