package co.formaloo.model.reportmaker.tableChart.list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TableReportListRes(
    @SerializedName("status")
    @Expose
    private val status: Int? = null,

    @SerializedName("data")
    @Expose
    private val data: TableReportListData? = null
) : Serializable
