package com.iadvigun.study.utils;

import com.iadvigun.study.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AlarmApi {
    @GET("alarms")
    Call<List<Product>> getAlarms();

}

