package com.in.den.android.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harumi on 13/12/2016.
 */
public class TopicArrayAdapter extends ArrayAdapter<Topic> {

    LayoutInflater inflater;


    public TopicArrayAdapter(Context context, List<Topic> objects) {
        super(context, R.layout.simple_list_item, objects);

        inflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TopicArrayAdapter.TopicViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_list_item, null);

            viewHolder = new TopicArrayAdapter.TopicViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TopicArrayAdapter.TopicViewHolder) convertView.getTag();
        }

        final Topic topic = getItem(position);

        viewHolder.title.setText(topic.title);
        viewHolder.desc.setText(topic.desc);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), ChatRoom.class);
                intent.putExtra("topic", topic);
                getContext().startActivity(intent);

            }
        });


        return convertView;

    }

    class TopicViewHolder {
        TextView title;
        TextView desc;
        TopicViewHolder(View view) {

            title = (TextView)view.findViewById(R.id.txtviewtitle);
            desc = (TextView)view.findViewById(R.id.txtviewdesc);
        }

    }
}