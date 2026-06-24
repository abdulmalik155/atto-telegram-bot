package api.auto.generate.table.controller;

import api.auto.generate.table.dto.TerminalDto;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.service.AdminService;

import java.util.List;
import java.util.Optional;

public class AdminController {
    private final AdminService adminService = new AdminService();

    public boolean createCard() {
        return adminService.createCard();
    }

    public List<Card> getCards() {
        return adminService.getCards();
    }

    public boolean updateCardNumber(Card thisCard, String newCardNumber) {
        return adminService.updateCardNumber(thisCard, newCardNumber);
    }

    public boolean updateCardExpDate(Card thisCard, String newExpDate) {
        return adminService.updateCardExpDate(thisCard, newExpDate);
    }

    public Optional<Card> findCard( String expDate, String cardNumber) {
        return adminService.findCard( expDate, cardNumber);
    }

    public boolean changeCardStatus(Status status, Card card) {
        return adminService.changeCardStatus(status, card);
    }

    public boolean deleteCard() {
        return adminService.deleteCard();
    }

    public String createTerminal(TerminalDto terminalDto) {
        return adminService.createTerminal(terminalDto);
    }

    public List<Terminal> terminalList() {
            return adminService.terminalList();
    }

    public Optional<Terminal> findTerminal(TerminalDto terminalDto) {
        return adminService.findTerminal(terminalDto);
    }

    public boolean updateTerminalCode(Terminal terminal, String newCode) {
        return adminService.updateTerminalCode(terminal, newCode);
    }

    public boolean updateTerminalAddress(Terminal terminal, String newAddress) {
        return adminService.updateTerminalAddress(terminal, newAddress);
    }

    public boolean changeTerminalStatus(Status status, Terminal terminal) {
        return adminService.changeTerminalStatus(status, terminal);
    }

    public boolean deleteTerminal(Terminal terminal) {

        return adminService.deleteTerminal(terminal);
    }
}
