package co.formaloo.model.cat

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

import co.formaloo.model.form.folders.ParentFolder
import java.io.Serializable
import kotlin.collections.ArrayList

@Entity
data class Folder(
    @PrimaryKey
    @ColumnInfo(name = "category_slug")
    var slug: String,
    var title: String? = null,
    var color: String? = null,
    var shared_access: String? = null,
    @Embedded(prefix = "parent")
    var parent_category: ParentFolder? =null,
    var subcategories: ArrayList<Folder>? = null

) : Serializable
