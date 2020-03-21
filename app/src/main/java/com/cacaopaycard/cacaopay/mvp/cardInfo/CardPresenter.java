package com.cacaopaycard.cacaopay.mvp.cardInfo;

import com.cacaopaycard.cacaopay.Modelos.Tarjeta;

public class CardPresenter implements CardInteractor.CardBalanceRequest{

    private CardView view;
    private CardInteractor interactor;

    public CardPresenter(CardView view, CardInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void getCardBalance(Tarjeta card){
        interactor.getCardBalance(card, this);
        view.showProgress();
    }

    public void lockUnlockCard(boolean statusDesired, String cardNumber){
        interactor.lockCard(statusDesired, cardNumber, this);
        view.showProgress();
    }


    @Override
    public void onError(String error) {
        view.hideProgress();
        view.setSwitchPreviousState();
        view.showError(error);
    }

    @Override
    public void onSuccess(Tarjeta card) {
        view.hideProgress();
        view.showCardInfo(card);
    }

    @Override
    public void onLockUnlockSuccess(boolean newStatus) {
        view.hideProgress();
        if (newStatus) view.showCardLocked();
        else view.showCardUnLocked();
    }
}
