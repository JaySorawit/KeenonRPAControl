package com.example.keenonrpacontrol

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var accessibilityService: MyAccessibilityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start Ktor server
        CoroutineScope(Dispatchers.IO).launch {
            startKtorServer()
        }

        checkAccessibilityService()
    }

    private fun startKtorServer() {
        try{
            Log.d("KtorServer", "Starting Ktor server...")
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/tap") {
                        val x = call.parameters["x"]?.toIntOrNull() ?: 500
                        val y = call.parameters["y"]?.toIntOrNull() ?: 500
                        if (::accessibilityService.isInitialized) {
                            accessibilityService.performTap(x, y)
                            call.respondText("Tapped at ($x, $y)", status = HttpStatusCode.OK)
                        } else {
                            call.respondText("Accessibility Service not initialized", status = HttpStatusCode.InternalServerError)
                        }
                    }

                    get("/swipe") {
                        val startX = call.parameters["startX"]?.toIntOrNull() ?: 300
                        val startY = call.parameters["startY"]?.toIntOrNull() ?: 500
                        val endX = call.parameters["endX"]?.toIntOrNull() ?: 800
                        val endY = call.parameters["endY"]?.toIntOrNull() ?: 500
                        if (::accessibilityService.isInitialized) {
                            accessibilityService.performSwipe(startX, startY, endX, endY)
                            call.respondText("Swiped from ($startX, $startY) to ($endX, $endY)", status = HttpStatusCode.OK)
                        } else {
                            call.respondText("Accessibility Service not initialized", status = HttpStatusCode.InternalServerError)
                        }
                    }
                }
            }.start(wait = true)
            Log.d("KtorServer", "Ktor server started on port 8080")
        } catch (e: Exception) {
            Log.e("KtorServer", "Error starting Ktor server", e)
        }
    }

    private fun checkAccessibilityService() {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""

        // Split the enabled services by comma
        val componentNames = enabledServices.split(",")

        // Check if the accessibility service is enabled
        for (componentName in componentNames) {
            if (componentName.equals("$packageName/.MyAccessibilityService", ignoreCase = true)) {
                return // Accessibility service is enabled
            }
        }

        // Guide user to enable the service
        Log.d("AccessibilityService", "Please enable the accessibility service.")
    }


}
