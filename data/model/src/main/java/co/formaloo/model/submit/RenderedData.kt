package co.formaloo.model.submit

import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.submit.row.CalculationScore
import java.io.Serializable

data class RenderedData(
    var title: String? = null,
    var slug: String? = null,
    var type: String? = null,
    var file_type: String? = null,
    var delta_score: CalculationScore? = null,
    var value: Any? = null,
    var raw_value: Any? = null,
    var choice_items: ArrayList<ChoiceItem>? =  null,
    var choice_groups: ArrayList<ChoiceItem>? =  null
): Serializable
