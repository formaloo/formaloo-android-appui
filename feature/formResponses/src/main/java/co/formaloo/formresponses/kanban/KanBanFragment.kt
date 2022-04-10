package co.formaloo.formresponses.kanban

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.collection.arrayMapOf
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.formaloo.common.BuildConfig
import co.formaloo.common.Constants
import co.formaloo.common.base.BaseFragment
import co.formaloo.common.base.BaseViewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.loadImage
import co.formaloo.common.showMsg
import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.formresponses.R
import co.formaloo.formresponses.databinding.FragmentKanBanBinding
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.form.Fields
import co.formaloo.model.form.tags.Tag
import co.formaloo.model.submit.RenderedData
import co.formaloo.model.submit.Row
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.BoardView.BoardListener
import com.woxthebox.draglistview.ColumnProperties
import com.woxthebox.draglistview.DragItem
import kotlinx.android.synthetic.main.activity_row_detail.*
import kotlinx.android.synthetic.main.layout_kanban_content_detail.*
import kotlinx.android.synthetic.main.layout_kanban_content_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class KanBanFragment : BaseFragment() {
    private val boardsVM: BoardsViewModel by viewModel()

    private var blockSlug: String? = null

    override fun getViewModel(): BaseViewModel = boardsVM
    private lateinit var binding: FragmentKanBanBinding

    private var mColumns = 0
    private val mGridLayout = false
    private var sCreatedItems: Long = 0

    companion object {
        fun newInstance(blockItemSlug: String?,blockSlug:String?) = KanBanFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.SLUG, blockSlug)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentKanBanBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBundle()
        initView()
        initData()
    }

    private fun initView() {
        close_btn.setOnClickListener {
            binding.contentDetailLay.invisible()
        }

    }
    private fun checkBundle() {
        arguments?.let { it ->
            it.getString(Constants.SLUG)?.let {
                blockSlug = it
            }
        }
    }
    private fun initData() {

        boardsVM.retrieveSharedBlockDetail(BuildConfig.APPUI_ADDRESS,blockSlug?:"")

        boardsVM.block.observe(viewLifecycleOwner) {
            it?.let {block->

                val columnsField = block.columns_field
                val featuredImageField = block.featured_image_field
                val itemsField = block.items_field
                blockSlug = block.slug
                columnsField?.choice_items?.let { choices ->
                    choices.forEach { choiceItem ->
                        addColumn(
                            columnsField.slug ?: "",
                            choiceItem,
                            featuredImageField,
                            itemsField
                        )

                    }

                }


            }
        }

        initBoard()



    }


    private fun initBoard() {
        val mBoardView = binding.boardView
        mBoardView.setColumnWidth(350)
mBoardView.isDragEnabled=false

        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)
        mBoardView.setBoardListener(object : BoardListener {
            override fun onItemDragStarted(column: Int, row: Int) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
            }

            override fun onItemChangedPosition(
                oldColumn: Int,
                oldRow: Int,
                newColumn: Int,
                newRow: Int
            ) {
                //Toast.makeText(mBoardView.getContext(), "Position changed - column: " + newColumn + " row: " + newRow, Toast.LENGTH_SHORT).show();
            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
                //Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragStarted(position: Int) {
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
                //Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragEnded(position: Int) {
                //Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
            }
        })
//        mBoardView.setBoardCallback(object : BoardCallback {
//            override fun canDragItemAtPosition(column: Int, dragPosition: Int): Boolean {
//                // Add logic here to prevent an item to be dragged
//                return false
//            }
//
//            override fun canDropItemAtPosition(
//                oldColumn: Int,
//                oldRow: Int,
//                newColumn: Int,
//                newRow: Int
//            ): Boolean {
//                // Add logic here to prevent an item to be dropped
//                return false
//            }
//        })

