package com.JinxMarket.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAALr58zo:APA91bFYg7abQY5Z_54QlR8346YcDQCup_aH0OX7SeRaAmMV1jL-yKugJ8iKijOK8FsQzXFK4fS_kAvAvrTJ0-m1k4-bFsuoQejCwbTQkMCBHioc8lVi-T1PBgTjj_85fOMKeY5u3qC4"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
