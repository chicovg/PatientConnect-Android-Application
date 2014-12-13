package com.chicovg.symptommgmt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.chicovg.symptommgmt.rest.domain.Doctor;
import com.chicovg.symptommgmt.rest.domain.User;

import static com.chicovg.symptommgmt.util.BundleUtil.fromBundle;
import static com.chicovg.symptommgmt.util.BundleUtil.toBundle;

/**
 *
 */
public class DoctorProfile extends Activity {

    private User mUser;
    private Doctor mDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        mUser = (User)fromBundle(getIntent().getBundleExtra(getString(R.string.USER_EXTRA)));
        mDoctor = (Doctor)fromBundle(getIntent().getBundleExtra(getString(R.string.DOCTOR_EXTRA)));

        ((TextView)findViewById(R.id.username)).setText(mDoctor.getUsername());
        ((TextView)findViewById(R.id.firstName)).setText(mDoctor.getFirstName());
        ((TextView)findViewById(R.id.lastName)).setText(mDoctor.getLastName());
        ((TextView)findViewById(R.id.email)).setText(mDoctor.getEmailAddress());
        ((TextView)findViewById(R.id.phone)).setText(mDoctor.getPhoneNumber());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(getString(R.string.USER_EXTRA), toBundle(mUser));
                upIntent.putExtra(getString(R.string.DOCTOR_EXTRA), toBundle(mDoctor));
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            case R.id.action_logout :
                break;
        }
        return true;
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
