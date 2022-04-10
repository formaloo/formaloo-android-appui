package co.formaloo.formCommon

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.formaloo.repository.FormzRepo
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import timber.log.Timber

class SubmitWorker(
    context: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            Timber.e("doWork")
            val repo: FormzRepo = get(named("FormzRepo"))
            repo.sendSavedSubmitToServer()

            Result.success()

        } catch (ex: Exception) {
            Timber.e(ex, "Error ")
            Result.failure()
        }
    }


    companion object {
        private const val TAG = "SubmitWorker"
    }
}
