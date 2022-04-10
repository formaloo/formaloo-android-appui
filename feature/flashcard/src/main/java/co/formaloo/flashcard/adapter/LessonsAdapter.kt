package co.formaloo.flashcard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import co.formaloo.flashcard.R
import co.formaloo.flashcard.databinding.LessonContentBinding
import org.koin.core.component.KoinComponent
import java.io.Serializable


class LessonsAdapter(
    progressMap: HashMap<String?, Int?>?,
    private val listener: LessonListListener
) : PagingDataAdapter<co.formaloo.model.form.Form, LessonsAdapter.ViewHolder>(DiffUtilCallBack()), Serializable {

    private var formsProgressMap: HashMap<String?, Int?>? = progressMap
    private var lastOpenPos = 0

    fun resetProgress(formsProgressMap: HashMap<String?, Int?>?, openedForm: co.formaloo.model.form.Form?) {
        this.formsProgressMap = formsProgressMap
        val items = this.snapshot().items
        items.find {
            it.slug == openedForm?.slug
        }?.let {
            val index = items.indexOf(it)
            notifyItemChanged(index)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.lesson_content, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindItems(item!!, listener, (formsProgressMap ?: hashMapOf())[item?.slug!!])

    }

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), Serializable, KoinComponent {
        val binding = LessonContentBinding.bind(itemView)

        fun bindItems(
            form: co.formaloo.model.form.Form?,
            listener: LessonListListener,
            progress: Int?
        ) {
            binding.item = form
            binding.progress = progress ?: 0
            binding.done = progress ?: 0 == -1
            binding.listener = listener

        }
    }

}


