package com.hasbrouckproductions.rhasbrouck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by hasbrouckr on 6/2/2016.
 * This class will load and save the settings
 * Only setting currently saved is highest time
 *
 */
public class Settings {
    public static long longestTime = 1;
    public static int highScore = 0;
    public  static Preferences prefs;

    public final static String FINALLONGTIMESTRING = "com.hasbrouckproductions.rhasbrouck.pediddle.FINALLONGTIMESTRING";
    public final static String SETTINGSHIGHSCORE = "com.hasbrouckproductions.rhasbrouck.pediddle.SETTINGSHIGHSCORE";
    public final static String SETTINGSPREFS = "com.hasbrouckproductions.rhasbrouck.pediddle.SETTINGSPREFS";

    public static void load () {
        prefs = Gdx.app.getPreferences(SETTINGSPREFS);
        longestTime = prefs.getLong(FINALLONGTIMESTRING, 0);
        highScore = prefs.getInteger(SETTINGSHIGHSCORE, 0);
        Gdx.app.log("SETTINGS longestTime", "" + longestTime);

    }

    public static void save () {

        prefs.putLong(FINALLONGTIMESTRING, longestTime);
        prefs.putInteger(SETTINGSHIGHSCORE, highScore);
        prefs.flush();
    }

    public static void addTime (long time) {
        Gdx.app.log("SETTINGS TXT addScore", "" + time);
        longestTime = time;
    }

    public static void addHighScore(int score){
        highScore = score;
    }
}
