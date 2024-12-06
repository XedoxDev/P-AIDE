package com.xedox.paide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xedox.paide.utils.adapters.ProjectsAdapter;
import com.xedox.paide.utils.dialogs.CreateProjectDialog;
import com.xedox.paide.utils.Project;

import java.util.List;

import static com.xedox.paide.PAIDE.*;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProjectsAdapter projectsAdapter;
    private RecyclerView projects;
    private TextView isEmptyProjects;
    private FloatingActionButton newProjectFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initSchemes(this);
        
        toolbar = findViewById(R.id.toolbar);
        projects = findViewById(R.id.projects);
        isEmptyProjects = findViewById(R.id.empty_projects);
        newProjectFAB = findViewById(R.id.newProjectButton);

        setSupportActionBar(toolbar);
        projects.setLayoutManager(new LinearLayoutManager(this));

        projectsAdapter =
                new ProjectsAdapter(
                        this,
                        isEmpty ->
                                isEmptyProjects.setVisibility(isEmpty ? View.VISIBLE : View.GONE));
        projectsAdapter.oicl =
                (project, ii) -> {
                    try {
                        String name = project.name;
                        Intent i = new Intent(MainActivity.this, EditorActivity.class);
                        i.putExtra("name", name);
                        startActivity(i);
                        finish();
                    } catch (Exception err) {
                        err.printStackTrace();
                        debug(err.toString());
                    }
                };
        projects.setAdapter(projectsAdapter);
        
        List<Project> projectList = getProjects(this);
        projectsAdapter.setProjects(projectList);

        newProjectFAB.setOnClickListener(v -> CreateProjectDialog.show(this, projectsAdapter));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        projectsAdapter = null;
        toolbar = null;
        newProjectFAB = null;
        isEmptyProjects = null;
    }
}
