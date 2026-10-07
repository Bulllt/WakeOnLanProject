package com.example.wakeonlan.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.example.wakeonlan.R
import com.example.wakeonlan.utils.WakeHelper

class WakeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val intent =
            Intent(context, WakeWidgetProvider::class.java).apply {
                action = ACTION_WAKE
            }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_wake)
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_WAKE) {
            val pending = goAsync()
            WakeHelper.wake(context) { msg ->
                Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
                pending.finish()
            }
        }
    }

    companion object {
        const val ACTION_WAKE = "com.example.wakeonlan.ACTION_WAKE"
    }
}
