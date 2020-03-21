package com.cacaopaycard.cacaopay.Utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class MaskEditText extends androidx.appcompat.widget.AppCompatEditText {

    public CompleteEntry completeEntry;
    public Boolean isVisible = false; //
    private Boolean inputCorrect = false;

    private static int SIZE_CARD = 16; // tamaño de caracteres del input de tarjetas.
    private static int SIZE_INPUT_CLABE = 18; // tamaño de caracteres del input CLABE.
    private static int NUMBER_SPLITS = 4; // número de veces que se repetirá la separación por espacios.


    /**
     * Interface que escucha cuando se completa la entrada del input del número de tarjeta
     * o número de clabe.
     */
    public interface CompleteEntry{
        void isCardComplete(String textFinal, boolean inputIsCorrect);
    }

    public MaskEditText(Context context) {

        super(context);
    }

    public MaskEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // se hace módulo de 5 para obtener el momento en el que el usuario tenga
                // una cantidad de dígitos menor a 5

                //
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (' ' == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.

                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(' ')).length <= NUMBER_SPLITS) {
                        s.insert(s.length() - 1, String.valueOf(' '));
                    }
                }
                /*if(s.toString().matches("(\\d{4}[ ]?){4}")){
                    System.out.println("editable" + s);
                    if(completeEntry != null)
                        completeEntry.isCardComplete(s.toString(), true);
                } else if(getRowText().length() == 16){
                    if(completeEntry != null)
                        completeEntry.isCardComplete(s.toString(), false);
                }
                if(s.toString().matches("[0-9]{16}")){
                    System.out.println("tarjeta con 16 digitos");
                    if(completeEntry != null)
                        completeEntry.isCardComplete(s.toString(), true);
                }*/


                if (getRowText().length() == SIZE_CARD || getRowText().length() == SIZE_INPUT_CLABE){
                    if(completeEntry != null) {
                        completeEntry.isCardComplete(s.toString(), true);
                    }
                } else {
                    if(completeEntry != null) {
                        completeEntry.isCardComplete(s.toString(), false);
                    }
                }

            }
        });
    }


    public void inputFinalListener(CompleteEntry completeEntry){
        this.completeEntry = completeEntry;
    }


    public String getRowText(){
        Editable editable = this.getText();
        return editable.toString().replaceAll(" ","");
    }




}