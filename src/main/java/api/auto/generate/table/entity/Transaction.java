package api.auto.generate.table.entity;

import api.auto.generate.table.enums.TransactionType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction {
    @EqualsAndHashCode.Include

    private String cardNumber;
    private double amount;
    private String terminalCode;
    private TransactionType type;
    private LocalDate createdDate;
    private Card card;

    @Override
    public String toString() {
        return String.format(
                "\n🧾 TRANSACTION RECEIPT\n" +
                        "🔢 Terminal: `%s`\n" +
                        "💳 Card: `**** %s`\n" +
                        "💰 Amount: %,.2f UZS\n" +
                        "ℹ️ Type: %s\n" +
                        "📅 Date: %s\n",
                terminalCode,
                (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "xxxx"),
                amount,
                type,
                createdDate
        );
    }


}