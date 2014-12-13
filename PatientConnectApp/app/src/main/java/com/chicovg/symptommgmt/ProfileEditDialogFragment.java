package com.chicovg.symptommgmt;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.Patient;
import com.chicovg.symptommgmt.rest.domain.UserType;
import com.chicovg.symptommgmt.service.HttpService;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;


/**
 *
 */
public class ProfileEditDialogFragment extends DialogFragment {

    private static final String USER_ARG = "USER";
    private static final String PATIENT_ARG = "PATIENT";
    private static final String DOCTOR_ARG = "DOCTOR";

    private Patient mPatient;
    private Doctor mDoctor;
    private UserType mUserType;

    public static ProfileEditDialogFragment getInstance(Patient patient){
        ProfileEditDialogFragment fragment = new ProfileEditDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(PATIENT_ARG, toBundle(patient));
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileEditDialogFragment getInstance(Doctor doctor){
        ProfileEditDialogFragment fragment = new ProfileEditDialogFragment();
        Bundle args = new Bundle();
        args.putBundle(DOCTOR_ARG, toBundle(doctor));
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileEditDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=getArguments()){
            if(getArguments().containsKey(PATIENT_ARG)){
                mUserType = UserType.PATIENT;
                mPatient = (Patient)fromBundle(getArguments().getBundle(PATIENT_ARG));
            }
            if(getArguments().containsKey(DOCTOR_ARG)){
                mUserType = UserType.DOCTOR;
                mDoctor = (Doctor)fromBundle(getArguments().getBundle(DOCTOR_ARG));
            }
        }
    }

    @Override
    @SuppressLint("WrongViewCast")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(UserType.PATIENT.equals(mUserType) ?
                        R.layout.fragment_patient_profile_edit_dialog :
                        R.layout.fragment_doctor_profile_edit_dialog, null);

        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

        final EditText firstName = (EditText)view.findViewById(R.id.firstName);
        final EditText lastName = (EditText)view.findViewById(R.id.lastName);
        final TextView dateOfBirth = (TextView)view.findViewById(R.id.dateOfBirth);
        final ImageButton editDateOfBirth = (ImageButton)view.findViewById(R.id.editDateOfBirth);
        final EditText phone = (EditText)view.findViewById(R.id.phone);
        final EditText email = (EditText)view.findViewById(R.id.email);

        editDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                        dateOfBirth.setText(dateFormat.format(cal.getTime()));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(UserType.PATIENT.equals(mUserType)){
            firstName.setText(mPatient.getFirstName());
            lastName.setText(mPatient.getLastName());
            cal.setTime(mPatient.getDateOfBirth());
            dateOfBirth.setText(dateFormat.format(cal.getTime()));
            email.setText(mPatient.getEmailAddress());
            phone.setText(mPatient.getPhoneNumber());

            builder.setView(view)
                    .setTitle(getActivity().getString(R.string.edit_profile_title))
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPatient.setFirstName(firstName.getText().toString());
                            mPatient.setLastName(lastName.getText().toString());
                            mPatient.setEmailAddress(email.getText().toString());
                            mPatient.setPhoneNumber(phone.getText().toString());
                            mPatient.setDateOfBirth(cal.getTime());
                            mPatient.setLastUpdtDatetime(new Date());

                            HttpService.pushPatient(getActivity(), mPatient);
                        }
                    });

        } else {
            firstName.setText(mDoctor.getFirstName());
            lastName.setText(mDoctor.getLastName());
            email.setText(mDoctor.getEmailAddress());
            phone.setText(mDoctor.getPhoneNumber());

            builder.setView(view)
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDoctor.setFirstName(firstName.getText().toString());
                            mDoctor.setLastName(lastName.getText().toString());
                            mDoctor.setEmailAddress(email.getText().toString());
                            mDoctor.setPhoneNumber(phone.getText().toString());

                            HttpService.pushDoctor(getActivity(), mDoctor);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileEditDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
