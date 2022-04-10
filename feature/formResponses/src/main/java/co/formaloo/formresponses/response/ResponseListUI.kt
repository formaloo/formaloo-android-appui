package co.formaloo.formresponses.response

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import co.formaloo.common.theme.colorLightGray
import co.formaloo.common.BaseMethod
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants

import co.formaloo.formCommon.vm.HomeUiState
import co.formaloo.formCommon.vm.ResponsesViewModel
import co.formaloo.model.form.tags.Tag
import co.formaloo.model.submit.Row
import co.formaloo.model.submit.TopFields
import co.formaloo.model.submit.submitList.SubmitListData
import co.formaloo.formresponses.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


var lastClickedRow: Row? = null
val baseMethod = BaseMethod()

var minColumnWidth = baseMethod.convertPixelsToDp(baseMethod.getScreenWidth().toFloat()).dp
val cell_height = 42.dp
val card_elevation = 4.dp
val divider_size = 1.dp
val base_padding = 16.dp
val small_padding = 8.dp
val x_small_padding = 4.dp
var no_answer = "---"
val cellWidth = 150.dp

var topFields = emptyList<TopFields>()

var removeFilter = mutableStateOf<String?>(null)
var loading = mutableStateOf<Boolean>(true)
var emptyView = mutableStateOf<Boolean>(true)
var uiSubmitData = mutableStateOf<SubmitListData?>(null)

var block_slug: String? = null

/**
 * Displays the Home screen.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param responsesVM ViewModel that handles the business logic of this screen
 * @param openRow (event) request navigation to Article screen
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun HomeScreen(
    responsesVM: ResponsesViewModel,
    openRow: (Row, String?, MutableState<Row>) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    // UiState of the HomeScreen
    val uiState by responsesVM.uiState.collectAsState()


    HomeScreen(
        uiState = uiState,
        onRefreshPosts = { tags, choices, query ->
            responsesVM.resetRetreiveResponses()
            resetData()
            tags?.let {
                responsesVM.initRowTagStr(tags)
            }
            choices?.let {
                responsesVM.initFieldChoice(choices)
            }
            query?.let {
                responsesVM.initRowQuery(query)
            }
            responsesVM.refreshPosts(BuildConfig.APPUI_ADDRESS, block_slug ?: "", true)

        },
        onErrorDismiss = { responsesVM.errorShown(it) },
        openRow = openRow,
        scaffoldState = scaffoldState,
        loadMore = {
            responsesVM.initPage(responsesVM.page + 1)
            responsesVM.refreshPosts(BuildConfig.APPUI_ADDRESS, block_slug ?: "", false)
        }
    )
}


/**
 * Displays the Home screen.
 *
 * Stateless composable is not coupled to any specific state management.
 *
 * @param uiState (state) the data to show on the screen
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) error message was shown
 * @param openRow (event) request navigation to Article screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefreshPosts: (String?, MutableMap<String, Any>?, String?) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openRow: (Row, String?, MutableState<Row>) -> Unit,
    scaffoldState: ScaffoldState,
    loadMore: () -> Unit
) {
    val scrollState = rememberLazyListState()

    scrollState.isScrollInProgress.let {
        if (it && scrollState.layoutInfo.visibleItemsInfo.last().index == scrollState.layoutInfo.totalItemsCount - 1)
            loadMore()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it, modifier = Modifier.padding(base_padding)) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = uiState.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = uiState.loading,
            onRefresh = onRefreshPosts,
            content = {
                HomeScreenErrorAndContent(
                    posts = uiState.posts,
                    topFields = uiState.topFields,
                    isShowingErrors = uiState.errorMessages.isNotEmpty(),
                    onRefresh = onRefreshPosts,
                    openRow = openRow,
                    modifier = modifier.fillMaxSize(),
                    scrollState = scrollState
                )
            }
        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = errorMessage.messageId
        val retryMessageText = stringResource(id = R.string.try_again)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState(null, null, null)
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = { onRefresh(null, null, null) },
            content = content,
        )
    }
}

/**
 * Responsible for displaying any error conditions around [PostList].
 *
 * @param posts (state) list of posts to display
 * @param isShowingErrors (state) whether the screen is showing errors or not
 * @param favorites (state) all favorites
 * @param onRefresh (event) request to refresh data
 * @param openRow (event) request navigation to Article screen
 * @param onToggleFavorite (event) request a single favorite be toggled
 * @param modifier modifier for root element
 */
