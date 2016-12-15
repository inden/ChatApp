package com.in.den.android.notification;

import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.in.den.android.notification.ChatArrayAdaptor.*;

public class ChatRoom extends AppCompatActivity {
    ListView listView;
    ChatArrayAdaptor chatArrayAdaptor;
    private DatabaseReference topicReference = FirebaseDatabase.getInstance().getReference().child("topics");
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");
    private HashMap<String, User> userHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        String myname = getIntent().getStringExtra("user_name");

        userHashMap = new HashMap<String,User>();

        listView = (ListView) findViewById(R.id.listViewChat);
        Button button = (Button) findViewById(R.id.button2);
        final EditText editText = (EditText) findViewById(R.id.editText2);

        SharedPreferenceOp sharedPreferenceOp = new SharedPreferenceOp(this);
        final String userkey = sharedPreferenceOp.getUserkey();
        final String username = sharedPreferenceOp.getUserName();
        final String iconuuid = sharedPreferenceOp.getIconUuid();

        chatArrayAdaptor = new ChatArrayAdaptor(this, R.layout.chat_list_item);
        chatArrayAdaptor.setMyName(userkey);
        chatArrayAdaptor.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                listView.setSelection(chatArrayAdaptor.getCount() - 1);
            }
        });

        Topic topic = (Topic) getIntent().getSerializableExtra("topic");
        String chatroom = topic.title;


        setTitle(chatroom);

        final DatabaseReference root = topicReference.child(topic.key).child("chat");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                String temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference dr = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("msg", editText.getText().toString());
                map2.put("userkey", userkey);
                map2.put("username", username);
                map2.put("useruuid", iconuuid);
                dr.updateChildren(map2);

                editText.setText("");

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
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
        });

        listView.setAdapter(chatArrayAdaptor);
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Object o = dataSnapshot.getValue();
        if(o instanceof HashMap) {
            HashMap<String, String> map = (HashMap<String,String> )o;
            /*
              map2.put("userket", userkey);
                map2.put("username", username);
                map2.put("useruuid", iconuuid);
             */

            if(map.containsKey("msg") && map.containsKey("username")) {
                User user = new User();
                String msg = map.get("msg");
                user.name = map.get("username");
                user.iduser = map.get("userkey");
                user.iconUuid = map.get("useruuid");

                ChatArrayAdaptor.ChatMessage cmsg = chatArrayAdaptor.createChatMessage(user, msg);
                chatArrayAdaptor.add(cmsg);
            }
        }
    }
}
