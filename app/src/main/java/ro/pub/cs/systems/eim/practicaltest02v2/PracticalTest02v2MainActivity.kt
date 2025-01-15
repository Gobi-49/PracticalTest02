package ro.pub.cs.systems.eim.practicaltest02v2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class PracticalTest02v2MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v2_main)

        val inputWord = findViewById<EditText>(R.id.input_word)
        val searchButton = findViewById<Button>(R.id.search_button)
        val definitionOutput = findViewById<TextView>(R.id.definition_output)

        // Register the BroadcastReceiver
        registerReceiver(
            DefinitionReceiver(definitionOutput),
            IntentFilter("ro.pub.cs.systems.eim.practical_test02v2.DEFINITION_BROADCAST"),
            RECEIVER_NOT_EXPORTED
        )

        searchButton.setOnClickListener {
            val word = inputWord.text.toString()
            if (word.isNotEmpty()) {
                Log.d("MainActivity", "Searching for word: $word")
                val definition = getWordDefinition(word)
                Log.d("MainActivity", "Raw API response: $definition")

                // Emit a broadcast with the definition
                val broadcastIntent = Intent("ro.pub.cs.systems.eim.practical_test02v2.DEFINITION_BROADCAST")
                broadcastIntent.putExtra("definition", parseDefinitionFromResponse(definition))
                broadcastIntent.setPackage(packageName) // Ensure the intent targets your app
                sendBroadcast(broadcastIntent)

                Log.d("MainActivity", "Broadcast sent with definition.")
            } else {
                definitionOutput.text = "Introduceți un cuvânt."
                Log.d("MainActivity", "Empty input. Prompting user to enter a word.")
            }
        }
    }

    fun getWordDefinition(word: String): String {
        val url = URL("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
        var response = "Error fetching definition"

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        try {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream.bufferedReader().use {
                        response = it.readText()
                    }
                } else {
                    response = "HTTP error code: $responseCode"
                }
            }
        } catch (e: Exception) {
            response = "Exception: ${e.message}"
        }

        return response
    }

    private fun parseDefinitionFromResponse(jsonResponse: String): String {
        return try {
            val jsonArray = JSONArray(jsonResponse)
            val definition = jsonArray.getJSONObject(0)
                .getJSONArray("meanings")
                .getJSONObject(0)
                .getJSONArray("definitions")
                .getJSONObject(0)
                .getString("definition")
            definition
        } catch (e: Exception) {
            "Eroare la procesarea răspunsului JSON: ${e.message}"
        }
    }
}

class DefinitionReceiver(private val definitionOutput: TextView) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val definition = intent?.getStringExtra("definition") ?: "Nicio definiție primită."
        Log.d("DefinitionReceiver", "Definition received: $definition")

        // Update the TextView with the received definition
        definitionOutput.text = definition
    }
}
