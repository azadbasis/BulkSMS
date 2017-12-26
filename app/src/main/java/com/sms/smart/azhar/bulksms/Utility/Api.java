package com.sms.smart.azhar.bulksms.Utility;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sadi on 11/18/2017.
 */

public interface Api {

    //String BASE_URL = "http://offerian.com/";
    //String BASE_URL = "http://192.168.0.119/renewableenergy/api/";
    String BASE_URL = "http://portal.smsinbd.com/";

//    String api_key="7b52009b64fd0a2a49e6d8a939753077792b055463bc077f9d41c9b27e8fd7ba727adfd0";
//                String type="text";
//                //String contacts="01717121839";
//                String senderid="";
//                String msg="hello";


    @GET("smsapi")
    Call<String> sendPhonNumber(
            @Query("api_key") String api_key,
            @Query("type") String type,
            @Query("contacts") String contacts,
            @Query("senderid") String senderid,
            @Query("hello") String hello

    );





}
