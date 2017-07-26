package com.sibosop.schlubcontroller;

/**
 * Created by brian on 7/24/17.
 */

class SchlubHost extends Object {
    String  id;
    String  status;
    Boolean isMaster;
    Integer vol;
    String  sound;
    String  phrase;


    SchlubHost() {
        super();
        id = "none";
    }
    @Override
    public String toString() {
        return
                id
                + " " + status
                + " " + isMaster.toString()
                + " " + vol.toString()
                + " " + sound
                + " " + phrase
                ;
    }
}
