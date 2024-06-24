package com.healthcareapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AppointmentAdapter extends BaseAdapter {


    private Context context;
    private List<Appointment> appointmentList;
    private LayoutInflater inflater;
    private DatabaseReference userAppointmentsRef;
    private DatabaseReference globalAppointmentsRef;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.inflater = LayoutInflater.from(context);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userAppointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        globalAppointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.appointment_card, parent, false);
        }

        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        TextView textViewNotes = convertView.findViewById(R.id.textViewNotes);
        Button editButton = convertView.findViewById(R.id.buttonEdit);
        Button deleteButton = convertView.findViewById(R.id.buttonDelete);

        Appointment appointment = appointmentList.get(position);

        textViewDate.setText("Date: " + appointment.getDate());
        textViewTime.setText("Time: " + appointment.getTime());
        textViewNotes.setText("Notes: " + appointment.getNotes());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before deletion
                new AlertDialog.Builder(context)
                        .setTitle("Delete Appointment")
                        .setMessage("Are you sure you want to delete this appointment?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform deletion
                                deleteAppointment(appointment, position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditAppointmentActivity.class);
                intent.putExtra("appointmentId", appointment.getId());
                intent.putExtra("date", appointment.getDate());
                intent.putExtra("time", appointment.getTime());
                intent.putExtra("notes", appointment.getNotes());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void deleteAppointment(Appointment appointment, int position) {
        String appointmentId = appointment.getId(); // Get ID from Appointment object
        if (appointmentId != null && !appointmentId.isEmpty()) {
            // Remove appointment from user-specific node
            if (userAppointmentsRef != null) {
                userAppointmentsRef.child(appointmentId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Remove appointment from global node
                            if (globalAppointmentsRef != null) {
                                globalAppointmentsRef.child(appointmentId).removeValue()
                                        .addOnSuccessListener(aVoid1 -> {
                                            // Both deletions successful
                                            Toast.makeText(context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                                            // Remove the appointment from the list and notify the adapter
                                            if (position >= 0 && position < appointmentList.size()) {
                                                appointmentList.remove(position);
                                                notifyDataSetChanged(); // Refresh ListView after deletion
                                            } else {
                                                Log.e("DeleteAppointment", "Invalid position: " + position);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to delete from global node
                                            Toast.makeText(context, "Failed to delete appointment from global: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("DeleteAppointment", "Failed to delete from global: " + e.getMessage());
                                        });
                            } else {
                                Log.e("DeleteAppointment", "globalAppointmentsRef is null");
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Failed to delete from user-specific node
                            Toast.makeText(context, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("DeleteAppointment", "Failed to delete from user-specific node: " + e.getMessage());
                        });
            } else {
                Log.e("DeleteAppointment", "userAppointmentsRef is null");
            }
        } else {
            Toast.makeText(context, "Appointment ID is null or empty", Toast.LENGTH_SHORT).show();
            Log.e("DeleteAppointment", "Appointment ID is null or empty");
        }
    }

}