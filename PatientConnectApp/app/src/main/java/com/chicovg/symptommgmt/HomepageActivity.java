package com.chicovg.symptommgmt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.chicovg.symptommgmt.service.HttpService;


public class HomepageActivity extends Activity {

    private static final String TAG = "HomepageActivity";

    private Button mLoginButton;
    private EditText mUsernameField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_homepage);

        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mUsernameField = (EditText)findViewById(R.id.username);
        mPasswordField = (EditText)findViewById(R.id.password);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HttpService.BROADCAST_HTTP_REQUEST_FAILED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HttpService.BROADCAST_LOGIN_FAILED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HttpService.BROADCAST_NO_CONNECTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    protected void login(){
        setProgressBarIndeterminate(true);
        String uName = mUsernameField.getText().toString();
        String pass = mPasswordField.getText().toString();
        HttpService.login(this, uName, pass);
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
                Bundle userBundle = intent.getBundleExtra(getString(R.string.USER_EXTRA));
                Bundle responseEntityBundle = intent.getBundleExtra(getString(R.string.HTTP_RESPONSE_ENTITY_EXTRA));
                if(HttpService.ACTION_PULL_PATIENT.equals(requestAction)) {
                    Intent pIntent = new Intent(HomepageActivity.this, PatientDetail.class);
                    pIntent.putExtra(getString(R.string.USER_EXTRA), userBundle) ;
                    pIntent.putExtra(getString(R.string.PATIENT_EXTRA), responseEntityBundle);
                    startActivity(pIntent);
                }
                if(HttpService.ACTION_PULL_DOCTOR.equals(requestAction)) {
                    Intent dIntent = new Intent(HomepageActivity.this, DoctorDashboard.class);
                    dIntent.putExtra(getString(R.string.USER_EXTRA), userBundle);
                    dIntent.putExtra(getString(R.string.DOCTOR_EXTRA), responseEntityBundle);
                    startActivity(dIntent);
                }
            } else if(HttpService.BROADCAST_HTTP_REQUEST_FAILED.equals(action)){
                Log.e("", "Http request failed!");
            } else if(HttpService.BROADCAST_LOGIN_FAILED.equals(action)){
                Toast.makeText(HomepageActivity.this, R.string.error_save_failed, Toast.LENGTH_LONG).show();
            } else if(HttpService.BROADCAST_NO_CONNECTION.equals(action)){
                Toast.makeText(HomepageActivity.this, getString(R.string.error_login_no_connection), Toast.LENGTH_LONG).show();

            }
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
