package com.in.den.android.notification;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTopicFragment extends Fragment
                                implements UserArrayAdapter.UserListOp {

    private ImageButton btnEndDate;
    private ImageButton btnReset;
    private ImageButton btnInvite;
    private ImageButton btnNewTopicOk;
    private EditText editTextTopic;
    private EditText editTextTopicDesc;
    private TextView textViewEndDate;
    private ListView listViewInvites;
    private ArrayList<User> userList = new ArrayList<User>();
    private UserArrayAdapter userArrayAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyy MMM dd E");
    private SharedPreferenceOp sharedPreferenceOp;
    private String topicowner;

    public NewTopicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_new_topic, container, false);
        init(root);

        sharedPreferenceOp = new SharedPreferenceOp(getContext());
        topicowner = sharedPreferenceOp.getUserkey();
        boolean bItemdeletable = true;
        boolean bCheckBoxvisible = false;
        userArrayAdapter = new UserArrayAdapter(getContext(),R.layout.item_list_users,
                userList, false, bItemdeletable);
        userArrayAdapter.setUserListOp(this);

        listViewInvites.setAdapter(userArrayAdapter);

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndDate();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetTopic();
            }
            });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("SELECT_USERS_ACTION");
                intent.putExtra("checkboxvisible", true);

                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        });

        btnNewTopicOk.setOnClickListener(getOkClickListner());

        return root;
    }

    private void init(View root) {
        btnEndDate = (ImageButton)root.findViewById(R.id.btnDateEnd);
        btnReset = (ImageButton)root.findViewById(R.id.btnNwTopicReset);
        btnInvite = (ImageButton)root.findViewById(R.id.btnInvite);
        btnNewTopicOk = (ImageButton)root.findViewById(R.id.btnNwTopicOk);
        editTextTopic = (EditText)root.findViewById(R.id.editTextNewTopicTitre);
        editTextTopicDesc = (EditText)root.findViewById(R.id.editTextNewTopicDesc);
        textViewEndDate = (TextView) root.findViewById(R.id.tvDateEnd);
        listViewInvites = (ListView) root.findViewById(R.id.listViewInvite);

        setDefaultTopicEnd();

    }

    private View.OnClickListener getOkClickListner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTopic.getText().toString();
                String desc = editTextTopicDesc.getText().toString();
                String sEnddate = formatDate(textViewEndDate.getText().toString());
                int nbinvites = userList.size();

                if(!title.isEmpty() && !desc.isEmpty() && !sEnddate.isEmpty() && nbinvites > 0) {
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    map.put("OWNER", topicowner);
                    map.put("TITLE", title);
                    map.put("DESC", desc);
                    map.put("STARTDATE", getStartdate());
                    map.put("ENDDATE", sEnddate);

                    ArrayList<String> group = new ArrayList<String>();
                    for(int i=0; i < nbinvites; i++) {
                        group.add(userList.get(i).iduser);
                    }

                    if(!group.contains(topicowner)) {
                        group.add(topicowner);
                    }

                    map.put("GROUP", group);

                    Intent intent = new Intent();
                    intent.setAction("REGISTER_TOPIC_ACTION");
                    intent.putExtra("map", map);

                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                    resetTopic();
                }
            }
        };
    }

    private String formatDate(String sdate) {
        String formateddate = "";
        try {
            Date d = sdf.parse(sdate);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
            formateddate = sdf2.format(d);
        }
        catch(Exception ex) {}

        return formateddate;
    }

    private String getStartdate() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        String formateddate = sdf2.format(Calendar.getInstance().getTime());
        return formateddate;
    }

    private void setDefaultTopicEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        textViewEndDate.setText(sdf.format(calendar.getTime()));
    }


    public void resetUserList(List<User> newlist) {
        userArrayAdapter.clear();
        userArrayAdapter.addAll(newlist);
    }


    private void resetTopic() {
        editTextTopicDesc.setText("");
        editTextTopic.setText("");
        setDefaultTopicEnd();
        userList.clear();
        userArrayAdapter.notifyDataSetChanged();

    }

    private void sendBrodcast() {
        Intent intent = new Intent();
        intent.setAction("INVITED_USERS_ACTION");
        intent.putExtra("invited", userList);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void setEndDate() {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        Date enddate = calendar.getTime();
                        String sdate = sdf.format(enddate);
                        textViewEndDate.setText(sdate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void removeUser(User user) {

        userList.remove(user);
       // sendBrodcast();
        userArrayAdapter.notifyDataSetChanged();
    }

}
