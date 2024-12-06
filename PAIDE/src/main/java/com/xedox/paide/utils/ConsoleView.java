package com.xedox.paide.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import com.xedox.paide.R;

public class ConsoleView extends EditText {
    public ConsoleView(Context c) {
        super(c);
        init();
    }

    public ConsoleView(Context c, AttributeSet attrs) {
        super(c, attrs);
        init();
    }

    public void init() {
        setTextSize(17); 
        setGravity(Gravity.START);
        setTextColor(getContext().getColor(R.color.text));
        setPadding(7, 7, 7, 7);
        setBackgroundColor(0x00000000);
        setActivated(false);
        setHorizontalScrollBarEnabled(true);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setTextIsSelectable(true); 
        setHeight(400);
    }

    public synchronized void print(String txt) {
        append(txt);
    }

    public synchronized void println(String txt) {
        append(txt + "\n");
    }
}
