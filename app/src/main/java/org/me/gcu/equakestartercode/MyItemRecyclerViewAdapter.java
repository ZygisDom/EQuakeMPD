///////////////////////////////////////////////////////////////////////////////
//
// ZYGIMANTAS DOMARKAS
// S1718169
//
///////////////////////////////////////////////////////////////////////////////

package org.me.gcu.equakestartercode;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link QuakeData}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    private double magnitude;

    public interface ItemListener{
        void onItemClicked(QuakeData quakeData);
    }

    public void setItemListener(ItemListener listener){
        itemListener = listener;
    }

    private ItemListener itemListener;

    private List<QuakeData> mValues;

    public void updateValues(List<QuakeData> newValues) {
        mValues = newValues;
        notifyDataSetChanged();
    }

    public MyItemRecyclerViewAdapter(List<QuakeData> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mValues.get(position));
//        holder.mIdView.setText(mValues.get(position).description.location);
//        holder.mContentView.setText(mValues.get(position).description.getMagnitude());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView locationView;
        public final TextView magnitudeView;
        private QuakeData quakeData;
        private LinearLayout linearLayout;
        private double magnitude;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            locationView = (TextView) view.findViewById(R.id.locationTextView);
            magnitudeView = (TextView) view.findViewById(R.id.magnitudeTextView);
            linearLayout = (LinearLayout) view.findViewById(R.id.lLayout);
            mView.setOnClickListener(itemView -> itemListener.onItemClicked(quakeData));
        }

        public void bind(QuakeData data) {
            quakeData = data;
            locationView.setText(data.getLocation());
            magnitudeView.setText(data.getMagnitude());
            linearLayout.setBackgroundColor(Color.parseColor(mapMagnitudeColor(quakeData.getMagnitude())));
//            mContentView.setTextColor();
        }

        private String mapMagnitudeColor(String magnitudeString){
            Double magnitude = Double.parseDouble(magnitudeString.split(" ")[3]);
            return getMagColour(magnitude);
        }

        public String getMagColour(Double magnitude){

            String colourString;

            if(magnitude >= 3){
                colourString = "#d94141";
            }
            else if(magnitude >= 2){
                colourString = "#ffd119";
            }
            else if (magnitude >= 1){
                colourString = "#fffd94";
            }
            else{
                colourString = "#bdffbe";
            }

            return colourString;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + magnitudeView.getText() + "'";
        }
    }
}