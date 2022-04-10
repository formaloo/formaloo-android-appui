package co.formaloo.formresponses.kanban

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.BaseMethod
import co.formaloo.common.Constants
import co.formaloo.common.Constants.DROPDOWN
import co.formaloo.common.Constants.FILE
import co.formaloo.common.Constants.LOCATION
import co.formaloo.common.Constants.MATRIX
import co.formaloo.common.Constants.MULTI_SELECT
import co.formaloo.common.Constants.SIGNATURE
import co.formaloo.common.Constants.SINGLE_SELECT
import co.formaloo.model.form.ChoiceItem
import co.formaloo.model.submit.RenderedData
import co.formaloo.formresponses.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates


class RowKanbanAdapter(private val listener: RowListener) :
    RecyclerView.Adapter<RowKanbanAdapter.BtnsViewHolder>() {

    private var mPageNumber: Int = 1

    internal var collection: List<RenderedData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    fun setPageNUmber(pageNumber: Int) {
        mPageNumber = pageNumber
    }

    override fun getItemCount(): Int {
        return if (collection != null) {
            collection.size
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BtnsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_rows_kanban, parent, false)
        return BtnsViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: BtnsViewHolder, position: Int) {
        val btnItem = collection[position]
        holder.bindItems(btnItem, position, listener, mPageNumber)
        holder.setIsRecyclable(false)

    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    class BtnsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), KoinComponent {


        private val cxt = itemView.context
        private val baseMethod: BaseMethod by inject()

        var rows_question_txt = itemView.findViewById<TextView>(R.id.rows_question_txt)
        var row_container = itemView.findViewById<LinearLayout>(R.id.row_container)

        fun bindItems(
            item: RenderedData,
            position: Int,
            listener: RowListener,
            mPageNumber: Int
        ) {
            with(item) {

                createAnswer(row_container, this, listener)

                rows_question_txt.text = this.title
//                if (mPageNumber > 1) {
//                    if (position == 9) {
//                        rows_number.text = " " + mPageNumber + 0
//
//                    } else {
//                        rows_number.text = " " + (mPageNumber - 1) + (position + 1)
//
//                    }
//
//                } else {
//                    rows_number.text = " " + (position + 1)
//
//                }
//
//                val ctx = row_currency_txt.context
//
//
//                item?.delta_score?.currency?.let { currency ->
//                    row_currency_txt.text = "$currency"
//
//                }
//
//                item?.delta_score?.grade?.let { grade ->
//                    row_garde_label.text = "$grade"
//
//                }

            }

        }

        private fun createAnswer(
            rowContainer: LinearLayout,
            renderedData: RenderedData,
            listener: RowListener
        ) {

            when (renderedData.type) {
                LOCATION -> {
                    createLocationView(renderedData, rowContainer, listener)

                }
                MATRIX -> {
                    createMatrixView(renderedData, rowContainer, listener)

                }
                DROPDOWN -> {
                    createOneChoiceView(renderedData, rowContainer, listener)

                }
                MULTI_SELECT -> {
                    createMultiChoiceView(renderedData, rowContainer, listener)

                }
                FILE -> {
                    createFileView(renderedData, rowContainer, listener)
                }
                SIGNATURE -> {
                    createFileView(renderedData, rowContainer, listener)
                }
                SINGLE_SELECT -> {
                    createOneChoiceView(renderedData, rowContainer, listener)

                }
                else -> {
                    renderedData.value?.let {
                        when (it) {
                            is String -> {
                                createTxtView(it, row_container, listener)
                            }
                            is Int -> {
                                var title =
                                    BigDecimal(it.toString()).stripTrailingZeros().toPlainString()
                                title = baseMethod.formatter(title)?.toString() ?: title
                                createTxtView(title, row_container, listener)

                            }
                            is Double -> {
                                var title =
                                    BigDecimal(it.toString()).stripTrailingZeros().toPlainString()
                                title = baseMethod.formatter(title)?.toString() ?: title
                                createTxtView(title, row_container, listener)

                            }
                            is Long -> {
                                var title =
                                    BigDecimal(it.toString()).stripTrailingZeros().toPlainString()
                                title = baseMethod.formatter(title)?.toString() ?: title
                                createTxtView(title, row_container, listener)

                            }
                            is Float -> {
                                var title =
                                    BigDecimal(it.toString()).stripTrailingZeros().toPlainString()
                                title = baseMethod.formatter(title)?.toString() ?: title
                                createTxtView(title, row_container, listener)

                            }
                        }
                    }
                }
            }

        }

        private fun createFileView(
            renderedData: RenderedData,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val context = rowContainer.context
            val value = renderedData.value
            if (value is String && value.contains(Constants.HREF)) {
                var v = value
                v = v.substring(v.indexOf("\"") + 1)
                v = v.substring(0, v.indexOf("\""))
                if (v.contains(Constants.HTTP)) {
                    val link = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(value)
                    }


                    val layout = LinearLayout(context)
                    layout.orientation = LinearLayout.VERTICAL


                    val txv = createTxv(context, link.toString(), listener)

                    txv.setText(link.toString())
                    txv.setTextColor(ContextCompat.getColor(context, R.color.colorButton))

                    if (renderedData.file_type == Constants.image || renderedData.type == Constants.SIGNATURE) {
                        val imgv = setImage(v, context)
                        layout.addView(imgv)

                    }

                    layout.addView(txv)
                    val outValue = TypedValue();
                    context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground,
                        outValue,
                        true
                    );
                    txv.setBackgroundResource(outValue.resourceId)
                    txv.setOnClickListener {
                        listener.downloadFile(v)
                    }

                    rowContainer.addView(layout)
                }
            }


        }

        private fun createLocationView(
            renderedData: RenderedData,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val context = rowContainer.context
            val value = renderedData.value
            val raw_value = renderedData.raw_value


            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            if (value is String && value.contains(Constants.HREF)) {
                var v = value
                v = v.substring(v.indexOf("\"") + 1)
                v = v.substring(0, v.indexOf("\""))
                if (v.contains(Constants.HTTP)) {
                    val link = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(value)
                    }


                    val txv = createTxv(context, link.toString(), listener)

                    txv.setText(link.toString())
                    txv.setTextColor(ContextCompat.getColor(context, R.color.colorButton))

                    layout.addView(txv)

                    val outValue = TypedValue();
                    context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground,
                        outValue,
                        true
                    );
                    txv.setBackgroundResource(outValue.resourceId)
                    txv.setOnClickListener {
                        baseMethod.openUri(it.context, v)
                    }

                }
            }
