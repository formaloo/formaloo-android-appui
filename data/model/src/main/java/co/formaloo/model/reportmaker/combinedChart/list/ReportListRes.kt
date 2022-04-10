package co.formaloo.model.reportmaker.combinedChart.list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReportListRes(
    @SerializedName("status")
    @Expose
    private val status: Int? = null,

    @SerializedName("data")
    @Expose
    private val data: ReportListData? = null
) : Serializable
