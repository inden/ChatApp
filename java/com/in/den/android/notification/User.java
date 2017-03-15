package com.in.den.android.notification;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by harumi on 07/12/2016.
 */
public class User implements Serializable {

    final String suri1 = "put your adresse here";
    final String suri2 = ".jpg?alt=media";

    String iduser;  // auto generated user key
    String iconUri; //uri adress to download the user icon
    String name;    //uer name
    String iconUuid; //uuid to point to the user id

    public User() {
        iconUri = null;
        name = "";
        iduser = "";
        iconUuid = "";

    }

    public Uri getIconUri() {
        return Uri.parse(iconUri);
    }

    public Uri constructIconUriFromUUID() {
        String uri = suri1 + iconUuid + suri2;
        return Uri.parse(uri);
    }


}
