package com.sibosop.schlubcontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by brian on 7/23/17.
 */
class HostInfo extends Object {
    String subnet ="";
    String all;
    List<String> ids = new ArrayList<>();

    HostInfo(String a) {
        super();
        all = a;
    }
    @Override
    public String toString() {
        String rval;
        rval = subnet;
        rval += ":";
        for ( String id : ids ) {
            rval += id + " ";
        }
        return rval;
    }
    public void sort() {
        Collections.sort(ids,new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            }
        });
        ids.add(all);
    }
}
