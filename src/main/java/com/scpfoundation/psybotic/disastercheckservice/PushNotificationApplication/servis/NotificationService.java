package com.scpfoundation.psybotic.disastercheckservice.PushNotificationApplication.servis;

import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
class NotificationService {

    @Value("\${service.firebase-config-file}")
    private lateinit var firebaseConfig: String

    private val logger: Logger = LoggerFactory.getLogger(NotificationService::class.java)

    @PostConstruct
    private fun initFirebase() {

        val serviceAccount = FileInputStream(firebaseConfig)

        try {
            val options = FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)

            } else {
                FirebaseApp.getInstance()
            }
        } catch (e: IOException) {
            logger.error("Fail Create FirebaseApp", e)
        }
    }

    fun sendToDevice(notification: NotificationDTO) {

        val message: Message = Message.builder()
                .setToken(notification.targetToken)
                .setNotification(Notification.builder()
                        .setTitle(notification.messageTitle)
                        .setBody(notification.messageBody).build())
                .putData("content", notification.messageTitle)
                .putData("body", notification.messageBody)
                .build()

        try {
            FirebaseMessaging.getInstance().send(message)
        } catch (e: FirebaseMessagingException) {
            logger.error("Fail to send notification to Device", e)
        }
    }
}