package co.formaloo.flashcard.lesson.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.flashcard.databinding.LayoutFlashCardNpsItemBinding

import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.viewmodel.UIViewModel


class NPSHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardNpsItemBinding.bind(view)
    fun bindItems(
        item: co.formaloo.model.form.Fields,
        pos: Int,

        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel,
        lessonListener: LessonListener
    ) {
        binding.field = item
        binding.form = form

        binding.fieldUiHeader.field = item
        binding.fieldUiHeader.form = form
        binding.viewmodel = uiViewModel
        binding.lifecycleOwner = binding.npsRec.context as LifecycleOwner


        binding.npsRec.apply {
            adapter =
                NpsItemAdapter(item, form, uiViewModel,lessonListener)
            layoutManager = GridLayoutManager(context, 11)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.HORIZONTAL))
        }

    }

}
