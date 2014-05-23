package com.stuffsels.muzei.deviantart;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import com.stuffsels.muzei.deviantart.helpers.PreferenceHelper;


public class EditIntPreference extends EditTextPreference implements View.OnKeyListener {

    private Integer mInteger;

    public EditIntPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        getEditText().setOnKeyListener(this);
    }

    public EditIntPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        getEditText().setOnKeyListener(this);
    }

    public EditIntPreference(Context context) {
        super(context);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        getEditText().setOnKeyListener(this);
    }

    @Override public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();
        mInteger = parseInteger(text);
        String key = this.getKey();
        if (mInteger == null || mInteger <= 0)
            mInteger = (key == null || key.equals(PreferenceHelper.PREF_REFRESHTIME)) ? 180 : 30;
        persistString(mInteger.toString());
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) notifyDependencyChange(isBlocking);
    }

    @Override public String getText() {
        return mInteger != null ? mInteger.toString() : null;
    }

    private static Integer parseInteger(String text) {
        try { return Integer.parseInt(text); }
        catch (NumberFormatException e) { return null; }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText editText = (EditText)v;
        Editable text = editText.getText();
        if (((text == null || text.length() != 0) && keyCode == KeyEvent.KEYCODE_0)
                || (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9)
                || keyCode == KeyEvent.KEYCODE_DEL
                || keyCode == KeyEvent.KEYCODE_HOME
                || keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_ENDCALL) {
            return false;
        }
        return true;
    }
}
