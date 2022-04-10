package co.formaloo.formCommon.di

import co.formaloo.formCommon.vm.*
import co.formaloo.repository.di.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val formVMModule = module {
    viewModel() { UIViewModel(get(named(submitRepoConstants.RepoName)),get(named(formAllRepoConstants.RepoName))) }
    viewModel { ResponsesViewModel(get(named(boardsRepoConstants.RepoName))) }
}
