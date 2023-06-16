package com.mobiledevelopment.core.repository;

import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationRequest;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.NotificationResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAD7m4f_c:APA91bGvOYSoloSJ5aB_BwbXEAqywVY8k06qM8TefvyAVpGPKsFCYrznPc2qqy-AVhLutkfEEKcBEY3z7SN8x2b0UeU3IZwGySIttXvW-AseUsyoGRAKxd2qxSMzD5YJ5u3iZwBOO8uD"
    })
    @POST("send")
    Call<NotificationResult> sendNotification(@Body NotificationRequest notificationRequest);
}
