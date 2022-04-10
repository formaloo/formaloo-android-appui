package co.formaloo.formfields.field.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.formaloo.common.MainBinding.choiceImage
import co.formaloo.common.afterTextChanged
import co.formaloo.model.form.ChoiceItem
import co.formaloo.formfields.R
import co.formaloo.formfields.databinding.LayoutNewChiceItemBinding
import com.woxthebox.draglistview.DragItemAdapter
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.common.extension.isVisible

class ChoicesAdapter(private val listener: ChoiceListener, private val isGroup: Boolean) :
    DragItemAdapter<Pair<Long, ChoiceItem>, ChoicesAdapter.ViewHolder>() {

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first
    }


    override fun getItemCount(): Int {
        return if (mItemList == null) 0
        else mItemList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_new_chice_item, parent, false)
        return ViewHolder(itemView, R.id.move_choice, true)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val btnItem = mItemList[position].second
        holder.bindItems(btnItem, holder, position, this, listener, itemList)
        if (isGroup) {
            holder.binding.choiceImg.invisible()
        } else {
            holder.binding.choiceImg.visible()
        }
    }


    class ViewHolder(
        itemView: View,
        handleResId: Int,
        dragOnLongPress: Boolean
    ) :
        DragItemAdapter.ViewHolder(itemView, handleResId, dragOnLongPress) {

        var _position: Int = -1
        lateinit var _item: ChoiceItem
        lateinit var _listener: ChoiceListener
        lateinit var _adapter: ChoicesAdapter

        val binding = LayoutNewChiceItemBinding.bind(itemView)

        init {
            binding.choiceImg.setOnClickListener {
                _listener.selectImage(this, _adapter)
            }
            binding.removeChoice.setOnClickListener {
                _adapter.removeItemFromList(_position)
            }
            binding.removeImage.setOnClickListener {
                _adapter.removeImageFromItem(_item, this)
            }
        }

        fun bindItems(
            items: ChoiceItem,
            holder: ViewHolder,
            position: Int,
            adapter: ChoicesAdapter,
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
            choiceImage(binding.choiceImg, items.image)
        }

    }

    fun removeImageFromItem(items: ChoiceItem, holder: ViewHolder) {
        itemList[holder.adapterPosition].second.image = null
        holder.binding.choiceImg.setImageResource(R.drawable.ic_pic)
        listener.itemsChanged(itemList)
        holder.binding.removeImage.invisible()

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
        listener.itemsChanged(itemList)
    }

    fun removeItemFromList(pos: Int) {
        removeItem(pos)
        listener.itemsChanged(itemList)

    }

    fun updateChoiceImage(image: String, holder: ViewHolder) {
        itemList[holder.adapterPosition].second.image = image
        holder.binding.removeImage.visible()
    }
}



