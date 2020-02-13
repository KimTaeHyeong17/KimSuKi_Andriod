package com.example.kimsuki_naver_ai.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kimsuki_naver_ai.Network.Network;
import com.example.kimsuki_naver_ai.R;
import com.example.kimsuki_naver_ai.Useful;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class URLMediaPlayerActivity extends Activity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageButton btn_backward, btn_pause, btn_play, btn_forward;
    private TextView tv_now_playing_text;
    private Uri uri;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {

                //set max value
                int mDuration = mediaPlayer.getDuration();
                seekBar.setMax(mDuration);

                //update total time text view
                TextView totalTime = (TextView) findViewById(R.id.totalTime);
                totalTime.setText(getTimeString(mDuration));

                //set progress to current position
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);

                //update current time text view
                TextView currentTime = (TextView) findViewById(R.id.currentTime);
                currentTime.setText(getTimeString(mCurrentPosition));

                //handle drag on seekbar
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });
            }
            //repeat above code every second
            mHandler.postDelayed(this, 10);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate layout
        setContentView(R.layout.activity_media_player);

        // get data from main activity intent
        Uri audioFile = getIntent().getData();
        uri = audioFile;
        String audioName = getIntent().getExtras().getString("name");
        // setup ui
        bindUI();
        // setup mediaplayer
        setUpMediaPlayer(audioFile, audioName);
    }
    //UI
    private void bindUI() {
        btn_backward = findViewById(R.id.btn_backward);
        btn_pause = findViewById(R.id.btn_pause);
        btn_play = findViewById(R.id.btn_play);
        btn_forward = findViewById(R.id.btn_forward);

        btn_backward.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_forward.setOnClickListener(this);

        tv_now_playing_text = findViewById(R.id.now_playing_text);

    }
    //FUNCTIONS
    private void setUpMediaPlayer(Uri audioFile, String audioName) {
        // create a media player
        mediaPlayer = new MediaPlayer();
        // try to load data and play
        try {
            // give data to mediaPlayer
            mediaPlayer.setDataSource(getApplicationContext(), audioFile);
            // media player asynchronous preparation
            mediaPlayer.prepareAsync();
            // create a progress dialog (waiting media player preparation)
            final ProgressDialog dialog = new ProgressDialog(URLMediaPlayerActivity.this);
            // set message of the dialog
            dialog.setMessage("로딩중입니다.");
            // prevent dialog to be canceled by back button press
            dialog.setCancelable(false);
            // show dialog at the bottom
            dialog.getWindow().setGravity(Gravity.CENTER);
            // show dialog
            dialog.show();
            // display title
            tv_now_playing_text.setText(audioName);
            // execute this code at the end of asynchronous media player preparation
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mp) {
                    //start media player
                    mp.start();
                    // link seekbar to bar view
                    seekBar = (SeekBar) findViewById(R.id.seekBar);
                    //update seekbar
                    mRunnable.run();
                    //dismiss dialog
                    dialog.dismiss();
                }
            });
        } catch (IOException e) {
            Activity a = this;
            a.finish();
            Toast.makeText(this, "파일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void stop(View view) {
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
    }
    public void seekForward() {
        //set seek time
        int seekForwardTime = 5000;
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }
    public void seekBackward() {
        //set seek time
        int seekBackwardTime = 5000;
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }
    public void onBackPressed() {
        super.onBackPressed();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        finish();
    }
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                mediaPlayer.start();
                break;
            case R.id.btn_pause:
                mediaPlayer.pause();
                break;
            case R.id.btn_forward:
                seekForward();
                break;
            case R.id.btn_backward:
                seekBackward();
                break;

            default:
                break;
        }
    }
}