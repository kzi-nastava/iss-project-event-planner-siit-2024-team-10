package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.accountreport.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.GetAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.SuspensionStatusDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.AccountReport;
import com.ftn.iss.eventPlanner.model.AccountStatus;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.repositories.AccountReportRepository;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountReportService {
    @Autowired
    private AccountReportRepository accountReportRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    private final int SUSPENTION_TIME = 3;

    public List<GetAccountReportDTO> findAllPending() {
        List<AccountReport> pendingReports = accountReportRepository.findAll().stream()
                .filter(report -> report.getStatus() == Status.PENDING)
                .collect(Collectors.toList());

        return mapToAccountReportsDTO(pendingReports);
    }

    public CreatedAccountReportDTO create(CreateAccountReportDTO createAccountReportDTO) {
        AccountReport accountReport = new AccountReport();
        Account reporter = accountRepository.findByEmail(createAccountReportDTO.getReporterEmail());
        if (reporter == null) {
            throw  new IllegalArgumentException("Reporter not found");
        }
        Account reportee = accountRepository.findByEmail(createAccountReportDTO.getReporteeEmail());
        if (reportee == null) {
            throw  new IllegalArgumentException("Reportee not found");
        }

        if(accountReportRepository.existsByReporter_EmailAndReportee_Email(createAccountReportDTO.getReporterEmail(), createAccountReportDTO.getReporteeEmail())) {
            throw  new IllegalStateException("Report has already been created");
        }

        accountReport.setReporter(reporter);
        accountReport.setReportee(reportee);
        accountReport.setStatus(Status.PENDING);
        accountReport.setDescription(createAccountReportDTO.getDescription());
        accountReportRepository.save(accountReport);

        return mapToCreatedAccountReportDTO(accountReport);
    }

    public void acceptReport(int reportId){
        AccountReport accountReport = accountReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        accountReport.setStatus(Status.ACCEPTED);
        accountReport.setProcessingTimestamp(LocalDateTime.now().plusDays(SUSPENTION_TIME));

        Account reportee = accountRepository.findByEmail(accountReport.getReportee().getEmail());
        if (reportee == null) {
            throw  new IllegalArgumentException("Reportee not found");
        }

        accountService.suspendAccount(reportee.getId());
        accountReportRepository.save(accountReport);
    }

    public void rejectReport(int reportId){
        AccountReport accountReport = accountReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        accountReport.setStatus(Status.DENIED);
        accountReportRepository.save(accountReport);
    }

    public SuspensionStatusDTO getSuspensionDetails(int accountId) {
        Optional<LocalDateTime> suspensionUntil= getSuspensionIfActive(accountId);
        SuspensionStatusDTO dto = new SuspensionStatusDTO();

        if (suspensionUntil.isPresent()) {
            dto.setSuspended(true);
            dto.setSuspendedUntil(suspensionUntil.get());
        } else {
            dto.setSuspended(false);
            dto.setSuspendedUntil(null);
        }

        return dto;
    }

    private Optional<LocalDateTime> getSuspensionIfActive(int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (account.getStatus() != AccountStatus.SUSPENDED) {
            return Optional.empty();
        }

        Optional<AccountReport> reportOpt = accountReportRepository
                .findTopByReportee_IdAndStatusOrderByProcessingTimestampDesc(accountId, Status.ACCEPTED);

        if (reportOpt.isEmpty()) {
            return Optional.empty();
        }

        AccountReport report = reportOpt.get();

        if (report.getProcessingTimestamp() == null || report.getProcessingTimestamp().isBefore(LocalDateTime.now())) {
            accountService.endAccountSuspension(accountId); // clean up expired status
            return Optional.empty();
        }

        return Optional.of(report.getProcessingTimestamp());
    }

    private List<GetAccountReportDTO> mapToAccountReportsDTO(List<AccountReport> accountReports) {
        return accountReports.stream()
                .map(this::mapToAccountReportDTO)
                .collect(Collectors.toList());
    }

    private GetAccountReportDTO mapToAccountReportDTO(AccountReport accountReport) {
        GetAccountReportDTO dto = new GetAccountReportDTO();
        dto.setId(accountReport.getId());
        dto.setDescription(accountReport.getDescription());
        dto.setReporterEmail(accountReport.getReporter().getEmail());
        dto.setReporteeEmail(accountReport.getReportee().getEmail());
        dto.setStatus(accountReport.getStatus());
        return dto;
    }

    private CreatedAccountReportDTO mapToCreatedAccountReportDTO(AccountReport accountReport) {
        CreatedAccountReportDTO dto = new CreatedAccountReportDTO();

        dto.setId(accountReport.getId());
        dto.setDescription(accountReport.getDescription());
        dto.setReporterEmail(accountReport.getReporter().getEmail());
        dto.setReporteeEmail(accountReport.getReportee().getEmail());
        dto.setStatus(accountReport.getStatus());

        return dto;
    }

}
