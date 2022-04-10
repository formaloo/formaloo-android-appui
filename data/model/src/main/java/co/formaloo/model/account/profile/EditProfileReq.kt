package co.formaloo.model.account.profile

data class EditProfileReq(
    val first_name: String,
    val phone_number: String,
    val email: String,
    val weekly_digest: Boolean
)
