package danny.productions.ltd.data.transport

class SyncManager {
    // Coordinates between TeacherSocketServer and StudentSocketClient.
    // In full impl, this parses JSON payloads from client, validates them
    // against ValidateQRUseCase, verifies face embedding with FaceMatcher,
    // and writes to AttendanceRepository.
}
