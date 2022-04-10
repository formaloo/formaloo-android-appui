package co.formaloo.formresponses.reportMaker

import com.anychart.chart.common.dataentry.ValueDataEntry

class CustomDataEntry2 internal constructor(
    x: String?,
    values: List<Number>
) : ValueDataEntry(x, values[0]) {
    init {
        values.let {
            for (i in values.indices) {
                setValue("value${i}", values[i])

            }
        }

    }
}
