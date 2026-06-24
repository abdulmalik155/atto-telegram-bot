package api.auto.generate.table.dto;

import java.util.UUID;

public record TerminalDto (
        String code,
        String address,
        String companyName
){}
