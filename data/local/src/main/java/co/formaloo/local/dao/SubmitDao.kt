package co.formaloo.local.dao

import androidx.room.Dao
import androidx.room.Query
import co.formaloo.model.form.SubmitEntity

@Dao
abstract class SubmitDao : AppUIBaseDao<SubmitEntity>() {

    @Query("SELECT * FROM submit")
    abstract suspend fun getSubmitEntityList(): List<SubmitEntity>

    @Query("SELECT * FROM submit WHERE formSlug = :formSlug")
    abstract suspend fun getSubmitEntity(formSlug: String): SubmitEntity

    @Query("DELETE FROM submit WHERE uniqueId = :uniqueId")
    abstract suspend fun deleteSubmitEntity(uniqueId: Int)

    // ---
    @Query("DELETE FROM submit")
    abstract suspend fun deleteAllFromTable()

    suspend fun save(SubmitEntity: SubmitEntity) {
        insert(SubmitEntity)
    }

    suspend fun save(SubmitEntitys: List<SubmitEntity>) {
        insert(SubmitEntitys)
    }
}
