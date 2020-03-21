package com.cacaopaycard.cacaopay.mvp.cardInfo;

import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;

import java.util.List;

public interface CardInfoView {
    void showProgress();
    void dismissProgress();
    void showCardLocked();
    void showCardUnlocked();
    void showCards(List<Tarjeta> cards);
    void showMovements(List<Movimiento> movimientos, List<Fecha> fechas);
    void showEmptyMovements();
    void showError(String error);
    void showEmptyCards();
}
