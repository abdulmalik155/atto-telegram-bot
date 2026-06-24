package api.auto.generate.table.service;

import api.auto.generate.table.dto.AuthConfirm;
import api.auto.generate.table.dto.AuthLogin;
import api.auto.generate.table.dto.AuthRegister;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.enums.UserStatus;
import api.auto.generate.table.repository.AuthRepository;

import java.time.LocalDate;
import java.util.List;

public class AuthService {
    private final AuthRepository authRepository = new AuthRepository();

    public String register(AuthRegister request) {
        Profile user = authRepository.findUser(new AuthLogin(request.phone()));
        if (user == null) {
            Profile profile = new Profile();
            profile.setName(request.name());
            profile.setSurname(request.surname());
            profile.setPhone(request.phone());
            profile.setPswd(request.pswd());
            profile.setRole(Role.USER);
            profile.setStatus(UserStatus.CREATED_USER);
            profile.setCreatedDate(LocalDate.now());
            profile.setVisibleUser(Boolean.TRUE);
            boolean saved = authRepository.save(List.of(profile), true);
            return saved ? "Successfully registered!" : "failed to register!";
        }
        return null;
    }


    public Profile findUserByPhone(AuthLogin username) {
        return authRepository.findUser(username);
    }

    public Profile login(AuthLogin request) {
        return authRepository.confirmLogin(request);
    }

    public boolean confirmCode(AuthConfirm request) {
        return request.userInput()
                .equals(String.valueOf(request.confirmCode())) &&
                request.createdTime()
                        .plusMinutes(1)
                        .isAfter(request.inputTime());
    }
}
