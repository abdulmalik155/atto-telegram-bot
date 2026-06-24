package api.auto.generate.table.dto;

import api.auto.generate.table.entity.Profile;

public record RequestForCard(
        Profile profile,
        String cardType
){}

