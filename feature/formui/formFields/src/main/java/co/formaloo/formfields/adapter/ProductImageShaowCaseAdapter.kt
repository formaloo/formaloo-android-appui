package co.formaloo.formfields.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.*
import co.formaloo.model.form.Fields
import co.formaloo.model.form.Image
import co.formaloo.formfields.R
import co.formaloo.formfields.loadCustomImageUrl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class ProductImageShaowCaseAdapter(private val field: Fields,private val listener: ProductChangeListener) :
    RecyclerView.Adapter<ProductImageShaowCaseAdapter.BtnsViewHolder>() {

    internal var collection: ArrayList<Image> = arrayListOf()

    fun setCollection(list: ArrayList<Image>) {
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
                .inflate(R.layout.layout_product_show_case_view, parent, false)
        return BtnsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BtnsViewHolder, position: Int) {
        val item = collection[holder.adapterPosition]
        holder.bindItems(item)

        holder.itemView.setOnClickListener {
            listener.changePhoto(collection[holder.absoluteAdapterPosition])


        }


    }


    class BtnsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), KoinComponent {
        private val baseMethod: BaseMethod by inject()

        var product_imv = itemView.findViewById<ImageView>(R.id.product_imv)


        fun bindItems(item: Image) {
            val image = item.image
            Timber.e("loadCustomImageUrl $image")
            loadCustomImageUrl(product_imv, image)



        }

    }

}


