package com.in.den.android.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harumi on 07/12/2016.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    LayoutInflater inflater;
    boolean bCheckBoxVisible = false;
    boolean bItmDeletable = false;
    List<User> userlist;
    List<User> checkedUserList;


    public UserArrayAdapter(Context context, int resource, List<User> list, boolean bCBoxVisible)
    {
        this(context, resource, list, bCBoxVisible, false);
    }

    public UserArrayAdapter(Context context, int resource, List<User> list, boolean bCBoxVisible,
                            boolean bItemDeletable) {
        super(context, resource, list);
        inflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        userlist = list;
        bCheckBoxVisible = bCBoxVisible;
        bItmDeletable = bItemDeletable;
        checkedUserList = new ArrayList<User>();
    }

    public void setCheckBoxVisible(boolean visible) {
        boolean bchanged = false;
        if (bCheckBoxVisible != visible) {
            bchanged = true;
        }
        bCheckBoxVisible = visible;
        if (bchanged) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserListViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_users, null);

            viewHolder = new UserListViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UserListViewHolder) convertView.getTag();
        }

        final User user = getItem(position);

        viewHolder.username.setText(user.name);

        if(bItmDeletable) {
            viewHolder.username.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String[] items = {"supprimer"};
                    new AlertDialog.Builder(getContext())
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // item_which pressed
                                    if(myUserListOp != null) {
                                        myUserListOp.removeUser(user);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .show();
                    return true;
                }
            });
        }

        if (bCheckBoxVisible) {
            viewHolder.checkBox.setVisibility(CheckBox.VISIBLE);
        } else {
            viewHolder.checkBox.setVisibility(CheckBox.GONE);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {

                    if (!checkedUserList.contains(user)) checkedUserList.add(user);
                } else {
                    checkedUserList.remove(user);
                }
            }
        });

        if (user.iconUri != null) {
            Picasso.with(this.getContext()).load(user.iconUri)
                    .error(R.drawable.neko_shiro)
                    .resize(30, 30).centerCrop().into(viewHolder.userimage);
        } else {
            Picasso.with(this.getContext()).load(R.drawable.neko_shiro)
                    .resize(30, 30).centerCrop().into(viewHolder.userimage);
        }

        return convertView;
    }

    public List<User> getCheckedUserList() {
        return checkedUserList;
    }

    class UserListViewHolder {
        ImageView userimage;
        TextView username;
        CheckBox checkBox;

        public UserListViewHolder(View base) {
            checkBox = (CheckBox) base.findViewById(R.id.checkBoxUser);
            userimage = (ImageView) base.findViewById(R.id.imageViewUserItem);
            username = (TextView) base.findViewById(R.id.textViewUserItem);

        }

    }

    interface UserListOp {
        void removeUser(User user);
    }

    private UserListOp myUserListOp = null;
    public void setUserListOp(UserListOp userListOp) {
        myUserListOp = userListOp;
    }
}
