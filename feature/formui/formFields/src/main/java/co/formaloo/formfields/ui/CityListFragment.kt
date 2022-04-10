package co.formaloo.formfields.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.cityChoices.CityChoicesObject
import co.formaloo.model.form.Fields
import co.formaloo.formfields.databinding.FragmentCityListBinding

class CityListFragment(private val viewmodel: UIViewModel, private val initList:List<CityChoicesObject>, private val field: Fields) : DialogFragment() {

    private lateinit var binding: FragmentCityListBinding
    val dropAdapter = CityListAdapter(viewmodel)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCityListBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.cityChoicesData.observe(this) {
            it?.let {
                it.objects?.let { cities ->
                    dropAdapter.collection = cities
                    dropAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.cityRv.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=dropAdapter
        }

        dropAdapter.collection=initList
        dropAdapter.notifyDataSetChanged()


        binding.citySearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.length > 1 || newText.isEmpty()) {
                        viewmodel.initFieldSlug(field.slug ?: "")
                        viewmodel.initCityQuery(newText)
                        viewmodel.searchCityFieldChoices()
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
