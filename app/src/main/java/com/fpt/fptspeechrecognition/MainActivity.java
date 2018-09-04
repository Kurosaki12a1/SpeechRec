package com.fpt.fptspeechrecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.fpt.GSON.SpeechAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btnClick) Button btnClick;
    @BindView(R.id.txtView) TextView txtView;
    @BindView(R.id.btnSelect) Button btnSelect;

    private String APIKey= "7d992a2996b54c14b0d1503d49c7ed45";

    private static String URL="https://api.openfpt.vn/";

    private int codeSuccessful = 1;

    private String textVoice="";

    static Retrofit retrofit = null;

    Observable<SpeechAPI> observable;

    APIService apiService;

    Uri fileUriRecord;

    File fileRecord;

    MultipartBody.Part body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkPermissionApp()) {
                requestForSpecificPermission();
            }
        }

       /* try {
            String sDecoded =  URLEncoder.encode(myString,"UTF-8");
            txtView.setText(sDecoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/



    }

    private boolean checkPermissionApp() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }


    public static Retrofit getRetrofit(){
        if(retrofit==null){
            Retrofit.Builder builder=new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

            OkHttpClient.Builder httpClientBuilder=new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60,TimeUnit.SECONDS)
                    .writeTimeout(60,TimeUnit.SECONDS);

            httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

            retrofit=builder.client(httpClientBuilder.build()).build();
        }
        return retrofit;
    }

    @OnClick(R.id.btnClick)
    public void onClick() {

            apiService =  getRetrofit().create(APIService.class);

            observable = apiService.getTextFromVoice(APIKey, body);

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SpeechAPI>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(SpeechAPI speechAPI) {
                            switch (speechAPI.getStatus()){
                                case 0:
                                    txtView.setText("Đang xử lý....");
                                    textVoice = speechAPI.getHypotheses().get(0).getUtterance();
                                    break;
                                case 1:
                                    textVoice= "Không có âm thanh";
                                    break;
                                case 2:
                                    textVoice= "Bị Hủy";
                                    break;
                                case 9:
                                    textVoice="Hệ thống bận";
                                    break;
                                case 5:
                                    textVoice="Hệ thống bận , xin vui lòng thử lại";
                                    break;

                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                                txtView.setText(textVoice);
                      /*  try {
                            String encode = URLDecoder.decode(textVoice, "UTF-8");
                            txtView.setText(encode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }*/
                        }
                    });

    }

    @OnClick(R.id.btnSelect)
    public void onSelect(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please select your record"), codeSuccessful);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == codeSuccessful && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // get URI from data choose
            fileUriRecord=data.getData();

            //get File from exactly Path
            fileRecord=new File(getRealPathFromURI(fileUriRecord));

            //get RequestBody then convert to multipart
            RequestBody reqFile=RequestBody.create(MediaType.parse("multipart/form-data"),fileRecord);

            body=MultipartBody.Part.createFormData("audio",fileRecord.getName(),reqFile);

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



}
