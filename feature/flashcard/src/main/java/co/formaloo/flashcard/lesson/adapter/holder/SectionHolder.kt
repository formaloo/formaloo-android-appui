package co.formaloo.flashcard.lesson.adapter.holder

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.flashcard.databinding.LayoutFlashCardSectionItemBinding
import co.formaloo.flashcard.lesson.adapter.SwipeStackAdapter
import co.formaloo.flashcard.lesson.listener.SwipeStackListener

import co.formaloo.flashcard.viewmodel.UIViewModel
import link.fls.swipestack.SwipeStack
import co.formaloo.common.extension.invisible
import co.formaloo.common.extension.visible
class SectionHolder(view: View, private val swipeStackListener: SwipeStackListener) :
    RecyclerView.ViewHolder(view) {
    val binding = LayoutFlashCardSectionItemBinding.bind(view)
    fun bindItems(
        item: co.formaloo.model.form.Fields,
        pos: Int,

        form: co.formaloo.model.form.Form,
        uiViewModel: UIViewModel
    ) {
        binding.field = item
        binding.form = form

        binding.lifecycleOwner = binding.swipeStack.context as LifecycleOwner

        // Prepare the View for the animation
        binding.swipeStack.visible()
        binding.swipeStack.alpha = 0.0f

        // Start the animation
        binding.swipeStack.animate()
            .translationY(binding.swipeStack.height.toFloat())
            .alpha(1.0f)
            .setListener(null);

        setSwipeStack(arrayListOf(item), form,  pos)

    }

    private fun setSwipeStack(
        fields: ArrayList<co.formaloo.model.form.Fields>?,
        form: co.formaloo.model.form.Form,
        fieldsPos: Int
    ) {
        val swipeStackAdapter = SwipeStackAdapter(fields ?: arrayListOf(), form)
        binding.swipeStack.adapter = swipeStackAdapter
        swipeStackAdapter.notifyDataSetChanged()

        binding.swipeStack.setListener(object : SwipeStack.SwipeStackListener {
            override fun onViewSwipedToLeft(position: Int) {

            }

            override fun onViewSwipedToRight(position: Int) {

            }

            override fun onStackEmpty() {
                swipeStackListener.onSwipeEnd(fieldsPos)

            }


        })
    }

}
