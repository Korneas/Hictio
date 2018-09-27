package com.example.camilomontoya.hictio.Misc;

public class User {

    private static User ref;
    private boolean[][] fishes;

    private User(){
        fishes = new boolean[10][3];
    }

    public static User getRef(){
        if(ref == null){
            ref = new User();
        }
        return ref;
    }

    public void setFishes(int fish, int track, boolean value){
        fishes[fish][track] = value;
    }

}
