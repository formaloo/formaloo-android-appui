package co.formaloo.flashcard.lesson.adapter.holder

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.common.Constants
import co.formaloo.flashcard.databinding.LayoutFlashCardStarItemBinding

import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.viewmodel.UIViewModel

class StarHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardStarItemBinding.bind(view)
    fun bindItems(
        item: co.formaloo.model.form.Fields,
        pos: Int,

        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel,
        lessonListener: LessonListener
    ) {
        binding.field = item
        binding.form = form
        binding.viewmodel = uiViewModel

        binding.fieldUiHeader.field = item
        binding.fieldUiHeader.form = form


        binding.starRating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            uiViewModel.addKeyValueToReq(item.slug!!, fl)
            Handler(Looper.getMainLooper()).postDelayed({
                lessonListener.next()
            }, Constants.AUTO_NEXT_DELAY)
        }

        if (item.required == true) {
            uiViewModel.reuiredField(item)

        } else {

        }
    }



}
