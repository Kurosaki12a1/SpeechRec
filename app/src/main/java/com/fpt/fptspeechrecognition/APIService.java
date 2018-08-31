package com.fpt.fptspeechrecognition;


import com.fpt.GSON.SpeechAPI;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {
    @Multipart
    @POST("/fsr")
    Observable<SpeechAPI> getTextFromVoice(@Header("api_key") String keyAPI , @Part MultipartBody.Part file);

}
