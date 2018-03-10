package com.rogi.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rogi.Model.NotesModel;
import com.rogi.R;
import com.rogi.View.Utils;

import java.util.List;

/**
 * Created by "Mayuri" on 4/8/17.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<NotesModel> notesDatas;
    private OnEditNoteListner editNoteClickListener;
    private OnDeleteNoteListner deleteNoteClickListner;
    boolean noteFlag;

    public NoteListAdapter(Context context, List<NotesModel> notesDatas, OnEditNoteListner editNoteClickListener,
                           OnDeleteNoteListner deleteNoteClickListner, boolean noteFlag) {
        super();
        this.context = context;
        this.notesDatas = notesDatas;
        this.editNoteClickListener = editNoteClickListener;
        this.deleteNoteClickListner = deleteNoteClickListner;
        this.noteFlag = noteFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_task_noteview_list, parent, false);
        RecyclerView.ViewHolder holder = new ViewHolder(v);
        return (ViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotesModel mainData = notesDatas.get(position);
        holder.noteTime.setText(Utils.parseDateFromyyyyMMdd(mainData.getNote_created_date()));
        holder.noteText.setText(mainData.getNote());

        if (noteFlag) {

            holder.editNoteBTN.setVisibility(View.VISIBLE);
            holder.deleteNoteBTN.setVisibility(View.VISIBLE);

            holder.editNoteBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNoteClickListener.onItemClick(mainData);
                }
            });

            holder.deleteNoteBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNoteClickListner.onItemClick(mainData);
                }
            });
        } else {
//            holder.editNoteBTN.setOnClickListener(null);
//            holder.deleteNoteBTN.setOnClickListener(null);

            holder.editNoteBTN.setVisibility(View.GONE);
            holder.deleteNoteBTN.setVisibility(View.GONE);
        }
    }

    public interface OnEditNoteListner {
        void onItemClick(NotesModel data);
    }

    public interface OnDeleteNoteListner {
        void onItemClick(NotesModel data);
    }

    @Override
    public int getItemCount() {
        return notesDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTime, noteText;
        ImageView editNoteBTN, deleteNoteBTN;

        public ViewHolder(View itemView) {
            super(itemView);

            noteTime = (TextView) itemView.findViewById(R.id.noteTime);
            noteText = (TextView) itemView.findViewById(R.id.noteText);
            editNoteBTN = (ImageView) itemView.findViewById(R.id.editNoteBTN);
            deleteNoteBTN = (ImageView) itemView.findViewById(R.id.deleteNoteBTN);
        }
    }
}

