package ro.pub.cs.systems.eim.practicaltest02v2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class ServerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        val connectButton = findViewById<Button>(R.id.connect_button)
        val serverTimeOutput = findViewById<TextView>(R.id.server_time_output)

        connectButton.setOnClickListener {
            // Run the network operation in a background thread
            Thread {
                val serverTime = getServerTime()
                runOnUiThread {
                    serverTimeOutput.text = "Ora serverului: $serverTime"
                }
                Log.d("ServerActivity", "Ora primită de la server: $serverTime")
            }.start()
        }
    }

    private fun getServerTime(): String {
        val host = "10.41.204.39" // Adresa serverului (înlocuiește cu IP-ul calculatorului)
        val port = 12345          // Portul serverului

        return try {
            // Conectare la server
            val socket = Socket(host, port)
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val serverTime = input.readLine() // Citește timpul trimis de server
            socket.close()
            serverTime
        } catch (e: Exception) {
            Log.e("ServerActivity", "Eroare la conectare: ${e.message}")
            "Eroare la conectare: ${e.message}"
        }
    }
}
