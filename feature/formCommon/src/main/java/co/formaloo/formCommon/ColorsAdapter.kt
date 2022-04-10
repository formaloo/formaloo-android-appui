package co.formaloo.formCommon

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.BaseMethod
import co.formaloo.formCommon.listener.ColorListener
import com.google.android.material.imageview.ShapeableImageView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import kotlin.properties.Delegates


class ColorsAdapter(private val colorListener: ColorListener) :
    RecyclerView.Adapter<ColorsAdapter.ViewHolder>(), KoinComponent {
    val baseMethod: BaseMethod by inject()

    var selectedColor = "3394CC"

    public var collection: List<String> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_color_item, parent, false)

        return ViewHolder(
            itemView
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = collection[position]

        val background = holder.color_imv.background
        if (background is ShapeDrawable) {
            // cast to 'ShapeDrawable'
            background.paint.color = baseMethod.parseColor("#$item")
        } else if (background is GradientDrawable) {
            // cast to 'ShapeDrawable'
            background.setColor(baseMethod.parseColor("#$item"))
        } else if (background is ColorDrawable) {
            // cast to 'ShapeDrawable'
            background.setColor(baseMethod.parseColor("#$item"))
        }

        holder.color_imv.background = background

        selectedColor?.let {
            Timber.e("onBindViewHolder $it")
            if (item == selectedColor) {
                lastSelectedItem = holder.color_check_imv

                holder.color_check_imv.visible()
            }
        }

        holder.itemView.setOnClickListener {
            lastSelectedItem?.invisible()
            lastSelectedItem = holder.color_check_imv
            holder.color_check_imv.visible()
            colorListener.colorSelected(collection[holder.adapterPosition])

        }
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    private var lastSelectedItem: ImageView? = null

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var color_imv = itemView.findViewById<ShapeableImageView>(R.id.color_imv)
        var color_check_imv = itemView.findViewById<ImageView>(R.id.color_check_imv)


    }

}


