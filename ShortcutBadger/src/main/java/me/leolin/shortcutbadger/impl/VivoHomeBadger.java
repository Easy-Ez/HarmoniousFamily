package me.leolin.shortcutbadger.impl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;

/**
 * @author leolin
 * see also https://dev.vivo.com.cn/documentCenter/doc/459.
 */
public class VivoHomeBadger implements Badger {

    @SuppressLint("WrongConstant")
    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", componentName.getClassName());
        intent.putExtra("notificationNum", badgeCount);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.addFlags(0x01000000); // Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND for Android 8.0
        }
        context.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.vivo.launcher",
                "com.bbk.launcher2"
        );
    }
}
