package api.auto.generate.table.controller;

import api.auto.generate.table.dto.AuthConfirm;
import api.auto.generate.table.dto.AuthLogin;
import api.auto.generate.table.dto.AuthRegister;
import api.auto.generate.table.entity.Profile;
import api.auto.generate.table.service.AdminService;
import api.auto.generate.table.service.AuthService;

public class AuthController {
    private final AuthService authService = new AuthService();
    private final AdminService adminService = new AdminService();

    public String register(AuthRegister request) {
        return authService.register(request);
    }

    public Profile login(AuthLogin request) {
        return authService.login(request);
    }

    public Profile findUser(AuthLogin request) {
        return authService.findUserByPhone(request);
    }

    public boolean confirmCode(AuthConfirm request) {
        return authService.confirmCode(request);
    }

    public void triggerAutomationWorker() {
        adminService.runAutomationWorker();
    }

    public void triggerAutomationWorker2() {
        adminService.runtimeAutomationWorker2();
    }
}
