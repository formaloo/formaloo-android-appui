package co.formaloo.model.boards.board.detail

import co.formaloo.model.boards.board.Board
import java.io.Serializable

data class BoardDetailData(
    var board: Board? = null
) : Serializable
