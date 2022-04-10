package co.formaloo.model.field

import co.formaloo.model.form.ChoiceItem
import java.io.Serializable

data class FieldReq(
    var form:String? = null,
    var type: String? = null,
    var sub_type: String? = null,
    var title: String? = null,
    var description: String? = null,
    var position: Int? = null,
    var required: Boolean? = null,
    var max_size: Int? = null,
    var min_value: Int? = null,
    var max_value: Int? = null,
    var from_date: String? = null,
    var to_date: String? = null,
    var from_time: String? = null,
    var to_time: String? = null,
    var file_type: String? = null,
    var currency: String? = null,
    var phone_type: String? = null,
    var choice_items: List<ChoiceItem>? = null,
    var choice_groups: List<ChoiceItem>? = null

//    var choice_items: List<HashMap<String,String?>>? = null


):Serializable
