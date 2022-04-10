package co.formaloo.model.boards.board

import co.formaloo.model.boards.BlockLocation
import co.formaloo.model.form.Form
import java.io.Serializable

data class Board(
    var slug: String,
    var primary_form: Form? = null,
    var blocks: BlockLocation? = null,
    var is_primary: Boolean? = null,
    var title: String? = null,
    var share_address: String? = null,
    var description: String? = null,
) : Serializable
