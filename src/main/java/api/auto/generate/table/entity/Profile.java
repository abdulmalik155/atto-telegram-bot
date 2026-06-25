package api.auto.generate.table.entity;

import api.auto.generate.table.enums.Role;
import api.auto.generate.table.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Profile {
    @EqualsAndHashCode.Include
    private String phone;
    private String name;
    private String surname;
    private String pswd;
    private LocalDate createdDate;
    private UserStatus status;
    private Boolean visibleUser;
    private Role role;

    @Override
    public String toString() {
        String header = (role != null && role.name().equalsIgnoreCase("ADMIN"))
                ? "✨ [ SYSTEM ADMINISTRATOR ] ✨"
                : "💳 [ CLIENT PROFILE CARD ] 💳";

        String statusFlag = "⚪ UNKNOWN";
        if (status != null) {
            statusFlag = switch (status) {
                case ACTIVE_USER -> "🟢 ACTIVE";
                case DELETED_USER -> "🔴 DELETED";
                default -> "🟡 RESTRICTED";
            };
        }

        String dateStr = (createdDate != null) ? createdDate.toString() : "N/A";

        return String.format("""
            %s
            -----------------------------------
            ▪️ *Full Name:* %s %s
            ▪️ *Phone:* `%s`
            ▪️ *Role:* `%s`
            -----------------------------------
            ▪️ *Status:* %s
            ▪️ *Visibility:* %s
            ▪️ *Reg Date:* `%s`
            -----------------------------------
            """,
                header,
                (name != null ? name : "N/A"),
                (surname != null ? surname : ""),
                (phone != null ? phone : "N/A"),
                (role != null ? role.name() : "USER"),
                statusFlag,
                (Boolean.TRUE.equals(visibleUser) ? "🌐 VISIBLE" : "🔒 HIDDEN"),
                dateStr
        );
    }

}
