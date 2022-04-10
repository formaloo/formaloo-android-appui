package co.formaloo.appuisample.di

import co.formaloo.boards.di.boardModule
import co.formaloo.common.BuildConfig.BASE_URL
import co.formaloo.common.BuildConfig.X_API_KEY
import co.formaloo.local.di.formBuilderLocalModule
import co.formaloo.repository.di.formRepositoryModule
import co.formaloo.flashcard.di.featureModule
import co.formaloo.formCommon.di.formVMModule
import co.formaloo.remote.di.*

val appComponent = listOf(
    createRemoteAllFormzModule(BASE_URL, X_API_KEY),
    createRemoteBoardModule(BASE_URL, X_API_KEY),
    formBuilderLocalModule,
    formRepositoryModule,
    boardModule,
    formVMModule,
    featureModule
)
