package com.in.den.android.notification;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by harumi on 21/11/2016.
 */

public class ChatArrayAdaptor extends ArrayAdapter {

    ArrayList<ChatMessage> messageArrayList = new ArrayList<ChatMessage>();
    String mMyName;



    public ChatArrayAdaptor(Context context, int resource) {
        super(context, resource);

    }

    @Override
    public void add(Object message) {
        if(message instanceof  ChatMessage) {
            messageArrayList.add((ChatMessage)message);
            super.add(message);
        }
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }


    public ChatMessage getItem(int index) {
        return this.messageArrayList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_list_item, parent, false);
        }
        LinearLayout singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        TextView chatText = (TextView) row.findViewById(R.id.singleMessage);
        ImageView imageViewMe = (ImageView) row.findViewById(R.id.imageViewChatMe);
        ImageView imageViewYou = (ImageView) row.findViewById(R.id.imageViewChatYou);



        if(chatMessageObj.left) {
            chatText.setText(chatMessageObj.message);
            imageViewYou.setVisibility(View.GONE);

            Picasso.with(getContext()).load(chatMessageObj.myUser.constructIconUriFromUUID())
                    .resize(30,30).centerCrop().into(imageViewMe);
        }
        else {

            chatText.setText(chatMessageObj.username + " : " + chatMessageObj.message);

            imageViewMe.setVisibility(View.GONE);

            Picasso.with(getContext()).load(chatMessageObj.myUser.constructIconUriFromUUID())
                    .error(R.drawable.neko_shiro).resize(30,30).centerCrop().into(imageViewYou);

        }
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_b : R.drawable.bubble_a);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public void setMyName(String name) {
        mMyName = name;
    }

    public ChatMessage createChatMessage(User user, String message) {
        return new ChatMessage(user, message);
    }

    public class ChatMessage {

        String username;
        String message;
        boolean left = false;
        User myUser;

        public ChatMessage(User user, String message) {
            myUser = user;
            this.message = message;
            username = user.name;

            if(myUser.iduser.equals(mMyName)) {
                left = true;
            }
        }
    }
}
