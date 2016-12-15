package com.in.den.android.notification;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListUserFragment extends Fragment {


    private ListView listViewUser;
    private LinearLayout layoutSelect;
    private UserArrayAdapter userArrayAdapter;
    private Button btnSelection;
    private TextView textViewSelection;
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private StorageReference iconStorageReference = FirebaseStorage.getInstance().getReference().child("profilicon");
    private boolean bCheckBoxVisible = false;
    private List<User> userList = new ArrayList<User>();


    public ListUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_list_user, container, false);
        init(root);
        //--------------------------------
        userList.clear();
        userArrayAdapter = new UserArrayAdapter(getContext(),R.layout.item_list_users,
                userList, bCheckBoxVisible);
        listViewUser.setAdapter(userArrayAdapter);

        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> invitedusers = (ArrayList<User>) userArrayAdapter.getCheckedUserList();

                Intent intent = new Intent();
                intent.setAction("INVITED_USERS_ACTION");
                intent.putExtra("invited", invitedusers);

                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

            }
        });

        textViewSelection.setOnClickListener(getTextViewSelectionOnClickLister());

        usersReference.addChildEventListener(getChildEventLister());

        //--------------------------------

        return root;
    }

    private void init(View root) {
        listViewUser = (ListView)root.findViewById(R.id.listViewUsers);
        btnSelection = (Button)root.findViewById(R.id.btnUserSelect);
        textViewSelection = (TextView)root.findViewById(R.id.textViewSelect);
        layoutSelect = (LinearLayout) root.findViewById(R.id.layoutSelect);
    }

    private void changeVisibility() {
        if(bCheckBoxVisible) {
            layoutSelect.setVisibility(LinearLayout.VISIBLE);
        }
        else {
            layoutSelect.setVisibility(LinearLayout.GONE);
        }
    }

    private View.OnClickListener getTextViewSelectionOnClickLister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //not implemented
            }
        };
    }

    private ChildEventListener getChildEventLister() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String iduser = dataSnapshot.getKey();
                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();
                String username ="";
                String iconfile = "";

                while(ite.hasNext()) {

                    DataSnapshot ds = ite.next();
                    String key = ds.getKey();

                    if(key.equals("username")) {
                        username = (String)ds.getValue();

                    }
                    else if(key.equals("iconfile")) {
                        iconfile = (String)ds.getValue();
                    }
                }

                if(!username.isEmpty()) {
                    final User user = new User();
                    user.name = username;
                    user.iduser = iduser;

                  //  userArrayAdapter.add(user);
                    // I comment this image existing check
                    if(!iconfile.isEmpty()) {
                        StorageReference sr = iconStorageReference.child(iconfile + ".jpg");

                        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                user.iconUri = uri.toString();
                                userArrayAdapter.add(user);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userArrayAdapter.add(user);
                            }
                        });
                    }
                    else {
                        userArrayAdapter.add(user);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


    public void changeCheckBoxVisible(boolean visible) {
        if(bCheckBoxVisible != visible) {
            bCheckBoxVisible = visible;
            changeVisibility();
            userArrayAdapter.setCheckBoxVisible(bCheckBoxVisible);
        }
    }


}
