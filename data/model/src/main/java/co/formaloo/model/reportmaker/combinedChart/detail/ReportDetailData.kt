package co.formaloo.model.reportmaker.combinedChart.detail

import co.formaloo.model.reportmaker.combinedChart.CombinedReport
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReportDetailData(
    @SerializedName("report")
    @Expose
     val report: CombinedReport? = null
): Serializable
