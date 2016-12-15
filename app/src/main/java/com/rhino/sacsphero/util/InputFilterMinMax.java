package com.rhino.sacsphero.util;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private int min;
    private int max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());

            if(newVal.length() == 0) {
                newVal = "0";
            }

            int input = Integer.parseInt(newVal);

            if(isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return "";
    }

    private boolean isInRange(int min, int max, int input) {
        return input >= min && input <= max;
    }
}
