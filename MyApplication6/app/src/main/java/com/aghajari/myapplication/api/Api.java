package com.aghajari.myapplication.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface Api {

    @Headers("authorization: Bearer jfhdsjfnsdkfjks;dufgw;irhk98543g8t4gr6e5g78f4d5g5tbd")
    @GET("get_data.php")
    Call<ResponseBody> getData();
}
