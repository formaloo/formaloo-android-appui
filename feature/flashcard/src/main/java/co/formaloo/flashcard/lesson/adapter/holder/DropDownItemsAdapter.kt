package co.formaloo.flashcard.lesson.adapter.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import co.formaloo.flashcard.R
import kotlin.properties.Delegates
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class DropDownItemsAdapter(private val form: co.formaloo.model.form.Form) : BaseAdapter() {

    internal var listItemsTxt: List<co.formaloo.model.form.ChoiceItem> by Delegates.observable(emptyList())
    { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.layout_cats, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh

        } else {
            view = convertView
            vh = view.tag as ItemRowHolder

        }
        vh.label.text = listItemsTxt[position].title
        return view
    }

    override fun getItem(position: Int): co.formaloo.model.form.ChoiceItem? {

        return listItemsTxt[position]

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView = row?.findViewById(R.id.title_text) as TextView

    }
}
