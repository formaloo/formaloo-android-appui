package co.formaloo.formresponses.charts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.BaseMethod
import co.formaloo.formresponses.R
import co.formaloo.common.Constants.MATRIX
import co.formaloo.common.Constants.MULTI_SELECT
import co.formaloo.common.Constants.Table
import co.formaloo.model.form.Fields
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.ArrayList


class StatsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_PIE = 0
    private val TYPE_BAR = 1
    private val TYPE_BAR_Set = 2


    private var items= arrayListOf<Fields>()

    fun setCollection(items:ArrayList<Fields>) {
        this.items = items
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView: View
        when (viewType) {
            TYPE_PIE -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_bar, parent, false);
                return PieHolder(itemView)
            }
            TYPE_BAR -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_bar, parent, false);
                return BarVHolder(itemView)
            }
            TYPE_BAR_Set -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_bar, parent, false);
                return BarSetHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_bar, parent, false);
                return PieHolder(itemView)

            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position_: Int) {
        val items = items[position_]
        when (getItemViewType(position_)) {
            TYPE_BAR -> {
                (holder as BarVHolder).bindItems(items)

            }
            TYPE_BAR_Set -> {
                (holder as BarSetHolder).bindItems(items)

            }
            TYPE_PIE -> {
                (holder as PieHolder).bindItems(items)

            }
            else -> {
                (holder as PieHolder).bindItems(items)

            }
        }

        holder.setIsRecyclable(false)

    }

    companion object {
        const val TYPE = "type"
        const val SUB_TYPE = "sub_type"
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]

        return when (item.type) {
            MATRIX -> {
                TYPE_BAR_Set
            }
            Table -> {
                TYPE_BAR_Set
            }
            MULTI_SELECT -> {
                TYPE_BAR
            }
            else -> {
                TYPE_PIE
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    open class BaseChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        KoinComponent {
        val baseMethod: BaseMethod by inject()

    }


}


