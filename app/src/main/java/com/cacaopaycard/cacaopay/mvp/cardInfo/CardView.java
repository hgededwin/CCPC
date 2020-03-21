package com.cacaopaycard.cacaopay.mvp.cardInfo;

import com.cacaopaycard.cacaopay.Modelos.Tarjeta;

public interface CardView {
    void showCardInfo(Tarjeta card);
    void showCardLocked();
    void showCardUnLocked();
    void setSwitchPreviousState();
    void showError(String message);
    void showProgress();
    void hideProgress();
}
