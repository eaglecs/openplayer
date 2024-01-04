package com.audionowdigital.android.openplayerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.audionowdigital.android.openplayer.LogDebug;
import com.audionowdigital.android.openplayer.Player;
import com.audionowdigital.android.openplayer.Player.DecoderType;
import com.audionowdigital.android.openplayer.PlayerEvents;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSource;


// This activity demonstrates how to use JNI to encode and decode ogg/vorbis audio
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity ";

    private File filesDirApp;

    private TextView logArea;

    private EditText urlArea;

    private Button initWithFile;

    private Button initWithUrl;

    private Button play;
    private Button connect;

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
        filesDirApp = getFilesDir();
//        testOgg();

        switch (type) {
            case VORBIS: //urlArea.setText("http://icecast1.pulsradio.com:80/mxHD.ogg"); LENGTH = -1; break;
//                urlArea.setText("https://file-examples-com.github.io/uploads/2017/11/file_example_OOG_1MG.ogg");
                urlArea.setText("http://commondatastorage.googleapis.com/codeskulptor-assets/Evillaugh.ogg");
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
                        if (!lstData.isEmpty()) {
                            String dataFile = lstData.get(0);
                            byte[] bytesFileFirst = hexStringToByteArray(dataFile);
                            InputStream is = new ByteArrayInputStream(bytesFileFirst);
                            player.setDataSource(is, -1);
                            lstData.remove(0);
                        }
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

    private void testTTSChatGPT() {
        Observable<ResponseBody> observable = ServiceImpl.Companion.getResponseChatGPT()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribeWith(getObserverRequestGPT());
    }

    private void connectChatbot() {
        Observable<ResponseBody> observable = ServiceImpl.Companion.getConnectChatbot()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribeWith(getObserverRequestGPT());
    }

    private Observer<ResponseBody> getObserverRequestGPT() {
        return new Observer<ResponseBody>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(ResponseBody s) {
                new Thread(() -> {
                    BufferedSource source = s.source();
                    try {
                        String result = "";
                        while (!source.exhausted()) {
                            String text = source.getBuffer().readUtf8();
                            if (!text.isEmpty()) {
                                result = result + text;
                                if (result.endsWith("$END_JSON")) {
                                    String response = result;
                                    if (response.contains("ExpectSpeech")) {
                                        handleResponseChatbot(response);
                                    }
                                    result = "";
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    private void handleResponseChatbot(String response) {
        runOnUiThread(() -> {
            LogDebug.d("", "result---xxx----" + response);
            Gson gson = new Gson();
            DirectiveResponse directiveResponse = gson.fromJson(response.replace("$START_JSON", "").replace("$END_JSON", ""), DirectiveResponse.class);
            String serverMessageId = directiveResponse.getHeader().getServerMessageId();
            new Thread(() -> {
                LogDebug.d("send request stream = ", "");
                Observable<ResponseBody> observable = ServiceImpl.Companion.streamChat(serverMessageId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                observable.subscribeWith(getObserver());
            }).start();
        });
    }


    private void testOgg() {
        testTTSChatGPT();
    }


    private Observer<ResponseBody> getObserver() {
        return new Observer<ResponseBody>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(ResponseBody s) {
                try {
                    LogDebug.d("receive response ", "");
                    playStreamTTS(s);
                } catch (IOException e) {
                    return;
                }
//                handleResultTTS(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    String lstFullData = "";

    private void playStreamTTS(ResponseBody s) throws IOException {
        InputStream inputStream = s.byteStream();
//        BufferedSource source = s.source();
//        while (!source.exhausted()) {
//            InputStream inputStream = source.inputStream();
        String resultCurrent = "";
        int read = 0;
        byte[] buffer = new byte[16 * 1024];
        String endFileHexStr = str2HexStr("--boundary-olli-maika-ogg-files");
        while (read != -1) {
//            LogDebug.d("receive data buffer = ", read + "");
            read = inputStream.read(buffer);
            if (read != -1) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(buffer, 0, read);
                out.close();
                String bytesToHex = bytesToHex(out.toByteArray());

                lstFullData = lstFullData + bytesToHex;
                resultCurrent = resultCurrent + bytesToHex;
                if (resultCurrent.contains(endFileHexStr)) {
                    String[] lstData = resultCurrent.split(endFileHexStr);
                    for (int i = 0; i < lstData.length; i++) {
                        if (i == lstData.length - 1) {
                            resultCurrent = lstData[i];
                        } else {
                            LogDebug.d("data response i = " + i, lstData[i]);
                        }
                    }
                }
            } else {
                if (resultCurrent.contains(endFileHexStr)) {
                    String[] lstData = resultCurrent.split(endFileHexStr);
                    for (int i = 0; i < lstData.length; i++) {
                        LogDebug.d("data response last= ", lstData[i]);
                    }
                } else {
                    LogDebug.d("data response last not endFileHexStr= ", resultCurrent);
                }
            }
        }
        playFullData();
//        byte[] bytesFileFirst = hexStringToByteArray(first);
//        InputStream is = new ByteArrayInputStream(bytesFileFirst);
//        player.setDataSource(is, -1);

//            byte[] bytes = toByteArray(inputStream);
//            String bytesToHex = bytesToHex(bytes);
//            LogDebug.d("part stream = ", "");
//        }
    }

    ArrayList<String> lstData = new ArrayList<>();

    private void playFullData() {
        String endFileHexStr = str2HexStr("--boundary-olli-maika-ogg-files");
        lstData.addAll(Arrays.asList(lstFullData.split(endFileHexStr)));
        if (!lstData.isEmpty()) {
            byte[] bytesFileFirst = hexStringToByteArray(lstData.get(0));
            InputStream is = new ByteArrayInputStream(bytesFileFirst);
            player.setDataSource(is, -1);
            lstData.remove(0);
        }
    }

    private void handleResultTTS(ResponseBody s) {
        InputStream inputStream = s.byteStream();
        try {
            byte[] bytes = toByteArray(inputStream);
            String bytesToHex = bytesToHex(bytes);
            String str2HexStr = str2HexStr("--boundary-olli-maika-ogg-files");
            String[] lstData = bytesToHex.split(str2HexStr);
            LogDebug.d("lstData size = ", lstData.length + "");
            if (lstData.length > 2) {
                String firstResult = lstData[2];
                byte[] bytesFileFirst = hexStringToByteArray(firstResult);
                InputStream is = new ByteArrayInputStream(bytesFileFirst);
                player.setDataSource(is, -1);
            }
        } catch (IOException e) {

        }
    }

    public String isToString(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void playOggFile(byte[] bytearray) {
        try {
            LogDebug.d("playOggFile", "....");
            File tempFile = File.createTempFile("mobile", "ogg", getCacheDir());
            tempFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytearray);
            fos.close();
            MediaPlayer mediaPlayer = new MediaPlayer();

            FileInputStream fis = new FileInputStream(tempFile);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private void playLocalFile(InputStream inputStream) {
        try {
            File file = File.createTempFile("test", ".ogg", getFilesDir());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[16384];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            MediaPlayer mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            FileInputStream fileInputStream = new FileInputStream(file);
            mp.setDataSource(fileInputStream.getFD());
            mp.prepare();
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static String str2HexStr(String str) {
        byte[] bytes = str.getBytes();
        int bLen = bytes.length;
        StringBuffer buf = new StringBuffer(bLen * 2);
        int i;//w  w w. j  a  v a2  s . c  o  m
        for (i = 0; i < bLen; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buf.toString().toLowerCase();
    }

    private void initUi() {
        logArea = (TextView) findViewById(R.id.log_area);
        urlArea = (EditText) findViewById(R.id.url_area);
        initWithFile = (Button) findViewById(R.id.init_file);
        initWithUrl = (Button) findViewById(R.id.init_url);
        play = (Button) findViewById(R.id.play);
        connect = (Button) findViewById(R.id.connect);
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
//                            player.setDataSource(urlArea.getEditableText().toString(), LENGTH);
                            testOgg();
                        } else {
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

        pause.setOnClickListener(arg0 -> {
            if (player != null && player.isPlaying()) {
                logArea.setText("Paused");
                player.pause();
            } else
                logArea.setText("Player not initialized or not playing");
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

        connect.setOnClickListener(arg0 -> {
            new Thread(() -> connectChatbot()).start();
        });
    }
}
