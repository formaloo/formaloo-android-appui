package co.formaloo.model.account.pass.change

data class ChangePassReq(
        val new_password: String,
        val current_password: String
)
