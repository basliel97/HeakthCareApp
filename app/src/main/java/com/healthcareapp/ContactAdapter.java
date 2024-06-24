package com.healthcareapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private int mResource;
    private DatabaseReference emergencyContactsRef;

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        emergencyContactsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("emergency_contacts");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
        }

        // Lookup view for data population
        TextView contactName = convertView.findViewById(R.id.contactName);
        TextView phoneNumber = convertView.findViewById(R.id.phoneNumer);
        Button callButton = convertView.findViewById(R.id.callbutton);
        Button editButton = convertView.findViewById(R.id.contactEdit);
        Button deleteButton = convertView.findViewById(R.id.contactDelete);

        // Populate the data into the template view using the data object
        if (contact != null) {
            contactName.setText(contact.getName());
            phoneNumber.setText(contact.getPhoneNumber());

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the call button click event
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
                    mContext.startActivity(intent);
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditEmergencyContactActivity.class);
                    intent.putExtra("contactId", contact.getId());
                    intent.putExtra("contactName", contact.getName());
                    intent.putExtra("phoneNumber", contact.getPhoneNumber());
                    mContext.startActivity(intent);
                    // Handle the edit button click event
                    // Add your edit contact logic here
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show confirmation dialog before deletion
                    new AlertDialog.Builder(mContext)
                            .setTitle("Delete Contact")
                            .setMessage("Are you sure you want to delete this contact?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Perform deletion
                                    deleteContact(contact, position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private void deleteContact(Contact contact, int position) {
        String contactId = contact.getId(); // Get ID from Contact object
        if (contactId != null && !contactId.isEmpty()) {
            // Delete the contact from Firebase using the ID
            emergencyContactsRef.child(contactId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(mContext, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                        // Remove the contact from the list and notify the adapter
                        remove(contact);
                        notifyDataSetChanged(); // Refresh ListView after deletion
                    })
                    .addOnFailureListener(e -> Toast.makeText(mContext, "Failed to delete contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(mContext, "Contact ID is null or empty", Toast.LENGTH_SHORT).show();
        }
    }
}
