package com.in.den.android.notification;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by harumi on 13/12/2016.
 */

public class Topic implements Serializable {

    String key;
    String owner;
    String title;
    String desc;
    String startdate;
    String enddate;
    ArrayList<String> group;
}
