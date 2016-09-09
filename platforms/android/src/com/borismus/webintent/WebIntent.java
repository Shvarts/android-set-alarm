package com.borismus.webintent;

import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.provider.AlarmClock;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;

/**
 * WebIntent is a PhoneGap plugin that bridges Android intents and web
 * applications:
 *
 * 1. web apps can spawn intents that call native Android applications. 2.
 * (after setting up correct intent filters for PhoneGap applications), Android
 * intents can be handled by PhoneGap web applications.
 *
 * @author boris@borismus.com
 *
 */
public class WebIntent extends CordovaPlugin {

    private CallbackContext onNewIntentCallbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if (action.equals("setAlarm")) {
                JSONObject obj = args.getJSONObject(0);
                setAlarm(obj.getInt("time"), obj.getInt("hours"), obj.getInt("minutes"));
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                return true;
            } else if (action.equals("addAlarm")) {
                JSONObject obj = args.getJSONObject(0);
                addAlarm(obj.getInt("time"));
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
            } else if (action.equals("deleteAlarm")) {
                JSONObject obj = args.getJSONObject(0);
                deleteAlarm(obj.getInt("time"));
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            String errorMessage=e.getMessage();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION,errorMessage));
            return false;
        }
    }

    void setAlarm(Integer time, Integer hours, Integer minutes) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.getExtras();
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm");
        i.putExtra(AlarmClock.EXTRA_HOUR, hours);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);

        ((CordovaActivity)this.cordova.getActivity()).startActivity(i);
    }

    void addAlarm(Integer time) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, time, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    void deleteAlarm(Integer time) {
        // pendingIntent = PendingIntent.getActivity(mContext, id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, time, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }
}
