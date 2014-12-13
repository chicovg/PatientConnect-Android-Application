package com.chicovg.symptommgmt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chicovg.symptommgmt.rest.domain.CheckIn;
import com.chicovg.symptommgmt.rest.domain.CheckInResponse;
import com.chicovg.symptommgmt.rest.domain.MedicationTaken;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;


/**
 *
 */
public class CheckInDetailFragment extends DialogFragment {
    private static final String CHECK_IN_ARG = "CHECK_IN";

    private CheckIn mCheckIn;

    public static CheckInDetailFragment newInstance(CheckIn checkIn) {
        CheckInDetailFragment fragment = new CheckInDetailFragment();
        Bundle args = new Bundle();
        args.putBundle(CHECK_IN_ARG, toBundle(checkIn));
        fragment.setArguments(args);
        return fragment;
    }
    public CheckInDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCheckIn = (CheckIn)fromBundle(getArguments().getBundle(CHECK_IN_ARG));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.fragment_check_in_dialog, null);
        if(null != mCheckIn.getResponses()){
            List<CheckInResponse> responses = mCheckIn.getResponses();
            Collections.sort(responses, new Comparator<CheckInResponse>() {
                @Override
                public int compare(CheckInResponse lhs, CheckInResponse rhs) {
                    return lhs.getQuestionId() - rhs.getQuestionId();
                }
            });
            for(CheckInResponse response: responses){
                View questionResponseView = getActivity().getLayoutInflater().inflate(R.layout.check_in_question_display, null);
                ((TextView)questionResponseView.findViewById(R.id.question)).setText(getQuestionText(response.getQuestionId(), response.getMedication()));
                String responseString = response.getResponse();
                if(null!=response.getMedicationTaken()){
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity());
                    MedicationTaken mt = response.getMedicationTaken();
                    String medicationText = dateFormat.format(mt.getTimestamp()) + " " + timeFormat.format(mt.getTimestamp());
                    responseString = String.format("%s: %s", responseString, medicationText);
                }
                ((TextView)questionResponseView.findViewById(R.id.response)).setText(responseString);
                viewGroup.addView(questionResponseView);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewGroup)
                .setTitle(getActivity().getString(R.string.check_in_detail_dialog_title))
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckInDetailFragment.this.getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    private String getQuestionText(int questionId, String medication){
        switch (questionId){
            case CheckIn.OVERALL_PAIN_QUESTION_ID: return getActivity().getString(R.string.check_in_question_1);
            case CheckIn.GENERAL_MEDICATION_QUESTION_ID: return getActivity().getString(R.string.check_in_question_2);
            case CheckIn.SPECIFIC_MEDICATION_QUESTION_ID: return
                    String.format(getActivity().getString(R.string.check_in_question_3), medication);
            case CheckIn.EATING_DRINKING_QUESTION_ID: return getActivity().getString(R.string.check_in_question_4);
        }
        return null;
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
