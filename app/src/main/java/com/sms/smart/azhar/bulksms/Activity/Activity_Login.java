package com.sms.smart.azhar.bulksms.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sms.smart.azhar.bulksms.R;
import com.sms.smart.azhar.bulksms.Receiver.NetworkConnectionReceiver;
import com.sms.smart.azhar.bulksms.Utility.AppController;
import com.sms.smart.azhar.bulksms.Utility.Operation;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission_group.CAMERA;


public class Activity_Login extends AppCompatActivity implements NetworkConnectionReceiver.ConnectivityRecieverListener {


    Boolean isConnected;
    LinearLayout myLinearLayout;
    public static final String NA = "NA";
    EditText usernameEditText, passwordEditText;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ListView showContactList;
    public static Activity_Login activityLogin;
    EditText etSinglePhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLogin = this;
        if (checkPermission()) {

        } else if (!checkPermission()) {
            requestPermission();
        }
        String persistenceUsernameString = Operation.getString("email", "");
        if (persistenceUsernameString.length() > 0) {
            startActivity(new Intent(AppController.getAppContext(), Activity_Main.class));
            finish();
        } else {
              setContentView(R.layout.login);

        }

        initialiseByID();



    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS, READ_CONTACTS, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;


                    if (locationAccepted && cameraAccepted && readPhoneAccepted) {

                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_PHONE_STATE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Activity_Login.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void initialiseByID() {
        myLinearLayout = (LinearLayout) findViewById(R.id.activity_main);
        usernameEditText = (EditText) findViewById(R.id.usernameET);
        passwordEditText = (EditText) findViewById(R.id.passwordET);
        showContactList = (ListView) findViewById(R.id.showContactList);
        etSinglePhoneNumber = (EditText) findViewById(R.id.etSinglePhoneNumber);


    }
    public void goSignIn(View view) {


        if (checkConnectivity()) {

            String emailString = usernameEditText.getText().toString();
            String passwordString = passwordEditText.getText().toString();
            if (emailString.length() == 0) {
                usernameEditText.setError("Insert Username");
            }
            if (passwordString.length() == 0) {
                passwordEditText.setError("Insert Password");
            }
            if (emailString.length() > 0 && passwordString.length() > 0) {
                Operation.saveString("email", emailString);
                startActivity(new Intent(this, Activity_Main.class));
                finish();
            }

        } else {
            showSnackBar();
        }

    }
    private void showSnackBar() {

        Snackbar.make(myLinearLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.btn_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }).setActionTextColor(Color.RED).show();
    }
    private boolean checkConnectivity() {
        return NetworkConnectionReceiver.isConnected();
    }
    @Override
    public void OnNetworkChange(boolean inConnected) {
        this.isConnected = inConnected;
    }


}
