package com.example.signuploginrealtime.ListItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.signuploginrealtime.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    ArrayList<Note> arrayList;
    OnItemClickListener onItemClickListener;

    public NoteAdapter(Context context, ArrayList<Note> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
        return new ViewHolder(view);
    }


    // Di dalam onBindViewHolder method dalam NoteAdapter
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = arrayList.get(position);

        // Use a unique key for each radio button based on its position
        String radioButtonKey = "RadioButtonStatus_" + position;
        boolean radioButtonStatus = SharedPreferencesHelper.getRadioButtonStatus(context, radioButtonKey);

        holder.title.setText(note.getTitle());
        holder.subtitle.setText(note.getContent());
        holder.radioButton.setChecked(radioButtonStatus);

        // Set listener for the whole item view (excluding radio button)
        holder.itemView.setOnClickListener(view -> {
            onItemClickListener.onClick(note); // Trigger item click event
        });

        // Set listener only for radio button click
        holder.radioButton.setOnClickListener(view -> {
            boolean newStatus = !SharedPreferencesHelper.getRadioButtonStatus(context, radioButtonKey);
            SharedPreferencesHelper.setRadioButtonStatus(context, radioButtonKey, newStatus);
            holder.radioButton.setChecked(newStatus);
            onItemClickListener.onRadioClick(note); // Trigger radio button click event
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setNotes(ArrayList<Note> notes) {
        arrayList = notes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            subtitle = itemView.findViewById(R.id.list_item_subtitle);
            radioButton = itemView.findViewById(R.id.radio_button);
        }
    }

    public interface OnItemClickListener {
        void onRadioClick(Note note);
        void onClick(Note note);
    }
}
