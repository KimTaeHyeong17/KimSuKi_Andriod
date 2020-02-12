package com.example.kimsuki_naver_ai.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.kimsuki_naver_ai.Adapter.Adapter;
import com.example.kimsuki_naver_ai.FileChooser;
import com.example.kimsuki_naver_ai.Model.AudioModel;
import com.example.kimsuki_naver_ai.R;
import com.example.kimsuki_naver_ai.Service.MyService;
import com.loopj.android.http.RequestParams;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finder, btn_recordStart, btn_recordStop;
    private ListView listview;
    private ArrayList<AudioModel> audioModelArrayList = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindUI();
        requestPermission();
    }
    //UI
    private void bindUI() {
        btn_finder = findViewById(R.id.btn_finder);
        btn_recordStart = findViewById(R.id.btn_recordStart);
        btn_recordStop = findViewById(R.id.btn_recordStop);
        listview = findViewById(R.id.listview);

        btn_finder.setOnClickListener(this);
        btn_recordStart.setOnClickListener(this);
        btn_recordStop.setOnClickListener(this);

        adapter = new Adapter(this, audioModelArrayList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioModel data = audioModelArrayList.get(position);
                uploadFile(data.getPath());
//                playAudioFile(data.getUri(), data.getName());
            }
        });

    }
    //FUNCTIONS
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
    //NETWORK
    private void uploadFile(String path) {
        RequestParams params = new RequestParams();

        File file = new File(path);

        try {
            params.put("voicefile", file);
            params.put("phone", "test010");
            params.put("createdAt", "test");
            Log.e("file in parameter","success");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("file in parameter","fail");

        }
//        Network.post(this, "/voices", params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                try {
//                    Toast.makeText(getApplicationContext(), response.getString("id"), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.d("Failed: ", "" + statusCode);
//                Log.d("Error : ", "" + throwable);
//            }
//        });//network
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    InputStream in = new FileInputStream(FileChooser.getPath(this, uri));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //the selected audio.
                Uri AudioUri = data.getData();
                String fileName = getFileName(AudioUri);

                Log.e("audio file name : ", fileName);
                Log.e("audio file uri : ", AudioUri.getPath());
                Log.e("new path", FileChooser.getPath(this, uri));


                AudioModel audioModel = new AudioModel();
                audioModel.setDate("test date");
                audioModel.setName(fileName);
                audioModel.setUri(AudioUri);
                audioModel.setPath(FileChooser.getPath(this, uri));

                audioModelArrayList.add(audioModel);
                adapter.notifyDataSetChanged();

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
            case R.id.btn_recordStart:
                Log.d("test", "액티비티-서비스 시작버튼클릭");
                Intent intent1 = new Intent(
                        getApplicationContext(),//현재제어권자
                        MyService.class); // 이동할 컴포넌트
                intent1.putExtra("number", "fromButton");
                startService(intent1); // 서비스 시작
                break;
            case R.id.btn_recordStop:
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
