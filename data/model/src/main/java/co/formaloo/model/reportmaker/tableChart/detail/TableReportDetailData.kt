package co.formaloo.model.reportmaker.tableChart.detail

import co.formaloo.model.reportmaker.tableChart.TableReport
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableReportDetailData(
    @SerializedName("report")
    @Expose
     val report: TableReport? = null
): Serializable
