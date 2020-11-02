package com.company.blumeSunzi.Remote;

import com.company.blumeSunzi.Model.FCMResponse;
import com.company.blumeSunzi.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA_afe4eQ:APA91bEFW19jwLAE2SW-cB6xExN1pVOywTnZrri9_VLna-ENRLN_WuZu1ohnt2c6-T37c7ZNqv5yu2DgBHHQURXrrvxALfrvcY-xUTF34eWv4LuHfVsnkPxO9bbfOyjCflUdAo265na0"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}

