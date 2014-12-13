package com.chicovg.symptommgmt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.Patient;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.rest.domain.UserType;
import com.chicovg.symptommgmt.service.HttpService;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;

/**
 *  This activity is used for both the patient and doctor apps
 *
 *  For the patient app, the activity is the main landing page and consists of 3 tabs
 *      1) Patient Dashboard
 *      2) Patient Profile
 *      3) Patient Medications
 *
 *  For the doctor app, the activity is shown whenever a doctor clicks on a patient
 *    and consists of 3 tabs:
 *      1) Patient Profile
 *      2) Patient Check In History
 *      3) Patient Medications
 *
 *
 */
public class PatientDetail extends Activity {

    private static final String LOG_TAG = "PatientDetail";

    PatientDetailPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    private Patient mPatient;
    private Doctor mDoctor;
    private User mUser;

    final List<Fragment> fragmentList = new LinkedList<>();
    final List<String> titleList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_patient_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_LOGOUT_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_FAILED));

        Intent intent = getIntent();
        mUser = (User)fromBundle(intent.getBundleExtra(getString(R.string.USER_EXTRA)));
        mPatient = (Patient)fromBundle(intent.getBundleExtra(getString(R.string.PATIENT_EXTRA)));
        if(UserType.DOCTOR.equals(mUser.getType()) && intent.hasExtra(getString(R.string.DOCTOR_EXTRA))){
            mDoctor = (Doctor)fromBundle(intent.getBundleExtra(getString(R.string.DOCTOR_EXTRA)));
        }

        prepareFragmentList(mUser.getType());
        mPagerAdapter = new PatientDetailPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void prepareFragmentList(UserType userType){
        Locale l = Locale.getDefault();
        if(UserType.PATIENT.equals(userType)){
            fragmentList.add(PatientDashboardFragment.newInstance(mPatient));
            titleList.add(getString(R.string.title_patient_dashboard_tab).toUpperCase(l));

            fragmentList.add(PatientProfileFragment.newInstance(mUser, mPatient));
            titleList.add(getString(R.string.title_patient_profile_tab).toUpperCase(l));

            fragmentList.add(PatientMedicationListFragment.newInstance(mPatient.getId()));
            titleList.add(getString(R.string.title_patient_medications_tab).toUpperCase(l));
        }else{
            fragmentList.add(PatientProfileFragment.newInstance(mUser, mPatient));
            titleList.add(getString(R.string.title_patient_profile_tab).toUpperCase(l));

            fragmentList.add(PatientCheckInListFragment.newInstance(mPatient));
            titleList.add(getString(R.string.title_patient_check_ins_tab).toUpperCase(l));

            fragmentList.add(PatientMedicationListFragment.newInstance(mPatient.getId(), true));
            titleList.add(getString(R.string.title_patient_medications_tab).toUpperCase(l));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(UserType.DOCTOR.equals(mUser.getType())){
                    Intent upIntent = new Intent(this, DoctorDashboard.class);
                    upIntent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
                    if(null!=mDoctor)
                        upIntent.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
                    NavUtils.navigateUpTo(this, upIntent);
                }else{
                    Intent upIntent = new Intent(this, HomepageActivity.class);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.action_logout:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class PatientDetailPagerAdapter extends FragmentStatePagerAdapter {

        public PatientDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_patient_detail, container, false);
            TextView tv = (TextView)rootView.findViewById(R.id.section_label);
            tv.setText("POOP!");
            return rootView;
        }
    }

    /**
     * Receives messages from the {@link com.chicovg.symptommgmt.service.HttpService}
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(LOG_TAG, "Broadcast Message Received: " + action);
            if (HttpService.BROADCAST_LOGOUT_SUCCESS.equals(action)){
                NavUtils.navigateUpFromSameTask(PatientDetail.this);
            } else if (HttpService.BROADCAST_HTTP_REQUEST_SUCCESS.equals(action)){
                String successfulAction = intent.getStringExtra(getString(R.string.ACTION_EXTRA));
                Log.d("Patient Detail", "Http request successful, action: " + successfulAction);
                if(isPatientView() && HttpService.ACTION_PUSH_CHECK_IN.equals(successfulAction)){
                    PatientDashboardFragment dashboardFragment = (PatientDashboardFragment)fragmentList.get(0);
                    dashboardFragment.reloadData(getLoaderManager());
                } else if(isPatientView() && HttpService.ACTION_PUSH_PATIENT.equals(successfulAction)){
                    PatientProfileFragment profileFragment = (PatientProfileFragment)fragmentList.get(1);
                    profileFragment.updatePatient((Patient) fromBundle(intent.getBundleExtra(getString(R.string.HTTP_RESPONSE_ENTITY_EXTRA))));
                } else if(isPatientView() && HttpService.ACTION_PUSH_REMINDER.equals(successfulAction)){
                    PatientProfileFragment profileFragment = (PatientProfileFragment)fragmentList.get(1);
                    profileFragment.reloadReminders(getLoaderManager());
                } else if(!isPatientView() && HttpService.ACTION_NEW_MEDICATION.equals(successfulAction) ||  HttpService.ACTION_DELETE_MEDICATION.equals(successfulAction) ||
                        HttpService.ACTION_UPDATE_MEDICATION.equals(successfulAction)){
                    PatientMedicationListFragment medicationListFragment = (PatientMedicationListFragment)fragmentList.get(2);
                    medicationListFragment.reloadData(getLoaderManager());
                }
            } else if (HttpService.BROADCAST_HTTP_REQUEST_FAILED.equals(action)){
                Toast.makeText(PatientDetail.this, R.string.error_save_failed, Toast.LENGTH_LONG).show();
            } else if (HttpService.BROADCAST_NO_CONNECTION.equals(action)){
                Toast.makeText(PatientDetail.this, R.string.error_no_connection, Toast.LENGTH_LONG).show();

            }
        }


        private boolean isPatientView(){
            return UserType.PATIENT.equals(PatientDetail.this.mUser.getType());
        }

    };

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
