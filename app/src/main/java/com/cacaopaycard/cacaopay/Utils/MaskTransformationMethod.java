package com.cacaopaycard.cacaopay.Utils;


import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Clase que modifica la máscara del input de tarjetas (MaskEditText)
 */
public class MaskTransformationMethod extends PasswordTransformationMethod {

    private static int MASK_INDEX_END = 15; // a partir de este índice se quitará la máscara.
    private static char MASK_CHAR = '*'; // caracter que se colocará como máscara.

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        System.out.println("Transformation....");
        return new PasswordCharSequence(source);
    }


    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }
        public char charAt(int index) {


            if(mSource.charAt(index) == ' '){
                return ' ';

            } else if(index >= MASK_INDEX_END){

                return mSource.charAt(index);

            } else

                return MASK_CHAR;
        }
        public int length() {
            return mSource.length(); // Return default
        }

        public CharSequence subSequence(int start, int end) {

            return mSource.subSequence(start, end); // Return default
        }
    }
}
