package com.xedox.paide;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.ConsoleView;
import com.xedox.paide.utils.adapters.FilesAdapter;
import com.xedox.paide.utils.adapters.TabsAdapter;
import com.xedox.paide.utils.compiler.Builder;
import com.xedox.paide.utils.editor.Editor;
import com.xedox.paide.utils.editor.EditorFragment;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    public Toolbar toolbar;

    public TabsAdapter tabsAdapter;
    public TabLayout tabLayout;
    public ViewPager2 viewPager;

    public ProgressBar progress;

    public Project project;

    public DrawerLayout drawer;
    public ActionBarDrawerToggle drawerToggle;
    public IFile currentDir;
    public FilesAdapter filesAdapter;

    public RecyclerView filesList;

    public ImageButton undo, redo, openConsole, mkfile, mkdir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        filesList = findViewById(R.id.files_list);
        drawer = findViewById(R.id.drawer_layout);
        progress = findViewById(R.id.progress);

        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        openConsole = findViewById(R.id.console);
        mkfile = findViewById(R.id.mkfile);
        mkdir = findViewById(R.id.mkdir);
        setupActionButtons();

        String projectName = getIntent().getStringExtra("name");
        project = new Project(projectName);
        currentDir = project.dir;

        tabsAdapter = new TabsAdapter(this);
        filesAdapter = new FilesAdapter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(project.name);

        filesAdapter.onItemClickListener =
                (view, file) -> {
                    String fileName = file.getName();
                    if (fileName == "..") {
                        toParentDirectory();
                    } else if (file.isDir()) {
                        toDirectory(file);
                    } else if (file.isFile()) {
                        openFile(file);
                    }
                };
        filesAdapter.setList(Arrays.asList(project.dir.ifiles()));
        refreshFileList();

        filesList.setLayoutManager(new LinearLayoutManager(this));
        filesList.setAdapter(filesAdapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(
                        tabLayout,
                        viewPager,
                        (tab, pos) -> {
                            tab.setText(tabsAdapter.editors.get(pos).file.getName());
                        })
                .attach();
        cv = new ConsoleView(EditorActivity.this);
    }

    private void toParentDirectory() {
        IFile cd = currentDir;
        currentDir = new FileX(cd.parent());
        refreshFileList();
    }

    private void toDirectory(IFile directory) {
        IFile cd = currentDir;
        currentDir = new FileX(cd, directory.getName());
        refreshFileList();
    }

    private void openFile(IFile file) {
        try {
            tabsAdapter.add(file, Editor.SORA_EDITOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshFileList() {
        List<IFile> f = new ArrayList<>(Arrays.asList(currentDir.ifiles()));
        f.add(0, new FileX(".."));
        if (f != null && f.size() > 0) {
            filesAdapter.setList(f);
        } else {
            filesAdapter.setList(new ArrayList<IFile>());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.save) {
            for (EditorFragment ef : tabsAdapter.editors) {
                ef.save();
            }
        }
        /*if (item.getItemId() == R.id.launch) {
            for (EditorFragment ef : tabsAdapter.editors) {
                ef.save();
            }
            Intent i = new Intent(this, PreviewActivity.class);
            i.putExtra("name", project.getName());
            startActivity(i);
            finish();
        }*/
        if (item.getItemId() == R.id.format_code) {
            tabsAdapter.get(tabLayout.getSelectedTabPosition()).editor.formate();
        }
        if (item.getItemId() == R.id.build) {
            Builder.build(this, cv, project, progress);
        }
        return super.onOptionsItemSelected(item);
    }

    private ConsoleView cv;
    private BottomSheetDialog console;

    public void setupActionButtons() {
        undo.setOnClickListener(
                v -> tabsAdapter.get(tabLayout.getSelectedTabPosition()).editor.undo());
        redo.setOnClickListener(
                v -> tabsAdapter.get(tabLayout.getSelectedTabPosition()).editor.redo());
        openConsole.setOnClickListener(v -> showConsole());
    }

    public void showConsole() {
        if (console == null) {
            console = new BottomSheetDialog(this);
            console.setContentView(cv);
        }
        console.show();
    }
}
