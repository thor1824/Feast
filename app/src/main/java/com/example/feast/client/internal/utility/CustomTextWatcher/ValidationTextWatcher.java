package com.example.feast.client.internal.utility.CustomTextWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ValidationTextWatcher implements TextWatcher {

    private ArrayList<EditText> editTexts;
    private Button submitButton;
    public ValidationTextWatcher(Button submitButton) {
        this.editTexts = new ArrayList<>();
        this.submitButton = submitButton;
    }

    public ArrayList<EditText> getEditTexts() {
        return editTexts;
    }

    private boolean isValid() {

        for (EditText editText : editTexts) {

            if (editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * We only check on if the text is changed.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        submitButton.setEnabled(isValid());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
