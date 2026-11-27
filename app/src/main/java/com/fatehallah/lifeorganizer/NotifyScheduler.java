package com.fatehallah.lifeorganizer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The web app builds the next 14 days of messages (texts depend on your data) and hands them here.
 * We store them and set one alarm per message. Alarms are re-set after a reboot or app update.
 */
public class NotifyScheduler {
    static final String PREFS = "notif";
    static final String KEY_JSON = "schedule";
    static final String KEY_COUNT = "count";
    static final String CHANNEL = "reminders";
    static final String ACTION = "com.fatehallah.lifeorganizer.NOTIFY";
    static final int MAX = 90;

    static void schedule(Context c, String json) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_JSON, json).apply();
        apply(c);
    }

    static synchronized void apply(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        int prev = p.getInt(KEY_COUNT, 0);
        for (int i = 0; i < prev; i++) am.cancel(pending(c, i, null));

        int n = 0;
        try {
            JSONArray a = new JSONArray(p.getString(KEY_JSON, "[]"));
            long now = System.currentTimeMillis();
            for (int i = 0; i < a.length() && n < MAX; i++) {
                JSONObject o = a.getJSONObject(i);
                long at = o.getLong("at");
                if (at <= now) continue;
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(c, n, o));
                n++;
            }
        } catch (Exception ignored) { }
        p.edit().putInt(KEY_COUNT, n).apply();
    }

    private static PendingIntent pending(Context c, int code, JSONObject o) {
        Intent i = new Intent(c, NotifyReceiver.class);
        i.setAction(ACTION);
        if (o != null) {
            i.putExtra("title", o.optString("title"));
            i.putExtra("text", o.optString("text"));
            i.putExtra("tab", o.optString("tab", "home"));
            i.putExtra("nid", (int) ((o.optLong("at") / 60000L) % 1000000L));
        }
        return PendingIntent.getBroadcast(c, code, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void ensureChannel(Context c) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        if (nm != null && nm.getNotificationChannel(CHANNEL) == null) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "Reminders & encouragement", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Daily check-ins, streaks and motivation from Life Organizer");
            nm.createNotificationChannel(ch);
        }
    }

    static void show(Context c, String title, String text, String tab, int id) {
        ensureChannel(c);
        Intent open = new Intent(c, MainActivity.class);
        open.putExtra("tab", tab == null ? "home" : tab);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, id, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setColor(0xFF0F766E)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        try { if (nm != null) nm.notify(id, n); } catch (SecurityException ignored) { }
    }
}
