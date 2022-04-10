package co.formaloo.flashcard.lesson.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import co.formaloo.flashcard.databinding.LayoutStackSectionItemBinding

class StackHolder(view: View) {
    val binding = LayoutStackSectionItemBinding.bind(view)
    fun bindItems(item: co.formaloo.model.form.Fields, pos: Int, form: co.formaloo.model.form.Form) {
        binding.field = item
        binding.form = form
        binding.lifecycleOwner = binding.keyTxv.context as LifecycleOwner


    }


}