@Composable
private fun HomeScreenErrorAndContent(
    posts: List<Row>,
    topFields: List<TopFields>,
    isShowingErrors: Boolean,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,
    openRow: (Row, String?, MutableState<Row>) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {
    if (posts.isNotEmpty()) {
        PostList(posts, topFields, onRefresh, openRow, modifier, scrollState)
    } else if (!isShowingErrors) {
        // if there are no posts, and no error, let the user refresh manually
        TextButton(onClick = { onRefresh(null, null, null) }, modifier.fillMaxSize()) {
            Text(
                LocalContext.current.getString(R.string.empty_submit_list),
                textAlign = TextAlign.Center
            )
        }
    } else {
        // there's currently an error showing, don't show any content
        Box(modifier.fillMaxSize()) { /* empty screen */ }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PostList(
    rowList: List<Row>,
    topFields: List<TopFields>,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,
    openRow: (postId: Row, formSlug: String?, rowStat: MutableState<Row>) -> Unit,
    modifier: Modifier = Modifier,
    listScrollState: LazyListState = rememberLazyListState(),
) {


    val currentLocalContext = LocalContext.current

    no_answer = currentLocalContext.getString(R.string.no_answer)

//    lastRows.addAll(rowList)

    emptyView.value = rowList.isEmpty()

    val size = if (topFields.isNotEmpty()) {
        topFields.size
    } else {
        1
    }


    /**
    here we use LazyColumn that has build-in nested scroll, but we want to act like a
    parent for this LazyColumn and participate in its nested scroll.
    Let's make a collapsing toolbar for LazyColumn
     */
    val textWidth = 150.dp

    val textWidthPx =
        with(LocalDensity.current) { (textWidth * size).roundToPx().toFloat() }
    // our offset to collapse toolbar
    val textOffsetWidthPx = remember { mutableStateOf(0f) }
    // now, let's create connection to the nested scroll system and listen to the scroll
    // happening inside child LazyColumn
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.x
                val newOffset = textOffsetWidthPx.value + delta
                textOffsetWidthPx.value = newOffset.coerceIn(-textWidthPx, 0f)

                // here's the catch: let's pretend we consumed 0 in any case, since we want
                // LazyColumn to scroll anyway for good UX
                // We're basically watching scroll without taking it
                return Offset.Zero
            }
        }
    }


    if (topFields.isNotEmpty()) {
        val headerSlug = topFields[0].slug ?: ""

        LazyRow(
            Modifier
                .fillMaxSize()
                // attach as a parent to the nested scroll system
                .nestedScroll(nestedScrollConnection)
                .background(colorLightGray)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .defaultMinSize(minColumnWidth)
                ) {
                    FieldsHeader(ArrayList(topFields))

                    if (removeFilter.value?.isNotEmpty() == true) {
                        SelectedTagView(onRefresh)
                    } else if (removeFilter.value != null) {
                    }


                    LazyColumn(state = listScrollState, modifier = Modifier.padding(base_padding)) {

                        items(rowList) { row ->

                            val rememberRow = remember { mutableStateOf(row) }
                            rememberRow.value = row
                            val indexOf = rowList.indexOf(row)

                            Card(
//                                onClick = {
//                                    lastClickedRow = rememberRow.value
//                                    try {
//                                        openRow(
//                                            rememberRow.value,
//                                            (row.form as Form).slug,
//                                            rememberRow
//                                        )
//
//                                    } catch (e: Exception) {
//
//                                    }
//
//                                }
//                                ,
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .padding(0.dp, 0.dp, 0.dp, 12.dp),
                                backgroundColor = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(base_padding),
                                elevation = card_elevation

                            ) {
                                Row {
                                    Column(
                                        modifier = Modifier
                                            .defaultMinSize(minColumnWidth)
                                    ) {
                                        RowStickyHeader(
                                            headerSlug,
                                            rememberRow,
                                            textOffsetWidthPx,
                                            onRefresh,
                                            indexOf
                                        )

                                        Divider(
                                            color = Color.LightGray,
                                            modifier = Modifier
                                                .defaultMinSize(minColumnWidth)
                                                .width((cellWidth * topFields.size) + (base_padding * topFields.size))
                                                .height(divider_size)
                                        )

                                        RowAnswers(row, ArrayList(topFields), onRefresh)
                                    }
                                }
                            }


                        }

                    }

                }
            }

        }
    } else {

    }


}


/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}


@ExperimentalMaterialApi
@Composable
fun SelectedTagView(onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit) {
    CellSelectedTag(onRefresh, removeFilter.value ?: "")

}


