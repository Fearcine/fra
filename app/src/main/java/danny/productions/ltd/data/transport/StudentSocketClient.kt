package danny.productions.ltd.data.transport

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class StudentSocketClient {

    suspend fun sendMessage(hostIp: String, port: Int, message: String): Result<String> {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(hostIp, port), 5000)
                
                val output = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                
                output.println(message)
                val response = input.readLine()
                
                if (response != null) {
                    Result.success(response)
                } else {
                    Result.failure(Exception("Empty response from server"))
                }
            } catch (e: Exception) {
                Log.e("FRA_CLIENT", "Client error", e)
                Result.failure(e)
            } finally {
                socket?.close()
            }
        }
    }
}
