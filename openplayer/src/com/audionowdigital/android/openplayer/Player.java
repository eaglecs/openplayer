/*
 * Player.java - The Player is responsible for decoding a bitstream into raw PCM data to play to an {@link AudioTrack}
 *
 * (C) 2014 Radu Motisan, radu.motisan@gmail.com
 *
 * Part of the OpenPlayer implementation for Alpine Audio Now Digital LLC
 */

package com.audionowdigital.android.openplayer;

import android.os.Handler;
import android.os.Process;

import net.pocketmagic.android.openmxplayer.MXDecoder;

import org.xiph.opus.decoderjni.OpusDecoder;
import org.xiph.vorbis.decoderjni.VorbisDecoder;

import java.io.InputStream;

/**
 * Created by radhoo on /14.
 */

public class Player implements Runnable {

	public enum DecoderType {
		OPUS,
		VORBIS,
		MX,
		UNKNOWN
	};
	
	DecoderType type = DecoderType.UNKNOWN;

    /**
     * Logging tag
     */
    private static final String TAG = "duc_anh_Player";

    /**
     * The decode feed to read and write pcm/encoded data respectively
     */
    private  ImplDecodeFeed decodeFeed;

    /**
     * Current state of the player
     */
    private PlayerStates playerState = new PlayerStates();
    
    /**
     * The player events used to inform a client
     */
    private PlayerEvents events = null;

    private long streamSecondsLength = -1;

    public Player(Handler handler, DecoderType type) {
    	 if (handler == null) {
             throw new IllegalArgumentException("Handler must not be null.");
         }
    	 this.type = type;
    	 events = new PlayerEvents(handler);
    	 this.decodeFeed = new ImplDecodeFeed(playerState, events, type);
    	 
    	 
    	 // pass the DecodeFeed interface to the native JNI layer, we will get all calls there
        LogDebug.d(TAG,"Player constructor, type:"+type);
    	/* switch (type) {
    	 case DecoderType.OPUS: 
    	 }*/
        LogDebug.e(TAG, "preparing to init:"+type);
    	 switch (type) {
    		 case OPUS: OpusDecoder.initJni(1); break;
    		 case VORBIS: VorbisDecoder.initJni(1); break;
    		 case MX: MXDecoder.init(1); break;
		default:
			break;
    	 }
    	  
    }

    public void stopAudioTrack(){
        decodeFeed.stopAudioTrack();
        LogDebug.d("Player_Status", "stop audio track");

    }

    /**
     * Set an input stream as data source and starts reading from it
     */
    public void setDataSource(String path, long streamSecondsLength) {
    	if (playerState.get() != PlayerStates.STOPPED) {
            throw new IllegalStateException("Must be stopped to change source!");
        }
        LogDebug.d(TAG, "setDataSource: given length:" + streamSecondsLength);
    	// set an input stream as data source
    	this.streamSecondsLength = streamSecondsLength;
    	decodeFeed.setData(path, streamSecondsLength);
    	// start the thread, will go directly to "run" method
    	new Thread(this).start();
    }

    public void setDataSource(InputStream inputStream, long streamSecondsLength){
        if (playerState.get() != PlayerStates.STOPPED) {
            playerState.set(PlayerStates.STOPPED);
//            throw new IllegalStateException("Must be stopped to change source!");
        } else {
            LogDebug.d(TAG, "setDataSource: given length:" + streamSecondsLength);
            // set an input stream as data source
            this.streamSecondsLength = streamSecondsLength;
            decodeFeed.setData(inputStream, streamSecondsLength);
            new Thread(this).start();
        }
    }



    /**
     * Return the data source from the decode feed
     */
    public DataSource getDataSource(){
        return decodeFeed.getDataSource();
    }

    public long getDuration() {
    	return streamSecondsLength;
    }

    public void play() {
        LogDebug.d("duc_anh","play.....");
        if (playerState.get() == PlayerStates.READING_HEADER){
            LogDebug.d("duc_anh","PlayerStates == READING_HEADER");
            stop();
            return;
        }
        if (playerState.get() == PlayerStates.STOPPED){
            LogDebug.d("duc_anh","PlayerStates == STOPPED");
            return;
        }
    	if (playerState.get() != PlayerStates.READY_TO_PLAY) {
            throw new IllegalStateException("Must be ready first!");    		
        }
    	playerState.set(PlayerStates.PLAYING);
    	// make sure the thread gets unlocked
    	decodeFeed.syncNotify();
    }
    
