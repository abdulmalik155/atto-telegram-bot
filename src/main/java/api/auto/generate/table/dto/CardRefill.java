package api.auto.generate.table.dto;

import api.auto.generate.table.entity.Profile;

public record CardRefill(
        Profile profile,
        String cardNumber,
        double amount
) {
}
