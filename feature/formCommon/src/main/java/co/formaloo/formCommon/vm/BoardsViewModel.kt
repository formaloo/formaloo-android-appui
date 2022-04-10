package co.formaloo.formCommon.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.formaloo.common.base.BaseViewModel
import co.formaloo.model.boards.BlockType
import co.formaloo.model.boards.block.Block
import co.formaloo.model.boards.block.content.BlockContentRes
import co.formaloo.model.boards.block.detail.BlockDetailRes
import co.formaloo.model.boards.board.Board
import co.formaloo.model.boards.board.detail.BoardDetailRes
import co.formaloo.repository.board.BoardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BoardsViewModel(private val repository: BoardRepo) : BaseViewModel() {


    private val _board = MutableLiveData<Board>().apply { value = null }
    val board: LiveData<Board> = _board


    private val _blockContentRes = MutableLiveData<BlockContentRes>().apply { value = null }
    val blockContentRes: LiveData<BlockContentRes> = _blockContentRes


    private val _block = MutableLiveData<Block>().apply { value = null }
    val block: LiveData<Block> = _block

    private val _rawMenuBlock = MutableLiveData<Block>().apply { value = null }
    val rawMenuBlock: LiveData<Block> = _rawMenuBlock

    private val _menuBlock = MutableLiveData<Block>().apply { value = null }
    val menuBlock: LiveData<Block> = _menuBlock

    private val _rawStatBlock = MutableLiveData<Block>().apply { value = null }
    val rawStatBlock: LiveData<Block> = _rawStatBlock

    private val _statBlock = MutableLiveData<Block>().apply { value = null }
    val statBlock: LiveData<Block> = _statBlock

    private val _blockList = MutableLiveData<List<Block>>().apply { value = null }
    val blockList: LiveData<List<Block>> = _blockList


    private val _localBlockList = MutableLiveData<List<Block>>().apply { value = null }
    val localBlockList: LiveData<List<Block>> = _localBlockList


    fun retrieveBlockFromDB(blockSlug: String) = viewModelScope.launch {

        val result = withContext(Dispatchers.IO) { repository.getBlockFromDB(blockSlug) }
        _block.value = result

    }

    fun retrieveBlockDetailFromDB(blockSlug: String) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { repository.getBlockFromDB(blockSlug) }

    }

    fun retrieveMenuBlockDetailFromDB(blockSlug: String) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { repository.getBlockFromDB(blockSlug) }
        Timber.e("Menu $result")
        _menuBlock.value = result
    }

    fun retrieveStatBlockDetailFromDB(blockSlug: String) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { repository.getBlockFromDB(blockSlug) }
        Timber.e("Stat $result")

        _statBlock.value = result
    }

    fun retrieveBlockListFromDB() = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { repository.getBlocksFromDB() }
        _localBlockList.value = result

    }


    fun retrieveSharedBoardDetail(sharedBoardAddress: String) = viewModelScope.launch {
        Timber.e("retrieveSharedBoardDetail ${System.currentTimeMillis()}")
        val result =
            withContext(Dispatchers.IO) { repository.getSharedBoardDetail(sharedBoardAddress) }
        result.either(::handleFailure, ::handleBoardDetailData)
    }


    fun getSharedBlockContentFlow(
        sharedBoardAddress: String,
        blockSlug: String,
        field_choice: androidx.collection.ArrayMap<String, Any>
    ) = viewModelScope.launch {
        val sharedBlockContentFlow =
            repository.getSharedBlockContentFlow(sharedBoardAddress, blockSlug, field_choice)
        Timber.e("blockSlug $blockSlug")
        Timber.e("field_choice $field_choice")
        Timber.e("raw ${sharedBlockContentFlow.raw()}")
        _blockContentRes.value = sharedBlockContentFlow.body()
        val data = sharedBlockContentFlow.body()?.data

//        if (data?.next!=null){
//            val page=  (data.current_page?:1)+1
//            getSharedBlockContentFlow(sharedBoardAddress, blockSlug, field_choice)
//        }
    }


    fun getSharedBlockContentFlow1(
        sharedBoardAddress: String,
        blockSlug: String,
        field_choice: androidx.collection.ArrayMap<String, Any>
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val content =
                repository.getSharedBlockContentFlow(sharedBoardAddress, blockSlug, field_choice)
//                    .asLiveData()

        }

    }

    fun retrieveSharedBoardContent(
        sharedBoardAddress: String,
        blockSlug: String,
        field_choice: androidx.collection.ArrayMap<String, Any>
    ) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) {
            repository.getSharedBlockContent(
                sharedBoardAddress,
                blockSlug,
                field_choice
            )
        }
        result.either(::handleFailure, ::handleSubmits)
    }

    fun handleSubmits(res: BlockContentRes) {

    }

    private fun handleBoardDetailData(res: BoardDetailRes?) {
        res?.let {
            it.data?.let {
                Timber.e("handleBoardDetailData ${System.currentTimeMillis()}")

                val header = (it.board?.blocks?.header ?: listOf())
                val leftSideBar = it.board?.blocks?.leftSideBar ?: listOf()
                val rightSideBar = it.board?.blocks?.rightSideBar ?: listOf()
                val footer = it.board?.blocks?.footer ?: listOf()

                val allBlock = arrayListOf<Block>()


                allBlock.addAll(header)
                allBlock.addAll(leftSideBar)
                allBlock.addAll(rightSideBar)
                allBlock.addAll(footer)

                val menuStatBlockList = arrayListOf<Block>()

                allBlock.findLast {
                    it.type == BlockType.MENU.slug
                }?.let {
                    menuStatBlockList.add(it)
                    _rawMenuBlock.value = it

                }

                allBlock.findLast {
                    it.type == BlockType.STATS.slug
                }?.let {
                    _rawStatBlock.value = it
                    menuStatBlockList.add(it)

                }

                saveBlockListToDB(menuStatBlockList)

            }
        }
    }

    private fun saveBlockListToDB(blocks: ArrayList<Block>) = viewModelScope.launch {
        repository.saveBlocksToDB(blocks)
    }

    private fun saveBlockData(block: Block) = viewModelScope.launch {
        repository.saveBlockToDB(block)


    }

    fun retrieveMenuBlockDetail(sharedBoardAddress: String, blockSlug: String) =
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getSharedBlockDetail(
                    sharedBoardAddress,
                    blockSlug
                )
            }
            result.either(::handleFailure, ::handleMenuBlockDetailData)
        }

    private fun handleMenuBlockDetailData(res: BlockDetailRes?) {
        res?.let {
            it.data?.block?.let { block ->
                _menuBlock.value = block
                saveBlockData(block)
            }
        }
    }

    fun retrieveStatBlockDetail(sharedBoardAddress: String, blockSlug: String) =
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getSharedBlockDetail(
                    sharedBoardAddress,
                    blockSlug
                )
            }
            result.either(::handleFailure, ::handleStatBlockDetailData)
        }

    private fun handleStatBlockDetailData(res: BlockDetailRes?) {
        res?.let {
            it.data?.block?.let { block ->
                _statBlock.value = block
                saveBlockData(block)
            }
        }
    }

    fun retrieveSharedBlockDetail(sharedBoardAddress: String, blockSlug: String) =
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getSharedBlockDetail(
                    sharedBoardAddress,
                    blockSlug
                )
            }
            result.either(::handleFailure, ::handleBlockDetailData)
        }

    private fun handleBlockDetailData(res: BlockDetailRes?) {
        res?.let {
            it.data?.block?.let { block ->
                _block.value = block
                saveBlockData(block)
            }
        }
    }


}
