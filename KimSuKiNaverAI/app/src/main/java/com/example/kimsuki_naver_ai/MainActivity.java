package com.example.kimsuki_naver_ai;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_finder, button1, button2;
    ListView listview;
    private ArrayList<Data> arrayList = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindUI();
        requestPermission();
    }

    private void bindUI() {
        btn_finder = findViewById(R.id.btn_finder);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        listview = findViewById(R.id.listview);

        btn_finder.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        adapter = new Adapter(this, arrayList);
        listview.setAdapter(adapter);

        loadFiles();

    }

    //FUNCTIONS
    private void getAudioFile() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 1);
    }

    private void playAudioFile(Uri uri, String AudioName) {
        /** open player  */
        Intent intent = new Intent(this, URLMediaPlayerActivity.class);
        intent.setData(uri);
        intent.putExtra("name", AudioName);
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

    private void loadFiles() {



        adapter.notifyDataSetChanged();
    }

    private void requestPermission() {

        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();

        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();

        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_PHONE_STATE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();

        AndPermission.with(this)
                .runtime()
                .permission(Permission.RECORD_AUDIO)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //the selected audio.
                Uri AudioUri = data.getData();
                String fileName = getFileName(AudioUri);

                Log.e("audio file name : ", fileName);
                playAudioFile(AudioUri, fileName);

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
            case R.id.button1:
                Log.d("test", "액티비티-서비스 시작버튼클릭");
                Intent intent1 = new Intent(
                        getApplicationContext(),//현재제어권자
                        MyService.class); // 이동할 컴포넌트
                intent1.putExtra("number", "fromButton");
                startService(intent1); // 서비스 시작
                break;
            case R.id.button2:
                // 서비스 종료하기
                Log.d("test", "액티비티-서비스 종료버튼클릭");
                Intent intent2 = new Intent(
                        getApplicationContext(),//현재제어권자
                        MyService.class); // 이동할 컴포넌트
                intent2.putExtra("number", "fromButton");
                stopService(intent2); // 서비스 종료

                break;

            default:

                break;
        }
    }
}
