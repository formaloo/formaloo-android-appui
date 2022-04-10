package co.formaloo.flashcard.lesson.listener


interface LessonListener {
    fun closePage()
    fun share()
    fun next()
    fun pre()
    fun openFullScreen(field: co.formaloo.model.form.Fields, link: String)
}
