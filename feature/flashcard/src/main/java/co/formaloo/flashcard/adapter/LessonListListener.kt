package co.formaloo.flashcard.adapter

import android.view.View

interface LessonListListener {
    fun openLessonPage(form: co.formaloo.model.form.Form?, formItemLay: View, progress:Int)}
