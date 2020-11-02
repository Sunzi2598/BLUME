package com.company.blumeSunzi.Remote;

import io.reactivex.Observable ;

import androidx.lifecycle.Observer;

import com.company.blumeSunzi.Model.BraintreeToken;
import com.company.blumeSunzi.Model.BraintreeTransaction;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ICloudFunctions  {
    @GET("token")
    Observable<BraintreeToken>getToken();

    @POST("checkout")
    @FormUrlEncoded
    Observable<BraintreeTransaction> submitPayment(
            @Field("amount")double amount,
            @Field("payment_method_nonce") String nonce);
}