package com.iadvigun.study.utils;

import com.iadvigun.study.Product;
import com.iadvigun.study.Shop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ShopApi {
    @GET("shop")
    Call<List<Shop>> getShops();

    @POST("shop")
    Call<Shop> createPost(@Body Shop shop);

    @PUT("shop/{id}")
    Call<Shop> createPUT(@Path("id") Long id, @Body Shop shop);

    @DELETE("shop/{id}")
    Call<Void> deleteProduct (@Path("id") Long id);
}
