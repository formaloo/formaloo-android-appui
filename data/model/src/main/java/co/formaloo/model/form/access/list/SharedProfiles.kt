package co.formaloo.model.form.access.list

import co.formaloo.model.account.signIn.FormProfile
import co.formaloo.model.form.Form
import java.io.Serializable

data class SharedProfiles(
    var profile:FormProfile? = null,
    var form:Form? = null,
    var access: String? = null,
    var slug: String? = null

): Serializable
