package com.in.den.android.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by harumi on 21/11/2016.
 */

public class SharedPreferenceOp {

    SharedPreferences sharedPreferences;
    final String CHATROOM = "chatroom";
    final String USERNAME = "username";
    final String EMAIL = "email";
    final String PASSWORD = "password";
    final String ICONFILE = "iconuri";
    final String  LOCALICON = "localicon";
    final String REALPATH = "realpath";
    final String USERKEY = "userkey";
    final String ICONUUID = "iconuuid";

    final File appdir = new File(Environment.getExternalStorageDirectory(), File.separator + "app");
    final File profileiconfile = new File(appdir, "profileicon.png");



    public SharedPreferenceOp(Context context) {
        sharedPreferences = context.getSharedPreferences(CHATROOM, Context.MODE_PRIVATE);

    }

    public String getRealpath() { return sharedPreferences.getString(REALPATH, "");}

    public String getUserName() {
        return sharedPreferences.getString(USERNAME, "");
    }

    public String getEmail()  {
        return sharedPreferences.getString(EMAIL, "");
    }

    public String getPassword()  {
        return sharedPreferences.getString(PASSWORD, "");
    }

    public String getUserkey() { return sharedPreferences.getString(USERKEY,"");}

    public String getIconUuid() { return sharedPreferences.getString(ICONUUID,"");}

    public boolean isIconFile() {

        return sharedPreferences.getBoolean(LOCALICON, false);
    }

    public void setUserName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, name);
        editor.commit();
    }

    public void setUserkey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERKEY, key);
        editor.commit();
    }

    public void setIconUuid(String uuid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ICONUUID, uuid);
        editor.commit();
    }

    public void setInfo(String username, String email, String password, boolean localicon, String realpath) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USERNAME, username);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.putBoolean(LOCALICON, localicon);
        editor.putString(REALPATH, realpath);

        editor.commit();
    }
}
