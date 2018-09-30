package com.example.camilomontoya.hictio.Misc;

public class User {

    private static User ref;
    private boolean[][] fishesNarrative;
    private boolean[] fishState, fishGesture;

    private User(){
        fishesNarrative = new boolean[10][3];
        fishState = new boolean[10];
        fishGesture = new boolean[10];
    }

    public static User getRef(){
        if(ref == null){
            ref = new User();
        }
        return ref;
    }

    public void setFishes(int fish, int track, boolean value){
        fishesNarrative[fish][track] = value;
        if(fishesNarrative[fish][0] && fishesNarrative[fish][1] && fishesNarrative[fish][2]){
            fishState[fish] = true;
        }
    }

    public boolean getFishAudioState(int fish, int value){
        return fishesNarrative[fish][value];
    }

    public boolean getFishState(int value){
        return fishState[value];
    }

    public boolean getFishGesture(int fish) {
        return fishGesture[fish];
    }

    public void setFishGesture(int fish, boolean value) {
        this.fishGesture[fish] = value;
    }
}