//The Google Maps Platform server rejected your request.
// You must enable Billing on the Google Cloud Project at https://console.cloud.google.com/project/_/billing/enable
// Learn more at https://developers.google.com/maps/gmp-get-started
//            if (raw_value != null) {
//                val rawValueMap = JSONObject(raw_value.toString())
//
//                Timber.e("rawValueMap $rawValueMap")
//                var lat: Double? = null
//                var long: Double? = null
//                if (rawValueMap.has("lat")) {
//                    lat = rawValueMap["lat"] as Double
//                } else {
//                }
//
//                if (rawValueMap.has("long")) {
//                    long = rawValueMap["long"] as Double
//                } else {
//
//                }
//
//                val address = "https://maps.googleapis.com/maps/api/staticmap?" +
//                        "center=$lat,$long" +
//                        "&zoom=12" +
//                        "&size=400x400" +
//                        "&key=${context.getString(R.string.mapstaticSecretKey)}"
//
//                Timber.e("address $address")
//
//                if (lat != null && long != null) {
//
//                    val imgv = setImage(address, context)
//                    layout.addView(imgv)
//
//                } else {
//
//                }
//
//            }


            rowContainer.addView(layout)

        }

        private fun createMatrixView(
            renderedData: RenderedData,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val groups = renderedData.choice_groups
            val choices = renderedData.choice_items
            val rawValue = renderedData.raw_value

            if (groups != null && rawValue != null && choices != null) {
                if (rawValue is Map<*, *>) {
                    val rawValueMap = rawValue as Map<String, String>
                    rawValueMap.keys.forEach { groupSlug ->
                        rawValueMap[groupSlug]?.let { choiseSlug ->
                            val choice = choices.find {
                                it.slug == choiseSlug
                            }
                            val group = groups.find {
                                it.slug == groupSlug
                            }
                            if (choice != null && group != null) {
                                createChoiceGroupView(choice, group, rowContainer, listener)
                            }
                        }

                    }

                }
            }

        }

        private fun createOneChoiceView(
            renderedData: RenderedData,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val choices = renderedData.choice_items
            val rawValue = renderedData.raw_value

            if (rawValue != null && choices != null) {
                if (rawValue is String) {
                    val choice = choices.find {
                        it.slug == rawValue
                    }
                    if (choice != null) {
                        createChoiceView(choice, rowContainer, listener)
                    }

                }
            }

        }

        private fun createMultiChoiceView(
            renderedData: RenderedData,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val choices = renderedData.choice_items
            val rawValue = renderedData.raw_value

            if (rawValue != null && choices != null) {
                if (rawValue is ArrayList<*>) {
                    val rawValueList = rawValue as ArrayList<String>

                    rawValueList.forEach { choiceSlug ->
                        val choice = choices.find {
                            it.slug == choiceSlug
                        }
                        if (choice != null) {
                            createChoiceView(choice, rowContainer, listener)
                        }
                    }


                }
            }

        }

        private fun createChoiceGroupView(
            choice: ChoiceItem,
            group: ChoiceItem,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val context = rowContainer.context

            val groupView = createTxv(context, group.title, listener)

            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.HORIZONTAL


            val txv = createTxv(context, choice.title, listener)

            if (choice.image != null) {
                val imgv = setImage(choice.image!!, context)
                layout.addView(imgv)

            }
            layout.addView(txv)

            rowContainer.addView(groupView)
            rowContainer.addView(layout)

        }

        private fun createTxv(context: Context, title: String?, listener: RowListener): TextView {
            return TextView(context).apply {
                val layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                layoutParams.bottomMargin = 30

                gravity = Gravity.CENTER_VERTICAL

                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.font_large)
                )


                setLineSpacing(0f, 1.33f)
                setTextColor(ContextCompat.getColor(context, R.color.colorBlack))

                textAlignment = View.TEXT_ALIGNMENT_VIEW_START

                title?.let {
                    text = title

                    setOnLongClickListener {
                        listener.copySubmitToClipBoard(title)
                        true
                    }
                }

            }

        }

        private fun createChoiceView(
            choice: ChoiceItem,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val context = rowContainer.context

            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.HORIZONTAL

            val txv = createTxv(context, choice.title, listener)

            choice.image?.let {
                val imgv = setImage(it, context)
                layout.addView(imgv)

            }

            layout.addView(txv)

            rowContainer.addView(layout)

        }

        private fun createTxtView(
            value: String,
            rowContainer: LinearLayout,
            listener: RowListener
        ) {
            val context = rowContainer.context

            val view = createTxv(context, value, listener)

            rowContainer.addView(view)


        }

        private fun setImage(it: String, context: Context): ImageView {
            return ImageView(context).apply {
                val size = context.resources.getDimensionPixelSize(R.dimen.amount_card)
                val layoutParam = RelativeLayout.LayoutParams(size, size)
                layoutParam.marginEnd = 16
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = layoutParam

                Picasso.get().load(it).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        bitmap?.let {
                            setImageDrawable(BitmapDrawable(bitmap))

                        }
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        Log.e("TAG", "onBitmapFailed: $e")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                })

            }

        }

    }

}


