package com.sibosop.schlubcontroller;

import java.util.ArrayList;

/**
 * Created by brian on 8/14/17.
 */

public class SoundList extends Object {
    public String status = "";
    public ArrayList<String> sounds ;

    SoundList() {
        status = "";
        sounds = new ArrayList<>();
    }
    boolean isEmpty() {
        return sounds.isEmpty();
    }
}
