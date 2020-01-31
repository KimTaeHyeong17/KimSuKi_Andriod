package com.example.kimsuki_naver_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_finder;
    String AUDIO_URL = "audio_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindUI();
    }

    private void bindUI() {
        btn_finder = findViewById(R.id.btn_finder);
        btn_finder.setOnClickListener(this);
    }
    private void getAudioFile(){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }
    private void playAudioFile(String uriStr){
        /** open player  */

        Intent intent = new Intent(this, URLMediaPlayerActivity.class);
        intent.putExtra("uri", uriStr);
//        intent.putExtra(IMG_URL, "https://dl.dropboxusercontent.com/u/2763264/RSS%20MP3%20Player/img3.jpg");
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                //the selected audio.
                Uri uri = data.getData();
                Log.e("data uri", String.valueOf(uri));
                AUDIO_URL = String.valueOf(uri);
                playAudioFile(String.valueOf(uri));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finder:
                getAudioFile();
                break;
            default:

                break;
        }
    }
}
