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

    /*  public Profile confirmLogin(AuthLogin request) {
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
      }*/
    public Profile confirmLogin(AuthLogin request) {
        List<Profile> users = read();

        // Search the list by phone number to find the exact array position
        int targetIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getPhone().equals(request.username())) {
                targetIndex = i;
                break;
            }
        }

        // If user is found, update status, swap in the list, and write to disk
        if (targetIndex != -1) {
            Profile profile = users.get(targetIndex);
            profile.setStatus(UserStatus.ACTIVE_USER);
            users.set(targetIndex, profile);

            boolean saved = save(users, false);
            if (saved) {
                return profile;
            }
        }
        return null;
    }

}