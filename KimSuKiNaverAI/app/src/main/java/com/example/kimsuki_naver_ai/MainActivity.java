package com.example.kimsuki_naver_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_finder;

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
    //FUNCTIONS
    private void getAudioFile(){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }
    private void playAudioFile(Uri uri, String AudioName){
        /** open player  */
        Intent intent = new Intent(this, URLMediaPlayerActivity.class);
        intent.setData(uri);
        intent.putExtra("name",AudioName);
        startActivity(intent);

    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                //the selected audio.
                Uri AudioUri = data.getData();
                String fileName = getFileName(AudioUri);

                Log.e("audio file name : ", fileName);
                playAudioFile(AudioUri,fileName);

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
