package co.formaloo.flashcard.lesson.adapter.holder

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.Constants
import co.formaloo.flashcard.databinding.LayoutFlashCardLikeDislikeItemBinding

import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.viewmodel.UIViewModel
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class LikeDislikeHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardLikeDislikeItemBinding.bind(view)
    fun bindItems(
        field: co.formaloo.model.form.Fields,
        pos: Int,

        form: co.formaloo.model.form.Form,
        viewmodel: UIViewModel,
        lessonListener: LessonListener
    ) {
        binding.field = field
        binding.form = form
        binding.fieldUiHeader.field = field
        binding.fieldUiHeader.form = form

        binding.viewmodel = viewmodel
        binding.lifecycleOwner = binding.dislikeBtn.context as LifecycleOwner

        binding.dislikeBtn.setOnClickListener {
            it.isSelected = true
            binding.likeBtn.isSelected = false
            viewmodel.addKeyValueToReq(field.slug?:"",-1)
            Handler(Looper.getMainLooper()).postDelayed({
                lessonListener.next()
            }, Constants.AUTO_NEXT_DELAY)
        }

        binding.likeBtn.setOnClickListener {
            it.isSelected = true
            binding.dislikeBtn.isSelected = false

            viewmodel.addKeyValueToReq(field.slug?:"",1)

            Handler(Looper.getMainLooper()).postDelayed({
                lessonListener.next()
            }, Constants.AUTO_NEXT_DELAY)
        }


        if (field.required == true) {
            viewmodel.reuiredField(field)

        } else {

        }
    }


}
