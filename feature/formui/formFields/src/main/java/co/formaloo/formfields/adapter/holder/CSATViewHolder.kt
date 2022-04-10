package co.formaloo.formfields.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.formaloo.common.extension.invisible
import co.formaloo.formCommon.vm.UIViewModel
import co.formaloo.model.form.Fields
import co.formaloo.formfields.*
import co.formaloo.formfields.adapter.CSATAdapter
import co.formaloo.formfields.adapter.CSATListener
import co.formaloo.formfields.databinding.LayoutUiCsatItemBinding
import kotlinx.android.synthetic.main.layout_ui_field_footer.view.*
import kotlinx.android.synthetic.main.layout_ui_field_header.view.*

class CSATViewHolder(itemView: View) : BaseVH(itemView) {

    val binding = LayoutUiCsatItemBinding.bind(itemView)

    override fun initView(

    ) {
        val fieldUiHeader = binding.csatLay
        fillHeaderData(fieldUiHeader.key_txv,fieldUiHeader.desc_txv)

        val context = binding.starlay.context


        errStatus.observe(lifeCycleOwner){
            fieldBackgroundUI(binding.starlay,it)
            if (it == true){
                displayErrLay(binding.errLay, binding.errLay.error_txv)

            }
        }

        var selectedItem: Int? = null
        fieldRenderedData?.value?.let {
            if (it is Double) {
                selectedItem = it.toInt()

            }

        }

        setUpCSATIconList(field, viewmodel, selectedItem, fromEdit)


        viewmodel.resetData.observe(context as LifecycleOwner) {
            it?.let {
                if (it) {
                    setUpCSATIconList(field, viewmodel, null, fromEdit)

                } else {

                }
            }
        }

        if (fromEdit) {
//            binding.csatIconRes.isLayoutFrozen = true
            binding.csatIconRes.suppressLayout(true)
        } else {

        }



    }

    private fun setUpCSATIconList(
        field: Fields,
        viewmodel: UIViewModel,
        selectedItem: Int?,
        fromEdit: Boolean
    ) {
        val list = when (field.thumbnail_type) {
            CSATThumbnailType.flat_face.name -> {

                arrayListOf(
                    CSATFlat.ANGRY,
                    CSATFlat.SAD,
                    CSATFlat.NEUTRAL,
                    CSATFlat.SMILE,
                    CSATFlat.LOVE
                )
            }
            CSATThumbnailType.funny_face.name -> {
                arrayListOf(
                    CSATFunny.ANGRY,
                    CSATFunny.SAD,
                    CSATFunny.NEUTRAL,
                    CSATFunny.SMILE,
                    CSATFunny.LOVE
                )

            }
            CSATThumbnailType.outlined.name -> {
                arrayListOf(
                    CSATOutline.ANGRY,
                    CSATOutline.SAD,
                    CSATOutline.NEUTRAL,
                    CSATOutline.SMILE,
                    CSATOutline.LOVE
                )
            }
            CSATThumbnailType.heart.name -> {
                arrayListOf(
                    CSATHEART.ANGRY,
                    CSATHEART.SAD,
                    CSATHEART.NEUTRAL,
                    CSATHEART.SMILE,
                    CSATHEART.LOVE
                )

            }
            CSATThumbnailType.monster.name -> {
                arrayListOf(
                    CSATMonster.ANGRY,
                    CSATMonster.SAD,
                    CSATMonster.NEUTRAL,
                    CSATMonster.SMILE,
                    CSATMonster.LOVE
                )
            }
            CSATThumbnailType.star.name -> {
                arrayListOf(
                    CSATSTAR.ANGRY,
                    CSATSTAR.SAD,
                    CSATSTAR.NEUTRAL,
                    CSATSTAR.SMILE,
                    CSATSTAR.LOVE
                )
            }

            else -> {
                binding.csatIconRes.invisible()
                arrayListOf()

            }
        }

        binding.csatIconRes.apply {
            layoutManager = StaggeredGridLayoutManager(5, RecyclerView.VERTICAL)
            adapter = CSATAdapter(
                list,
                (selectedItem ?: 0) - 1,
                object : CSATListener {
                    override fun csatSelected(pos: Int) {
                        viewmodel.addKeyValueToReq(field.slug!!, pos)
                        hideErr(binding.errLay, viewmodel)
                    }

                },
                fromEdit
            )
        }


    }



}
