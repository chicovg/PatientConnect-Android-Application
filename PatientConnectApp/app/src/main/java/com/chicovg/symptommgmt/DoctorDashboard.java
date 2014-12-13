package com.chicovg.symptommgmt;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.Patient;
import com.chicovg.symptommgmt.rest.domain.User;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.util.Date;

import static com.chicovg.symptommgmt.service.ContentService.extractPatientFromCursor;
import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;
import static com.chicovg.symptommgmt.util.ContentUtil.getColorForPainLevel;


public class DoctorDashboard extends ListActivity implements LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG = "DoctorDashboard";

    private User mUser;
    private Doctor mDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        mUser = (User)fromBundle(getIntent().getBundleExtra(getString(R.string.USER_EXTRA)));
        mDoctor = (Doctor)fromBundle(getIntent().getBundleExtra(getString(R.string.DOCTOR_EXTRA)));

        Log.d(LOG_TAG, mDoctor.getUsername());

        final DateFormat androidDateFormat = android.text.format.DateFormat.getDateFormat(this);
        final DateFormat androidTimeFormat = android.text.format.DateFormat.getTimeFormat(this);

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item_patient, null,
                new String[]{SymptomMgmtContract.PatientColumns.FIRST_NAME, SymptomMgmtContract.PatientColumns.LAST_NAME, SymptomMgmtContract.PatientColumns.LAST_PAIN_LEVEL},
                new int[]{R.id.patient_name, R.id.last_reported_summary, R.id.reported_since_summary}, 0);

        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                Patient patient = extractPatientFromCursor(cursor);
                switch (view.getId()){
                    case R.id.patient_name: ((TextView)view).setText(String.format("%s, %s", patient.getLastName(), patient.getFirstName()));
                        break;
                    case R.id.reported_since_summary:
                        if(null!=patient.getLastPainLevelChangedDtm()){
                            Period period = new Period(new DateTime(patient.getLastPainLevelChangedDtm()), new DateTime(new Date()));
                            int hours = period.getHours();
                            String lastChangedDate = androidDateFormat.format(patient.getLastPainLevelChangedDtm()) + " " + androidTimeFormat.format(patient.getLastPainLevelChangedDtm());
                            String summary = String.format(getString(R.string.reported_since_summary), patient.getLastPainLevel().toString(), lastChangedDate, hours);
                            ((TextView) view).setText(summary);
                            ((TextView) view).setTextColor(getColorForPainLevel(DoctorDashboard.this, patient.getLastPainLevel()));
                        }else{
                            view.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.last_reported_summary:
                        if(null!=patient.getLastPainLevelReportedDtm()){
                            String lastReportedDate = androidDateFormat.format(patient.getLastPainLevelReportedDtm()) + " " + androidTimeFormat.format(patient.getLastPainLevelReportedDtm());
                            String summary = String.format(getString(R.string.last_reported_summary), lastReportedDate);
                            ((TextView) view).setText(summary);
                        }else{
                            ((TextView)view).setText(R.string.patient_has_not_reported);
                        }
                        break;

                }

                return true;
            }
        });

        setListAdapter(simpleCursorAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile :
                Intent intent = new Intent(this, DoctorProfile.class);
                intent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
                intent.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
                startActivity(intent);
                break;
            case R.id.action_search :
                Intent intent1 = new Intent(this, PatientCheckInSearch.class);
                intent1.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
                intent1.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
                startActivity(intent1);
                break;
            case android.R.id.home :
                Intent upIntent = new Intent(this, HomepageActivity.class);
                NavUtils.navigateUpTo(this, upIntent);
            case R.id.action_logout :
                break;
        }
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                SymptomMgmtContract.PatientColumns.URI,
                SymptomMgmtContract.PatientColumns.PROJECTION,
                SymptomMgmtContract.PatientColumns.QUERY_BY_DOCTOR,
                new String[]{String.valueOf(mDoctor.getId())},
                SymptomMgmtContract.PatientColumns.SORT_FOR_DOCTOR_DASHBOARD);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        ((SimpleCursorAdapter)getListAdapter()).swapCursor((Cursor)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        ((SimpleCursorAdapter)getListAdapter()).swapCursor(null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = ((SimpleCursorAdapter)getListAdapter()).getCursor();
        c.moveToPosition(position);
        Patient p = extractPatientFromCursor(c);
        Intent intent = new Intent(this, PatientDetail.class);
        intent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
        intent.putExtra(getString(R.string.PATIENT_EXTRA), toBundle(p));
        intent.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SymptomMgmtApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SymptomMgmtApplication.activityPaused();
    }
}
