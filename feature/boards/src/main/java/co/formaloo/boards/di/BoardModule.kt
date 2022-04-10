package co.formaloo.boards.di

import co.formaloo.formCommon.vm.BoardsViewModel
import co.formaloo.repository.di.boardsRepoConstants
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val boardModule = module {
    viewModel { BoardsViewModel(get(named(boardsRepoConstants.RepoName))) }

}
