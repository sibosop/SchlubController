package com.sibosop.schlubcontroller;

/**
 * Created by brian on 7/24/17.
 */

class SchlubHost extends Object {
    String  id;
    String  status;
    Boolean isMaster;
    Boolean auto;
    Integer vol;
    String  sound;
    String  phrase;
    Integer threads;
    String  speaker;
    String  collection;
    Boolean phraseScatter;
    Integer maxEvents;


    SchlubHost() {
        super();
        id = "all";
        status = "";
        isMaster = false;
        auto = true;
        vol = 0;
        sound = "";
        phrase = "";
        threads = 0;
        speaker = "";
        collection = "";
        phraseScatter = false;
        maxEvents = 1;
    }
    @Override
    public String toString() {
        return
                id
                + " " + status
                + " " + isMaster.toString()
                + " " + auto.toString()
                + " " + vol.toString()
                + " " + sound
                + " " + phrase
                + " " + threads.toString()
                + " " + speaker
                + " " + collection
                + " " + phraseScatter.toString()
                + " " + maxEvents.toString()
                ;
    }
}
