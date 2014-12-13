package com.chicovg.symptommgmt.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.chicovg.symptommgmt.R;
import com.chicovg.symptommgmt.SymptomMgmtApplication;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import com.chicovg.symptommgmt.service.HttpService;
import com.chicovg.symptommgmt.service.NotificationService;

import java.util.Calendar;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;

/**
 * Created by victorguthrie on 10/31/14.
 *
 * This broadcast received will handle all broadcasts coming from the system
 *  alarm manager
 *  This includes:
 *      - alarms to sync data
 *      - alarms to send user check in notifications
 */
public class SystemAlarmBrodcastReceiver extends BroadcastReceiver {

    private static final String ACTION_DATA_SYNC = "com.chicovg.symptommgmt.service.action.DATA_SYNC";

    public static void scheduleDataSync(Context context, User user, boolean immediate){
        Calendar time = Calendar.getInstance();
        if(!immediate){
            time.add(Calendar.MINUTE, 5);
        }

        Intent syncIntent = new Intent(context, SystemAlarmBrodcastReceiver.class);
        syncIntent.setAction(ACTION_DATA_SYNC);
        syncIntent.putExtra(context.getString(R.string.USER_EXTRA), toBundle(user));
        syncIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, syncIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            Log.d("SystemAlarmBrodcastReceiver", "Intent recieved, action: "+action);
            if(NotificationService.ACTION_PATIENT_REMINDER.equals(action)){
                NotificationService.sendCheckInNotification(context);
            }else if(ACTION_DATA_SYNC.equals(action) && SymptomMgmtApplication.isActivityVisible()){
                User user = (User)fromBundle(intent.getBundleExtra(context.getString(R.string.USER_EXTRA)));
                if(UserType.PATIENT.equals(user.getType())){
                    HttpService.pullPatient(context, user.getId());
                } else {
                    HttpService.pullDoctor(context, user.getId());
                }
            }
        }

    }
}
