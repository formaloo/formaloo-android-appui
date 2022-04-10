package co.formaloo.repository.board

import co.formaloo.common.exception.Failure
import co.formaloo.common.functional.Either
import co.formaloo.model.boards.block.Block
import co.formaloo.model.boards.block.content.BlockContentRes
import co.formaloo.model.boards.block.detail.BlockDetailRes
import co.formaloo.model.boards.block.list.BlockListRes
import co.formaloo.model.boards.board.detail.BoardDetailRes
import retrofit2.Response

interface BoardRepo {


    suspend fun getSharedBoardDetail(shared_board_slug: String): Either<Failure, BoardDetailRes>
    suspend fun getSharedBlockList(
        shared_board_slug: String,
        page: Int
    ): Either<Failure, BlockListRes>

    suspend fun getSharedBlockDetail(
        shared_board_slug: String,
        block_slug: String
    ): Either<Failure, BlockDetailRes>

    suspend fun getSharedBlockContent(
        shared_board_slug: String,
        block_slug: String,
        field_choice: Map<String, Any>?

    ): Either<Failure, BlockContentRes>

    suspend fun getBlocksFromDB(): List<Block>
    suspend fun getBlockFromDB(slug: String): Block?
    suspend fun saveBlocksToDB(blocks: List<Block>)
    suspend fun saveBlockToDB(block: Block)
    suspend  fun getSharedBlockContentFlow(
        shared_board_slug: String,
        block_slug: String,
        field_choice: Map<String, Any>?
    ): Response<BlockContentRes>
}
