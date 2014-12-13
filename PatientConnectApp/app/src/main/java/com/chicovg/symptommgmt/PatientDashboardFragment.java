package com.chicovg.symptommgmt;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.domain.CheckIn;
import com.chicovg.symptommgmt.rest.domain.PainLevel;
import com.chicovg.symptommgmt.rest.domain.Patient;

import java.util.Calendar;

import static com.chicovg.symptommgmt.service.ContentService.extractCheckInFromCursor;
import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;
import static com.chicovg.symptommgmt.util.ContentUtil.timestampFromContentValues;


/**
 *
 */
public class PatientDashboardFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String PATIENT_ARG = "PATIENT";
    private static final String PATIENT_ID_ARG = "PATIENT_ID";

    private Long mPatientId;
    private Patient mPatient;

    private View mView;

    private ListView mCheckInListView;
    private ListAdapter mCheckInAdapter;

    private static final int LOADER_ID = 3;

    private OnDashboardLoadListener mListener;

    public static PatientDashboardFragment newInstance(Patient patient) {
        PatientDashboardFragment fragment = new PatientDashboardFragment();
        Bundle args = new Bundle();
        args.putBundle(PATIENT_ARG, toBundle(patient));
        fragment.setArguments(args);
        return fragment;
    }

    public static PatientDashboardFragment newInstance(Long patientId) {
        PatientDashboardFragment fragment = new PatientDashboardFragment();
        Bundle args = new Bundle();
        args.putLong(PATIENT_ID_ARG, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientDashboardFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mListener = (OnDashboardLoadListener)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            if(getArguments().containsKey(PATIENT_ARG)){
                mPatient = (Patient)fromBundle(getArguments().getBundle(PATIENT_ARG));
                mPatientId = mPatient.getId();
            }
            if(getArguments().containsKey(PATIENT_ID_ARG)){
                mPatientId = getArguments().getLong(PATIENT_ID_ARG);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);

        /* Initialize Check In History Loader */
        mCheckInAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_check_in,
                null, new String[]{SymptomMgmtContract.CheckInColumns.TIMESTAMP, SymptomMgmtContract.CheckInColumns.OVERALL_PAIN_LEVEL},
                new int[]{R.id.timestamp, R.id.painLevel}, 0);

        ((SimpleCursorAdapter)mCheckInAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                String text = cursor.getString(columnIndex);
                int viewId = view.getId();
                switch (viewId){
                    case R.id.timestamp:
                        if(columnIndex == cursor.getColumnIndex(SymptomMgmtContract.CheckInColumns.TIMESTAMP)) {
                            String timestamp = timestampFromContentValues(getActivity(), text);
                            ((TextView) view).setText(timestamp);
                        }
                        break;
                    case R.id.painLevel:
                        if(columnIndex == cursor.getColumnIndex(SymptomMgmtContract.CheckInColumns.OVERALL_PAIN_LEVEL)) {
                            ((TextView) view).setText(text);
                            if(PainLevel.SEVERE.toString().equals(text)){
                                ((TextView) view).setTextColor(getActivity().getResources().getColor(R.color.severe));
                            }else if(PainLevel.MODERATE.toString().equals(text)){
                                ((TextView) view).setTextColor(getActivity().getResources().getColor(R.color.moderate));
                            }else if(PainLevel.WELL_CONTROLLED.toString().equals(text)){
                                ((TextView) view).setTextColor(getActivity().getResources().getColor(R.color.well_controlled));
                            }
                        }
                        break;
                }
                return true;
            }
        });

        mCheckInListView = (ListView)mView.findViewById(R.id.check_in_table);
        mCheckInListView.setAdapter(mCheckInAdapter);
        mCheckInListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((SimpleCursorAdapter) mCheckInAdapter).getCursor();
                c.moveToPosition(position);
                CheckIn checkIn = extractCheckInFromCursor(getActivity(), c);
                CheckInDetailFragment.newInstance(checkIn).show(getFragmentManager(), "dialog");
            }
        });
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);

        /* Populate patient dashboard info */
        if(null!=mPatient){
            updateDashboard();
        }
        return mView;
    }

    private void updateDashboard(){
        Log.d("PatientDashboardFragment", "Updating dashboard");

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if(hour<12){
            greeting = "Good morning, "+mPatient.getFirstName()+"!";
        }else if(hour<5){
            greeting = "Good afternoon, "+mPatient.getFirstName()+"!";
        }else{
            greeting = "Good evening, "+mPatient.getFirstName()+"!";
        }

        TextView mHowAreYou = (TextView)mView.findViewById(R.id.greeting);
        mHowAreYou.setText(greeting);

        final Long mPatientId = mPatient.getId();

        Button mCheckInButton = (Button)mView.findViewById(R.id.check_in_button);
        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInDialogFragment.newInstance(mPatientId).show(getFragmentManager(), "dialog");
            }
        });

        mHowAreYou.setVisibility(View.VISIBLE);
        mCheckInButton.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                SymptomMgmtContract.CheckInColumns.URI,
                SymptomMgmtContract.CheckInColumns.PROJECTION,
                SymptomMgmtContract.CheckInColumns.QUERY_BY_PATIENT,
                new String[]{String.valueOf(mPatientId)},
                SymptomMgmtContract.CheckInColumns.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(null!=data){
            Cursor c = (Cursor) data;
            ((SimpleCursorAdapter)mCheckInListView.getAdapter()).swapCursor(c);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        ((SimpleCursorAdapter)mCheckInListView.getAdapter()).swapCursor(null);
    }

    public void setPatient(Patient patient){
        mPatient = patient;
        updateDashboard();
    }

    public void reloadData(LoaderManager loaderManager){
        loaderManager.destroyLoader(LOADER_ID);
        loaderManager.initLoader(LOADER_ID, getArguments(), this);
    }

    public interface OnDashboardLoadListener {

    }

}
