package com.example.ahmad_khadijah_s2330453.view;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ahmad_khadijah_s2330453.R;
import com.example.ahmad_khadijah_s2330453.model.Situation;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import android.graphics.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class SituationAdapter extends
        RecyclerView.Adapter<SituationAdapter.ViewHolder> {

    private final ArrayList<Situation> mDataSet;
    private int expandedPosition = -1;

    public SituationAdapter(ArrayList<Situation> dataSet) {
        mDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView expandIcon;
        ImageView situationIcon;
        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            expandIcon = v.findViewById(R.id.expandIcon);
            situationIcon = itemView.findViewById(R.id.situationIcon);
        }

        public TextView getTextView() {
            return textView;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    private String displayValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not available";
        }
        return value;
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return "Not available";
        }

        try {
            return OffsetDateTime.parse(date).format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
            );
        } catch (Exception e) {
            return date;
        }
    }



    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder viewHolder, int position) {

        Situation situation = mDataSet.get(position);
        //icons
        int icon = getIcon(situation);

        viewHolder.situationIcon.setImageResource(icon);
        viewHolder.itemView.setBackgroundTintList( //set the card colour
                android.content.res.ColorStateList.valueOf(getDurationColour(situation))
        );
        // gives the incidents an outlien and rowadworks the colours
        MaterialCardView card = (MaterialCardView) viewHolder.itemView;

        if ("Roadworks".equals(situation.getFeedType())) {
            card.setStrokeWidth(0);
        } else {
            card.setStrokeColor(Color.parseColor("#200c52"));
            card.setStrokeWidth(4);
        }

        String type = situation.getSituationType();
        String description = situation.getDescription();

        //format the data
        if (type != null) {
            type = type.replaceAll("([a-z])([A-Z])", "$1 $2");
        } else {
            type = "Unknown";
        }

        String roadName = situation.getRoadName();
        if (roadName == null || roadName.isEmpty()) {
            roadName = "Unknown Road";
        }
        boolean isExpanded = position == expandedPosition;

        String text;

        if (isExpanded) {
            text = roadName + "\n" +
                    type + "\n\n" +
                    displayValue(description) + "\n\n" +
                    "Start: " + formatDate(situation.getStartTime()) + "\n" +
                    "End: " + formatDate(situation.getEndTime()) + "\n\n" +
                    "Traffic impact:\n" +
                    "Delay type: " + displayValue(situation.getDelaysType())
                    .replaceAll("([a-z])([A-Z])", "$1 $2") + "\n\n" +
                    "Location:\n" +
                    "Latitude: " + situation.getLatitude() + "\n" +
                    "Longitude: " + situation.getLongitude();

            viewHolder.expandIcon.setImageResource(R.drawable.ic_expand_less);

        } else {
            text = roadName + "\n" + type;

            viewHolder.expandIcon.setImageResource(R.drawable.ic_expand_more);
        }

        SpannableString spannable = new SpannableString(text);

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                roadName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        viewHolder.getTextView().setText(spannable);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = viewHolder.getBindingAdapterPosition();

                if (clickedPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                int previousPosition = expandedPosition;

                if (expandedPosition == clickedPosition) {
                    expandedPosition = -1;
                } else {
                    expandedPosition = clickedPosition;
                }

                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition);
                }

                if (expandedPosition != -1) {
                    notifyItemChanged(expandedPosition);
                }
            }
        });
    }

    private static int getIcon(Situation situation) {
        String description = situation.getDescription() == null
                ? ""
                : situation.getDescription().toLowerCase();

        String type = situation.getSituationType();

        int icon;

        if (description.contains("lane closure") ||
                description.contains("narrow lanes")) {

            icon = R.drawable.ic_lane_closed;

        } else if (description.contains("closed") ||
                description.contains("closure")) {

            icon = R.drawable.ic_closed;

        } else if (description.contains("fuel") ||
                description.contains("service") ||
                description.contains("unavailable")) {

            icon = R.drawable.ic_serv_dist;

        } else if ("GeneralObstruction".equals(type)) {

            icon = R.drawable.ic_warning;

        } else if ("Roadworks".equals(situation.getFeedType())) {

            icon = R.drawable.ic_roadworks;

        } else {

            icon = R.drawable.ic_incident;
        }
        return icon;
    }


    private int getDurationColour(Situation situation) {
        int defaultColour = Color.parseColor("#E6F2F0");

        if (!"Roadworks".equals(situation.getFeedType())) {
            return defaultColour;
        }

        try {
            OffsetDateTime end = OffsetDateTime.parse(situation.getEndTime());
            //works out the remaining time
            long minutes = Duration.between(OffsetDateTime.now(), end).toMinutes();

            if (minutes < 0) return defaultColour;
            if (minutes <= 10080) return Color.parseColor("#CDFAA0"); // 7 days or less = green
            if (minutes <= 43200) return Color.parseColor("#FAFAA0"); // 7 to 30 days = yellow

            return Color.parseColor("#FF6962"); //more than 30 is red


        } catch (Exception e) {
            return defaultColour;
        }
    }




    //number of situations
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
