package co.formaloo.formfields.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.cityChoices.CityChoicesObject
import co.formaloo.model.form.Fields
import co.formaloo.formfields.R
import timber.log.Timber
import kotlin.properties.Delegates

class CityItemsAdapter(private val viewmodel: UIViewModel, private val fields: Fields) :
    BaseAdapter() {

    private val _dialogOpen = MutableLiveData<Boolean>().apply { value = null }
    val dialogOpen: LiveData<Boolean> = _dialogOpen


    internal var listItemsTxt: List<CityChoicesObject> by Delegates.observable(emptyList())
    { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val itemVH: ItemRowHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.layout_cats, parent, false)
            itemVH = ItemRowHolder(view,viewmodel,fields)
            view?.tag = itemVH
            itemVH.label.text = listItemsTxt[position].title

        } else {
            view = convertView
            itemVH = view.tag as ItemRowHolder


        }
        dialogOpen.observe(view.context as LifecycleOwner){
            it?.let {
                if (it && position==0){
                    itemVH.sv.visible()

                }else{
                    itemVH.sv.invisible()

                }
            }
        }

        return view
    }

    override fun getItem(position: Int): CityChoicesObject? {

        return listItemsTxt[position]

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    fun dialogOpened(dialogOpen_: Boolean) {
        Timber.e("dialogOpen_ ${dialogOpen_}")

        _dialogOpen.value=dialogOpen_
    }

    private class ItemRowHolder(row: View?, viewmodel: UIViewModel, field: Fields) {

        val label: TextView = row?.findViewById(R.id.title_text) as TextView

        val sv: SearchView = row?.findViewById(R.id.city_search_view) as SearchView

        init {

            sv.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (newText.length > 1 || newText.isEmpty()) {
                            viewmodel.initFieldSlug(field.slug ?: "")
                            viewmodel.initCityQuery(newText)
                            viewmodel.getCityFieldChoices()
                        }
                    }

                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {

                    }

                    return true
                }

            })
        }

    }


}
