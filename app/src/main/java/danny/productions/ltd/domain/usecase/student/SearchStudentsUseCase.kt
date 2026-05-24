package danny.productions.ltd.domain.usecase.student

import danny.productions.ltd.domain.model.Student
import danny.productions.ltd.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow

class SearchStudentsUseCase(private val studentRepository: StudentRepository) {

    operator fun invoke(query: String): Flow<List<Student>> {
        return if (query.isBlank()) {
            studentRepository.getAll()
        } else {
            studentRepository.search(query.trim())
        }
    }
}
