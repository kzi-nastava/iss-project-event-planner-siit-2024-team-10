package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.accountreport.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.GetAccountReportDTO;
import com.ftn.iss.eventPlanner.exception.AccountSuspendedException;
import com.ftn.iss.eventPlanner.exception.ReportAlreadySentException;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.AccountReport;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.repositories.AccountReportRepository;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    private final int SUSPENSION_TIME = 3;

    public List<GetAccountReportDTO> findAllPending() {
        List<AccountReport> pendingReports = accountReportRepository.findAll().stream()
                .filter(report -> report.getStatus() == Status.PENDING)
                .collect(Collectors.toList());

        return mapToAccountReportsDTO(pendingReports);
    }

    public CreatedAccountReportDTO create(CreateAccountReportDTO createAccountReportDTO) {
        AccountReport accountReport = new AccountReport();
        Account reporter = accountRepository.findById(createAccountReportDTO.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter id"));

        Account reportee = accountRepository.findById(createAccountReportDTO.getReporteeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reportee id"));


        if(accountReportRepository.existsByReporter_IdAndReportee_Id(createAccountReportDTO.getReporterId(), createAccountReportDTO.getReporteeId())) {
            throw  new ReportAlreadySentException("Report has already been sent.");
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
        accountReport.setProcessingTimestamp(LocalDateTime.now().plusDays(SUSPENSION_TIME));

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

    public void checkSuspensionStatus(int accountId) {
        Optional<AccountReport> reportOpt = accountReportRepository.findTopByReportee_IdAndStatusOrderByProcessingTimestampDesc(accountId, Status.ACCEPTED);
        if (reportOpt.isPresent()) {
            AccountReport report = reportOpt.get();
            if (report.getProcessingTimestamp().isBefore(LocalDateTime.now())) {
                accountService.endAccountSuspension(accountId); // clean up expired status
                report.setStatus(Status.DENIED); // the report is no longer valid
                accountReportRepository.save(report);
                return;
            }
            throw new AccountSuspendedException("Your account has been suspended.\nTime left until you can log in again: " + formatRemainingTime(report.getProcessingTimestamp()));
        }
    }

    public String formatRemainingTime(LocalDateTime suspendedUntil) {
        LocalDateTime now = LocalDateTime.now();
        if (suspendedUntil.isBefore(now)) {
            return "Suspension ended";
        }
        Duration duration = Duration.between(now, suspendedUntil);

        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.toSeconds();

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(days == 1 ? " day " : " days ");
        if (hours > 0) sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        if (minutes > 0) sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        if (seconds > 0) sb.append(seconds).append(minutes == 1 ? " second" : " seconds");

        return sb.toString().trim();
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
        return dto;
    }

    private CreatedAccountReportDTO mapToCreatedAccountReportDTO(AccountReport accountReport) {
        CreatedAccountReportDTO dto = new CreatedAccountReportDTO();

        dto.setId(accountReport.getId());
        dto.setDescription(accountReport.getDescription());
        dto.setReporterEmail(accountReport.getReporter().getEmail());
        dto.setReporteeEmail(accountReport.getReportee().getEmail());

        return dto;
    }

}
