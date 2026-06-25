package api.auto.generate.table.service;

import api.auto.generate.table.dto.CardRefill;
import api.auto.generate.table.dto.PaymentRequest;
import api.auto.generate.table.dto.RequestForCard;
import api.auto.generate.table.entity.*;
import api.auto.generate.table.enums.CardRequestStatus;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.enums.TransactionType;
import api.auto.generate.table.enums.UserStatus;
import api.auto.generate.table.repository.CardRepository;
import api.auto.generate.table.repository.CardRequestRepository;
import api.auto.generate.table.repository.TerminalRepository;
import api.auto.generate.table.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProfileService {
    private final CardRequestRepository cardRequestRepository = new CardRequestRepository();
    private final CardRepository cardRepository = new CardRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final TerminalRepository terminalRepository = new TerminalRepository();

    public boolean cardRequest(RequestForCard request) {
        return cardRequestRepository.request(request);
    }


    public Card addCard(Profile profile) {
        Optional<CardRequest> cardRequests = cardRequestRepository.read()
                .stream().filter(cardRequest -> cardRequest.getProfile().getPhone().equals(profile.getPhone())
                        && cardRequest.getStatus().equals(CardRequestStatus.DONE)).findFirst();

        if (cardRequests.isPresent()) {
            CardRequest cardRequest = cardRequests.get();
            Card card = cardRequest.getCard();
            card.setActivationDate(LocalDate.now());

            List<Card> currentCards = cardRepository.read();
            List<Card> updatedCardList = new java.util.ArrayList<>();

            for (Card card1 : currentCards) {
                if (card1.getUser() != null &&
                        card1.getUser().getPhone() != null &&
                        card1.getUser().getPhone().equals(profile.getPhone()) &&
                        card1.getCardNumber().equals(card.getCardNumber())) {

                    card1.setActivationDate(card.getActivationDate());
                    card1.setStatus(Status.ACTIVE);
                    updatedCardList.add(card1);
                } else {
                    updatedCardList.add(card1);
                }
            }

            if (cardRepository.save(updatedCardList, false)) {
                List<CardRequest> requests = cardRequestRepository
                        .read()
                        .stream()
                        .filter(request -> !request.getProfile().getPhone().equals(profile.getPhone()))
                        .toList();
                cardRequestRepository.save(requests, false);
                return card;
            }
        }
        return null;
    }

    /*    public Card addCard(Profile profile) {
        Optional<CardRequest> cardRequests = cardRequestRepository.read()
                .stream().filter(cardRequest -> cardRequest.getProfile().getPhone().equals(profile.getPhone())
                        && cardRequest.getStatus().equals(CardRequestStatus.DONE)).findFirst();
        if (cardRequests.isPresent()) {
            CardRequest cardRequest = cardRequests.get();
            Card card = cardRequest.getCard();
            card.setActivationDate(LocalDate.now());
            List<Card> cardList = cardRepository.read()
                    .stream()
                    .map(card1 -> {
                        if (card1.getUser().getPhone().equals(profile.getPhone()) && card1.getCardNumber().equals(card.getCardNumber())) {
                            card1.setActivationDate(card.getActivationDate());
                            card1.setStatus(Status.ACTIVE);
                            return card1;
                        }
                        return card1;
                    }).toList();
            if (cardRepository.save(cardList, false)) {
                List<CardRequest> requests = cardRequestRepository
                        .read()
                        .stream()
                        .filter(request -> !request.getProfile().getPhone().equals(profile.getPhone()))
                        .toList();
                cardRequestRepository.save(requests, false);
                return card;
            }
        }
        return null;
    }*/

    public List<Card> cardList(Profile profile) {
        return cardRepository
                .read()
                .stream()
                .filter(cards -> cards.getUser().getPhone().equals(profile.getPhone())
                        && cards.getStatus().equals(Status.ACTIVE)).toList();
    }

    public boolean deleteCard(String cardNumber, String expDate) {
        LocalDate localDate = LocalDate.parse(expDate);
        List<Card> cardList = cardRepository
                .read()
                .stream()
                .map(card -> {
                    if (card.getCardNumber().equals(cardNumber)
                            && card.getExpirationDate().equals(localDate)) {
                        card.setStatus(Status.DELETED);
                        return card;
                    }
                    return card;
                }).toList();

        return cardRepository.save(cardList, false);
    }

    public String reFill(CardRefill cardRefill) {
        if (cardRefill.amount() == 0) return "Ooo bu nol somli transaction qibomidi";
        Optional<Card> foundCard = cardRepository.read()
                .stream()
                .filter(card -> card.getCardNumber().equals(cardRefill.cardNumber())
                        && card.getStatus().equals(Status.ACTIVE)
                        && !card.getUser().getVisibleUser().equals(Boolean.FALSE)
                        && !card.getUser().getStatus().equals(UserStatus.DELETED_USER)
                        && card.getUser().getPhone().equals(cardRefill.profile().getPhone())).findFirst();
        if (foundCard.isPresent()) {
            Card card = foundCard.get();
            card.setBalance(card.getBalance() + cardRefill.amount());
            List<Card> cardList = cardRepository
                    .read()
                    .stream()
                    .map(card1 -> {
                        if (card1.getCardNumber().equals(card.getCardNumber())) {
                            card1.setBalance(card.getBalance());
                            return card1;
                        }
                        return card1;
                    }).toList();

            Transaction transaction = new Transaction();
            transaction.setAmount(cardRefill.amount());
            transaction.setType(TransactionType.REFILL);
            transaction.setCardNumber(card.getCardNumber());
            transaction.setCreatedDate(LocalDate.now());
            if (cardRepository.save(cardList, false)
                    && transactionRepository.save(List.of(transaction), true)) {
                return "Success";
            }
        }
        return "Error!";
    }

    public String makePayment(PaymentRequest paymentRequest, Profile profile) {
        Optional<Terminal> terminalz = terminalRepository
                .read()
                .stream()
                .filter(terminal -> terminal.getCode().equals(paymentRequest.terminalNumber())).findFirst();
        List<Card> currentCardList = cardRepository.read();
        Optional<Card> userCard = currentCardList.stream()
                .filter(cards -> cards.getCardNumber().equals(paymentRequest.cardNumber()))
                .findFirst();

        if (userCard.isPresent()) {
            Card card = userCard.get();
            if (card.getBalance() >= 1700) {
                String result = pay(terminalz, paymentRequest);
                if (result.equals("Success")) {
                    List<Card> updatedCards = currentCardList.stream()
                            .map(cardz -> {
                                if (cardz.getCardNumber().equals(paymentRequest.cardNumber())) {
                                    cardz.setBalance(cardz.getBalance() - 1700);
                                    return cardz;
                                }
                                return cardz;
                            }).toList();

                    if (cardRepository.save(updatedCards, false)) {
                        return result;
                    }
                }
            }
            return "Pul kam!";
        }
        return "Error!";
    }

    private String pay(Optional<Terminal> terminalz, PaymentRequest paymentRequest) {
        if (terminalz.isPresent()) {
            Terminal terminal = terminalz.get();
            Card card = terminal.getCard();
            card.setBalance(card.getBalance() + 1700);
            List<Terminal> terminals = terminalRepository
                    .read()
                    .stream()
                    .map(term -> {
                        if (term.getCode().equals(terminal.getCode())) {
                            term.setCard(card);
                            return term;
                        }
                        return term;
                    }).toList();
            if (terminalRepository.save(terminals, false)) {
                return makeTransaction(terminal, paymentRequest);
            }
        }
        return "Error!";
    }

    private String makeTransaction(Terminal terminal, PaymentRequest paymentRequest) {
        Transaction transaction = new Transaction();
        transaction.setAmount(1700);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setCardNumber(paymentRequest.cardNumber());

        Optional<Card> card = cardRepository
                .read()
                .stream()
                .filter(card1 -> card1.getCardNumber().equals(paymentRequest.cardNumber())).findFirst();
        card.ifPresent(transaction::setCard);
        transaction.setCreatedDate(LocalDate.now());
        transaction.setTerminalCode(terminal.getCode());
        if (transactionRepository.save(List.of(transaction), true)) {
            return "Success";
        }
        return "Error!";
    }


    public Card reFillForBot(CardRefill cardRefill) {
        if (cardRefill.amount() == 0) return null;
        Optional<Card> foundCard = cardRepository.read()
                .stream()
                .filter(card -> card.getCardNumber().equals(cardRefill.cardNumber())
                        && card.getStatus().equals(Status.ACTIVE)
                        && !card.getUser().getVisibleUser().equals(Boolean.FALSE)
                        && !card.getUser().getStatus().equals(UserStatus.DELETED_USER)
                        && card.getUser().getPhone().equals(cardRefill.profile().getPhone())).findFirst();
        if (foundCard.isPresent()) {
            Card card = foundCard.get();
            card.setBalance(card.getBalance() + cardRefill.amount());
            List<Card> cardList = cardRepository
                    .read()
                    .stream()
                    .map(card1 -> {
                        if (card1.getCardNumber().equals(card.getCardNumber())) {
                            card1.setBalance(card.getBalance());
                            return card1;
                        }
                        return card1;
                    }).toList();

            Transaction transaction = new Transaction();
            transaction.setAmount(cardRefill.amount());
            transaction.setType(TransactionType.REFILL);
            transaction.setCard(card);
            transaction.setCardNumber(card.getCardNumber());
            transaction.setCreatedDate(LocalDate.now());
            if (cardRepository.save(cardList, false)
                    && transactionRepository.save(List.of(transaction), true)) {
                return card;
            }
        }
        return null;

    }

    public List<Transaction> transactionList(Profile profile) {
        List<Card> cards = cardRepository
                .read()
                .stream()
                .filter(card -> card.getUser().getPhone().equals(profile.getPhone()))
                .toList();
        return transactionRepository
                .read()
                .stream()
                .filter(transaction -> cards.contains(transaction.getCard())).toList();
    }

    public synchronized String verifyCardGenerationStatus(long chatId) {
        Optional<Card> pendingCard = cardRepository.read().stream()
                .filter(card -> card.getUser() != null && card.getUser().getChatId() == chatId)
                .findFirst();

        if (pendingCard.isPresent()) {
            Card card = pendingCard.get();
            return card.getStatus().toString();
        }
        return "NOT_FOUND";
    }
}