@ExperimentalMaterialApi
@Composable
fun EmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Cell(LocalContext.current.getString(R.string.empty_submit_list))
    }
}

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowAnswers(
    row: Row,
    customField: ArrayList<TopFields>,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,

    ) {
    Row(
        modifier = Modifier
            .height(cell_height), verticalAlignment = Alignment.CenterVertically
    ) {
        val renderedData = row.rendered_data ?: mapOf()

        val last = customField.last()
        val first = customField.first()

        customField.forEach { field ->
            if (field != first) {
                val topFieldSlug = field.slug
                Box(
                    modifier = Modifier
                        .width(cellWidth)
                        .padding(x_small_padding, 0.dp, x_small_padding, 0.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (renderedData.contains(topFieldSlug)) {
                        val data = renderedData[topFieldSlug]
                        val value = data?.value ?: no_answer
                        val raw_value = data?.raw_value
                        val slug = data?.slug
                        val type = data?.type
                        val choice_items = data?.choice_items

                        val types = arrayListOf(
                            Constants.MATRIX,
                            Constants.MULTI_SELECT,
                            Constants.SINGLE_SELECT,
                            Constants.DROPDOWN
                        )

                        if (types.contains(type)) {

                            val valueSlugList = when (raw_value) {
                                is String -> {
                                    arrayListOf(raw_value)
                                }
                                is ArrayList<*> -> {
                                    raw_value as ArrayList<String>
                                }
                                else -> {
                                    arrayListOf(raw_value?.toString() ?: "")
                                }
                            }


                            Row {
                                if (valueSlugList.isNotEmpty()) {
                                    for (i in 0 until valueSlugList.size) {

                                        val itemSlug = valueSlugList[i]

                                        var choiceTitle = ""
                                        choice_items?.findLast {
                                            it.slug ?: "" == itemSlug
                                        }?.let {
                                            choiceTitle = it.title ?: ""
                                        }
                                        CellChoices(
                                            itemSlug,
                                            slug ?: "",
                                            type ?: "",
                                            choiceTitle,
                                            onRefresh
                                        )

                                    }

                                } else {

                                }

                            }

                        } else {
                            Cell(getTitle(value))

                        }


                    } else {
                        Cell(no_answer)

                    }

                }

                if (last != field) {
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .padding(16.dp, 0.dp, 4.dp, 0.dp)
                            .height(cell_height + base_padding)
                            .width(divider_size)
                    )


                } else {

                }
            }


        }
    }

}

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowStickyHeader(
    headerSlug: String,
    row: MutableState<Row>,
    toolbarOffsetHeightPx: MutableState<Float>,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,
    indexOfRow: Int
) {

    val myRow by row

    Row(modifier = Modifier
        .padding(x_small_padding, small_padding, x_small_padding, small_padding)
        .offset {
            IntOffset(
                x = -toolbarOffsetHeightPx.value.roundToInt(),
                y = 0
            )
        }) {
//        val header = "$i    " + getTitle((row.value.rendered_data ?: mapOf())[headerSlug]?.value)
        val header = getTitle((myRow.rendered_data ?: mapOf())[headerSlug]?.value)
        Text(
            text = header,
            maxLines = 1,
            modifier = Modifier
                .width(IntrinsicSize.Max)
        )

        for (item in myRow.row_tags ?: arrayListOf()) {
            CellTags(onRefresh, item)

        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FieldsHeader(customField: ArrayList<TopFields>) {

    Column(
        modifier = Modifier
            .defaultMinSize(minColumnWidth)
            .fillMaxWidth()

    ) {
        Card(
            modifier = Modifier
                .defaultMinSize(minColumnWidth)
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.background,
            shape = RectangleShape,
            elevation = card_elevation
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minColumnWidth)
                    .fillMaxWidth()
                    .height(cell_height)
                    .padding(base_padding, 0.dp, 0.dp, 0.dp)
            ) {
                val last = customField.last()
                val first = customField.first()

                customField.forEach {
                    if (it != first) {
                        Box(
                            modifier = Modifier
                                .width(cellWidth)
                                .padding(x_small_padding), contentAlignment = Alignment.CenterStart
                        ) {
                            Cell(it.title ?: "")
                        }

                        if (last != it) {
                            Spacer(modifier = Modifier.width(base_padding))

                            Divider(
                                color = Color.LightGray,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(divider_size)
                            )
                            Spacer(modifier = Modifier.width(x_small_padding))

                        } else {

                        }

                    }

                }
            }
        }

    }
}

@Composable
fun getTitle(value: Any?): String {
    val context = LocalContext.current

    return when (value) {

        is String -> {
            if (value.contains(Constants.HREF)) {
                context.getString(R.string.file)

            } else {
                value
            }
        }
        is Int -> {
            var strValue =
                BigDecimal(value.toString()).stripTrailingZeros()
                    .toPlainString()
            strValue =
                baseMethod.formatter(strValue)?.toString() ?: strValue

            strValue
        }
        is Float -> {
            var strValue =
                BigDecimal(value.toString()).stripTrailingZeros()
                    .toPlainString()
            strValue =
                baseMethod.formatter(strValue)?.toString() ?: strValue

            strValue
        }
        is Long -> {
            var strValue =
                BigDecimal(value.toString()).stripTrailingZeros()
                    .toPlainString()
            strValue =
                baseMethod.formatter(strValue)?.toString() ?: strValue

            strValue
        }
        is Double -> {
            var strValue =
                BigDecimal(value.toString()).stripTrailingZeros()
                    .toPlainString()
            strValue =
                baseMethod.formatter(strValue)?.toString() ?: strValue

            strValue

        }
        else -> ""
    }

}

@ExperimentalMaterialApi
@Composable
fun CellChoices(
    tagSlug: String,
    fieldSlug: String,
    fieldType: String,
    tagTitle: String,
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,

    ) {

    val backgroundColor = getSlugColor(tagSlug)

    val shape = RoundedCornerShape(100.dp)
    Card(
        onClick = {
            resetData()

            val fSlug = if (fieldType == Constants.MULTI_SELECT) {
                fieldSlug + "_has"
            } else {
                fieldSlug
            }


            onRefresh(null, mutableMapOf(fSlug to tagSlug), null)

            removeFilter.value = tagTitle
        },
        modifier = Modifier.padding(x_small_padding, 0.dp, x_small_padding, 0.dp),
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = card_elevation
    ) {

        Text(
            text = tagTitle,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.padding(small_padding, divider_size, small_padding, divider_size),
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis,

            )
    }


}

@ExperimentalMaterialApi
@Composable
fun CellTags(onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit, tag: Tag) {

    val backgroundColor = Color(baseMethod.parseColor("#${tag.color}"))

    val shape = RoundedCornerShape(100.dp)
    Card(
        onClick = {
            resetData()
            onRefresh(tag.slug, null, null)

            removeFilter.value = tag.title ?: ""
        },
        modifier = Modifier.padding(x_small_padding, 0.dp, x_small_padding, 0.dp),
        shape = shape,
        backgroundColor = backgroundColor,
        elevation = card_elevation
    ) {

        Text(
            text = tag.title ?: "",
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.padding(small_padding, divider_size, small_padding, divider_size),
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis,

            )
    }


}

@ExperimentalMaterialApi
@Composable
fun CellSelectedTag(
    onRefresh: (String?, MutableMap<String, Any>?, String?) -> Unit,
    title: String
) {
    val shape = RoundedCornerShape(100.dp)
    Card(
        modifier = Modifier.padding(base_padding, base_padding, base_padding, 0.dp),
        shape = shape,
        backgroundColor = Color.Gray,
        elevation = card_elevation
    ) {
        Row {
            Text(
                text = title,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.padding(
                    small_padding,
                    divider_size,
                    small_padding,
                    divider_size
                ),
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,

                )

            IconButton(modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {
                    removeFilter.value = ""
                    resetData()
                    onRefresh(null, null, null)

                }) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    "contentDescription",
                    tint = Color.White
                )
            }

        }


    }


}

@Composable
fun Cell(it: String) {

    val color = if (it == no_answer) {
        Color.LightGray
    } else {
        Color.Black
    }
    Text(
        text = it,
        color = color,
        maxLines = 1,
        style = MaterialTheme.typography.body1,
        overflow = TextOverflow.Ellipsis


    )


}

fun resetData() {
    loading.value = true
    topFields = emptyList()
}

private val colorList = hashMapOf<String, Color>()
fun getSlugColor(slug: String): Color {
    val rnd = Random()

    val color = Color(
        red = rnd.nextFloat(),
        green = rnd.nextFloat(),
        blue = rnd.nextFloat(),
        alpha = 1f
    )

    val backgroundColor = if (colorList.containsKey(slug)) {
        colorList[slug] ?: color
    } else {


        colorList[slug] = color

        color
    }
    return backgroundColor

}
