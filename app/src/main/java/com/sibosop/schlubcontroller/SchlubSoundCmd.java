package com.sibosop.schlubcontroller;

import com.google.gson.Gson;

/**
 * Created by brian on 3/10/18.
 */

public class SchlubSoundCmd extends Object {
    String cmd = "Sound";
    String file = "";
    SchlubSoundCmd(String file_) {
        super();
        file = file_;
    }
    String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this, SchlubSoundCmd.class);
    }
}
