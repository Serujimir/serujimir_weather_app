package ru.serujimir.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekForecastAdapter extends RecyclerView.Adapter<WeekForecastAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    public List<WeekForecast> weekForecastList;

    WeekForecastAdapter(Context context, List<WeekForecast> weekForecastList) {
        this.weekForecastList = weekForecastList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.week_forecast_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeekForecast weekForecast = weekForecastList.get(position);
        holder.tvDayOfWeek.setText(weekForecast.getDayOfWeek());
        holder.tvMinDayOfWeekTemp.setText(weekForecast.getMinDayOfWeekTemp());
        holder.tvMaxDayOfWeekTemp.setText(weekForecast.getMaxDayOfWeekTemp());
        setDayIcon(holder.imWeekDay, weekForecast.getIcon());

        Animation animation = AnimationUtils.loadAnimation(layoutInflater.getContext(), R.anim.adapter_update_scale);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return weekForecastList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDayOfWeek, tvMinDayOfWeekTemp, tvMaxDayOfWeekTemp;
        final ImageView imWeekDay, imBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imBar = itemView.findViewById(R.id.imBar);
            imWeekDay = itemView.findViewById(R.id.imWeekDay);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvMinDayOfWeekTemp = itemView.findViewById(R.id.tvMinDayOfWeekTemp);
            tvMaxDayOfWeekTemp = itemView.findViewById(R.id.tvMaxDayOfWeekTemp);

        }
    }
    public void Update() {
        notifyDataSetChanged();
    }
    public void setDayIcon(final ImageView tvCurrWeather, final String icon) {
        switch (icon) {
            case "01d":
                tvCurrWeather.setImageResource(R.drawable.d01);
                break;
            case "02d":
                tvCurrWeather.setImageResource(R.drawable.d02);
                break;
            case "03d":
                tvCurrWeather.setImageResource(R.drawable.d03);
                break;
            case "04d":
                tvCurrWeather.setImageResource(R.drawable.d04);
                break;
            case "09d":
                tvCurrWeather.setImageResource(R.drawable.d09);
                break;
            case "10d":
                tvCurrWeather.setImageResource(R.drawable.d10);
                break;
            case "11d":
                tvCurrWeather.setImageResource(R.drawable.d11);
                break;
            case "13d":
                tvCurrWeather.setImageResource(R.drawable.d13);
                break;
            case "50d":
                tvCurrWeather.setImageResource(R.drawable.d50);
                break;

            case "01n":
                tvCurrWeather.setImageResource(R.drawable.n01);
                break;
            case "02n":
                tvCurrWeather.setImageResource(R.drawable.n02);
                break;
            case "03n":
                tvCurrWeather.setImageResource(R.drawable.n03);
                break;
            case "04n":
                tvCurrWeather.setImageResource(R.drawable.n04);
                break;
            case "09n":
                tvCurrWeather.setImageResource(R.drawable.n09);
                break;
            case "10n":
                tvCurrWeather.setImageResource(R.drawable.n10);
                break;
            case "11n":
                tvCurrWeather.setImageResource(R.drawable.n11);
                break;
            case "13n":
                tvCurrWeather.setImageResource(R.drawable.n13);
                break;
            case "50n":
                tvCurrWeather.setImageResource(R.drawable.n50);
                break;

        }
    }
}
