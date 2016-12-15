package com.in.den.android.notification;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by harumi on 08/12/2016.
 */

public class FireBaseOperation {

    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StorageReference iconStorageReference = FirebaseStorage.getInstance().getReference().child("profilicon");
    private DatabaseReference topicReference = FirebaseDatabase.getInstance().getReference().child("topics");
    private String TAG = FireBaseOperation.class.getSimpleName();
    private SharedPreferenceOp mysharedPreferenceOp=null;

    public void setSharedPreferenceOp(SharedPreferenceOp sharedPreferenceOp) {
        mysharedPreferenceOp = sharedPreferenceOp;
    }


    public void findUserByEmail(final String email, final String name,
                                 final boolean biconchanged, final Uri iconuri) {

        usersReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap map = (HashMap)dataSnapshot.getValue();

                        //if no email found map is null
                        String uuid;

                        if(map==null) {
                            uuid = createUserOnFirebase(email, name);
                        }
                        else {
                            //email found;
                            Log.d(TAG, "user found :" + name);
                            String userKey = (String) map.keySet().iterator().next();
                            if(mysharedPreferenceOp!=null) {
                                mysharedPreferenceOp.setUserkey(userKey);
                            }

                            Iterator ite = map.values().iterator();

                            uuid = (String)((HashMap) ite.next()).get("iconfile");
                        }
                        Log.d(TAG, "uuid " + uuid);
                        if(mysharedPreferenceOp!=null) {
                            mysharedPreferenceOp.setIconUuid(uuid);
                        }

                        //check if iconfile loaded
                        if(biconchanged && iconuri != null) {
                            uploadIconOnFirebase(uuid, iconuri, email, name);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void uploadIconOnFirebase(String uuid, Uri file, String email, String name) {
        StorageReference riversRef = iconStorageReference.child(uuid +".jpg");

        StorageMetadata metadata = new StorageMetadata.Builder().
                setCustomMetadata("email",email).
                setCustomMetadata("name", name).build();

        riversRef.putFile(file, metadata);
    }

    private String createUserOnFirebase(String email, String username) {

        String tmpkey = usersReference.push().getKey();

        Map<String, Object> map = new HashMap<String, Object>();
        usersReference.updateChildren(map);

        DatabaseReference dr = usersReference.child(tmpkey);
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("email", email);
        map2.put("username", username);
        map2.put("iconfile", uuid);
        dr.updateChildren(map2);

        if(mysharedPreferenceOp!=null) {
            mysharedPreferenceOp.setUserkey(tmpkey);
        }

        return uuid;
    }

    public void createNewTopic(HashMap<String, Object>  topicmap) {
        String tmpkey = topicReference.push().getKey();
        Map<String, Object> map = new HashMap<String, Object>();
        topicReference.updateChildren(map);

        DatabaseReference dr = topicReference.child(tmpkey);

        dr.updateChildren(topicmap);
    }
    /*

    private void signIn(Context context) {

        firebaseAuth.signInWithEmailAndPassword("harumi.inden@gmail.com", "haruharu")
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("main", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("main", "signInWithEmail:failed", task.getException());

                        }

                        // ...
                    }
                });


    }*/
}
