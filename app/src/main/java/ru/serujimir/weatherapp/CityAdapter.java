package ru.serujimir.weatherapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    public final List<City> cityList;
    SQLiteDatabase sqLiteDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    private OnCityClickListener onCityClickListener;
    private Context context;
    CityAdapter(Context context,List<City> cityList, OnCityClickListener onCityClickListener) {
        this.context=context;
        this.cityList=cityList;
        this.layoutInflater=LayoutInflater.from(context);
        this.onCityClickListener = onCityClickListener;
        sharedPreferences = context.getSharedPreferences("Current_city", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.tvCity.setText(city.getCity().substring(0,1).toUpperCase() + city.getCity().substring(1).toLowerCase());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater.getContext());
                builder.setTitle(context.getString(R.string.delete_confirmation) + " " + city.getCity() + " ?");
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferencesEditor.putString("Current_city","Moscow");
                        sharedPreferencesEditor.apply();
                        DatabaseHelper databaseHelper = new DatabaseHelper(layoutInflater.getContext());
                        databaseHelper.create_db();
                        sqLiteDatabase = databaseHelper.open();
                        onCityClickListener.onCityClick(city);
                        sqLiteDatabase.delete("citiesDatabases", "_id = " + city.getId(), null);;
                        sqLiteDatabase.close();
                        Update();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });
        holder.tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesEditor.putString("Current_city", city.getCity());
                sharedPreferencesEditor.apply();
                onCityClickListener.onCityClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
    public void Update() {
        cityList.clear();
        DatabaseHelper databaseHelper = new DatabaseHelper(layoutInflater.getContext());
        SQLiteDatabase sqLiteDatabase = databaseHelper.open();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + DatabaseHelper.TABLE, null);
        if(cursor.moveToFirst()) {
            int id = cursor.getColumnIndex("_id");
            int city = cursor.getColumnIndex("city");

            do{
                String text_city = cursor.getString(city);
                String text_id = cursor.getString(id);
                cityList.add(new City(text_city,text_id));
            }while (cursor.moveToNext());
        }else {
        }
        cursor.close();
        sqLiteDatabase.close();
        notifyDataSetChanged();
    }
    public interface OnCityClickListener {
        void onCityClick(City city);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCity;
        final ImageButton btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tvCity);
            btnDelete = itemView.findViewById(R.id.imBtnDelete);
        }
    }
}
