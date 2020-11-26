/*
 * DataSource.java -- data source handler for both local and online content
 *
 * (C) 2014 Radu Motisan, radu.motisan@gmail.com
 *
 * Part of the OpenPlayer implementation for Alpine Audio Now Digital LLC
 */

package com.audionowdigital.android.openplayer;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Created by radhoo on /14.
 */

public class DataSource {

    /**
     * The debug tag
     */
    private String TAG = "DataSource";

    private final static int DATA_SRC_FINISHED = -2;
    private final static int DATA_SRC_INVALID = -1;
    private final static int DATA_SRC_LOCAL = 0;
    private final static int DATA_SRC_REMOTE = 1;

    private InputStream inputStream;
    private String dataPath = null;
    private int dataSource = DATA_SRC_INVALID;

    private long length = -1, readoffset = -1;

	private InputStream getRemote(String url, long offset) {
        Log.d(TAG, "getRemote:" + url);

		try {
			URLConnection cn = new URL( url ).openConnection();
            cn.setConnectTimeout(5000);
			cn.setRequestProperty ("Range", "bytes="+offset+"-");
			/* iOS implementtion:
			 NSString * str = [NSString stringWithFormat:@"GET %@ HTTP/1.0\r\nHost: %@\r\nRange: bytes=%ld-\r\n\r\n",
			                   [sourceUrl path], [sourceUrl host], offset];
			 */
			cn.connect();

			if (offset == 0)
				length = cn.getContentLength();

			readoffset = offset;

			return cn.getInputStream();
		} catch (MalformedURLException e ) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }

