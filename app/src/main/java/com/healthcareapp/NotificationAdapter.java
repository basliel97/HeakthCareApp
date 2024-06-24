package com.healthcareapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private Activity activity;
    private List<Notification> notificationList;

    public NotificationAdapter(Activity activity, List<Notification> notificationList) {
        this.activity = activity;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.notification_item, null);
        }

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewMessage = convertView.findViewById(R.id.textViewMessage);
        TextView textViewTimestamp = convertView.findViewById(R.id.textViewTimestamp);

        Notification notification = notificationList.get(position);
        textViewTitle.setText(notification.getTitle());
        textViewMessage.setText(notification.getMessage());
        textViewTimestamp.setText(String.valueOf(notification.getTimestamp()));

        return convertView;
    }
}
