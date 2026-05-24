package danny.productions.ltd.domain.usecase.student

import danny.productions.ltd.domain.repository.FaceRepository
import danny.productions.ltd.domain.repository.StudentRepository

class RemoveStudentUseCase(
    private val studentRepository: StudentRepository,
    private val faceRepository: FaceRepository
) {

    suspend operator fun invoke(studentId: String): Result<Unit> {
        faceRepository.deleteByStudentId(studentId)
        return studentRepository.delete(studentId)
    }
}
