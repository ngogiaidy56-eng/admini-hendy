package com.hendy.cybertech.bot

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*

val BOT_TOKEN = System.getenv("BOT_TOKEN") ?: "8517026315:AAELCCiSvwQb-9AWi0VRRMQvT7Pf6rAZzP8"
val WEBHOOK_SECRET = System.getenv("WEBHOOK_SECRET_TOKEN") ?: "hendy_secret_token_2026"
val CORE_BACKEND_URL = System.getenv("CORE_BACKEND_URL") ?: "https://telegram-mini-app.ngogiaidy56.workers.dev"

val users = mutableMapOf<String, UserSession>()
val userStates = mutableMapOf<String, UserState>()

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port) {
        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        routing {
            get("/") {
                call.respondText("🤖 Telegram Bot (Ktor Server) đang chạy mượt mà trên Render.com!")
            }

            // Webhook Endpoint nhận tín hiệu từ Telegram
            post("/webhook") {
                // Kiểm tra bảo mật Secret Token từ Telegram gửi sang
                val clientSecret = call.request.header("X-Telegram-Bot-Api-Secret-Token")
                if (clientSecret != WEBHOOK_SECRET) {
                    call.respond(HttpStatusCode.Forbidden, "Unauthorized Secret Token")
                    return@post
                }

                try {
                    val bodyText = call.receiveText()
                    val jsonElement = Json.parseToJsonElement(bodyText)

                    // Xử lý sự kiện bất đồng bộ
                    handleUpdate(jsonElement)

                    call.respond(HttpStatusCode.OK, "OK")
                } catch (e: Throwable) {
                    call.respond(HttpStatusCode.InternalServerError, "Lỗi xử lý: ${e.message}")
                }
            }
        }
    }.start(wait = true)
}

// Xử lý các bản cập nhật (tin nhắn & nút bấm) từ Telegram
suspend fun handleUpdate(update: JsonElement) {
    val rootObj = update.jsonObject

    if (rootObj.containsKey("message")) {
        val msg = rootObj["message"]!!.jsonObject
        val chatId = msg["chat"]!!.jsonObject["id"]!!.jsonPrimitive.content
        val text = msg["text"]?.jsonPrimitive?.content?.trim() ?: return
        val firstName = msg["from"]?.jsonObject?.get("first_name")?.jsonPrimitive?.content ?: "Khách"
        
        val u = users.getOrPut(chatId) { UserSession(name = firstName) }

        if (text.startsWith("/start")) {
            userStates.remove(chatId)
            val welcomeMsg = """
                🤖 *BOT HENDY CYBERTECH 2026 (RENDER SERVER)* 🚀
                Chào mừng bạn, *${u.name}* (ID: `$chatId`)
                --------------------------------------------------
                💎 *VIP 0*
                💰 **Ví Chính:** `${u.balance} VNĐ`
            """.trimIndent()

            callTelegramApi("sendMessage", buildJsonObject {
                put("chat_id", chatId)
                put("text", welcomeMsg)
                put("parse_mode", "Markdown")
                put("reply_markup", buildJsonObject {
                    put("inline_keyboard", buildJsonArray {
                        add(buildJsonArray { add(JsonObject(mapOf("text" to JsonPrimitive("🛍️ TRUNG TÂM MUA CODE"), "callback_data" to JsonPrimitive("shop_code_mini")))) })
                    })
                })
            })
        }
    }
}

suspend fun callTelegramApi(method: String, payload: JsonObject) {
    val url = "https://api.telegram.org/bot$BOT_TOKEN/$method"
    try {
        httpClient.post(url) {
            headers.append("Content-Type", "application/json")
            setBody(payload.toString())
        }
    } catch (_: Throwable) {}
}
