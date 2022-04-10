package co.formaloo.model.form.folders

import java.io.Serializable

data class ParentFolder(
    var slug: String? = null,
    var title: String? = null,
    var color: String? = null
) : Serializable
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParentFolder

        if (!slug.equals(other.slug)) return false

        return true

    }


    override fun hashCode(): Int {
        return slug.hashCode()
    }
}
