package com.mertadali.walkietalkie.service

import com.mertadali.walkietalkie.util.Constants.Companion.CONTENT_TYPE
import com.mertadali.walkietalkie.util.Constants.Companion.SERVER_KEY
import com.mertadali.walkietalkie.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
   suspend fun postNotification(
        @Body notification : PushNotification

    ): Response<ResponseBody>
}