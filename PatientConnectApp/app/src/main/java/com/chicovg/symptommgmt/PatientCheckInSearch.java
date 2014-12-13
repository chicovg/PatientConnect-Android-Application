package com.chicovg.symptommgmt;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.chicovg.symptommgmt.rest.domain.CheckIn;
import com.chicovg.symptommgmt.rest.domain.CheckInSearchResults;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.User;
import com.chicovg.symptommgmt.service.HttpService;

import java.text.DateFormat;
import java.util.List;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;
import static com.chicovg.symptommgmt.util.ContentUtil.getColorForPainLevel;


public class PatientCheckInSearch extends ListActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "PatientCheckInSearch";

    private User mUser;
    private Doctor mDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_patient_check_in_search);

        mUser = (User)fromBundle(getIntent().getBundleExtra(getString(R.string.USER_EXTRA)));
        mDoctor = (Doctor)fromBundle(getIntent().getBundleExtra(getString(R.string.DOCTOR_EXTRA)));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_LOGOUT_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_FAILED));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_check_in_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.check_in_query_hint));
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
                upIntent.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            case R.id.action_logout:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(null!=query && query.length()>0){
            setProgressBarIndeterminateVisibility(true);
            HttpService.searchCheckInData(this, query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;// suggest based on local??
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CheckIn checkIn = (CheckIn)getListAdapter().getItem(position);
        CheckInDetailFragment.newInstance(checkIn).show(getFragmentManager(), "dialog");
    }

    private void setSearchResults(CheckInSearchResults checkInSearchResults){
        setProgressBarIndeterminateVisibility(false);
        setListAdapter(new CheckInAdapter(this, checkInSearchResults.getCheckIns()));
    }

    private class CheckInAdapter extends ArrayAdapter<CheckIn> {

        DateFormat androidDateFormat;
        DateFormat androidTimeFormat;

        public CheckInAdapter(Context context, List<CheckIn> objects) {
            super(context, R.layout.list_item_check_in_search_result, objects);
            androidDateFormat = android.text.format.DateFormat.getLongDateFormat(context);
            androidTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_check_in_search_result, parent, false);
            }
            CheckIn checkIn = getItem(position);

            String patientName = String.format("%s, %s", checkIn.getPatientLastName(), checkIn.getPatientFirstName());
            ((TextView)convertView.findViewById(R.id.patient_name)).setText(patientName);

            String timestamp = androidDateFormat.format(checkIn.getTimestamp())+ " " +androidTimeFormat.format(checkIn.getTimestamp());
            ((TextView)convertView.findViewById(R.id.timestamp)).setText(timestamp);

            TextView painLevelView = (TextView) convertView.findViewById(R.id.pain_level);
            painLevelView.setText(checkIn.getOverallPainLevel().toString());
            painLevelView.setTextColor(getColorForPainLevel(getContext(), checkIn.getOverallPainLevel()));
            return convertView;
        }
    }

    /**
     * Receives messages from the {@link com.chicovg.symptommgmt.service.HttpService}
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setProgressBarIndeterminate(false);
            String action = intent.getAction();
            Log.d(TAG, "Broadcast Message Received: " + action);
            if(HttpService.BROADCAST_HTTP_REQUEST_SUCCESS.equals(action)){
                String requestAction = intent.getStringExtra(getString(R.string.ACTION_EXTRA));
                if(HttpService.ACTION_CHECK_IN_SEARCH.equals(requestAction)){
                    PatientCheckInSearch.this.setSearchResults((CheckInSearchResults)fromBundle(intent.getBundleExtra(getString(R.string.HTTP_RESPONSE_ENTITY_EXTRA))));
                }
            } else if (HttpService.BROADCAST_HTTP_REQUEST_FAILED.equals(action)){
                Toast.makeText(PatientCheckInSearch.this, R.string.error_save_failed, Toast.LENGTH_LONG).show();
            } else if (HttpService.BROADCAST_NO_CONNECTION.equals(action)){
                Toast.makeText(PatientCheckInSearch.this, R.string.error_no_connection, Toast.LENGTH_LONG).show();
            }
        }
    };
}
