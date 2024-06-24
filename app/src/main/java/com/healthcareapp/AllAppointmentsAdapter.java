package com.healthcareapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AllAppointmentsAdapter extends BaseAdapter {
    private Activity activity;
    private List<AllAppointments> appointmentsList;

    public AllAppointmentsAdapter(Activity activity, List<AllAppointments> appointmentsList) {
        this.activity = activity;
        this.appointmentsList = appointmentsList;
    }

    @Override
    public int getCount() {
        return appointmentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.all_appointment_card, null);
        }

        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        TextView textViewNotes = convertView.findViewById(R.id.textViewNotes);
        Button buttonConfirm = convertView.findViewById(R.id.buttonConfirm);
        Button buttonDeny = convertView.findViewById(R.id.buttonDeny);

        AllAppointments appointment = appointmentsList.get(position);
        textViewDate.setText("Date: " + appointment.getDate());
        textViewTime.setText("Time: " + appointment.getTime());
        textViewNotes.setText("Notes: " + appointment.getNotes());

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Confirm button click
                // You can implement specific actions here, like marking the appointment as confirmed
                // For now, let's just show a toast message
                Toast.makeText(activity, "Confirm clicked for appointment on " + appointment.getDate(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Deny button click
                // You can implement specific actions here, like marking the appointment as denied
                // For now, let's just show a toast message
                Toast.makeText(activity, "Deny clicked for appointment on " + appointment.getDate(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