    public void pause() {
        if (playerState.get() == PlayerStates.READING_HEADER){
            LogDebug.d("duc_anh","PlayerStates == READING_HEADER");
            stop();
            return;
        }
        if (playerState.get() == PlayerStates.READY_TO_PLAY){
            stop();
            return;
        }
        if (playerState.get() == PlayerStates.STOPPED){
            return;
        }
    	if (playerState.get() != PlayerStates.PLAYING) {
            throw new IllegalStateException("Must be playing first!");
        }
    	playerState.set(PlayerStates.READY_TO_PLAY);
    	// make sure the thread gets locked
    	decodeFeed.syncNotify();
    }
    
    /**
     * Stops the player and notifies the decode feed
     */
    public synchronized void stop() {
    	if (type == DecoderType.MX)
    		MXDecoder.stop();
    	
    	decodeFeed.onStop();
        // make sure the thread gets unlocked
    	decodeFeed.syncNotify();
    }
    

    @Override
    public void run() {
        LogDebug.e(TAG, "Start the native decoder");
        
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        
        int result = 0;
        switch (type) {
        	case OPUS:
                LogDebug.e(TAG, "call opus readwrite loop");
        		result = OpusDecoder.readDecodeWriteLoop(decodeFeed);
        	break;
        	case VORBIS:
                LogDebug.e(TAG, "call vorbis readwrite loop");
        		result = VorbisDecoder.readDecodeWriteLoop(decodeFeed);
        	break;
        	case MX:
                LogDebug.e(TAG, "call mx readwrite loop");
        		result = MXDecoder.readDecodeWriteLoop(decodeFeed);
        	break;
        }

        // Radu: why did I add the following code in the first place? Can't remember:
        // it was used mainly to signal exceptions from the inputstream
        /*if (decodeFeed.getDataSource() != null && !decodeFeed.getDataSource().isSourceValid()) {
            // Invalid data source
            Log.d(TAG, "Result: Invalid data source");
            events.sendEvent(PlayerEvents.PLAYING_FAILED);
            return;
        }*/
        // check for unexpected end:
        if (decodeFeed.getLastError() != decodeFeed.ERR_SUCCESS) {
            LogDebug.d(TAG, "Result: Ended unexpectedly:" + decodeFeed.getLastError());
            events.sendEvent(PlayerEvents.PLAYING_FAILED);
            return;
        }


        switch (result) {
            case DecodeFeed.SUCCESS:
                LogDebug.d(TAG, "Result: Normal: Successfully finished decoding");
                events.sendEvent(PlayerEvents.PLAYING_FINISHED);
                break;
            case DecodeFeed.INVALID_HEADER:
                LogDebug.e(TAG, "Result: Normal: Invalid header error received");
                events.sendEvent(PlayerEvents.PLAYING_FAILED);
                break;
            case DecodeFeed.DECODE_ERROR:
                LogDebug.e(TAG, "Result: Normal: Finished decoding with error");
                events.sendEvent(PlayerEvents.PLAYING_FAILED);
                break;
        }
    }

    /**
     * Checks whether the player is currently playing
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isPlaying() {
        return playerState.isPlaying();
    }

    /**
     * Checks whether the player is ready to play, this is the state used also for Pause
     *
     * @return <code>true</code> if ready, <code>false</code> otherwise
     */
    public synchronized boolean isReadyToPlay() {
        return playerState.isReadyToPlay();
    }
    
    /**
     * Checks whether the player is currently stopped (not playing)
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isStopped() {
        return playerState.isStopped();
    }

    /**
     * Checks whether the player is currently reading the header
     *
     * @return <code>true</code> if reading the header, <code>false</code> otherwise
     */
    public synchronized boolean isReadingHeader() {
        return playerState.isReadingHeader();
    }

    /**
     * Seek to a certain percentage in the current playing file.
     * @throws IllegalStateException for live streams
     * @param percentage - position where to seek
     */
    public synchronized void setPosition(int percentage) {
    	if (type == DecoderType.MX)
    		MXDecoder.setPositionSec((int) (percentage * getDuration() / 100));
    	else
    		decodeFeed.setPosition(percentage);
    }
    
    /**
     * 
     * @return returns the player position in track in seconds
     */
    public int getCurrentPosition(){
        return decodeFeed.getCurrentPosition();
    }

    public void setDecodeFeedListener(DecodeFeedListener listener) {
        decodeFeed.setDecodeFeedListener(listener);
    }

}
