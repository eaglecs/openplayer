/*
 * PlayerStates.java - a class that defines the internal states of the Player
 *
 * (C) 2014 Radu Motisan, radu.motisan@gmail.com
 *
 * Part of the OpenPlayer implementation for Alpine Audio Now Digital LLC
 */

package com.audionowdigital.android.openplayer;

/**
 * Created by radhoo on /14.
 */

public class PlayerStates {
	 /**
     * Playing state which can either be stopped, playing, or reading the header before playing
     */
    public static final int
    	READY_TO_PLAY = 0,
        PLAYING = 1, 
        STOPPED = 2, 
        READING_HEADER = 3; 
        //BUFFERING = 4;
    
    public int playerState = STOPPED;
    
    public int get() {
    	return playerState;
    }
    
    public void set(int state) {
        String stateStr = "";
        if (state == 0){
            stateStr = "READY_TO_PLAY" ;
        } else  if (state ==1){
            stateStr = "PLAYING" ;
        } else if (state ==2){
            stateStr = "STOPPED" ;
        } else if (state == 3){
            stateStr = "READING_HEADER" ;
        } else {
            stateStr = "other" ;
        }
        LogDebug.e("PlayerStates", "new state:"+stateStr);
    	playerState = state;
    }
    
    /**
     * Checks whether the player is currently playing
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isPlaying() {
        return playerState == PlayerStates.PLAYING;
    }
    
    /**
     * Checks whether the player is ready to play, this is the state used also for Pause
     *
     * @return <code>true</code> if ready, <code>false</code> otherwise
     */
    public synchronized boolean isReadyToPlay() {
        return playerState == PlayerStates.READY_TO_PLAY;
    }
    
    /**
     * Checks whether the player is currently stopped (not playing)
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isStopped() {
        return playerState == PlayerStates.STOPPED;
    }

    /**
     * Checks whether the player is currently reading the header
     *
     * @return <code>true</code> if reading the header, <code>false</code> otherwise
     */
    public synchronized boolean isReadingHeader() {
        return playerState == PlayerStates.READING_HEADER;
    }


}
