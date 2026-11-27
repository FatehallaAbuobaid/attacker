package com.fatehallah.lifeorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Fires when a scheduled message is due and shows it. */
public class NotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        String title = i.getStringExtra("title");
        String text = i.getStringExtra("text");
        if (title == null || text == null) return;
        NotifyScheduler.show(c, title, text, i.getStringExtra("tab"), i.getIntExtra("nid", 1));
    }
}
