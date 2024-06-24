package com.healthcareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HealthTipsAdapter extends ArrayAdapter<HealthTip> {

    public HealthTipsAdapter(Context context, List<HealthTip> healthTips) {
        super(context, 0, healthTips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HealthTip healthTip = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.health_tip_card, parent, false);
        }

        TextView tipContent = convertView.findViewById(R.id.tipContent);
        TextView tipDate = convertView.findViewById(R.id.tipDate);

        if (healthTip != null) {
            tipContent.setText(healthTip.getContent());
            tipDate.setText(healthTip.getDate());
        }

        return convertView;
    }
}
