package com.example.helloworldgrpckotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var host: TextView
    private lateinit var userPort: TextView
    private lateinit var message: TextView
    private lateinit var responseText: TextView



    private fun channel(): ManagedChannel {
        val url = host.text.toString()
        val port = userPort.text.toString()

        val builder = ManagedChannelBuilder.forAddress(url, port.toInt())
        builder.usePlaintext()

        return builder.executor(Dispatchers.Default.asExecutor()).build()
    }

    private val greeter by lazy { GreeterGrpcKt.GreeterCoroutineStub(channel())}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.submit)
        host = findViewById(R.id.host)
        userPort = findViewById(R.id.port)
        message = findViewById(R.id.message)
        responseText = findViewById(R.id.result)

        fun sendReq() = runBlocking {
            try {
                val request = HelloRequest.newBuilder().setName(message.text.toString()).build()
                val response = greeter.sayHello(request)

                responseText.text = response.message
            } catch (e: Exception) {
                responseText.text = e.message
                e.printStackTrace()
            }
        }

        button.setOnClickListener {
            sendReq()
        }
    }
}