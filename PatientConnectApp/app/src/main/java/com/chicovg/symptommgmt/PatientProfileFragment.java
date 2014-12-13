package com.chicovg.symptommgmt;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.domain.Patient;
import com.chicovg.symptommgmt.rest.domain.Reminder;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import com.chicovg.symptommgmt.service.HttpService;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.chicovg.symptommgmt.service.ContentService.extractReminderFromCursor;
import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;


/**

 */
public class PatientProfileFragment extends Fragment {

    private static final String USER_ARG = "USER";
    private static final String PATIENT_ARG = "PATIENT";

    private View mView;
    private Patient mPatient;
    private User mUser;

    private ListView mRemindersView;
    private SimpleCursorAdapter mRemindersAdapter;
    private List<Reminder> mReminders = new LinkedList<>();

    private static final int LOADER_ID = 2;

    public static PatientProfileFragment newInstance(User user, Patient patient) {
        PatientProfileFragment fragment = new PatientProfileFragment();
        Bundle args = new Bundle();
        args.putBundle(USER_ARG, toBundle(user));
        args.putBundle(PATIENT_ARG, toBundle(patient));
        fragment.setArguments(args);
        return fragment;
    }

    public static PatientProfileFragment newInstance(User user) {
        PatientProfileFragment fragment = new PatientProfileFragment();
        Bundle args = new Bundle();
        args.putBundle(USER_ARG, toBundle(user));
        fragment.setArguments(args);
        return fragment;
    }

    public PatientProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=getArguments()){
            mUser = (User)fromBundle(getArguments().getBundle(USER_ARG));
            mPatient = (Patient)fromBundle(getArguments().getBundle(PATIENT_ARG));
        }
    }

    private void loadReminders(LoaderManager loaderManager){
        loaderManager.initLoader(LOADER_ID, null, reminderLoaderCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(null!=mUser){
            if(mUser.getType().equals(UserType.PATIENT)){
                mView = inflater.inflate(R.layout.fragment_patient_profile, container, false);

                mView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProfileEditDialogFragment.getInstance(mPatient).show(getFragmentManager(), "dialog");
                    }
                });

                mRemindersView = (ListView)mView.findViewById(R.id.reminders);
                mRemindersAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_reminder,
                        null, new String[]{SymptomMgmtContract.ReminderColumns.HOUR}, new int[]{R.id.time},
                        0);
                mRemindersAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
                        if(R.id.time == view.getId()){
                            int hour = cursor.getInt(cursor.getColumnIndex(SymptomMgmtContract.ReminderColumns.HOUR));
                            int minute = cursor.getInt(cursor.getColumnIndex(SymptomMgmtContract.ReminderColumns.MINUTE));
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            ((TextView)view).setText(timeFormat.format(calendar.getTime()));
                        }
                        return true;
                    }
                });
                mRemindersView.setAdapter(mRemindersAdapter);
                mRemindersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor c = mRemindersAdapter.getCursor();
                        c.moveToPosition(position);
                        final Reminder reminder = extractReminderFromCursor(c);

                        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                reminder.setHour(hourOfDay);
                                reminder.setMinute(minute);
                                HttpService.pushReminder(getActivity(), reminder);
                            }
                        }, reminder.getHour(), reminder.getMinute(), false).show();

                    }
                });
                loadReminders(getLoaderManager());
            }else{
                mView = inflater.inflate(R.layout.fragment_patient_profile_doctor_view, container, false);
            }

            if(null!=mPatient){
                setPatientFields();
            }
        }else{
            mView = inflater.inflate(R.layout.fragment_empty, container, false);
        }
        return mView;
    }

    private void setPatientFields(){
        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getActivity());

        ((TextView)mView.findViewById(R.id.username)).setText(mPatient.getUsername());
        ((TextView)mView.findViewById(R.id.firstName)).setText(mPatient.getFirstName());
        ((TextView)mView.findViewById(R.id.lastName)).setText(mPatient.getLastName());
        ((TextView)mView.findViewById(R.id.dateOfBirth)).setText(dateFormat.format(mPatient.getDateOfBirth()));
        ((TextView)mView.findViewById(R.id.email)).setText(mPatient.getEmailAddress());
        ((TextView)mView.findViewById(R.id.phone)).setText(mPatient.getPhoneNumber());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private LoaderManager.LoaderCallbacks reminderLoaderCallbacks = new LoaderManager.LoaderCallbacks() {
        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    SymptomMgmtContract.ReminderColumns.URI,
                    SymptomMgmtContract.ReminderColumns.PROJECTION,
                    SymptomMgmtContract.ReminderColumns.QUERY_BY_PATIENT,
                    new String[]{String.valueOf(mPatient.getId())},
                    SymptomMgmtContract.ReminderColumns.SORT_HOUR_MIN_ASC);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            if(null!=data){
                PatientProfileFragment.this.mRemindersAdapter.swapCursor((Cursor)data);
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {
            PatientProfileFragment.this.mRemindersAdapter.swapCursor(null);
        }
    };

    public void reloadReminders(LoaderManager manager){
        manager.destroyLoader(LOADER_ID);
        loadReminders(manager);
    }

    public void updatePatient(Patient patient){
        mPatient = patient;
        setPatientFields();
    }

}
