package com.chicovg.symptommgmt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chicovg.symptommgmt.rest.domain.Medication;
import com.chicovg.symptommgmt.service.HttpService;

import java.util.Arrays;
import java.util.List;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;

/**
 * Created by victorguthrie on 11/30/14.
 */
public class EditMedicationDialogFragment extends DialogFragment {

    private static final String MEDICATION_ARG = "MEDICATION";
    private static final String PATIENT_ID_ARG = "PATIENT_ID";

    private Medication mMedication;
    private long mPatientId;
    private boolean mEditMode = false;

    public static EditMedicationDialogFragment newInstance(long patientId){
        EditMedicationDialogFragment fragment = new EditMedicationDialogFragment();
        Bundle args = new Bundle();
        args.putLong(PATIENT_ID_ARG, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public static EditMedicationDialogFragment newInstance(Medication medication, long patientId){
        EditMedicationDialogFragment fragment = new EditMedicationDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(MEDICATION_ARG, toBundle(medication));
        args.putLong(PATIENT_ID_ARG, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public EditMedicationDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPatientId = getArguments().getLong(PATIENT_ID_ARG);
            if(getArguments().containsKey(MEDICATION_ARG)){
                mMedication = (Medication)fromBundle(getArguments().getBundle(MEDICATION_ARG));
                mEditMode = true;
            } else {
                mMedication = new Medication();
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_medication_dialog, null);

        final EditText medicationName = (EditText)viewGroup.findViewById(R.id.edit_medication_name);
        final EditText dosage = (EditText)viewGroup.findViewById(R.id.edit_dosage);
        final Spinner dosageUnit = (Spinner)viewGroup.findViewById(R.id.dosage_unit);
        final List<String> units = Arrays.asList(new String[]{"mg", "g"});
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_list_item, units);
        dosageUnit.setAdapter(arrayAdapter);
        dosageUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMedication.setDosageUnit(units.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMedication.setDosageUnit(null);
            }
        });
        final EditText instructions = (EditText)viewGroup.findViewById(R.id.edit_instructions);

        if(mEditMode){
            medicationName.setText(mMedication.getName());
            dosage.setText(String.valueOf(mMedication.getDosage()));
            dosageUnit.setSelection(units.indexOf(mMedication.getDosageUnit()));
            instructions.setText(mMedication.getInstructions());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewGroup)
                .setTitle(mEditMode ? getActivity().getString(R.string.title_edit_medication_record) :
                        getActivity().getString(R.string.title_new_medication_record))
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean submit = true;
                        Editable mn = medicationName.getText();
                        if (null == mn) {
                            medicationName.setError(String.format(getActivity().getString(R.string.error_required_field), getActivity().getString(R.string.medication_name_label)));
                            submit = false;
                        } else {
                            mMedication.setName(mn.toString());
                        }

                        Editable dge = dosage.getText();
                        if (null == dge) {
                            dosage.setError(String.format(getActivity().getString(R.string.error_required_field), getActivity().getString(R.string.dosage_label)));
                            submit = false;
                        } else {
                            mMedication.setDosage(Integer.valueOf(dge.toString()));
                        }

                        if (null == mMedication.getDosageUnit()) {
                            dosage.setError(String.format(getActivity().getString(R.string.error_required_field), getActivity().getString(R.string.dosage_label)));
                            submit = false;
                        }

                        Editable inst = instructions.getText();
                        if (null == inst) {
                            instructions.setError(String.format(getActivity().getString(R.string.error_required_field), getActivity().getString(R.string.instructions_label)));
                            submit = false;
                        } else {
                            mMedication.setInstructions(inst.toString());
                        }

                        if(submit){
                            if(mEditMode){
                                HttpService.updateMedication(getActivity(), mPatientId, mMedication.getId(), mMedication);
                            } else {
                                HttpService.createMedication(getActivity(), mPatientId, mMedication);
                            }
                            d.dismiss();
                        }
                    }
                });
            }
        });
        return d;
    }
}
