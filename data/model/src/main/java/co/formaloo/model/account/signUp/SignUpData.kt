package co.formaloo.model.account.signUp

import co.formaloo.model.account.signIn.FormProfile
import java.io.Serializable

data class SignUpData(
    var profile: FormProfile? = null
): Serializable
