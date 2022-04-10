package co.formaloo.formfields.field.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.*
import co.formaloo.model.form.CustomImage
import co.formaloo.formfields.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ProductImagesAlbumAdapter() :
    RecyclerView.Adapter<ProductImagesAlbumAdapter.BtnsViewHolder>() {

    internal var collection: ArrayList<CustomImage> = arrayListOf()

    fun setCollection(list: ArrayList<CustomImage>) {
        collection = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return collection.size

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtnsViewHolder {
        val itemView: View

        itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_image_album_view, parent, false)
        return BtnsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BtnsViewHolder, position: Int) {
        val item = collection[holder.adapterPosition]
        holder.bindItems(item)
    }

    fun removeItem(pos: Int) {
        collection.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)


    }

    class BtnsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), KoinComponent {
        private val baseMethod: BaseMethod by inject()

        var product_imv = itemView.findViewById<ImageView>(R.id.product_imv)

        private val cxt = itemView.context

        fun bindItems(item: CustomImage) {
            val image = item.image
            if (URLUtil.isValidUrl(image)) {
                baseMethod.loadImage(image, product_imv)

            } else {
//                baseMethod.loadImageFromFile(image, product_imv)

            }


        }

    }

}


