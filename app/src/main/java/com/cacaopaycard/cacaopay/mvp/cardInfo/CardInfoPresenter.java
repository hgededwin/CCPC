package com.cacaopaycard.cacaopay.mvp.cardInfo;

import android.util.Log;

import com.cacaopaycard.cacaopay.Modelos.Fecha;
import com.cacaopaycard.cacaopay.Modelos.Movimiento;
import com.cacaopaycard.cacaopay.Modelos.Tarjeta;
import com.cacaopaycard.cacaopay.Modelos.Usuario;

import java.util.List;

public class CardInfoPresenter implements CardInfoInteractor.OnFinishCardInfoRequest {
    private final String TAG = "CardInfoPresenter";

    private CardInfoView view;
    private CardInfoInteractor interactor;

    public CardInfoPresenter(CardInfoView view, CardInfoInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void getCards(Usuario usuario){
        view.showProgress();
        interactor.getCards(this, usuario);
    }

    public void getCardMovements(Tarjeta card){
        view.showProgress();
        interactor.getCardMovements(this, card);
    }

    @Override
    public void onSuccessCardList(List<Tarjeta> cards) {
        view.dismissProgress();
        view.showCards(cards);
    }

    @Override
    public void onSuccessCardMovements(List<Movimiento> movimientos, List<Fecha> fechas) {
        view.dismissProgress();
        view.showMovements(movimientos, fechas);
    }

    @Override
    public void onError(String error) {
        view.dismissProgress();
        view.showError(error);
    }

    @Override
    public void onEmptyMovements() {
        view.dismissProgress();
        view.showEmptyMovements();
    }

    @Override
    public void onEmptyCards() {
        view.dismissProgress();
        view.showEmptyCards();
    }
}
