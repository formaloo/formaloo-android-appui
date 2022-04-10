package co.formaloo.model.account.profile

import co.formaloo.model.account.signIn.FormProfile
import java.io.Serializable

data class FormProfileData(var profile: FormProfile? = null): Serializable
