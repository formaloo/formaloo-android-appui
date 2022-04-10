package co.formaloo.repository.sharedRepo

import android.content.SharedPreferences
import co.formaloo.common.Constants
import org.json.JSONObject

interface SharedRepository {
    fun saveLessonProgress(progress: Map<String?, Int?>)
    fun retrieveLessonProgress(): HashMap<String?, Int?>
    fun saveLessonPercentage(progress: Map<String?, Int?>)
    fun retrieveLessonPercentage(): HashMap<String?, Int?>
    fun saveLastLesson(formSlug: String?)
    fun getLastLesson(): String?
    fun saveLastBlock(blockSlug: String?)
    fun getLastBlock(): String?
}


class SharedRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SharedRepository {


    override fun saveLessonProgress(progress: Map<String?, Int?>) {
        val jsonObject = JSONObject(progress)
        val lessonsProgressString = jsonObject.toString()
        sharedPreferences.edit().putString(Constants.PREFERENCES_PROGRESS, lessonsProgressString)
            .apply()
    }

    override fun retrieveLessonProgress(): HashMap<String?, Int?> {
        val outputMap = hashMapOf<String?, Int?>()

        sharedPreferences.getString(Constants.PREFERENCES_PROGRESS, null)?.let {
            val jsonObject = JSONObject(it)
            val keysItr = jsonObject.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = jsonObject[key] as Int?
                outputMap[key] = value
            }
        }


        return outputMap
    }


    override fun saveLessonPercentage(progress: Map<String?, Int?>) {
        val jsonObject = JSONObject(progress)
        val lessonsProgressString = jsonObject.toString()
        sharedPreferences.edit().putString(Constants.PREFERENCES_PROGRESS_VALUE, lessonsProgressString)
            .apply()
    }

    override fun retrieveLessonPercentage(): HashMap<String?, Int?> {
        val outputMap = hashMapOf<String?, Int?>()

        sharedPreferences.getString(Constants.PREFERENCES_PROGRESS_VALUE, null)?.let {
            val jsonObject = JSONObject(it)
            val keysItr = jsonObject.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                val value = jsonObject[key] as Int?
                outputMap[key] = value
            }
        }


        return outputMap
    }

    override fun saveLastLesson(formSlug: String?) {
        sharedPreferences.edit().putString(Constants.PREFERENCES_LAST_Lesson, formSlug).apply()

    }


    override fun getLastLesson(): String? {
        return sharedPreferences.getString(Constants.PREFERENCES_LAST_Lesson, "")
    }

    override fun saveLastBlock(blockSlug: String?) {
        sharedPreferences.edit().putString(Constants.PREFERENCES_LAST_BLOCK, blockSlug).apply()

    }


    override fun getLastBlock(): String? {
        return sharedPreferences.getString(Constants.PREFERENCES_LAST_BLOCK, "")
    }

}
