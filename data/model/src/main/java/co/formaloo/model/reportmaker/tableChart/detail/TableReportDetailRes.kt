package co.formaloo.model.reportmaker.tableChart.detail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableReportDetailRes(
    @SerializedName("status")
    @Expose
     val status: Int? = null,

    @SerializedName("data")
    @Expose
     val data: TableReportDetailData? = null
) : Serializable
