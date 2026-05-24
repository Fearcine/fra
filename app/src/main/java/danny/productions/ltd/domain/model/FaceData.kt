package danny.productions.ltd.domain.model

data class FaceData(
    val id: String,
    val studentId: String,
    val embedding: FloatArray,
    val angle: FaceAngle,
    val createdAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FaceData) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
