package api.auto.generate.table.dto;

import java.time.LocalDateTime;

public record AuthConfirm(
        LocalDateTime createdTime,
        LocalDateTime inputTime,
        String userInput,
        String confirmCode
) {
}
