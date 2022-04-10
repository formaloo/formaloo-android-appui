package co.formaloo.repository.di

import co.formaloo.remote.di.*
import co.formaloo.repository.FormzRepo
import co.formaloo.repository.FormzRepoImpl
import co.formaloo.repository.board.BoardRepo
import co.formaloo.repository.board.BoardRepoImpl
import co.formaloo.repository.sharedRepo.SharedRepository
import co.formaloo.repository.sharedRepo.SharedRepositoryImpl
import co.formaloo.repository.submit.SubmitRepo
import co.formaloo.repository.submit.SubmitRepoImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val formRepositoryModule = module(override = true) {

    single<BoardRepo>(named(boardsRepoConstants.RepoName)) {
        BoardRepoImpl(
            get(named(remoteBoardsModulConstant.DataSourceName)), get()
        )
    }


    single<SubmitRepo>(named(submitRepoConstants.RepoName)) {
        SubmitRepoImpl(get())
    }

    single<FormzRepo>(named(formAllRepoConstants.RepoName)) {
        FormzRepoImpl(
            get(named(remoteAllFormModulConstant.DataSourceName)),get()
        )
    }

    single<SharedRepository>(named("SharedRepositoryImpl")) { SharedRepositoryImpl(get()) }



}


object boardsRepoConstants {
    val RepoName = "BoardsRepo"

}

object rowRepoConstants {
    val RepoName = "RowsRepo"

}

object submitRepoConstants {
    val RepoName = "SubmitRepo"

}

object formAllRepoConstants {
    val RepoName = "FormzRepo"

}


