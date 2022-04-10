package co.formaloo.flashcard.adapter

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallBack : DiffUtil.ItemCallback<co.formaloo.model.form.Form>() {
    override fun areItemsTheSame(oldItem: co.formaloo.model.form.Form, newItem: co.formaloo.model.form.Form): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(oldItem: co.formaloo.model.form.Form, newItem: co.formaloo.model.form.Form): Boolean {
        return oldItem.slug == newItem.slug
                && oldItem.title == newItem.title
                && oldItem.description == newItem.description
                && oldItem.logo == newItem.logo
    }
}
