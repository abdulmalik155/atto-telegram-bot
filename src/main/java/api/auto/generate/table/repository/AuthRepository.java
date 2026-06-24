package api.auto.generate.table.repository;

import api.auto.generate.table.dto.AuthLogin;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.enums.Role;
import api.auto.generate.table.enums.Status;
import api.auto.generate.table.enums.UserStatus;
import api.auto.generate.table.utill.FileHandling;
import api.auto.generate.table.utill.ObjMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuthRepository extends FileHandling<Profile, Profile[]> {

    public AuthRepository() {
        super("profiles.json", Profile[].class);
        initializeAdmin();
    }

    private void initializeAdmin() {
        long found = read().stream().filter(profile -> profile.getRole().equals(Role.ADMIN)).count();
        if (found == 0) {
            Profile profile = new Profile();
            profile.setName("Adminbek");
            profile.setSurname("Adminjonov");
            profile.setPswd("1");
            profile.setPhone("1");
            profile.setRole(Role.ADMIN);
            profile.setStatus(UserStatus.ACTIVE_USER);
            profile.setCreatedDate(LocalDate.now());
            save(List.of(profile), true);
        }
    }

    public Profile findUser(AuthLogin request) {
        return read().stream()
                .filter(user ->
                        user.getPhone().equals(request.username()))
                .findFirst().orElse(null);
    }

    public Profile confirmLogin(AuthLogin request) {
        Profile profile = findUser(request);
        profile.setStatus(UserStatus.ACTIVE_USER);
        List<Profile> users = read();
        users.set(read().indexOf(
                        findUser(request)),
                profile);
        boolean saved = save(users, false);
        if (saved) {
            return profile;
        }
        return null;
    }
}