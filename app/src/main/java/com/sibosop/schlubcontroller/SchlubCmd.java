package com.sibosop.schlubcontroller;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by brian on 8/10/17.
 */

public class SchlubCmd extends Object {
    public String cmd;
    public ArrayList<String> args;
    SchlubCmd(String cmd_) {
        super();
        cmd = cmd_;
        args = new ArrayList<>();
    }
    SchlubCmd() {
        super();
        cmd = "";
        args = args = new ArrayList<>();
    }
    void putArg(String a)
    {
        args.add(a);
    }

    String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this, SchlubCmd.class);
    }
}
