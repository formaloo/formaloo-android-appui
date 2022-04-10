package co.formaloo.formCommon.base

import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.work.*
import co.formaloo.common.BaseAppCompatActivity
import co.formaloo.formCommon.SubmitWorker

open class FormBaseActivity : BaseAppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this,co.formaloo.common.R.color.colorGlass)
        super.onCreate(savedInstanceState)

        callWorker()

    }



    fun callWorker() {
        val constraint: Constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val submitWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SubmitWorker>()
            .setConstraints(constraint).build()

        val manager = WorkManager.getInstance(this)
        manager.enqueueUniqueWork("Submit", ExistingWorkPolicy.KEEP, submitWorkRequest);


    }


}
