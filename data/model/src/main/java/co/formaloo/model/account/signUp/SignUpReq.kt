package co.formaloo.model.account.signUp

data class SignUpReq(
    val first_name: String,
    val phone_number: String,
    val email: String,
    val password: String
)
