package com.sms.smart.azhar.bulksms.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.sms.smart.azhar.bulksms.Contact;
import com.sms.smart.azhar.bulksms.R;
import com.sms.smart.azhar.bulksms.Receiver.NetworkConnectionReceiver;
import com.sms.smart.azhar.bulksms.Utility.Operation;
import com.sms.smart.azhar.bulksms.Utility.ReadAllFile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Activity_Main extends AppCompatActivity implements NetworkConnectionReceiver.ConnectivityRecieverListener {

    Boolean isConnected;
    public static final String NA = "NA";
    String PathHolder;
    // GUI controls
    ConstraintLayout myConstraintLayout;
    List<Contact> listContact = new ArrayList<>();
    LinearLayout myLinearLayout;
    ListView showContactList;
    RadioGroup radioGroup;
    EditText etSinglePhoneNumber;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_panel);
        // bind GUI elements with local controls
        bindGUIElementWithLocalControls();
        myConstraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        listContact = Operation.loadContacts(this);

    }

    private void bindGUIElementWithLocalControls() {

        textInputLayout=(TextInputLayout)findViewById(R.id.input_SinglePhoneNumber);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        myLinearLayout = (LinearLayout) findViewById(R.id.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        showContactList = (ListView) findViewById(R.id.showContactList);
        etSinglePhoneNumber = (EditText) findViewById(R.id.etSinglePhoneNumber);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select Sender ID *")) {

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button

                        showContactList.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1: // secondbutton
                        textInputLayout.setVisibility(View.GONE);
                    showContactList.setVisibility(View.VISIBLE);
                    Operation.getPhoneList(showContactList);
                        break;

                    case 2: // secondbutton
                        textInputLayout.setVisibility(View.GONE);
                    showContactList.setVisibility(View.VISIBLE);
                    Toast.makeText(Activity_Main.this, "vjasdmnf ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 7);
                        break;
                }
            }
        });



    }

    public void sms(View view) {
        if (checkConnectivity()) {
            //Operation.sms(view);

String userPhoneNumber="";
            for (int i=0;i<listContact.size();i++){
               userPhoneNumber= listContact.get(i).getContactPhoneNumber();
                Operation.smsToServer("7b52009b64fd0a2a49e6d8a939753077792b055463bc077f9d41c9b27e8fd7ba727adfd0","text",userPhoneNumber,"8804445629100","test");
            }

        } else {
            showSnackBar();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch(requestCode){

            case 7:

                if(resultCode==RESULT_OK){

                    Uri uri = data.getData();
                    PathHolder =ReadAllFile.getPath(getApplicationContext(),uri);
                    Log.e("Path:",PathHolder);
                    if(PathHolder.contains(".xlsx")){
                        try {
                            ReadAllFile.readXLSXFile(PathHolder,showContactList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(PathHolder.contains(".xls")){
                        ReadAllFile.readExcelFile(getApplicationContext(),PathHolder,showContactList);
                    }else if(PathHolder.contains(".txt")){
                        ReadAllFile.readTxtfile(getApplicationContext(),PathHolder,showContactList);
                    }else {
                        Toast.makeText(this, "Invalid file", Toast.LENGTH_SHORT).show();
                    }

                }
                break;

        }
    }
    private void  read_file(Context context, String filename) {
        try {
            // Open stream to read file.
            FileInputStream in = new FileInputStream(filename);

            BufferedReader br= new BufferedReader(new InputStreamReader(in));

            StringBuilder sb= new StringBuilder();
            String s= null;
            while((s= br.readLine())!= null)  {
                sb.append(s).append("\n");
            }
            Toast.makeText(context, ""+sb.toString(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
  /*  @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }*/

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    private void showSnackBar() {

        Snackbar.make(myConstraintLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
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

