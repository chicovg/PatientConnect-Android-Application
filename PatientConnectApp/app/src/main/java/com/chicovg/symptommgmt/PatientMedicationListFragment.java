package com.chicovg.symptommgmt;

import android.app.*;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.chicovg.symptommgmt.provider.SymptomMgmtContract;
import com.chicovg.symptommgmt.rest.domain.Medication;
import com.chicovg.symptommgmt.service.HttpService;

import static com.chicovg.symptommgmt.service.ContentService.extractMedicationFromCursor;

/**
 *
 * A fragment to display a table of check in records
 *
 */
public class PatientMedicationListFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String ARG_PATIENT_ID = "patientId";
    private static final String ARG_DOCTOR_VIEW = "doctorView";

    private Long mPatientId;
    private boolean mDoctorView;

    private ListView mListView;
    private ListAdapter mAdapter;

    private static final int LOADER_ID = 1;

    /**
     * Create a new instance of this fragment with the specified
     *  patient id
     * @param patientId
     * @return
     */
    public static PatientMedicationListFragment newInstance(Long patientId) {
        PatientMedicationListFragment fragment = new PatientMedicationListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PatientMedicationListFragment newInstance(Long patientId, boolean doctorView) {
        PatientMedicationListFragment fragment = new PatientMedicationListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PATIENT_ID, patientId);
        args.putBoolean(ARG_DOCTOR_VIEW, doctorView);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientMedicationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPatientId = getArguments().getLong(ARG_PATIENT_ID);
            mDoctorView = getArguments().getBoolean(ARG_DOCTOR_VIEW, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getMedicationsView(inflater, container, savedInstanceState);
    }

    private View getMedicationsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_patient_medications, container, false);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_medication, null,
                new String[]{SymptomMgmtContract.MedicationColumns.MEDICATION_NAME, SymptomMgmtContract.MedicationColumns.DOSAGE,
                        SymptomMgmtContract.MedicationColumns.INSTRUCTIONS},
                new int[]{R.id.medication_name, R.id.dosage, R.id.instructions}, 0);

        ((SimpleCursorAdapter) mAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()){
                    case R.id.dosage: {
                        int dosage = cursor.getInt(cursor.getColumnIndex(SymptomMgmtContract.MedicationColumns.DOSAGE));
                        String unit = cursor.getString(cursor.getColumnIndex(SymptomMgmtContract.MedicationColumns.DOSAGE_UNIT));
                        ((TextView) view).setText(String.format("%d%s", dosage, unit));
                    }
                    default: ((TextView) view).setText(cursor.getString(columnIndex));
                }
                return true;
            }
        });

        mListView = (ListView)view.findViewById(R.id.medications_list);
        mListView.setAdapter(mAdapter);

        if(mDoctorView){
            registerForContextMenu(mListView);
            ImageButton addNewButton = (ImageButton)view.findViewById(R.id.add_new);
            addNewButton.setVisibility(View.VISIBLE);
            addNewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditMedicationDialogFragment.newInstance(mPatientId).show(getFragmentManager(), "dialog");
                }
            });
        }
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.menu_medication_options, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Cursor c = ((SimpleCursorAdapter) mListView.getAdapter()).getCursor();
        c.moveToPosition(menuInfo.position);
        Log.d("", "Selected item at: "+ menuInfo.position);
        final Medication medication = extractMedicationFromCursor(getActivity(), c);
        switch (item.getItemId()){
            case R.id.action_edit: {
                EditMedicationDialogFragment.newInstance(medication, mPatientId).show(getFragmentManager(), "dialog");
                break;
            }
            case R.id.action_delete: {
                new DialogFragment(){
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_delete_confirm_dialog, null))
                                .setTitle(getActivity().getString(R.string.title_are_you_sure))
                                .setPositiveButton(getActivity().getString(R.string.confirm_delete_label), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        HttpService.deleteMedication(getActivity(), PatientMedicationListFragment.this.mPatientId, medication.getId());
                                    }
                                })
                                .setNegativeButton(getActivity().getString(R.string.cancel_delete_title), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        return builder.create();
                    }
                }.show(getFragmentManager(), "dialog");
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                SymptomMgmtContract.MedicationColumns.URI,
                SymptomMgmtContract.MedicationColumns.PROJECTION,
                SymptomMgmtContract.MedicationColumns.QUERY_BY_PATIENT,
                new String[]{String.valueOf(mPatientId)},
                SymptomMgmtContract.MedicationColumns.DEFAULT_SORT);
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


    public void reloadData(LoaderManager loaderManager){
        loaderManager.destroyLoader(LOADER_ID);
        loaderManager.initLoader(LOADER_ID, getArguments(), this);
    }
}
