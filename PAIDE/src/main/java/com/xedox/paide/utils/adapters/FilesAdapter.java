package com.xedox.paide.utils.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.xedox.paide.R;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.VH> {

    private Context context;
    private List<IFile> list;
    public OnItemClickListener onItemClickListener;

    public FilesAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public class VH extends RecyclerView.ViewHolder {
        public View parent;
        public ImageView icon;
        public TextView name;
        public ImageButton more;

        public VH(@NonNull View parent) {
            super(parent);
            this.parent = parent;
            icon = parent.findViewById(R.id.icon);
            name = parent.findViewById(R.id.name);

            parent.findViewById(R.id.parent).setOnClickListener(
                    v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                            onItemClickListener.onItemClick(v, list.get(position));
                        }
                    });
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH vh, int pos) {
        IFile f = list.get(pos);
        vh.name.setText(f.getName());

        if (f.getName().equals("..")) {
            vh.icon.setImageResource(R.drawable.back_arrow);
        } else if (f.isFile()) {
            vh.icon.setImageResource(R.drawable.file);
        } else if (f.isDir()) {
            vh.icon.setImageResource(R.drawable.folder);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, IFile file);
    }
    
    public void setList(List<IFile> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void add(int pos, IFile file) {
        list.add(pos, file);
        notifyItemInserted(pos);
    }
    
    public void add(IFile file) {
        list.add(file);
        notifyItemInserted(list.size()-1);
    }

    public void add(int pos, String path) {
        add(pos, new FileX(path));
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        if (pos >= 0 && pos < list.size()) {
            list.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}
