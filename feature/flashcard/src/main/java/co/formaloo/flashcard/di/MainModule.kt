package co.formaloo.flashcard.di

import co.formaloo.formCommon.vm.SharedViewModel
import co.formaloo.flashcard.viewmodel.UIViewModel
import co.formaloo.repository.di.formAllRepoConstants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureModule = module {
    viewModel { UIViewModel(get(named(formAllRepoConstants.RepoName))) }
    viewModel { SharedViewModel(get(named("SharedRepositoryImpl"))) }

}
