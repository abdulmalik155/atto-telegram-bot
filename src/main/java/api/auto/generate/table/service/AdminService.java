package api.auto.generate.table.service;

import api.auto.generate.table.dto.TerminalDto;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.CardRequest;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.enums.CardRequestStatus;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.enums.UserStatus;
import api.auto.generate.table.repository.CardRepository;
import api.auto.generate.table.repository.CardRequestRepository;
import api.auto.generate.table.repository.TerminalRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdminService {
    private final CardRequestRepository cardRequestRepository = new CardRequestRepository();
    private final CardRepository cardRepository = new CardRepository();
    private final TerminalRepository terminalRepository = new TerminalRepository();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void runAutomationWorker() {
        scheduledExecutorService.scheduleAtFixedRate(
                ()->{
                    try{
                        createCard();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                },
                0,
                1,
                TimeUnit.MINUTES
                );

    }

    public void runtimeAutomationWorker2() {
        scheduledExecutorService.scheduleAtFixedRate(
                ()->{
                    try{
                        deleteCard();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                },
                1,
                2,
                TimeUnit.MINUTES
        );
    }
    public boolean createCard() {
        String uzCardType = "5614";
        String humoType = "9860";
        List<Card> allCards = cardRepository.read();
        List<CardRequest> uzCard = generatedCards(uzCardType, "1", allCards);
        List<CardRequest> humo = generatedCards(humoType, "2", allCards);
        List<CardRequest> allRequests = new ArrayList<>();
        if(!uzCard.isEmpty()) {
            allRequests.addAll(uzCard);
        }
        if(!humo.isEmpty()) {
            allRequests.addAll(humo);
        }
        return cardRequestRepository.save(allRequests, false)
                && cardRepository.save(allCards, false);
    }

    private List<CardRequest> generatedCards(String prefix, String cardType, List<Card> allCards) {
        return cardRequestRepository.
                findRequests(CardRequestStatus.PROCESSING, cardType)
                .stream()
                .map(request -> {
                    Card newCard = generateCard(prefix);
                    request.setCard(newCard);
                    request.setStatus(CardRequestStatus.DONE);
                    newCard.setUser(request.getProfile());
                    allCards.add(newCard);
                    return request;
                }).toList();
    }

    private Card generateCard(String cardType) {
        Card card = new Card();
        String cardNumber = cardType;
        for (int i = 0; i < 12; i++) {
            cardNumber += new Random().nextInt(10);
        }
        card.setCardNumber(cardNumber);
        LocalDate date = LocalDate.now();
        card.setIssueDate(date);
        card.setExpirationDate(date.plusYears(5));
        card.setStatus(Status.CREATED);
        return card;
    }

    public List<Card> getCards() {
        return cardRepository.read();
    }

    public boolean updateCardNumber(Card thisCard, String newCardNumber) {
        Card oldCard = getCard(thisCard);
        thisCard.setCardNumber(newCardNumber);

        return updateCard(oldCard, thisCard);
    }

    public boolean updateCardExpDate(Card thisCard, String newExpDate) {
        Card oldCard = getCard(thisCard);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate newDate = LocalDate.parse(newExpDate, formatter);

        thisCard.setExpirationDate(newDate);
        return updateCard(oldCard, thisCard);
    }

    private Card getCard(Card card) {
        return getCards().stream().filter(cards -> cards.equals(card)).toList().getFirst();
    }

    private boolean updateCard(Card oldCard, Card newCard) {
        List<Card> cards = getCards();
        cards.set(cards.indexOf(oldCard), newCard);
        return cardRepository.save(cards, false);
    }

    public Optional<Card> findCard(String expDate, String cardNumber) {
        return cardRepository.findCard(expDate, cardNumber);
    }

    public boolean changeCardStatus(Status status, Card card) {
        Card oldCard = getCard(card);
        card.setStatus(status);
        return updateCard(oldCard, card);
    }

    public boolean deleteCard() {
        List<Card> cards = getCards()
                .stream()
                .filter(c -> !c.getStatus().equals(Status.DELETED)).toList();
        return cardRepository.save(cards, false);
    }


    public String createTerminal(TerminalDto terminalDto) {
        if (findTermnl(terminalDto)) return "Bu nomli terminal uje bor!";
        Terminal terminal = new Terminal();
        terminal.setCode(terminalDto.code());
        terminal.setAddress(terminalDto.address());
        terminal.setStatus(Status.ACTIVE);
        terminal.setCreatedDate(LocalDate.now());

        Profile profile = new Profile();
        profile.setCreatedDate(LocalDate.now());
        profile.setVisibleUser(Boolean.TRUE);
        profile.setName(terminalDto.companyName());
        profile.setSurname(terminalDto.companyName());
        profile.setPswd("2");
        profile.setPhone("2");
        profile.setRole(Role.COMPANY);
        profile.setStatus(UserStatus.ACTIVE_USER);

        Card card = generateCard("5614");
        card.setStatus(Status.ACTIVE);
        card.setActivationDate(LocalDate.now());
        card.setUser(profile);

        terminal.setCard(card);
        if(terminalRepository.save(List.of(terminal), true)){
            return "Success";
        }
        return "Failed";
    }

    private boolean findTermnl(TerminalDto terminalDto) {
        Optional<Terminal> terminal1 = terminalRepository
                .read()
                .stream()
                .filter(termnls -> termnls.getCard().getUser().getName().equals(terminalDto.companyName()))
                .findFirst();
        return terminal1.isPresent();
    }

    public List<Terminal> terminalList() {
        return terminalRepository.read();
    }

    public Optional<Terminal> findTerminal(TerminalDto terminalDto) {
        return terminalRepository.findTerminal(terminalDto);
    }

    public boolean updateTerminalCode(Terminal terminal, String newCode) {
        Terminal oldTerminal = getTerminal(terminal);
        terminal.setCode(newCode);
        return updateTerminal(oldTerminal, terminal);
    }

    private Terminal getTerminal(Terminal terminal) {
        return terminalRepository
                .read()
                .stream()
                .filter(terminals -> terminals.getCode().equals(terminal.getCode()))
                .toList().getFirst();
    }

    private boolean updateTerminal(Terminal oldTerminal, Terminal newTerminal) {
        List<Terminal> terminals = terminalRepository.read();
        terminals.set(terminals.indexOf(oldTerminal), newTerminal);
        return terminalRepository.save(terminals, false);
    }

    public boolean updateTerminalAddress(Terminal terminal, String newAddress) {
        Terminal oldTerminal = getTerminal(terminal);
        terminal.setAddress(newAddress);
        return updateTerminal(oldTerminal, terminal);
    }

    public boolean changeTerminalStatus(Status status, Terminal terminal) {
        Terminal oldTerminal = getTerminal(terminal);
        terminal.setStatus(status);
        return updateTerminal(oldTerminal, terminal);
    }

    public boolean deleteTerminal(Terminal terminal) {
        List<Terminal> terminals = terminalRepository
                .read()
                .stream()
                .filter(terminalz -> !terminalz.getCode().equals(terminal.getCode())).toList();
        return terminalRepository.save(terminals, false);
    }


}