package com.audionowdigital.android.openplayerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.audionowdigital.android.openplayer.Player;
import com.audionowdigital.android.openplayer.Player.DecoderType;
import com.audionowdigital.android.openplayer.PlayerEvents;

import java.util.ArrayList;


// This activity demonstrates how to use JNI to encode and decode ogg/vorbis audio
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity ";



    private TextView logArea;

    private EditText urlArea;

    private Button initWithFile;

    private Button initWithUrl;

    private Button play;

    private Button pause;

    private Button stop;

    private SeekBar seekBar;

    private Player.DecoderType type = DecoderType.VORBIS;

    private Player player;

    // Playback handler for callbacks
    private Handler playbackHandler;


    private int LENGTH = 0;
    // Creates and sets our activities layout
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initUi();

        testOgg();

        switch (type) {
            case VORBIS: //urlArea.setText("http://icecast1.pulsradio.com:80/mxHD.ogg"); LENGTH = -1; break;
//                urlArea.setText("https://file-examples-com.github.io/uploads/2017/11/file_example_OOG_1MG.ogg");
                urlArea.setText("http://commondatastorage.googleapis.com/codeskulptor-assets/Evillaugh.ogg");
//                urlArea.setText("http://commondatastorage.googleapis.com/codeskulptor-demos/pyman_assets/extralife.ogg");
                LENGTH = 215;
                break;
            case OPUS:
                urlArea.setText("http://www.markosoft.ro/opus/02_Archangel.opus");
                LENGTH = 154;
                break;
            case MX:
                urlArea.setText("https://storage.googleapis.com/nfree/hello1605860308.mp3");
                LENGTH = -1;
                break;
        }

        playbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PlayerEvents.PLAYING_FAILED:
                        logArea.setText("The decoder failed to playback the file, check logs for more details");
                        break;
                    case PlayerEvents.PLAYING_FINISHED:
                        logArea.setText("The decoder finished successfully");
                        break;
                    case PlayerEvents.READING_HEADER:
                        logArea.setText("Starting to read header");
                        break;
                    case PlayerEvents.READY_TO_PLAY:
                        logArea.setText("READY to play - press play :)");
                        player.play();
                        break;
                    case PlayerEvents.PLAY_UPDATE:
                        logArea.setText("Playing:" + (msg.arg1 / 60) + ":" + (msg.arg1 % 60) + " (" + (msg.arg1) + "s)");
                        seekBar.setProgress((int) (msg.arg1 * 100 / player.getDuration()));
                        break;
                    case PlayerEvents.TRACK_INFO:
                        Bundle data = msg.getData();
                        logArea.setText("title:" + data.getString("title") + " artist:" + data.getString("artist") + " album:" + data.getString("album") +
                                " date:" + data.getString("date") + " track:" + data.getString("track"));
                        break;
                }
            }
        };

        // quick test for a quick player
        player = new Player(playbackHandler, type);

    }

    private void testOgg() {

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            result = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;

    }

    private void initUi(){
        logArea = (TextView) findViewById(R.id.log_area);
        urlArea = (EditText) findViewById(R.id.url_area);
        initWithFile = (Button) findViewById(R.id.init_file);
        initWithUrl = (Button) findViewById(R.id.init_url);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        seekBar = (SeekBar) findViewById(R.id.seek);

        initWithFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                logArea.setText("");
                switch (type) {
                    case VORBIS:
                        player.setDataSource("/data/user/0/com.olli.omni.demo/test6712552759787625685.ogg", 11);
                        break;
                    case OPUS:
                        player.setDataSource("/sdcard/countdown.opus", 11);
                        break;
                    case MX:
                        player.setDataSource("/sdcard/countdown.mp3", 11);
                        break;
                }
            }
        });

        initWithUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                logArea.setText("");
                Log.d(TAG, "Set source:" + urlArea.getEditableText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkIfAlreadyhavePermission()) {
                            player.setDataSource(urlArea.getEditableText().toString(), LENGTH);
                        }else  {
                            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissions, 100);
                            }
                        }
                    }
                }).start();
            }
        });



        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (player != null && player.isReadyToPlay()) {
                    logArea.setText("Playing... ");
                    player.play();
                } else
                    logArea.setText("Player not initialized or not ready to play");
            }
        });

        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (player != null && player.isPlaying()) {
                    logArea.setText("Paused");
                    player.pause();
                } else
                    logArea.setText("Player not initialized or not playing");
            }
        });

        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (player != null) {
                    logArea.setText("Stopped");
                    player.stop();
                } else
                    logArea.setText("Player not initialized");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    Log.d(TAG, "Seek:" + i + "");
                    player.setPosition(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
