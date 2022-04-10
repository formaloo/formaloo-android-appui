package co.formaloo.repository.board

import co.formaloo.common.exception.Failure
import co.formaloo.common.functional.Either
import co.formaloo.local.dao.BlockDao
import co.formaloo.model.boards.block.Block
import co.formaloo.model.boards.block.content.BlockContentRes
import co.formaloo.model.boards.block.detail.BlockDetailRes
import co.formaloo.model.boards.block.list.BlockListRes
import co.formaloo.model.boards.board.detail.BoardDetailRes
import co.formaloo.remote.boards.BoardsDatasource
import co.formaloo.repository.BaseRepo
import retrofit2.Response

const val TAG = "BoardRepoImpl"

class BoardRepoImpl(
    private val source: BoardsDatasource,
    private val blockDao: BlockDao
) : BoardRepo, BaseRepo() {


    override suspend fun getBlocksFromDB(): List<Block> {
        return blockDao.getBlockList()
    }

    override suspend fun getBlockFromDB(slug: String): Block? {
        return blockDao.getBlock(slug)

    }


    override suspend fun saveBlockToDB(block: Block) {
        return blockDao.save(block)

    }


    override suspend fun saveBlocksToDB(blocks: List<Block>) {
        return blockDao.save(blocks)

    }


    override suspend fun getSharedBlockList(
        shared_board_slug: String,
        page: Int
    ): Either<Failure, BlockListRes> {
        val call = source.getSharedBlockList(shared_board_slug, page)
        return try {
            request(call, { it.toBlockListRes() }, BlockListRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun getSharedBlockDetail(
        shared_board_slug: String,
        block_slug: String
    ): Either<Failure, BlockDetailRes> {
        val call = source.getSharedBlockDetail(shared_board_slug, block_slug)
        return try {
            request(call, { it }, BlockDetailRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun getSharedBlockContent(
        shared_board_slug: String,
        block_slug: String,
        field_choice: Map<String, Any>?
    ): Either<Failure, BlockContentRes> {
        val call = source.getSharedBlockContent(shared_board_slug, block_slug, field_choice)
        return try {
            request(call, { it.toBlockContentRes() }, BlockContentRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }

    override suspend fun  getSharedBlockContentFlow(
        shared_board_slug: String,
        block_slug: String,
        field_choice: Map<String, Any>?
    ): Response<BlockContentRes> {
        return source.getSharedBlockContentFlow(
                shared_board_slug,
                block_slug,
                field_choice
            )


    }


    override suspend fun getSharedBoardDetail(
        shared_board_slug: String
    ): Either<Failure, BoardDetailRes> {
        val call = source.getSharedBoardDetail(shared_board_slug)
        return try {
            request(call, { it.toBoardDetailRes() }, BoardDetailRes.empty())
        } catch (e: Exception) {
            Either.Left(Failure.Exception)
        }
    }
}
