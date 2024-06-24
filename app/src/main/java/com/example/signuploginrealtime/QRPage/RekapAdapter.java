package com.example.signuploginrealtime.QRPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginrealtime.R;

import java.util.List;

public class RekapAdapter extends RecyclerView.Adapter<RekapAdapter.ViewHolder> {

    private List<DataItem> dataItems;
    private Context context;
    private ItemClickListener itemClickListener;

    public RekapAdapter(List<DataItem> dataItems, Context context) {
        this.dataItems = dataItems;
        this.context = context;
    }

    public void setDataItems(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rekap, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataItem dataItem = dataItems.get(position);

        int nomorUrut = position + 1;
        holder.textColumnNumber.setText(String.valueOf(nomorUrut));

        holder.textColumn1.setText(dataItem.getItemName());
        holder.textColumn2.setText(dataItem.getInformation());
        holder.textColumn3.setText(dataItem.getDate());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(adapterPosition);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Implement item click event if needed
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Add null check to prevent NullPointerException
        return dataItems != null ? dataItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textColumnNumber, textColumn1, textColumn2, textColumn3;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textColumnNumber = itemView.findViewById(R.id.textColumnNumber);
            textColumn1 = itemView.findViewById(R.id.textColumn1);
            textColumn2 = itemView.findViewById(R.id.textColumn2);
            textColumn3 = itemView.findViewById(R.id.textColumn3);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
