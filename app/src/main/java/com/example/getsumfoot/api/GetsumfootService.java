package com.example.getsumfoot.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetsumfootService {
    String BASE_URL = "https://bullgota.ml";

    @GET("/model/info/{modelNum}")
    Call<ResponseSelectModel> checkModel(@Path("modelNum") String modelNum);

    @GET("/list/marker")
    Call<ResponseWithMarkerData> getMarkerAll();

    @Headers("Content-Type: application/json")
    @PUT("/model/lend/{modelNum}")
    Call<ResponseLendModel> lendModel(@Path("modelNum") String modelNum);

    @Headers("Content-Type: application/json")
    @PUT("/model/return/{modelNum}")
    Call<ResponseReturnModel> returnModel(@Path("modelNum") String modelNum, @Body RequestReturnModel requestReturnModel);
}

