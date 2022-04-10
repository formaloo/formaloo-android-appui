package co.formaloo.formfields.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.cityChoices.CityChoicesObject
import co.formaloo.formfields.R
import co.formaloo.formCommon.databinding.LayoutCatsBinding


class CityListAdapter(private val viewmodel: UIViewModel) :
    RecyclerView.Adapter<CityListAdapter.BtnsViewHolder>() {

    internal var collection: List<CityChoicesObject> = arrayListOf()
    override fun getItemCount(): Int {
        return collection.size

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BtnsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_cats, parent, false)
        return BtnsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BtnsViewHolder, position: Int) {
        val btnItem = collection[position]
        holder.bindItems(btnItem, position, viewmodel)
        holder.itemView.setOnClickListener {
            viewmodel.updateSelectedCity(btnItem)
        }
    }


    class BtnsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCatsBinding.bind(itemView)

        fun bindItems(
            items: CityChoicesObject,
            position: Int,
            viewModel: UIViewModel
        ) {
            binding.titleText.text = items.title

        }

    }

}


