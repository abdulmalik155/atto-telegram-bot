/*
package api.auto.generate.table.repository;

import api.auto.generate.table.dto.TerminalDto;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.utill.FileHandling;

import java.util.Optional;

public class TerminalRepository extends FileHandling<Terminal, Terminal[]> {

    public TerminalRepository() {
        super("terminals.json", Terminal[].class);
    }

    public Optional<Terminal>  findTerminal(TerminalDto terminalDto) {
        return read()
                .stream()
                .filter(terminal -> terminal.getAddress().equals(terminalDto.address())
                        && terminal.getCode().equals(terminalDto.code())).findFirst();
    }
}
*/
package api.auto.generate.table.repository;

import api.auto.generate.table.dto.TerminalDto;
import api.auto.generate.table.entity.Card;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.entity.Terminal;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.enums.UserStatus;
import api.auto.generate.table.utill.FileHandling;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public class TerminalRepository extends FileHandling<Terminal, Terminal[]> {
    public TerminalRepository() {
        super("terminals.json", Terminal[].class);
        initializeDefaultTerminals();
    }

    private void initializeDefaultTerminals() {
        if (read().isEmpty()) {
            // 1. Setup Company 1 Profile & Card Details
            Profile profile1 = new Profile();
            profile1.setRole(Role.COMPANY);
            profile1.setName("avtobusSaroy1");
            profile1.setSurname("kompaniyasi");
            profile1.setPhone("3333");
            profile1.setPswd("3333");
            profile1.setCreatedDate(LocalDate.now());
            profile1.setStatus(UserStatus.ACTIVE_USER);
            profile1.setVisibleUser(true);

            Card card1 = new Card();
            card1.setCardNumber("9860000000000386"); // Direct clean static terminal card generation
            card1.setBalance(0.0);
            card1.setStatus(Status.ACTIVE);
            card1.setActivationDate(LocalDate.now());
            card1.setExpirationDate(LocalDate.now().plusYears(5));
            card1.setUser(profile1);

            Terminal t1 = new Terminal();
            t1.setAddress("159");
            t1.setCode("386");
            t1.setStatus(Status.ACTIVE);
            t1.setCreatedDate(LocalDate.now());
            t1.setCard(card1);

            // 2. Setup Company 2 Profile & Card Details
            Profile profile2 = new Profile();
            profile2.setRole(Role.COMPANY);
            profile2.setName("avtobusSaroy2");
            profile2.setSurname("kompaniyasi");
            profile2.setPhone("2222");
            profile2.setPswd("2222");
            profile2.setCreatedDate(LocalDate.now());
            profile2.setStatus(UserStatus.ACTIVE_USER);
            profile2.setVisibleUser(true);

            Card card2 = new Card();
            card2.setCardNumber("5614000000000542"); // Direct clean static terminal card generation
            card2.setBalance(0.0);
            card2.setStatus(Status.ACTIVE);
            card2.setActivationDate(LocalDate.now());
            card2.setExpirationDate(LocalDate.now().plusYears(5));
            card2.setUser(profile2);

            Terminal t2 = new Terminal();
            t2.setAddress("126");
            t2.setCode("542");
            t2.setStatus(Status.ACTIVE);
            t2.setCreatedDate(LocalDate.now());
            t2.setCard(card2);

            // Save safely directly via your FileHandling layer
            // Optional Upgrade: Add this line to your repository initializer to register it globally
            new CardRepository().save(List.of(card1, card2), true);
            save(List.of(t1, t2), false);
            System.out.println("Default cloud mock terminals with secure merchant wallets written to terminals.json successfully.");
        }
    }

    public Optional<Terminal> findTerminal(TerminalDto terminalDto) {
        return read()
                .stream()
                .filter(terminal -> terminal.getAddress().equals(terminalDto.address())
                        && terminal.getCode().equals(terminalDto.code())).findFirst();
    }
}
