package com.sibosop.schlubcontroller;

import java.util.ArrayList;

/**
 * Created by brian on 8/14/17.
 */

public class SoundList extends Object {
    public String status = "";
    public class ListItem {
        String name;
        String enabled;
        String maxVol;
        ListItem() {
            name = "";
            enabled = "0";
            maxVol = "0";
        }
        @Override
        public String toString() {
            return name+":"+enabled;
        }
    }

    public ArrayList<ListItem> sounds ;

    SoundList() {
        status = "";
        sounds = new ArrayList<>();
    }
    boolean isEmpty() {
        return sounds.isEmpty();
    }

    public ListItem getItem(String n)
    {
        for ( ListItem i : sounds) {
            if ( i.name.equals(n)) {
                return i;
            }
        }
        return null;
    }
}
