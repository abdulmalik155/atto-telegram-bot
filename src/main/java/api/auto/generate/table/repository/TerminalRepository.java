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
