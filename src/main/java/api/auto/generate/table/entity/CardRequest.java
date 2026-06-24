package api.auto.generate.table.entity;

import api.auto.generate.table.enums.CardRequestStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CardRequest {
    @EqualsAndHashCode.Include
    private Profile profile;
    private String cardType;
    private CardRequestStatus status;
    private Card card;
}

