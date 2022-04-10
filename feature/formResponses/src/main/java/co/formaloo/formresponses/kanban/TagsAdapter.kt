package co.formaloo.formresponses.kanban

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.parseColor
import co.formaloo.formresponses.R
import org.koin.core.component.KoinComponent
import kotlin.properties.Delegates
import co.formaloo.model.form.tags.Tag

class TagsAdapter(private var listener: TagsListener) :
    RecyclerView.Adapter<TagsAdapter.ViewHolder>(), KoinComponent {

    internal var collection: List<Tag> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_kanban_tag_item, parent, false)

        return ViewHolder(
            itemView
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val btnItem = collection[position]
        holder.bindItems(btnItem, listener)

    }

    override fun getItemCount(): Int {
        return collection.size
    }


    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),KoinComponent {

        var tag_title_txv = itemView.findViewById<TextView>(R.id.tag_title_txv)
        var tag_color_card = itemView.findViewById<CardView>(R.id.tag_color_card)

        fun bindItems(
            it: Tag,
            listener: TagsListener
        ) {

            with(it) {
                tag_title_txv.text = title

                tag_color_card.setCardBackgroundColor(
                    try {
                        if (color != null) {
                            parseColor("#$color")

                        } else {
                            ContextCompat.getColor(tag_color_card.context,R.color.appui_color)
                        }
                    } catch (e: Exception) {
                        ContextCompat.getColor(tag_color_card.context,R.color.appui_color)

                    }
                )

            }

            itemView.setOnClickListener { view ->
//                listener.tagCLicked(it)
            }

        }

    }

}


