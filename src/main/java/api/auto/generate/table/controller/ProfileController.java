package api.auto.generate.table.controller;

import api.auto.generate.table.dto.CardRefill;
import api.auto.generate.table.dto.PaymentRequest;
import api.auto.generate.table.dto.RequestForCard;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.entity.Transaction;
import api.auto.generate.table.service.ProfileService;

import java.util.List;

public class ProfileController {
    private final ProfileService profileService = new ProfileService();
    public boolean cardRequest(RequestForCard request) {
        return profileService.cardRequest(request);
    }

    public Card addCard(Profile profile) {
        return profileService.addCard(profile);
    }

    public List<Card> cardList(Profile profile) {
        return profileService.cardList(profile);
    }

    public boolean deleteCard(String cardNumber, String expDate) {
        return profileService.deleteCard(cardNumber, expDate);
    }

    public String reFill(CardRefill cardRefill) {
            return profileService.reFill(cardRefill);
    }

    public Card reFillForBot(CardRefill cardRefill) {
            return profileService.reFillForBot(cardRefill);
    }

    public String makePayment(PaymentRequest paymentRequest, Profile profile) {
        return profileService.makePayment(paymentRequest, profile);
    }


    public List<Transaction> transactionList(Profile profile) {
        return profileService.transactionList(profile);
    }

    public String verifyCardGenerationStatus(long chatId){
        return profileService.verifyCardGenerationStatus(chatId);
    }
}
