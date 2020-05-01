package com.wibitty.ryantime

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random


/**
 * Implementation of App Widget functionality.
 */
class TimeText : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE = "com.wibitty.ryantime.action.UPDATE"

        fun scheduleUpdate(context: Context) {
            val now = LocalDateTime.now()
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = getAlarmIntent(context)
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + ((60 - now.second) * 1000),
                alarmIntent
            )
        }

        fun clearUpdate(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = getAlarmIntent(context)
            alarmMgr.cancel(alarmIntent)
        }

        fun getAlarmIntent(context: Context): PendingIntent {
            val intent = Intent(context, TimeText::class.java)
                .setAction(TimeText.ACTION_UPDATE)
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_UPDATE == intent.action) {
            onUpdate(context)
        } else {
            super.onReceive(context, intent)
        }
    }

    private fun onUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleUpdate(context)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        scheduleUpdate(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        clearUpdate(context)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val now = LocalDateTime.now()
    val minute = minutesToText(now.minute)
    val hour = hoursToText(twelfthHour(now.hour))
    val widgetText = "${preroll()}It is\n$hour\n$minute\n${amOrPM(now.hour)}"
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.time_text)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun twelfthHour(hour: Int): Int = if (hour > 12) {
    hour - 12
} else {
    hour
}

internal fun amOrPM(hour: Int): String = when (hour) {
    in 18..21 -> "in the evening"
    in 12..17 -> "in the afternoon"
    in 6..12 -> "in the morning"
    else -> "at night"
}

internal fun hoursToText(hour: Int): String = when (hour) {
    0 -> "twelve"
    1 -> "one"
    2 -> "two"
    3 -> "three"
    4 -> "four"
    5 -> "five"
    6 -> "six"
    7 -> "seven"
    8 -> "eight"
    9 -> "nine"
    10 -> "ten"
    11 -> "eleven"
    12 -> "twelve"
    else -> "Whoopsie"
}

internal fun minutesToText(minutes: Int): String = when (minutes) {
    0 -> "o cl0ck"
    1 -> "o one"
    2 -> "o two"
    3 -> "o three"
    4 -> "o four"
    5 -> "o five"
    6 -> "o six"
    7 -> "o seven"
    8 -> "o eight"
    9 -> "o nine"
    10 -> "ten"
    11 -> "eleven"
    12 -> "twelve"
    13 -> "thirteen"
    14 -> "fourteen"
    15 -> "fifteen"
    16 -> "sixteen"
    17 -> "seventeen"
    18 -> "eighteen"
    19 -> "nineteen"
    20 -> "twenty"
    30 -> "thirty"
    40 -> "forty"
    50 -> "fifty"
    in 21..29 -> "twenty\n${hoursToText(minutes-20)}"
    in 31..39 -> "thirty\n${hoursToText(minutes-30)}"
    in 41..49 -> "forty\n${hoursToText(minutes-40)}"
    in 51..59 -> "fifty\n${hoursToText(minutes-50)}"
    else -> "Yipee-Ki-Yay"
}

internal fun preroll(): String = if (Random.nextInt(12) == 1) {
    randomGreeting()
} else {
    ""
}

internal fun randomGreeting(): String = arrayOf(
    "Hi Ryan! ",
    "Hi bud, ",
    "¡HOLA! México, ",
    "Howdy! ",
    "Sup bro. ",
    "What's up doc? ",
    "How you doin'? ",
    "Hello, Newman. ",
    "Matey force b wit u ",
    "Whats cookin' good lookin'? ",
    "Hello, sunshine! ",
    "'Sup, homeslice? ",
    "I'm Batman. ",
    "'sup ",
    "Captain, captain, ",
    "I like your face ",
    "À bientôt! ",
    "Ahoy, matey! ",
    "Hello, governor! ",
    "Knock knock. ",
    "Ko (こ) ー こんにちは ",
    "Whazzup? ",
    "What’s up, buttercup? ",
    "How's life? ",
    "'O ā mai 'oe? ",
    "Come va? "
).random()