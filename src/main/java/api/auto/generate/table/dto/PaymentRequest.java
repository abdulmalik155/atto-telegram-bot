package api.auto.generate.table.dto;

public record PaymentRequest(
        String cardNumber,
        String terminalNumber) {
}
