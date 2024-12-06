package com.xedox.paide.utils;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import static com.xedox.paide.PAIDE.*;
import androidx.appcompat.widget.PopupMenu;

public class ContextMenu {

    public PopupMenu menu;
    public MenuInflater inflater;
    public OnItemClickListener oicl;

    public ContextMenu(View view, int menuId, OnItemClickListener oicl) {
        this.inflater = new MenuInflater(paide);
        menu = new PopupMenu(paide, view);
        this.oicl = oicl;
        menu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (oicl != null) {
                            return oicl.onItemClick(item);
                        }
                        return false;
                    }
                });
    }

    public static interface OnItemClickListener {
        public boolean onItemClick(MenuItem item);
    }
}
