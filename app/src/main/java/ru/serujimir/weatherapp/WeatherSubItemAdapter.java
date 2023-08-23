package ru.serujimir.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherSubItemAdapter extends RecyclerView.Adapter<WeatherSubItemAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final List<WeatherSubItem> weatherSubItemList;

    WeatherSubItemAdapter(Context context, List<WeatherSubItem> weatherSubItemList) {
        this.layoutInflater=LayoutInflater.from(context);
        this.weatherSubItemList=weatherSubItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.weather_sub_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherSubItem weatherSubItem = weatherSubItemList.get(position);
        holder.tvName.setText(weatherSubItem.getName());
        setIcon(holder.ivIcon, weatherSubItem.icon);
    }

    @Override
    public int getItemCount() {
        return weatherSubItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName;
        final ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }

    public void Update(){
        notifyDataSetChanged();
    }
    public void setIcon(final ImageView imageView, final String text_icon) {
        switch (text_icon) {
            case "visibility":
                imageView.setImageResource(R.drawable.ic_visibility);
                break;
            case "humidity":
                imageView.setImageResource(R.drawable.ic_humidity);
                break;
            case "wind":
                imageView.setImageResource(R.drawable.ic_weather);
                break;
            case "sun":
                imageView.setImageResource(R.drawable.ic_sun_rise_set);
                break;
            case "pressure":
                imageView.setImageResource(R.drawable.ic_humidity);
                break;
            case "coordinates":
                imageView.setImageResource(R.drawable.ic_humidity);
                break;
        }
    }
}
