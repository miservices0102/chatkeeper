package com.chatkeeper.app.service
import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.chatkeeper.app.data.AppDatabase
import com.chatkeeper.app.data.MessageEntity
import kotlinx.coroutines.runBlocking

class ChatNotificationListener : NotificationListenerService() {
    private val watchedPackages = setOf("com.whatsapp", "com.whatsapp.w4b")

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (sbn.packageName !in watchedPackages) return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        if (title.isBlank() || text.isBlank()) return
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val entity = MessageEntity(sender = title, message = text, timestamp = sbn.postTime, packageName = sbn.packageName)
        Thread { runBlocking { AppDatabase.getInstance(applicationContext).messageDao().insert(entity) } }.start()
    }
}
