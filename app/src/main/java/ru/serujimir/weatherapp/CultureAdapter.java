package ru.serujimir.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CultureAdapter extends RecyclerView.Adapter<CultureAdapter.ViewHolder> {
    public final List<CultureItem> cultureList;
    private final LayoutInflater layoutInflater;
    CultureAdapter(Context context, List<CultureItem> cultureItemList){
        this.cultureList=cultureItemList;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.culture_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CultureItem cultureItem = cultureList.get(position);
        holder.title.setText(cultureItem.getTitle());
        holder.text.setText(cultureItem.getText());
    }

    @Override
    public int getItemCount() {
        return cultureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text, title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            text = itemView.findViewById(R.id.tvText);
        }
    }
}
