package api.auto.generate.table.ui;

import api.auto.generate.table.controller.AdminController;
import api.auto.generate.table.dto.TerminalDto;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.utill.ScannerUtil;

import java.util.Optional;
import java.util.Random;

public class AdminUi {
    private final AdminController adminController = new AdminController();

    public AdminUi() {
    }

    public void run() {
        System.out.println("Login successful! Adminjon");
        while (true) {
            switch (menu()) {
                case 1 -> cardSection();
                case 2 -> terminalSection();
                case 3 -> profileSection();
                case 4 -> transactionSection();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private int menu() {
        return ScannerUtil.getOption("""
                Main menu
                1. Card menu
                2. Terminal menu
                3. Profile menu
                4. Transaction menu
                0. Exit
                Enter your choice:""");
    }

    /// ===================================== CARD SECTION =====================================
    private void cardSection() {
        while (true) {
            switch (cardMenu()) {
                case 1 -> createCard();
                case 2 -> cardList();
                case 3 -> updateCard();
                case 4 -> changeCardStatus();
                case 5 -> deleteCard();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private int cardMenu() {
        return ScannerUtil.getOption("""
                Card menu
                1. Create Card(number,exp_date)
                2. Card List
                3. Update Card (number,exp_date)
                4. Change Card status
                5. Delete Card
                0. Exit
                Enter your choice:""");
    }

    private void createCard() {
        boolean result = adminController.createCard();
        showMessage(result, "Card created!");
    }

    private void cardList() {
        adminController.getCards().forEach(System.out::println);
    }

    private void updateCard() {
        cardList();
        String oldCardNumber = ScannerUtil.getString("Enter old Card Number:");
        String expDate = ScannerUtil.getString("Enter Exp Date(yyyy-MM-dd):");
        Optional<Card> thisCard = adminController.findCard(expDate, oldCardNumber);
        boolean result = false;
        if (thisCard.isPresent()) {
            while (true) {
                switch (updateMenu()) {
                    case 1 -> {
                        String newCardNumber = ScannerUtil.getString("Enter new Card Number:");
                        result = adminController.updateCardNumber(thisCard.get(), newCardNumber);
                        showMessage(result, "Card updated!");
                    }
                    case 2 -> {
                        String newExpDate = ScannerUtil.getString("Enter new Exp Date(yyyy-MM-dd):");
                        result = adminController.updateCardExpDate(thisCard.get(), newExpDate);
                        showMessage(result, "Card updated!");
                    }
                    case 0 -> {
                        return;
                    }
                }
            }
        }
    }

    private int updateMenu() {
        return ScannerUtil.getOption("""
                        Card update
                1. Card number
                2. Card expiration date
                0. Exit
                Enter your choice:""");
    }

    private static void showMessage(boolean result, String x) {
        if (result) {
            System.out.println(x);
        } else {
            System.out.println("Error!");
        }
    }

    private void changeCardStatus() {
        cardList();
        String cardNumber = ScannerUtil.getString("Enter Card Number:");
        String expDate = ScannerUtil.getString("Enter Exp Date(yyyy-MM-dd):");
        Optional<Card> card = adminController.findCard(expDate, cardNumber);
        boolean result = false;
        if (card.isPresent()) {
            while (true) {
                switch (cardStatusMenu()) {
                    case 1 -> {
                        result = adminController.changeCardStatus(Status.ACTIVE, card.get());
                        showMessage(result, "Card Status: ACTIVE!");
                    }
                    case 2 -> {
                        result = adminController.changeCardStatus(Status.INACTIVE, card.get());
                        showMessage(result, "Card Status: INACTIVE!");
                    }
                    case 3 -> {
                        result = adminController.changeCardStatus(Status.BLOCKED, card.get());
                        showMessage(result, "Card Status: BLOCKED!");
                    }
                    case 0 -> {
                        return;
                    }
                }
            }
        }
        showMessage(result, "Card details could not be found!");

    }

    private int cardStatusMenu() {
        return ScannerUtil.getOption("""
                Card status menu
                1. ACTIVE
                2. INACTIVE
                3. BLOCKED
                0. Exit
                Enter your choice:""");
    }

    private void deleteCard() {
        boolean result = false;
        result = adminController.deleteCard(/*card.get()*/);
        showMessage(result, "Card deleted!");
    }

    /// ===================================== CARD SECTION END =====================================
//  (Terminal)
//    6. Create Terminal (code unique,address)
//    7. Terminal List
//    8. Update Terminal (code,address)
//    9. Change Terminal Status
//    10. Delete Terminal
//
    private void terminalSection() {
        while (true) {
            switch (terminalMenu()) {
                case 1 -> createTerminal();
                case 2 -> terminalList();
                case 3 -> updateTerminal();
                case 4 -> changeTerminalStatus();
                case 5 -> deleteTerminal();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void createTerminal() {
        String address = ScannerUtil.getString("Enter terminal address:");
        String companyName = ScannerUtil.getString("Enter terminal company name:");
        TerminalDto terminalDto = new TerminalDto(String.valueOf(new Random().nextInt(100, 999)),
                address,
                companyName);
        String result = adminController.createTerminal(terminalDto);
        System.out.println(result);
    }

    private void terminalList() {
        adminController.terminalList().forEach(System.out::println);
    }

    private void updateTerminal() {
        terminalList();
        String address = ScannerUtil.getString("Enter terminal address:");
        String terminalCode = ScannerUtil.getString("Enter terminal code:");
        String companyName = ScannerUtil.getString("Enter terminal company name:");
        Optional<Terminal> terminal = adminController.findTerminal(new TerminalDto(terminalCode, address,  companyName));
        boolean result = false;
        if (terminal.isPresent()) {
            while (true) {
                switch (terminalUpdateMenu()) {
                    case 1 -> {
                        String newCode = ScannerUtil.getString("Enter new terminal code (UUID):");
                        result = adminController.updateTerminalCode(terminal.get(), newCode);
                        showMessage(result, "Terminal updated!");
                    }
                    case 2 -> {
                        String newAddress = ScannerUtil.getString("Enter new terminal address:");
                        result = adminController.updateTerminalAddress(terminal.get(), newAddress);
                        showMessage(result, "Terminal updated!");
                    }
                    case 0 -> {
                        return;
                    }
                }
            }
        }
    }

    private int terminalUpdateMenu() {
        return ScannerUtil.getOption("""
                        Terminal update menu
                1. Code
                2. Address
                0. Exit
                Enter your choice:""");
    }

    private void changeTerminalStatus() {
        terminalList();
        String address = ScannerUtil.getString("Enter terminal address:");
        String code = ScannerUtil.getString("Enter terminal code :");
        String companyName = ScannerUtil.getString("Enter terminal company name:");
        Optional<Terminal> terminal = adminController.findTerminal(new TerminalDto(code, address,   companyName));
        boolean result = false;
        if (terminal.isPresent()) {
            while (true) {
                switch (terminalStatusMenu()) {
                    case 1 -> {
                        result = adminController.changeTerminalStatus(Status.ACTIVE, terminal.get());
                        showMessage(result, "Terminal status changed!");
                    }
                    case 2 -> {
                        result = adminController.changeTerminalStatus(Status.INACTIVE, terminal.get());
                        showMessage(result, "Terminal status changed!");
                    }
                    case 3 -> {
                        result = adminController.changeTerminalStatus(Status.BLOCKED, terminal.get());
                        showMessage(result, "Terminal status changed!");
                    }
                    case 0 -> {
                        return;
                    }
                }
            }
        }
    }

    private int terminalStatusMenu() {
        return ScannerUtil.getOption("""
                Terminal status menu
                1. ACTIVE
                2. INACTIVE
                3. BLOCKED
                0. Exit
                Enter your choice:""");
    }

    private void deleteTerminal() {
        terminalList();
        String code =ScannerUtil.getString("Enter terminal code (UUID):");
        String address = ScannerUtil.getString("Enter terminal address:");
        String companyName = ScannerUtil.getString("Enter terminal company name:");
        Optional<Terminal> terminal = adminController.findTerminal(new TerminalDto(code, address, companyName));
        if (terminal.isPresent()) {
            System.out.println(terminal.get());
            boolean result = ScannerUtil.getString("""
                Are you sure you want to delete this?
                1. Yes
                2. No
                Enter your choice:""").equals("1");
            if (result) {
                result =adminController.deleteTerminal(terminal.get());
                showMessage(result, "Terminal deleted!");
            }
        }
    }

    private int terminalMenu() {
        return ScannerUtil.getOption("""
                1. Create terminal
                2. Terminal list
                3. Update terminal
                4. Change terminal status
                5. Delete terminal
                0. Exit
                """);
    }
//    (Profile)
//    11. Profile List
//    12. Change Profile Status (by phone)

    private void profileSection() {

    }

    //    (Transaction)
//    13. Transaction List
//        CardNumber, TerminalNumber, Amount,TransactionDate,Type (oxirgi birinchi ko'rinadi)
//    14. Company Card Balance
//        (card bo'ladi shu cardga to'lovlar tushadi. bu sql da insert qilinga bo'ladi)
    private void transactionSection() {

    }
}
