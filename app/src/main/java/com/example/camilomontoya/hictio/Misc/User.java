package com.example.camilomontoya.hictio.Misc;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class User {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String[] fishList = {"OSCAR", "PIRANHA", "GHOST", "MONEDA"};
    private static User ref;
    private static String UID;

    private boolean[][] fishesNarrative;
    private boolean[] fishState, fishGesture;
    private boolean navTutorial, hapticTutorial;
    private boolean isUserNew, outApp;
    private Context c;

    private User() {
        fishesNarrative = new boolean[10][3];
        fishState = new boolean[10];
        fishGesture = new boolean[10];
    }

    public static User getRef() {
        if (ref == null) {
            ref = new User();
        }
        return ref;
    }

    public void setFishes(int fish, int track, boolean value) {
        fishesNarrative[fish][track] = value;
        if (fishesNarrative[fish][0] && fishesNarrative[fish][1] && fishesNarrative[fish][2] && !fishState[fish]) {
            fishState[fish] = true;
            SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean(fishList[fish], true);

            editor.apply();
        }
    }

    public void refreshFishes(){
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        for (int i = 0; i < fishList.length; i++) {
            fishState[i] = sharedPreferences.getBoolean(fishList[i], false);
        }
    }

    public boolean getFishAudioState(int fish, int value) {
        return fishesNarrative[fish][value];
    }

    public boolean getFishState(int value) {
        return fishState[value];
    }

    public boolean getFishGesture(int fish) {
        return fishGesture[fish];
    }

    public void setFishGesture(int fish, boolean value) {
        this.fishGesture[fish] = value;
    }

    public boolean isNavTutorial() {
        return navTutorial;
    }

    public void setNavTutorial(boolean navTutorial) {
        this.navTutorial = navTutorial;
    }

    public boolean isHapticTutorial() {
        return hapticTutorial;
    }

    public void setHapticTutorial(boolean hapticTutorial) {
        this.hapticTutorial = hapticTutorial;
    }

    public static String getUID() {
        return UID;
    }

    public static void setUID(String UID) {
        User.UID = UID;
    }

    public int getFishStateList() {
        return fishState.length;
    }

    public void setActualContext(Context context){
        this.c = context;
    }

    public boolean isUserNew() {
        return isUserNew;
    }

    public void setUserNew(boolean userNew) {
        isUserNew = userNew;
    }

    public boolean isOutApp() {
        return outApp;
    }

    public void setOutApp(boolean outApp) {
        this.outApp = outApp;
    }
}
