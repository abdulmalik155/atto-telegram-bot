package api.auto.generate.table.ui;

import api.auto.generate.table.controller.ProfileController;
import api.auto.generate.table.dto.CardRefill;
import api.auto.generate.table.dto.PaymentRequest;
import api.auto.generate.table.dto.RequestForCard;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.entity.Transaction;
import api.auto.generate.table.repository.TerminalRepository;
import api.auto.generate.table.utill.ScannerUtil;

import java.util.Collections;
import java.util.List;

public class ProfileUi {
    private final Profile profile;
    private final ProfileController profileController = new ProfileController();

    public ProfileUi(Profile profile) {
        this.profile = profile;
    }

    public void run() {
        System.out.println("Login successful! " + profile.getName());
        while (true) {
            switch (menu()) {
                case 1 -> cardSection();
                case 2 -> transactionSection();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void cardSection() {
        while (true) {
            switch (cardMenu()) {
                case 1 -> cardRequest();
                case 2 -> addCard();
                case 3 -> cardList();
                case 4 -> deleteCard();
                case 5 -> reFill();
                case 0 -> {
                    return;
                }

            }
        }
    }

    private void cardRequest() {
        int choice = ScannerUtil.getOption("""
                1. UzCard;
                2. Humo;
                Enter your choice:""");
        RequestForCard request = new RequestForCard(profile, String.valueOf(choice));
        boolean result = profileController.cardRequest(request);
        showMessage(result, "Card Request Success!");
    }

    private static void showMessage(boolean result,  String message) {
        if (result) {
            System.out.println(message);
        } else {
            System.out.println("Error!");
        }
    }

    private void addCard() {
        Card card = profileController.addCard(profile);
        if (card != null) {
            System.out.println("Card Add Success!");
            System.out.println("card = " + card);
            return;
        }
        System.out.println("Card Add Failed!");
    }

    private void cardList() {
        profileController.cardList(profile).forEach(System.out::println);
    }

    private void deleteCard() {
        cardList();
        String cardNumber = ScannerUtil.getString("Enter Card Number: ");
        String expDate = ScannerUtil.getString("Enter Exp Date(yyyy-MM-dd): ");
        boolean result = profileController.deleteCard(cardNumber, expDate);
        showMessage(result, "Card Delete Success!");
    }

    private void reFill() {
        cardList();
        String cardNumber = ScannerUtil.getString("Enter Card Number: ");
        Double amount = ScannerUtil.getDouble("Enter amount: ");
        CardRefill cardRefill = new CardRefill(profile, cardNumber, amount);
        String result = profileController.reFill(cardRefill);
        System.out.println(result);
    }

    private int cardMenu() {
        return ScannerUtil.getOption("""
                1. CardRequest
                2. AddCard
                3. CardList
                4. DeleteCard
                5. ReFill
                0. Exit
                Enter Your Choice:""");
    }

    private void transactionSection() {
        while (true) {
            switch (transactionMenu()) {
                case 1 -> transactionList();
                case 2 -> makePayment();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void transactionList() {
        List<Transaction> transactions = profileController.transactionList(profile);
        if(transactions.isEmpty()) {
            System.out.println("No transactions found!");
            return;
        }
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }

    }

    private void makePayment() {
        List<Terminal> terminalz = new java.util.ArrayList<>(new TerminalRepository().read().stream().toList());
   Collections.shuffle(terminalz);
        Terminal terminal = terminalz.getFirst();
        System.out.println(terminal);
        cardList();
        String cardNumber = ScannerUtil.getString("Enter Card Number: ");
        String terminalNumber = ScannerUtil.getString("Enter Terminal code: ");
        PaymentRequest paymentRequest = new PaymentRequest(cardNumber, terminalNumber);
        String result = profileController.makePayment(paymentRequest, profile);
        System.out.println(result);
    }

    private int transactionMenu() {
        return ScannerUtil.getOption("""
                        Transaction Menu
                1. Transaction list
                2. Make payment
                0. Exit
                Enter your choice:""");
    }

    private int menu() {
        return ScannerUtil.getOption("""
                        Menu
                1. Card section
                2. Transaction section
                0. Exit
                Enter your choice:""");
    }
}
