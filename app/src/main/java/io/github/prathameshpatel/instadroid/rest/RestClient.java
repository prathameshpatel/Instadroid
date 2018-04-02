package io.github.prathameshpatel.instadroid.rest;

import io.github.prathameshpatel.instadroid.Constants;
import io.github.prathameshpatel.instadroid.interfaces.RetrofitService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static RetrofitService getRetrofitService() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService.class);
    }
}
