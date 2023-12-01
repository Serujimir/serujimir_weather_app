package ru.serujimir.weatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        holder.imImage.setImageDrawable(cultureItem.getImImage());
        holder.title.setText(cultureItem.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(cultureItem.getTitle());
                builder.setMessage(cultureItem.getText());
                builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cultureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView imImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imImage = itemView.findViewById(R.id.imImage);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }
}
