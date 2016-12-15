package com.in.den.android.notification;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopicsFragment extends Fragment {

    private ListView listView;

    private TopicArrayAdapter topicArrayAdapter;
    private ArrayList<Topic> arrayList = new ArrayList<Topic>();
    private DatabaseReference topicReference = FirebaseDatabase.getInstance().getReference()
            .child("topics");

    public TopicsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_topics, container, false);

        listView = (ListView) root.findViewById(R.id.listView);
        arrayList.clear();
        topicArrayAdapter = new TopicArrayAdapter(getContext(),  arrayList);
        listView.setAdapter(topicArrayAdapter);

        topicReference.addValueEventListener(getValueEventListener());


        return root;
    }

    private ValueEventListener getValueEventListener() {

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<Topic> hashset = new HashSet<Topic>();
                Iterator ite = dataSnapshot.getChildren().iterator();

                while (ite.hasNext()) {

                    Topic atopic = new Topic();

                    DataSnapshot ds = ((DataSnapshot) ite.next());
                    atopic.key = ds.getKey();
                    HashMap<String, Object> map = (HashMap<String, Object>)ds.getValue();
                    ArrayList<String> group = (ArrayList<String>) map.get("GROUP");
                    atopic.group = group;
                    atopic.title = (String) map.get("TITLE");
                    atopic.desc = (String) map.get("DESC");
                    atopic.owner = (String) map.get("OWNER");
                    atopic.startdate = (String) map.get("STARTDATE");
                    atopic.enddate = (String) map.get("ENDDATE");

                   hashset.add(atopic);
                    //arrayList.add(atopic);
                }

                topicArrayAdapter.clear();
                topicArrayAdapter.addAll(hashset);
               // topicArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }



}
