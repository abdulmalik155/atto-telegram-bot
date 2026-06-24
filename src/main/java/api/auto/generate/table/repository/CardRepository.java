package api.auto.generate.table.repository;

import api.auto.generate.table.entity.Card;
import api.auto.generate.table.utill.FileHandling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CardRepository extends FileHandling<Card, Card[]> {
    public CardRepository() {
        super("cards.json", Card[].class);
    }

    public Optional<Card> findCard(String expDate, String cardNumber) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expirationDate = LocalDate.parse(expDate, dtf);
        return read().stream()
                .filter(card -> card.getCardNumber().equals(cardNumber)
                        && card.getExpirationDate().equals(expirationDate))
                .findFirst();
    }


}