//        mBoardView.setCustomDragItem(
//            if (mGridLayout) null else MyDragItem(
//                activity,
//                R.layout.column_item
//            )
//        )
//
//        mBoardView.setCustomColumnDragItem(
//            if (mGridLayout) null else MyColumnDragItem(
//                activity,
//                R.layout.column_drag_layout
//            )
//        )


    }


    private fun addColumn(
        columnSlug: String,
        columnItem: ChoiceItem,
        featuredImageField: Fields?,
        itemsField: Fields?
    ) {
        val columnItemSlug = columnItem.slug

        val mBoardView = binding.boardView
        val mItemArray = arrayListOf<Pair<Long?, Row?>>()
        val listAdapter = ItemAdapter(
            mItemArray,
            R.layout.column_item,
            R.id.item_layout,
            true,
            featuredImageField,
            itemsField,
            object :ColumnItemListener{
                override fun openContent(row: Row) {

                    initContentDetail(row,itemsField,columnItem,columnItemSlug,featuredImageField)
                }

            }
        )

        val fc = arrayMapOf<String, Any>()
        fc[columnSlug] = columnItemSlug
        fc["page"] = 1

        boardsVM.getSharedBlockContentFlow(BuildConfig.APPUI_ADDRESS, blockSlug ?: "", fc)

        boardsVM.blockContentRes.observe(viewLifecycleOwner) {
            it?.data?.let {
                Timber.e("_blockContentRes $it")
                it.rows?.forEachIndexed { index, row ->
                    val data = row.data
                    if (data?.contains(columnSlug) == true && data[columnSlug] == columnItemSlug) {
                        mItemArray.add(Pair((index).toLong(), row))

                    }
                }
                listAdapter.notifyDataSetChanged()

//                if (it.next != null) {
//                    val page = (it.current_page ?: 1) + 1
//                    fc["page"]=page
//                    boardsVM.getSharedBlockContentFlow(
//                        BuildConfig.APPUI_ADDRESS,
//                        blockSlug ?: "",
//                        fc
//                    )
//                }
            }

        }


        val header = View.inflate(activity, R.layout.column_header, null)
        (header.findViewById<View>(R.id.text) as TextView).text = columnItem.title

        header.setOnClickListener { v ->
//            val id: Long = sCreatedItems++
//            Timber.e("header $id")
//            val item = Pair(id, "Test $id")
//            mBoardView.addItem(mBoardView.getColumnOfHeader(v), 0, item, true)
//            mBoardView.moveItem(4, 0, 0, true);
//            mBoardView.removeItem(column, 0);
//            mBoardView.moveItem(0, 0, 1, 3, false);
//            mBoardView.replaceItem(0, 0, item1, true);

        }
        val footer = View.inflate(activity, R.layout.column_header, null)
        (footer.findViewById<View>(R.id.text) as TextView).text =
            "Column " + (mColumns + 1).toString() + " footer"

        val layoutManager = LinearLayoutManager(context)
        val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
            .setLayoutManager(layoutManager)
            .setHasFixedItemSize(false)
            .setColumnBackgroundColor(Color.TRANSPARENT)
            .setItemsSectionBackgroundColor(Color.TRANSPARENT)
            .setHeader(header)
//            .setColumnDragView(header)
            .build()
        mBoardView.addColumn(columnProperties)


        mColumns++
    }

    private class MyColumnDragItem(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        override fun onBindDragView(clickedView: View, dragView: View) {
            val clickedLayout: LinearLayout = clickedView as LinearLayout
            val clickedHeader: View = clickedLayout.getChildAt(0)
            val clickedFooter: View = clickedLayout.getChildAt(2)
            val clickedRecyclerView = clickedLayout.getChildAt(1) as RecyclerView
            val dragHeader = dragView.findViewById<View>(R.id.drag_header)
            val dragFooter = dragView.findViewById<View>(R.id.drag_footer)
            val dragScrollView: ScrollView = dragView.findViewById(R.id.drag_scroll_view)
            val dragLayout: LinearLayout = dragView.findViewById(R.id.drag_list)
            val clickedColumnBackground: Drawable = clickedLayout.getBackground()
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground)
            }
            val clickedRecyclerBackground: Drawable? = clickedRecyclerView.background
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground)
            }
            dragLayout.removeAllViews()
            (dragHeader.findViewById<View>(R.id.text) as TextView).text =
                (clickedHeader.findViewById<View>(R.id.text) as TextView).text

            (dragFooter.findViewById<View>(R.id.text) as TextView).text =
                (clickedFooter.findViewById<View>(R.id.text) as TextView).text
            for (i in 0 until clickedRecyclerView.childCount) {
                val view = View.inflate(dragView.context, R.layout.column_item, null)
                (view.findViewById<View>(R.id.text) as TextView).text =
                    (clickedRecyclerView.getChildAt(i)
                        .findViewById<View>(R.id.text) as TextView).text
                dragLayout.addView(view)
                if (i == 0) {
                    dragScrollView.scrollY = -clickedRecyclerView.getChildAt(i).top
                }
            }
            dragView.pivotY = 0f
            dragView.pivotX = (clickedView.measuredWidth / 2).toFloat()
        }

        override fun onStartDragAnimation(dragView: View) {
            super.onStartDragAnimation(dragView)
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start()
        }

        override fun onEndDragAnimation(dragView: View) {
            super.onEndDragAnimation(dragView)
            dragView.animate().scaleX(1f).scaleY(1f).start()
        }

        init {
            setSnapToTouch(false)
        }
    }

    private class MyDragItem internal constructor(context: Context?, layoutId: Int) :
        DragItem(context, layoutId) {
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            (dragView.findViewById<View>(R.id.text) as TextView).text = text
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.foreground =
                clickedView.resources.getDrawable(R.drawable.back_border_gray)
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight -
                    clickedCard.paddingRight
            val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom -
                    clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim: ObjectAnimator =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = 250
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim: ObjectAnimator =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = 250
            anim.start()
        }
    }

    fun initContentDetail(
        row: Row,
        itemsField: Fields?,
        columnItem: ChoiceItem,
        columnItemSlug: String?,
        featuredImageField: Fields?
    ) {
        val featuredImageFieldSlug = featuredImageField?.slug
        val rendered_data = row.rendered_data
        if (rendered_data != null && rendered_data.containsKey(featuredImageFieldSlug)) {
            val renderedData: RenderedData? = rendered_data.get(featuredImageFieldSlug)
            val imgUrl = renderedData?.value
            if (imgUrl != null && imgUrl.toString().contains(Constants.HREF)) {
                var v = imgUrl.toString()
                v = v.substring(v.indexOf("\"") + 1)
                v = v.substring(0, v.indexOf("\""))
                loadImage(v, content_feature_file)
            } else {
                content_feature_file.setVisibility(View.GONE)
            }
        } else {
            content_feature_file.setVisibility(View.GONE)
        }

        var tagsAdapter = TagsAdapter(object : TagsListener {
            override fun tagCLicked(it: Tag) {


            }

        })

        binding.contentDetailLay.row_tag_rv.apply {
            adapter = tagsAdapter
            layoutManager = StaggeredGridLayoutManager(3,RecyclerView.VERTICAL)
        }
        val rowTags = row.row_tags
        if (rowTags?.isNotEmpty() == true) {
            tagsAdapter.collection = rowTags

        } else {
        }


      val  rowAdapter = RowKanbanAdapter(object : RowListener {
            override fun downloadFile(_renderedData: String) {
//                renderedData = _renderedData
//
//                if (!hasPermissions(this@RowActivity, *PERMISSIONS)) {
//                    reqPermissions(
//                        this@RowActivity,
//                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                        PERMISSION_ALL
//                    )
//
//                } else {
//                    downloadFile(this@RowActivity, _renderedData)
//
//                }


            }

            override fun copySubmitToClipBoard(submit: String) {
                val clipboard = requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("", submit)
                clipboard.setPrimaryClip(clip)
                showMsg(row_lay, getString(R.string.result_txt_copied_to_clipboard))

            }

        })

        binding.contentDetailLay.row_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rowAdapter
        }

        rendered_data?.let {
            if (it.isNotEmpty()) {
              val  rowRenderedList = ArrayList(it.values)

                rowAdapter.collection = rowRenderedList
            }
        }

        binding.contentDetailLay.visible()

    }
}
