package co.formaloo.formfields.field.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.formaloo.common.afterTextChanged
import co.formaloo.model.form.ChoiceItem
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutNewColumnItemBinding
import com.woxthebox.draglistview.DragItemAdapter


class CulomnsAdapter(private val listener: ChoiceListener, private val isGroup: Boolean) :
    DragItemAdapter<Pair<Long, ChoiceItem>, CulomnsAdapter.ViewHolder>() {

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first
    }


    override fun getItemCount(): Int {
        return if (mItemList == null) 0
        else mItemList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_new_column_item, parent, false)
        return ViewHolder(itemView, R.id.move_choice, true)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val btnItem = mItemList[position].second
        holder.bindItems(btnItem, holder, position, this, listener, itemList)


    }


    class ViewHolder(
        itemView: View,
        handleResId: Int,
        dragOnLongPress: Boolean
    ) :
        DragItemAdapter.ViewHolder(itemView, handleResId, dragOnLongPress) {
        val context = itemView.context

        var _position: Int = -1
        lateinit var _item: ChoiceItem
        lateinit var _listener: ChoiceListener
        lateinit var _adapter: CulomnsAdapter

        val binding = LayoutNewColumnItemBinding.bind(itemView)


        fun bindItems(
            items: ChoiceItem,
            holder: ViewHolder,
            position: Int,
            adapter: CulomnsAdapter,
            listener: ChoiceListener,
            mItemList: MutableList<Pair<Long, ChoiceItem>>
        ) {
            _item = items
            _listener = listener
            _adapter = adapter
            _position = position

            itemView.tag = mItemList.get(position)
            binding.fieldNewChoiceEdt.setText(items.title)
            binding.fieldNewChoiceEdt.afterTextChanged {
                _item.title = it
            }


            binding.rbTypeNumber.isChecked =
                _item.type == context.getString(R.string.choice_type_number)
            binding.rbTypeBoolean.isChecked =
                _item.type == context.getString(R.string.choice_type_boolean)
            binding.rbTypeText.isChecked =
                _item.type == context.getString(R.string.choice_type_text)

            binding.removeChoice.setOnClickListener {
                _adapter.removeItemFromList(_position)
            }


        }


    }


    fun insertItem(v: View) {
        val pair = Pair((itemCount).toLong(), ChoiceItem(null, null, null, null))
        if (itemList == null) {
            itemList = listOf(pair)
        } else {
            val arrayList = ArrayList(itemList)
            arrayList.add(pair)
            itemList = arrayList

        }
        notifyItemInserted(itemCount)
        listener.itemsChanged(mItemList)

    }

    fun removeItemFromList(pos: Int) {
        removeItem(pos)
        listener.itemsChanged(itemList)

    }


}



