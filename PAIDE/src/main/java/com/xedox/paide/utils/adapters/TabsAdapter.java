package com.xedox.paide.utils.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.xedox.paide.utils.editor.EditorFragment;
import com.xedox.paide.utils.io.IFile;
import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentStateAdapter {

    public List<EditorFragment> editors;

    public TabsAdapter(FragmentActivity fa) {
        super(fa);
        editors = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return editors.size();
    }

    @Override
    public Fragment createFragment(int pos) {
        return editors.get(pos);
    }

    public void add(IFile file, int type) {
        EditorFragment ef = new EditorFragment(file, type);
        editors.add(ef);
        notifyItemInserted(editors.size() - 1);
    }

    public void delete(int pos) {
        editors.remove(pos);
        notifyItemRemoved(pos);
    }

    public EditorFragment get(int pos) {
        return editors.get(pos);
    }
}
