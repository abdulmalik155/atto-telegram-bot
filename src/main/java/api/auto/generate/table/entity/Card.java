package api.auto.generate.table.entity;

import api.auto.generate.table.enums.Status;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {
    @EqualsAndHashCode.Include
    String cardNumber;
    LocalDate expirationDate;
    LocalDate issueDate;
    LocalDate activationDate;
    double balance;
    Profile user;
    Status status;


    @Override
    public String toString() {
        String cardIcon = (status == Status.ACTIVE) ? "🟩 " : "🟥 ";
        return String.format(
                "\n💳 CARD DETAILED VIEW\n" +
                        "🔢 Number: `%s`\n" +
                        "%s Status: %s\n" +
                        "💰 Balance: %,.2f UZS\n" +
                        "📅 Issued: %s | ⏳ Expired: `%s`\n" +
                        "👤 Holder: %s\n",
                cardNumber,
                cardIcon, status,
                balance,
                issueDate, expirationDate,
                (user != null ? user.getName() + " " + user.getSurname() : "No Owner")
        );

    }

}
