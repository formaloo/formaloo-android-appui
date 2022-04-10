package co.formaloo.flashcard.lesson.adapter.holder

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.flashcard.R
import co.formaloo.common.Constants
import co.formaloo.flashcard.databinding.LayoutFlashCardNpsBtnItemBinding
import co.formaloo.flashcard.lesson.listener.LessonListener
import co.formaloo.flashcard.viewmodel.UIViewModel

class NpsItemAdapter(
    private val fields: co.formaloo.model.form.Fields,
    private val form: co.formaloo.model.form.Form,
    private val viewmodel: UIViewModel,
    private val lessonListener: LessonListener
) : RecyclerView.Adapter<NpsItemAdapter.NPSItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NPSItemViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flash_card_nps_btn_item, parent, false);
        return NPSItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NPSItemViewHolder, position_: Int) {
        holder.bindItems(fields, position_, form, viewmodel, lessonListener)

        holder.setIsRecyclable(false)
    }


    override fun getItemCount(): Int {
        return 11
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class NPSItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lastSelectedItem: AppCompatButton? = null
        val binding = LayoutFlashCardNpsBtnItemBinding.bind(itemView)

        fun bindItems(
            field: co.formaloo.model.form.Fields,
            position_: Int,
            form: co.formaloo.model.form.Form,
            viewmodel: UIViewModel,
            lessonListener: LessonListener

        ) {
            binding.field = field
            binding.form = form
            binding.holder = this
            binding.viewmodel = viewmodel
            binding.lifecycleOwner = binding.npsBtn.context as LifecycleOwner

            binding.npsBtn.setOnClickListener {
                viewmodel.npsBtnClicked(field, absoluteAdapterPosition)
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
}


