package com.sibosop.schlubcontroller;

import java.util.ArrayList;

/**
 * Created by brian on 8/14/17.
 */

public class CollectionList extends Object {
    public String status = "";
    public ArrayList<String> collections ;

    CollectionList() {
        status = "";
        collections = new ArrayList<>();
    }
    boolean isEmpty() {
        return collections.isEmpty();
    }

    public String getItem(String n)
    {
        for ( String i : collections) {
            if ( i.equals(n)) {
                return i;
            }
        }
        return null;
    }
}
