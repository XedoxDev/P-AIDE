package com.xedox.paide.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import com.xedox.paide.R;

public class ConsoleView extends EditText {
    public ConsoleView(Context c){
        super(c);
        init();
    }
    
    public ConsoleView(Context c, AttributeSet attrs){
        super(c, attrs);
        init();
    }
    
    public void init() {
        setTextSize(24);
        setGravity(Gravity.START);
        setTextColor(getContext().getColor(R.color.text));
        setPadding(7, 7, 7, 7);
        setClickable(false);
    }
    
    public void print(String txt) {
        setText(getText() + txt);
    }
    
    public void println(String txt) {
        setText(getText() + txt + "\n");
    }
}
