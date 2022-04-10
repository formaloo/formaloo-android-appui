package co.formaloo.formCommon.vm

import co.formaloo.common.base.BaseViewModel
import co.formaloo.repository.sharedRepo.SharedRepository

class SharedViewModel(private val repository: SharedRepository) : BaseViewModel() {

    fun saveLessonProgress(progress: HashMap<String?, Int?>) {
        repository.saveLessonProgress(progress)

    }

    fun retrieveLessonProgress(): HashMap<String?, Int?> {
        return repository.retrieveLessonProgress()

    }
    fun saveLessonPercentage(progress: HashMap<String?, Int?>) {
        repository.saveLessonPercentage(progress)

    }

    fun retrieveLessonPercentage(): HashMap<String?, Int?> {
        return repository.retrieveLessonPercentage()

    }

    fun saveLastBlock(blockSlug: String) {
        repository.saveLastBlock(blockSlug)
    }

    fun getLastBlock(): String? {
        return repository.getLastBlock()
    }

}
