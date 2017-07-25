package com.sibosop.schlubcontroller;

/**
 * Created by brian on 7/24/17.
 */

class SchlubHost extends Object {
    String  status;
    Boolean isMaster;
    Integer vol;
    String  sound;
    String  phrase;

    @Override
    public String toString() {
        return
                status
                + " " + isMaster.toString()
                + " " + vol.toString()
                + " " + sound
                + " " + phrase
                ;
    }
}
