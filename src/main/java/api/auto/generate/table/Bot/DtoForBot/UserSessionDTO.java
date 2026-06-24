package api.auto.generate.table.Bot.DtoForBot;

import api.auto.generate.table.entity.Profile;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSessionDTO {
    private String currentStep = "/start";
    private Profile authenticatedUser;
    private String cardNumber;
    private Profile registrationProfile = new Profile();
    private String confirmationCode;
    private LocalDateTime codeCreatedTime;
    private String savedControllerResponse;
    private int loginCount;
    public void setLoginCount(int loginCount) {
        this.loginCount += loginCount;
    }
}
