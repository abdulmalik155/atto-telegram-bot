package api.auto.generate.table.repository;

import api.auto.generate.table.dto.RequestForCard;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.CardRequest;
import api.auto.generate.table.enums.CardRequestStatus;
import api.auto.generate.table.utill.FileHandling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRequestRepository extends FileHandling<CardRequest, CardRequest[]> {

    public CardRequestRepository() {
        super("cardRequest.json", CardRequest[].class);
    }

    public boolean request(RequestForCard request) {
        int cardId = Integer.parseInt(request.cardType());
        if (!(cardId > 0 && cardId < 3)) {
            return false;
        }
        CardRequest cardRequest = new CardRequest();
        cardRequest.setCardType(request.cardType());
        cardRequest.setProfile(request.profile());
        cardRequest.setStatus(CardRequestStatus.PROCESSING);
        return save(List.of(cardRequest), true);
    }

    public List<CardRequest> findRequests(CardRequestStatus status, String cardType) {
        return read().stream()
                .filter(request ->
                        request.getStatus().equals(status)&&
                                request.getCardType().equals(cardType)
                )
                .toList();
    }

    public List<CardRequest> getAllRequests(CardRequestStatus status) {
        List<CardRequest> cardRequests = new ArrayList<>();
        cardRequests.addAll(findRequests(status, "1"));
        cardRequests.addAll(findRequests(status, "2"));
        return cardRequests;
    }

    public Optional<Card> findCard( String expDate, String cardNumber) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expirationDate = LocalDate.parse(expDate,dtf);
        Optional<Card> found = Optional.empty();
        Optional<CardRequest> foundRequest= read().stream()
                .filter(request ->
                        request.getCard().getCardNumber().equals(cardNumber)&&
                                request.getCard().getExpirationDate().equals(expirationDate)

                ).findFirst();
        if (foundRequest.isPresent()) {
            found = foundRequest.stream().map(CardRequest::getCard).findFirst();
        }
        return found;
    }
}
