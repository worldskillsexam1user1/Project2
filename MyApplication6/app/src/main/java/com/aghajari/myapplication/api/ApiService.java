package com.aghajari.myapplication.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static Api api = null;
    private static Call<ResponseBody> call = null;

    private static void init(){
        try {
            if (call != null) {
                call.cancel();
                call = null;
            }
        }catch (Exception ignore){}

        if (api == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .callTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            api = new Retrofit.Builder()
                    .baseUrl("https://ktvu.ir/worldskills/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build().create(Api.class);
        }
    }

    private static void start(Callback callback, Call<ResponseBody>  c){
        call = c;
        call.enqueue(new MyCallback(callback));
    }

    public static void getData(Callback callback){
        init();
        start(callback, api.getData());
    }

    public static abstract class Callback {

        public abstract void onResponse(String json);

        public void onError(boolean fromNetwork, int code){}
    }

    private static class MyCallback implements retrofit2.Callback<ResponseBody> {
        private final Callback mCallback;

        private MyCallback(Callback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                try {
                    mCallback.onResponse(response.body().string());
                } catch (IOException e) {
                    mCallback.onError(false, -1);
                }
            } else {
                mCallback.onError(false, response.code());
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mCallback.onError(true, -2);
        }

    }
}
