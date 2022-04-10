package co.formaloo.model.account.pass.email

data class ConfirmPassEmailReq(
        val email: String,
        val code: String,
        val token: String,
        val new_password: String
)
