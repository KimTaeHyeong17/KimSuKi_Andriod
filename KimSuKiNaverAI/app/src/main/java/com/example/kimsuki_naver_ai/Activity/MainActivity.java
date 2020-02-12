package com.example.kimsuki_naver_ai.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.kimsuki_naver_ai.Adapter.Adapter;
import com.example.kimsuki_naver_ai.FileChooser;
import com.example.kimsuki_naver_ai.Model.AudioModel;
import com.example.kimsuki_naver_ai.Network.Network;
import com.example.kimsuki_naver_ai.R;
import com.example.kimsuki_naver_ai.Service.MyService;
import com.example.kimsuki_naver_ai.Useful;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


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
        getVoiceList();
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
                if (data.getUri() == null){
                    Toast.makeText(getApplicationContext(),"uri가 널인깝숑",Toast.LENGTH_SHORT).show();
                }else{
                    playAudioFile(data.getUri(), String.valueOf(data.getId()));
                }
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제하기");
                builder.setMessage(audioModelArrayList.get(position).getId()+"롱클릭");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


                return true;
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
    private void uploadFile(AudioModel model) {
        RequestParams params = new RequestParams();
        File file = new File(model.getPath());

        try {
            params.put("voicefile", file);
            params.put("phone", "test010");
            params.put("createdAt", "test");
            Log.e("file in parameter", "success");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("file in parameter", "fail");

        }
        Network.post(this, "/voices", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Toast.makeText(getApplicationContext(),"파일 업로드가 완료되었습니다",Toast.LENGTH_SHORT).show();
                    Log.e("success response", response.toString());
                    getVoiceList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });//network
    }
    private void getVoiceList() {
        RequestParams params = new RequestParams();
        params.put("limit", "10");
        Log.e("getVoiceList", "called");

        Network.get(this, "/voices", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("success response",response.toString());
                    Gson gson = new Gson();

                    JSONArray value = response;
                    for (int i = 0; i < value.length(); i++) {
                        String jsonstr = value.get(i).toString();
                        Log.e("jsonstr",jsonstr);
                        AudioModel audioModel = gson.fromJson(jsonstr, AudioModel.class);
                        audioModelArrayList.add(audioModel);
                    }
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });//network
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
                audioModel.setPhoneNumber(fileName);
                audioModel.setUri(AudioUri); //for play media
                audioModel.setPath(FileChooser.getPath(this, uri)); //for upload

                uploadFile(audioModel);

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
