package com.ftn.iss.eventPlanner.security.auth;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.AccountStatus;
import com.ftn.iss.eventPlanner.services.AccountReportService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class CustomAccountStatusChecker implements UserDetailsChecker {

    private final AccountReportService accountReportService;

    public CustomAccountStatusChecker(AccountReportService accountReportService) {
        this.accountReportService = accountReportService;
    }

    @Override
    public void check(UserDetails user) {
        if (!(user instanceof Account account)) return;
        accountReportService.checkSuspensionStatus(account.getId());
        if (!account.isEnabled() && account.getStatus()!= AccountStatus.SUSPENDED) {
            throw new DisabledException("Account is not enabled.");
        }
    }
}
