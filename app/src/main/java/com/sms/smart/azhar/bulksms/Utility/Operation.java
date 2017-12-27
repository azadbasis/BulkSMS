package com.sms.smart.azhar.bulksms.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.sms.smart.azhar.bulksms.Contact;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nanosoft-Android on 11/20/2017.
 */

public class Operation {


    public static void saveString(String keyValue, String getValue) {

        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyValue, getValue);
        editor.apply();

    }

    public static String getString(String keyValue, String defaultValue) {

        SharedPreferences sharedPreferences = AppController.getAppContext().getSharedPreferences("SREDA", Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyValue, defaultValue);
    }

    public static void sms(View view) {


        String[] ePhoneNumber = {"01717121839", "01723335972"};
        String message = "hellow !it is test sms";
        String messageaddrese = "Nanosoft";
        SmsManager smsManager = SmsManager.getDefault();
        String sendNo = "";
        for (String temp : ePhoneNumber) {
            sendNo = temp;
            smsManager.sendTextMessage(sendNo, null, message + " " + messageaddrese, null, null);
        }
    }

    public static List<Contact> contactList = new ArrayList<>();

    public static List<Contact> loadContacts(Context applicationContext) {

        // List<Constant> listContact = new ArrayList<>();
        int count = 0;
        contactList = new ArrayList<>();
        Cursor phones = applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            // read all phone contact list name....
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            // read all phone contact list phone number....
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name, phoneNumber);
            contactList.add(contact);
            count++;
        }
        Toast.makeText(applicationContext, "total contact" + count++, Toast.LENGTH_LONG).show();

        phones.close();
        return contactList;
    }


    public static void smsToServer(String api_key, String type, String contacts, String senderid, String msg) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        Api api = retrofit.create(Api.class);
        Call<String> call = api.sendPhonNumber(api_key, type, contacts, senderid, msg);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

//                String respons = response.body();
//                Toast.makeText(AppController.getAppContext(), "RESPONSE " + respons, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public static void getPhoneList(ListView listView) {
        ArrayList<String> coStringList = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            coStringList.add(contactList.get(i).getContactName() + ":  " + " " + contactList.get(i).getContactPhoneNumber());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AppController.getAppContext(), android.R.layout.simple_expandable_list_item_1, coStringList);
        listView.setAdapter(arrayAdapter);
    }


}
