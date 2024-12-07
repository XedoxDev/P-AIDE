package com.xedox.paide.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xedox.paide.R;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.ContextMenu;

import java.util.ArrayList;
import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.VH> {

    public List<Project> data;
    public OnItemClickListener oicl;
    public EmptyListener isEmpty;
    public Activity context;

    public ProjectsAdapter(Activity context, EmptyListener el) {
        this.context = context;
        this.data = new ArrayList<>();
        this.isEmpty = el;
        updateEmptyState();
    }

    public void setProjects(List<Project> projects) {
        data.clear();
        data.addAll(projects);
        notifyDataSetChanged();
        updateEmptyState();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup vg, int pos) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_item, vg, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH vh, int pos) {
        Project project = data.get(pos);
        vh.name.setText(project.name);
        vh.more.setOnClickListener(v -> showContextMenu(v, project, pos));
    }

    private void showContextMenu(View v, Project project, int pos) {
        new ContextMenu(v.getContext(),
                v,
                R.menu.project,
                i -> {
                    if (i.getItemId() == R.id.delete) {
                        deleteProject(project, pos);
                    }
                    return true;
                });
    }

    private void deleteProject(Project project, int pos) {
        project.delete();
        data.remove(pos);
        notifyItemRemoved(pos);
        updateEmptyState();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        public RelativeLayout parent;
        public TextView name;
        public ImageButton more;

        public VH(View parent) {
            super(parent);
            this.parent = parent.findViewById(R.id.parent);
            this.name = parent.findViewById(R.id.name);
            this.more = parent.findViewById(R.id.more);

            this.parent.setOnClickListener(
                    v -> {
                        if (oicl != null) {
                            oicl.onItemClick(data.get(getAdapterPosition()), getAdapterPosition());
                        }
                    });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Project project, int pos);
    }

    public void add(Project p) {
        data.add(p);
        notifyItemInserted(data.size() - 1);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (isEmpty != null) {
            isEmpty.isEmpty(data.isEmpty());
        }
    }

    public interface EmptyListener {
        void isEmpty(boolean is);
    }
}
