package co.formaloo.model.form

import java.io.Serializable

data class EditFormReq(
    var title: String? = null,
    var address: String? = null,
    var form_redirects_after_submit: String? = null,
    var description: String? = null,
    var time_limit: String? = null,
    var button_text: String? = null,
    var success_message: String? = null,
    var send_emails_to: String? = null,
    var submit_email_notif: Boolean? = null,
    var send_user_confirm: Boolean? = null,
    var submit_push_notif: Boolean? = null,
    var include_data_on_redirect: Boolean? = null,
    var text_color: String? = null,
    var button_color: String? = null,
    var field_color: String? = null,
    var background_color: String? = null,
    var border_color: String? = null,
    var submit_text_color: String? = null,
    var category: String? = null,
    var payment_method: String? = null,
    var fixed_payment_amount: String? = null,
    var active: Boolean= true,
    var submit_start_time: String?= null,
    var submit_end_time: String?= null,
    var show_title: Boolean?= true,
    var shuffle_fields: Boolean?= false,
    var shuffle_choices: Boolean?= false,
    var max_submit_count:  String? = null,
    var theme_config:  ThemeConfig? = null,
    var type:  String? = null,
    var form_fields:  List<String>? = null,
    var background_image:String?=null

    ) : Serializable
