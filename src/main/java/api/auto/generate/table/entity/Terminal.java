package api.auto.generate.table.entity;

import api.auto.generate.table.enums.Status;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Terminal {
    @EqualsAndHashCode.Include
    String code;
    String address;
    Status status;
    LocalDate createdDate;
    Card card;

    @Override
    public String toString() {
        return String.format(
                "\n🏪 SHOP INFORMATION\n" +
                        "🔢 Merchant Code: `%s`\n" +
                        "📍 Address: %s\n",
                code,
                address
        );
    }

}
