package co.formaloo.local.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.formaloo.common.BaseMethod
import co.formaloo.local.FormBuilderDB
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val formBuilderLocalModule = module {
    single() { FormBuilderDB.buildDatabase(androidContext()) }
    factory { (get() as FormBuilderDB).submitDao() }
    factory { (get() as FormBuilderDB).blockDao() }
    single {
        provideSharePreferences(androidApplication())
    }
    single { BaseMethod() }

}
private fun provideSharePreferences(app: Application): SharedPreferences =
    app.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
