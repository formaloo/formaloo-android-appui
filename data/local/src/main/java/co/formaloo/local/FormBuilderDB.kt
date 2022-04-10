package co.formaloo.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.formaloo.local.convertor.FormConverters
import co.formaloo.local.dao.*
import co.formaloo.model.boards.block.Block
import co.formaloo.model.form.SubmitEntity


@Database(
    entities = [SubmitEntity::class, Block::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(FormConverters::class)
abstract class FormBuilderDB : RoomDatabase() {
    // DAO
    abstract fun submitDao(): SubmitDao
    abstract fun blockDao(): BlockDao

    companion object {

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FormBuilderDB::class.java,
                "AppUI.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
