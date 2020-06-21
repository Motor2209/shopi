package com.iadvigun.study.utils;

import com.iadvigun.study.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductAPI {

    @GET("product")
    Call<List<Product>> getProduct();

    @POST("product")
    Call<Product> createPost(@Body Product product);

    @PUT("product/{id}")
    Call<Product> createPUT(@Path("id") Long id, @Body Product product);

    @DELETE("product/{id}")
    Call<Void> deleteProduct (@Path("id") Long id);

}
