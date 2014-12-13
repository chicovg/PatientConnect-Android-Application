package com.chicovg.symptommgmt;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.domain.CheckIn;
import com.chicovg.symptommgmt.rest.domain.PainLevel;
import com.chicovg.symptommgmt.rest.domain.Patient;

import static com.chicovg.symptommgmt.service.ContentService.extractCheckInFromCursor;
import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;
import static com.chicovg.symptommgmt.util.ContentUtil.timestampFromContentValues;

/**
 *
 * A fragment to display a table of check in records
 *
 */
public class PatientCheckInListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String ARG_PATIENT = "patient";

    private Long mPatientId;
    private Patient mPatient;


    private ListView mListView;
    private ListAdapter mAdapter;

    /**
     * Create a new instance of this fragment with the specified
     *  patient id
     * @param patientId
     * @return
     */
    public static PatientCheckInListFragment newInstance(Patient patient) {
        PatientCheckInListFragment fragment = new PatientCheckInListFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PATIENT, toBundle(patient));
        fragment.setArguments(args);
        return fragment;
    }

    public PatientCheckInListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPatient = (Patient)fromBundle(getArguments().getBundle(ARG_PATIENT));
            mPatientId = mPatient.getId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getCheckInView(inflater, container, savedInstanceState);
    }

    private View getCheckInView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_check_in_table, container, false);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_check_in,
                null, new String[]{SymptomMgmtContract.CheckInColumns.TIMESTAMP, SymptomMgmtContract.CheckInColumns.OVERALL_PAIN_LEVEL},
                new int[]{R.id.timestamp, R.id.painLevel}, 0);
        ((SimpleCursorAdapter)mAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
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
        
        TextView checkInsTitle = (TextView)view.findViewById(R.id.check_in_title);
        checkInsTitle.setText(String.format(getActivity().getString(R.string.check_in_title_template) , mPatient.getFirstName(), mPatient.getLastName()));

        mListView = (ListView)view.findViewById(R.id.check_in_table);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((SimpleCursorAdapter) mAdapter).getCursor();
                c.moveToPosition(position);
                CheckIn checkIn = extractCheckInFromCursor(getActivity(), c);
                CheckInDetailFragment.newInstance(checkIn).show(getFragmentManager(), "dialog");
            }
        });
        getLoaderManager().initLoader(0, getArguments(), this);
        return view;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
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
            ((SimpleCursorAdapter)mListView.getAdapter()).swapCursor(c);
            //mListener.tableFragmentLoaded(mTableType);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        ((SimpleCursorAdapter)mListView.getAdapter()).swapCursor(null);
    }

    public void reloadData(){
        getLoaderManager().destroyLoader(0);
        getLoaderManager().initLoader(0, getArguments(), this);
    }
}
