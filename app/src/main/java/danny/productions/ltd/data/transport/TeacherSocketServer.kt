package danny.productions.ltd.data.transport

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TeacherSocketServer(private val port: Int = 8888) {
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    
    var onMessageReceived: ((String) -> String)? = null // Returns ACK/response

    suspend fun start() {
        withContext(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(port)
                isRunning = true
                Log.d("FRA_SERVER", "Server started on port $port")
                while (isRunning) {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                }
            } catch (e: Exception) {
                Log.e("FRA_SERVER", "Server error", e)
            }
        }
    }

    private fun handleClient(client: Socket) {
        Thread {
            try {
                val input = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = PrintWriter(client.getOutputStream(), true)
                val message = input.readLine()
                
                if (message != null) {
                    val response = onMessageReceived?.invoke(message) ?: "ERROR"
                    output.println(response)
                }
            } catch (e: Exception) {
                Log.e("FRA_SERVER", "Client handle error", e)
            } finally {
                client.close()
            }
        }.start()
    }

    fun stop() {
        isRunning = false
        serverSocket?.close()
        serverSocket = null
    }
}
