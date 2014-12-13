package com.chicovg.symptommgmt.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.chicovg.symptommgmt.HomepageActivity;
import com.chicovg.symptommgmt.R;
import com.chicovg.symptommgmt.receiver.SystemAlarmBrodcastReceiver;
import com.chicovg.symptommgmt.rest.domain.PainLevel;
import com.chicovg.symptommgmt.rest.domain.Patient;
import com.chicovg.symptommgmt.rest.domain.Reminder;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.chicovg.symptommgmt.service.ContentService.getReminders;

/**
 * Created by victorguthrie on 11/28/14.
 */
public class NotificationService {

    private static final String TAG = "NotificationService";

    public static final String ACTION_PATIENT_REMINDER = "com.chicovg.symptommgmt.service.action.PATIENT_REMINDER";

    public static final int PATIENT_REMINDER_NOTIFICATION_ID = 0;
    public static final int PATIENT_SEVERE_12HR_PAIN_ALERT_NOTIFICATION_ID = 1;
    public static final int PATIENT_MODERATE_OR_SEVERE_16HR_PAIN_ALERT_NOTIFICATION_ID = 2;
    public static final int PATIENT_CANNOT_EAT_12HR_ALERT_NOTIFICATION_ID = 3;

    public static void setRemindersForPatient(final Context context, final long patientId){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG, "Setting Patient Reminders...");

                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);

                List<Reminder> reminderList = getReminders(context);
                int requestCode = 0;
                if(null!=reminderList && reminderList.size()>0){
                    for(Reminder reminder : reminderList){
                        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
                        calendar.set(Calendar.MINUTE, reminder.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        Log.d("ReminderService", "Setting alarm for: "+dateFormat.format(calendar.getTime()));

                        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, SystemAlarmBrodcastReceiver.class);
                        intent.setAction(ACTION_PATIENT_REMINDER);
                        intent.putExtra(context.getString(R.string.PATIENT_ID_EXTRA), patientId);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                }
                return null;
            }
        }.execute();
    }

    public static void setReminders(final Context context){
        setRemindersForPatient(context, 0);
    }

    public static void sendPatientAlerts(final Context context, final List<Patient> patients){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(TAG, "Checking for and sending patient alerts...");

                if(null != patients){
                    for(Patient patient : patients){
                        Period period = new Period(new DateTime(patient.getLastPainLevelChangedDtm()), new DateTime(new Date()));
                        int hours = period.getHours();

                        String abbreviatedName = String.format("%s. %s", patient.getFirstName().charAt(0), patient.getLastName());

                        if(hours > 16 && (PainLevel.SEVERE.equals(patient.getLastPainLevel()) || PainLevel.MODERATE.equals(patient.getLastPainLevel()))){
                            sendPatientAlertNotification(context, PATIENT_MODERATE_OR_SEVERE_16HR_PAIN_ALERT_NOTIFICATION_ID, abbreviatedName);
                        } else if(hours >= 12 && PainLevel.SEVERE.equals(patient.getLastPainLevel())){
                            sendPatientAlertNotification(context, PATIENT_SEVERE_12HR_PAIN_ALERT_NOTIFICATION_ID, abbreviatedName);
                        }

                        if(null!=patient.getLastReportedUnableToEat()){
                            period = new Period(new DateTime(patient.getLastReportedUnableToEat()), new DateTime(new Date()));
                            hours = period.getHours();
                            if(hours > 12){
                                sendPatientAlertNotification(context, PATIENT_CANNOT_EAT_12HR_ALERT_NOTIFICATION_ID, abbreviatedName);
                            }
                        }
                    }
                }


                return null;
            }
        }.execute();

    }

    public static void sendCheckInNotification(Context context){
        Log.d(TAG, "Sending check-in notification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.check_in_notification_title))
                .setContentText(context.getString(R.string.check_in_notification_text))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);
        Intent intent = new Intent(context, HomepageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public static void sendPatientAlertNotification(Context context, int alertType, String name){
        Log.d(TAG, "Sending patient alert notification");
        String alertText = getAlertNotificationText(context, alertType, name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.alert_notification_title))
                .setContentText(alertText)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        Intent intent = new Intent(context, HomepageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private static String getAlertNotificationText( Context c, int alertType, String patientName){
        switch (alertType){
            case PATIENT_MODERATE_OR_SEVERE_16HR_PAIN_ALERT_NOTIFICATION_ID :
                return String.format(c.getString(R.string.severe_mod_16hr_alert_notification_text), patientName);
            case PATIENT_SEVERE_12HR_PAIN_ALERT_NOTIFICATION_ID :
                return String.format(c.getString(R.string.severe_12hr_alert_notification_text), patientName);
            case PATIENT_CANNOT_EAT_12HR_ALERT_NOTIFICATION_ID :
                return String.format(c.getString(R.string.cannot_eat_12hr_alert_notification_text), patientName);
            default: return "Unknown Patient Alert!";
        }
    }
}
