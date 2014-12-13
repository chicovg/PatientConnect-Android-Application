package com.chicovg.symptommgmt;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.chicovg.symptommgmt.rest.domain.*;
import com.chicovg.symptommgmt.service.ContentService;
import com.chicovg.symptommgmt.service.HttpService;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by victorguthrie on 11/14/14.
 */
public class CheckInDialogFragment extends DialogFragment {

    private Long mPatientId;
    private List<Medication> mMedications;
    private final Set<CheckInResponse> checkInResponses = new HashSet<CheckInResponse>();
    private final Map<String, Date> medicationTakenMap = new HashMap<>();

    private static final String ARG_PATIENT_ID = "patientId";

    public static CheckInDialogFragment newInstance(Long patientId) {
        CheckInDialogFragment fragment = new CheckInDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_PATIENT_ID, patientId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CheckInDialogFragment() {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPatientId = getArguments().getLong(ARG_PATIENT_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMedications = ContentService.getMedicationsByPatientId(getActivity(), mPatientId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ViewGroup viewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.fragment_check_in_dialog, null);

        viewGroup.addView(getQuestionView(CheckIn.OVERALL_PAIN_QUESTION_ID, R.string.check_in_question_1, R.string.response_well_controlled, R.string.response_moderate, R.string.response_severe));
        viewGroup.addView(getQuestionView(CheckIn.GENERAL_MEDICATION_QUESTION_ID, R.string.check_in_question_2, R.string.response_yes, R.string.response_no));

        for(Medication medication : mMedications){
            viewGroup.addView(getMedicationQuestionView(medication.getName()));
        }
        viewGroup.addView(getQuestionView(CheckIn.EATING_DRINKING_QUESTION_ID, R.string.check_in_question_4, R.string.response_no, R.string.response_some, R.string.response_i_can_eat));

        builder.setView(viewGroup);
        builder.setTitle(R.string.check_in_dialog_title);
        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CheckIn checkin = new CheckIn();
                checkin.setPatientId(mPatientId);
                checkin.setResponses(new LinkedList<>(checkInResponses));
                checkin.setStatus(CheckInStatus.COMPLETED);
                checkin.setTimestamp(new Date());
                for(CheckInResponse response : checkin.getResponses()){
                    if (response.getQuestionId() == CheckIn.OVERALL_PAIN_QUESTION_ID){
                        checkin.setOverallPainLevel(PainLevel.valueOf(response.getResponse()));
                    }
                    if(response.getQuestionId() == CheckIn.EATING_DRINKING_QUESTION_ID){
                        checkin.setUnableToEat(response.getResponse().equals(getString(R.string.response_i_can_eat)));
                    }
                }
                HttpService.pushCheckIn(getActivity(), checkin);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CheckInDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    private View getQuestionView(final int questionId, final int questionResourceId, int ... responseResourceIds){
        View questionView = getActivity().getLayoutInflater().inflate(R.layout.check_in_question, null);

        TextView qText = (TextView)questionView.findViewById(R.id.question);
        qText.setText(getString(questionResourceId));

        final RadioGroup responses = (RadioGroup)questionView.findViewById(R.id.responses);
        for(int r : responseResourceIds) {
            RadioButton response = new RadioButton(getActivity());
            response.setText(getString(r));
            responses.addView(response);
        }
        responses.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton)responses.findViewById(checkedId);
                CheckInResponse response = new CheckInResponse();
                response.setQuestionId(questionId);
                response.setResponse(CheckInDialogFragment.this.translateResponse(null!=button.getText() ? button.getText().toString() : ""));
                checkInResponses.add(response);
            }
        });
        return questionView;
    }

    private View getMedicationQuestionView(final String medicationName){
        final int questionResourceId = R.string.check_in_question_3;
        final int questionId = CheckIn.SPECIFIC_MEDICATION_QUESTION_ID;
        final View questionView = getActivity().getLayoutInflater().inflate(R.layout.check_in_question_medication, null);

        TextView qText = (TextView)questionView.findViewById(R.id.question);
        qText.setText(String.format(getString(questionResourceId), medicationName));
        final TextView medQText = (TextView)questionView.findViewById(R.id.medication_taken_datetime_question);
        medQText.setText(String.format(getString(R.string.check_in_question_3_med_taken), medicationName));

        final Calendar c = Calendar.getInstance(Locale.getDefault());
        final DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
        final View takenDatetime = questionView.findViewById(R.id.medication_taken_datetime);
        final Button takenDate = (Button) questionView.findViewById(R.id.medication_taken_date);
        final Button takenTime = (Button) questionView.findViewById(R.id.medication_taken_time);
        final TextView date = (TextView) questionView.findViewById(R.id.date);
        final TextView time = (TextView) questionView.findViewById(R.id.time);

        final CheckInResponse response = new CheckInResponse();

        takenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        date.setText(dateFormat.format(c.getTime()));
                        response.setMedicationTaken(new MedicationTaken(mPatientId, medicationName, c.getTime()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        takenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        time.setText(timeFormat.format(c.getTime()));
                        response.setMedicationTaken(new MedicationTaken(mPatientId, medicationName, c.getTime()));
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
            }
        });

        final RadioGroup responses = (RadioGroup)questionView.findViewById(R.id.responses);
        final int[] responseResourceIds = {R.string.response_yes, R.string.response_no};
        for(int i=0; i<responseResourceIds.length; i++) {
            RadioButton responseButton = new RadioButton(getActivity());
            responseButton.setId(i);
            responseButton.setText(getString(responseResourceIds[i]));
            responses.addView(responseButton);
        }
        responses.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) responses.findViewById(checkedId);
                response.setQuestionId(questionId);
                response.setMedication(medicationName);
                response.setResponse(button.getText().toString());
                checkInResponses.add(response);

                if (checkedId == 0) {
                    takenDatetime.setVisibility(View.VISIBLE);
                    medQText.setVisibility(View.VISIBLE);
                    takenDate.setVisibility(View.VISIBLE);
                    takenTime.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                } else {
                    takenDatetime.setVisibility(View.GONE);
                    medQText.setVisibility(View.GONE);
                    takenDate.setVisibility(View.GONE);
                    takenTime.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    response.setMedicationTaken(null);
                }
            }
        });
        return questionView;
    }

    private String translateResponse(String responseText){
        String response = responseText;
        if(getString(R.string.response_well_controlled).equals(response)){
            response = PainLevel.WELL_CONTROLLED.name();
        }else if(getString(R.string.response_moderate).equals(response)){
            response = PainLevel.MODERATE.name();
        }else if(getString(R.string.response_severe).equals(response)){
            response = PainLevel.SEVERE.name();
        }
        return response;
    }

    private String logTag(){
        return "CheckInDialogFragment";
    }

}
