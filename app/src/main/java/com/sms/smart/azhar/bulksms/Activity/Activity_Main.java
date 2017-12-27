package com.sms.smart.azhar.bulksms.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Region;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sms.smart.azhar.bulksms.Contact;
import com.sms.smart.azhar.bulksms.R;
import com.sms.smart.azhar.bulksms.Receiver.AlarmReceiver;
import com.sms.smart.azhar.bulksms.Receiver.NetworkConnectionReceiver;
import com.sms.smart.azhar.bulksms.Utility.Operation;
import com.sms.smart.azhar.bulksms.Utility.ReadAllFile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Activity_Main extends AppCompatActivity implements NetworkConnectionReceiver.ConnectivityRecieverListener {
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    Boolean isConnected;
    public static final String NA = "NA";
    String PathHolder;
    // GUI controls
    ConstraintLayout myConstraintLayout;
    List<Contact> listContact = new ArrayList<>();
    LinearLayout myLinearLayout;
    ListView showContactList;
    RadioGroup radioGroup,radioGroupSmsType;
    EditText etSinglePhoneNumber,etSmsBody;
    private String singlePhoneNumber;
    TextInputLayout textInputLayout;
    private TextView tvDate;
    private ImageView imgDate;
    private Button btnSendSms;

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    int cYear;
    int cMonth;
    int cDay;
    int cHour;
    int cMinute;
    Calendar customCalender;
    long startTime;
    private String type,contact,senderId,msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_panel);
        // bind GUI elements with local controls

        customCalender = Calendar.getInstance();
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        bindGUIElementWithLocalControls();
        myConstraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        //listContact = Operation.loadContacts(this);
        //dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    }


    private void bindGUIElementWithLocalControls() {

        tvDate = (TextView)findViewById(R.id.tvDate);
        DateFormat dateFormatter = new SimpleDateFormat("dd-M-yyyy hh:mm");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        tvDate.setText(s);


        imgDate = (ImageView) findViewById(R.id.imgDate);
        btnSendSms = (Button) findViewById(R.id.btnSendSms);

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                msg = etSmsBody.getText().toString();
                if(TextUtils.isEmpty(senderId)){
                    Toast.makeText(Activity_Main.this, "Select sender id!", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(type)){
                    Toast.makeText(Activity_Main.this, "Select type!", Toast.LENGTH_SHORT).show();

                }else if(listContact.size()==0){
                    Toast.makeText(Activity_Main.this, "Input phone number!", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(msg)){
                    Toast.makeText(Activity_Main.this, "Input msg!", Toast.LENGTH_SHORT).show();

                }else {

                  for(int i = 0; i<listContact.size();i++){
                      Operation.smsToServer("7b52009b64fd0a2a49e6d8a939753077792b0554d1b7cd7693d34c37216fb113611290cd",type,listContact.get(i).getContactPhoneNumber(),senderId,msg);
                  }

                }


            }
        });

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();

            }
        });




        textInputLayout=(TextInputLayout)findViewById(R.id.input_SinglePhoneNumber);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroupSmsType = (RadioGroup) findViewById(R.id.radioGroupSmsType);
        myLinearLayout = (LinearLayout) findViewById(R.id.activity_main);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        showContactList = (ListView) findViewById(R.id.showContactList);
        etSinglePhoneNumber = (EditText) findViewById(R.id.etSinglePhoneNumber);

        etSinglePhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Contact contact = new Contact();
                contact.setContactPhoneNumber(etSinglePhoneNumber.getText().toString());
                listContact.add(contact);
            }
        });

        etSmsBody = (EditText) findViewById(R.id.etSmsBody);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Select Sender ID *")){
                   senderId = spinner.getSelectedItem().toString();
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        radioGroupSmsType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);


                switch (index) {
                    case 0: // first button
                        type = "text";
                        break;
                    case 1: // secondbutton
                        type = "unicode";
                        break;
                }
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
                        listContact = Operation.loadContacts(getApplicationContext());
                        Operation.getPhoneList(showContactList);
                        showContactList.setVisibility(View.VISIBLE);

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

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        //*************Call Time Picker Here ********************

                        cYear = year;
                        cMonth = monthOfYear + 1;
                        cDay = dayOfMonth;

                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

//                        mHour = hourOfDay;
//                        mMinute = minute;
                        cHour = hourOfDay;
                        cMinute = minute;

                        customCalender.set(cYear,cMonth,cDay,cHour,cMinute);

                         startTime = customCalender.getTimeInMillis();
                        startAlarm((int) startTime);
                        tvDate.setText(date_time+" "+hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void startAlarm(int interval) {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //int interval = 10000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void sms(View view) {
        if (checkConnectivity()) {
            //Operation.sms(view);

String userPhoneNumber="";
            for (int i=0;i<listContact.size();i++){
               userPhoneNumber= listContact.get(i).getContactPhoneNumber();
                Operation.smsToServer("7b52009b64fd0a2a49e6d8a939753077792b0554d1b7cd7693d34c37216fb113611290cd","text","8801723335972","8804445629100","this test msg");
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
                            listContact = Operation.contactList;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(PathHolder.contains(".xls")){
                        ReadAllFile.readExcelFile(getApplicationContext(),PathHolder,showContactList);
                        listContact = Operation.contactList;
                    }else if(PathHolder.contains(".txt")){
                        ReadAllFile.readTxtfile(getApplicationContext(),PathHolder,showContactList);
                        listContact = Operation.contactList;
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

