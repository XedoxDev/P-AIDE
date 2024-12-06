package com.xedox.paide.utils.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.xedox.paide.R;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.adapters.ProjectsAdapter;

public class CreateProjectDialog {

    public static void show(Context context, ProjectsAdapter pa) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.create_project);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_create_project, null);
        TextInputEditText tiet = view.findViewById(R.id.project_name_edittext);

        tiet.setHint(R.string.project_name);

        builder.setView(view);

        builder.setPositiveButton(
                R.string.create_project,
                (DialogInterface i, int i2) -> {
                    String name = tiet.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Project project = new Project(name);
                        project.create();
                        pa.add(project);
                        i.dismiss();
                    } else {
                        Toast.makeText(context, R.string.empty_project_name, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        builder.setNegativeButton(R.string.cancel, (i, i2) -> i.dismiss());

        builder.create().show();
    }
}
