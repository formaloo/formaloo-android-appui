package co.formaloo.model.reportmaker.combinedChart.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReportDetailRes(
    @SerializedName("status")
    @Expose
     val status: Int? = null,

    @SerializedName("data")
    @Expose
     val data: ReportDetailData? = null
) : Serializable
