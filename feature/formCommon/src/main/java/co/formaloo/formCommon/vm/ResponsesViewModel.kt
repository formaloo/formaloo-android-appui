package co.formaloo.formCommon.vm

import android.util.ArrayMap
import androidx.lifecycle.viewModelScope
import co.formaloo.common.base.BaseViewModel
import co.formaloo.model.boards.block.content.BlockContentRes
import co.formaloo.model.submit.Row
import co.formaloo.model.submit.TopFields
import co.formaloo.repository.board.BoardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber


/**
 * UI state for the Home screen
 */
data class HomeUiState(
    val posts: List<Row> = emptyList(),
    val topFields: List<TopFields> = emptyList(),
    val loading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList()
) {
    /**
     * True if this represents a first load
     */
    val initialLoad: Boolean
        get() = posts.isEmpty() && topFields.isEmpty() && errorMessages.isEmpty() && loading
}

data class ErrorMessage(val id: Long, val messageId: String)


/**
 * ViewModel that handles the business logic of the Home screen
 */
class ResponsesViewModel(
    private val repository: BoardRepo
) : BaseViewModel() {

    var lastRows = arrayListOf<Row>()

    private var field_choice = mutableMapOf<String, Any>()
    private var rowQuery: String? = null
     var page: Int = 1
    private var pageCount: Int = 1
    private var hasNext: Boolean = false
    private var rowTagStr: String? = null

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    init {
//        refreshPosts()
//
//        // Observe for favorite changes in the repo layer
//        viewModelScope.launch {
//
//        }
//    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refreshPosts(sharedBoardAddress: String, blockSlug: String, loading: Boolean) {
        // Ui state is refreshing
        if (loading) {
            _uiState.compareAndSet(_uiState.value, HomeUiState(loading = true))
        }
//        _uiState.update { it.copy(loading = true) }

        fillQueries()
Timber.e("fillQueries $field_choice")
        fun handleSubmits(res: BlockContentRes) {
            res?.let { it ->
                Timber.e("handleSubmits ${it.data?.rows?.size}")
                it.data?.let {
                    pageCount = it.page_count?:0
                    var loadingStat=false

                    if (it.next == null) {
                        loadingStat=false
                        hasNext=false
                    } else {
                        hasNext=true
                    }

                    lastRows.addAll(it.rows?: arrayListOf())

                    Timber.e("lastRows ${lastRows.size}")


                    _uiState.compareAndSet(
                        _uiState.value,
                        HomeUiState(
                            loading = loadingStat,
                            topFields = it.columns ?: arrayListOf(),
                            posts = lastRows
                        )
                    )
                    Timber.e("_uiState ${_uiState.value.posts.size}")

                }
            }


        }

        if (loading || hasNext){
            viewModelScope.launch {
                val result =
                    async(Dispatchers.IO) { repository.getSharedBlockContent(sharedBoardAddress, blockSlug,field_choice) }.await()


                result.either(::handleFailure, ::handleSubmits)
            }
        }else{

        }

    }

    private fun fillQueries() {
        if (rowQuery?.isNotEmpty() == true) {
            field_choice["search"] = rowQuery!!
        }
        if (rowTagStr?.isNotEmpty() == true) {
            field_choice["tags"] = rowTagStr!!
        }
        field_choice["page"] = page

    }


    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        _uiState.value.let { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)

        }

    }

    fun resetRetreiveResponses() {
        hasNext=false
        initPage(1)
        field_choice.clear()
        initFieldChoice(mutableMapOf())
        initRowQuery("")
        initRowTagStr("")
        lastRows= arrayListOf()
    }

    private fun createRB(req: ArrayMap<String, Any>): RequestBody {
        return RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(req).toString()
        )
    }

    fun initFieldChoice(field_choice: MutableMap<String, Any>) {
        this.field_choice = field_choice
    }

    fun initRowQuery(rowQuery: String?) {
        this.rowQuery = rowQuery
    }

    fun initPage(page: Int) {
        this.page = page
    }


    fun initRowTagStr(tags: String?) {
        rowTagStr = tags
    }
}