        // check URL for type of content! other parameters can be accessed here!
        /*for (java.util.Map.Entry<String, java.util.List<String>> me : cn.getHeaderFields().entrySet()) {
            if ("content-type".equalsIgnoreCase( me.getKey())) {
                for (String s : me.getValue()) {
                    String ct = s;
                    Log.e(TAG, "content:" + s); // 04-18 13:20:51.121: E/Player(7417): content:audio/aacp
                }
            }
        }*/
		return null;
	}

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private InputStream getRemoteMaika(String url) {
        Log.d(TAG, "getRemote:" + url);

//        try {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/octet-stream")
                .addHeader("device-id", "71da8eae6dbfcc25")
                .addHeader("device-type", "android")
                .addHeader("client-version", "1.0.0")
                .addHeader("olli-session-id", "cf455b20-f8cb-11ea-905b-13d87e1a953c")
                .addHeader("meta", "eyJldmVudCI6eyJoZWFkZXIiOnsiZGlhbG9nUmVxdWVzdElkIjoiMjAyMDA5MTctYThiYTY5MWY0OWI2YjQ0YTg3NGYiLCJtZXNzYWdlSWQiOiJtZXNzYWdlSWQtMjAyMDA5MTctZjdjZDBjZmRjNTUyODJkNWU2Y2MiLCJuYW1lIjoiU3RyZWFtQXVkaW8iLCJuYW1lc3BhY2UiOiJUZXh0VG9TcGVlY2gifSwicGF5bG9hZCI6eyJlbmNvZGVGb3JtYXQiOiJvZ2ciLCJsYW5ndWFnZSI6InZpLVZOIiwidGV4dCI6IuG7nyBo4buTIGNow60gbWluaCwgTmhp4buHdCDEkeG7mSBsw6AgMzAgxJHhu5kgQywgbcOhdCBt4bq7LiBUcuG7nWkgcuG6pXQgbmhp4buBdSBtw6J5LiBDaOG7iSBz4buRIFVWIFRo4bqlcCwgbsOqbiBjaOG7iSBj4bqnbiDEkWVvIGvDrW5oIHLDom0gdsOgIHRob2Ega2VtIGNo4buRbmcgbuG6r25nIMSR4buDIGLhuqNvIHbhu4cgbMOgbiBkYSBsw6AgxJHGsOG7o2MuIEdpw7MgOSBrbS9oIHRoZW8gaMaw4bubbmcgYuG6r2MgdMOieSBi4bqvYywgZ2nDsyB0aOG7lWkgbmjhurkgduG7q2EgcGjhuqNpLiBMxrDhu6NuZyBtxrBhIGtob+G6o25nIDAgbW0uIEtow7RuZyBraMOtIDI1IEMsIGtow7RuZyBraMOtIG5n4buZdCBuZ+G6oXQsIGPhu7FjIGvhu7Mga2jDsyBjaOG7i3UgIn19fQ==")
                .addHeader("authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MDU5MzEzMDcsIm5iZiI6MTYwNTkzMTMwNywianRpIjoiNTg2NmM2ZmMtNjRkNi00MWFjLTlhOTUtNjUyMWJkMGQ5NGIxIiwiaWRlbnRpdHkiOiJ7XCJzdWJcIjogMTY3LCBcIm5hbWVcIjogXCJEdWMgQW5oXCIsIFwiZW1haWxcIjogXCJsZWR1Y2FuaC5ia2l0MTBAZ21haWwuY29tXCIsIFwicm9sZVwiOiAxLCBcInN0YXR1c1wiOiAxLCBcImRldmljZV9pZFwiOiBcInN0cmluZ1wiLCBcImRlZmF1bHRfbGFuZ3VhZ2VcIjogXCJ2aS1WTlwiLCBcImV4cHJpcmF0aW9uXCI6IDg2NDAwLCBcInBob25lX251bWJlclwiOiBcIjAxMjM0NTY3ODk2XCIsIFwiY2FsbGluZ19uYW1lXCI6IFwiTGUgRHVjIEFuaFwifSIsImZyZXNoIjpmYWxzZSwidHlwZSI6ImFjY2VzcyJ9.m8BhoVXokIXrPeIT8IEuuyJCLO1ISRC49jCvcg10ND0")
                .addHeader("user-id", "167")
                .addHeader("Range", "bytes=0-")
//                .post(body)
                .url("https://staging.chatbot.iviet.com/stream")
                .build();

//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
//                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
//                .cipherSuites(
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
//                .build();

        //TTSManage
        //FileAudioManage
        //StoryAudioManage
        //MusicManage
        //MediaPlayerManage

        client.connectionSpecs();
        try {
            Response response = client.newCall(request).execute();
            return response.body().byteStream();
        } catch (IOException e) {
            Log.e("", e.getMessage());
        }

//            HttpURLConnection cn = (HttpURLConnection) new URL("https://staging.chatbot.iviet.com/stream").openConnection();
//            cn.setConnectTimeout(5000);
//            cn.setRequestMethod("POST");
//            cn.setRequestProperty("content-type", "application/octet-stream");
//
//            cn.setRequestProperty("device-id", "71da8eae6dbfcc25");
//            cn.setRequestProperty("device-type", "android");
//            cn.setRequestProperty("client-version", "1.0.0");
//            cn.setRequestProperty("olli-session-id", "53997810-f1b6-11ea-a033-f91ffbe89bb5");
//            cn.setRequestProperty("meta", "eyJldmVudCI6eyJoZWFkZXIiOnsiZGlhbG9nUmVxdWVzdElkIjoiMjAyMDA5MDgtYTQ2ZWM2NmZiOTg4OTdhYjQyZTQiLCJtZXNzYWdlSWQiOiJtZXNzYWdlSWQtMjAyMDA5MDgtNTc2Y2MxMWU0MzBiY2NlYWI4MmYiLCJuYW1lIjoiU3RyZWFtQXVkaW8iLCJuYW1lc3BhY2UiOiJUZXh0VG9TcGVlY2gifSwicGF5bG9hZCI6eyJlbmNvZGVGb3JtYXQiOiJvZ2ciLCJsYW5ndWFnZSI6InZpLVZOIiwidGV4dCI6Ik3hu51pIGLhuqFuIG5naGUgYsOgaSDEkOG6pXQgTsaw4bubYyBkbyBUcuG7jW5nIFThuqVuIHRyw6xuaCBiw6B5IHRyw6puIE5o4bqhYyBD4bunYSBUdWkifX19¬");
//            cn.setRequestProperty("authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1OTczNzk3NjIsIm5iZiI6MTU5NzM3OTc2MiwianRpIjoiNzE3MmE3ZDktZmY0ZC00YmE2LWFlOTYtZDI5ODY2MDBjYTJhIiwiaWRlbnRpdHkiOiJ7XCJzdWJcIjogMTY3LCBcIm5hbWVcIjogXCJEdWMgQW5oXCIsIFwiZW1haWxcIjogXCJsZWR1Y2FuaC5ia2l0MTBAZ21haWwuY29tXCIsIFwicm9sZVwiOiAxLCBcInN0YXR1c1wiOiAxLCBcImRldmljZV9pZFwiOiBcIjcxZGE4ZWFlNmRiZmNjMjVcIiwgXCJkZWZhdWx0X2xhbmd1YWdlXCI6IFwidmktVk5cIiwgXCJleHByaXJhdGlvblwiOiA4NjQwMH0iLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.ZCgBepGkH2bWtcY-1q5u-w7gG_RnymX4DLS3KbiGar0");
//            cn.setRequestProperty("user-id", "167");
////            cn.setRequestProperty("content-length", "0");
////            cn.setRequestProperty("accept-encoding", "gzip");
//
//
////			cn.setRequestProperty("user-agent","okhttp/4.8.1");
//
//			/* iOS implementtion:
//			 NSString * str = [NSString stringWithFormat:@"GET %@ HTTP/1.0\r\nHost: %@\r\nRange: bytes=%ld-\r\n\r\n",
//			                   [sourceUrl path], [sourceUrl host], offset];
//			 */
//            cn.connect();
//
//            if (offset == 0)
//                length = cn.getContentLength();
//
//            readoffset = offset;
//
//            return cn.getInputStream();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // check URL for type of content! other parameters can be accessed here!
        /*for (java.util.Map.Entry<String, java.util.List<String>> me : cn.getHeaderFields().entrySet()) {
            if ("content-type".equalsIgnoreCase( me.getKey())) {
                for (String s : me.getValue()) {
                    String ct = s;
                    Log.e(TAG, "content:" + s); // 04-18 13:20:51.121: E/Player(7417): content:audio/aacp
                }
            }
        }*/
        return null;
    }

    private InputStream getLocal(String path) {
        File f = new File(path);
        if (f.exists() && f.length() > 0) {
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(f));
                length = f.length();
                is.markSupported();
                is.mark((int) length);

                return is;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param path can be a local path or a remote url
     */
    DataSource(String path) {
        dataSource = DATA_SRC_INVALID;
        inputStream = null;
        dataPath = path;

        // first see if it's a local path
        inputStream = getLocal(path);
        if (inputStream != null) {
            Log.d(TAG, "Local Source length:" + length);
            dataSource = DATA_SRC_LOCAL;
            return;
        }

        inputStream = getRemote(path, 0);
        if (inputStream != null) {
            Log.d(TAG, "Remote Source length:" + length);
            dataSource = DATA_SRC_REMOTE;
            return;
        }

    }

    DataSource(InputStream input) {
        dataSource = DATA_SRC_INVALID;
        inputStream = null;
        Log.d(TAG, "Remote Source length:" + length);
        this.inputStream = input;
        dataSource = DATA_SRC_REMOTE;

    }

    public void release() {
        Log.d(TAG, "release called.");
        try {
            inputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "source already released");
        }
        inputStream = null;
        dataSource = DATA_SRC_INVALID;
    }

    /**
     * @return the current track size in bytes, or -1 for live streams
     */
    public long getSourceLength() {
        return length;
    }

    /**
     * @return the current read position in bytes, must be smaller then source lengths @getSourceLength()
     */
    public long getReadOffset() {
        return readoffset;
    }

    public boolean isSourceValid() {
        return dataSource != DATA_SRC_INVALID;
    }


    public synchronized int read(byte buffer[], int byteOffset, int byteCount) {
        try {
            if (dataSource != DATA_SRC_INVALID) {
                // Reads up to byteCount bytes from this stream and stores them in the byte array buffer starting at byteOffset.
                // Returns the number of bytes actually read or -1 if the end of the stream has been reached.
                // if the stream is closed or another IOException occurs.
                int bytes = inputStream.read(buffer, byteOffset, byteCount);
                Log.d("Stream size = ","" + bytes);
                if (bytes > 0) readoffset += bytes;
                //Log.d(TAG, "readoffset:" + readoffset)
                if (bytes == -1)
                    return DATA_SRC_FINISHED;
                else
                    return bytes;
            }
        } catch (IOException e) {
            Log.d(TAG, "InputStream exception:" + e.getMessage());
            e.printStackTrace();
        }

        return DATA_SRC_INVALID;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getPath() {
        return dataPath;
    }

    public synchronized int skip(long offset) {
        // invalid content fix : make sure we are pass the header always
        if (offset < 500) offset = 500;

        if (dataSource == DATA_SRC_LOCAL) {
            try {
                int retry = 10;
                long skip = 0;
                // enforce skip to correct position for local inputstreams
                do {
                    inputStream.reset(); // will reset to mark: TODO: test if memory is ok on local big files

                    skip = inputStream.skip(offset);
                    retry--;
                    Log.e("SKIP", "res skip:" + skip + " retry:" + retry);
                } while (Math.abs(skip - offset) > 4096 && retry > 0);

                readoffset = offset;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dataSource == DATA_SRC_REMOTE) {
            if (!dataPath.isEmpty()) {
                inputStream = getRemote(dataPath, offset);
            }
            if (inputStream != null) {
                Log.d(TAG, "Skip reconnect to:" + offset);
                dataSource = DATA_SRC_REMOTE;
            }
        }

        //return DATA_SRC_INVALID;
        return 0; // return result not used
    }
}