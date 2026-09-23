package bank.api;

import bank.domain.Account;
import bank.domain.AuditEvent;
import bank.domain.Customer;
import bank.domain.Transaction;
import bank.domain.TransactionType;
import bank.service.TransactionQuery;
import bank.service.BankService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public List<AccountResponse> accounts(Authentication authentication) {
        return accountsVisibleTo(authentication).stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public AccountResponse account(@PathVariable UUID accountId, Authentication authentication) {
        requireAccountAccess(accountId, authentication);
        return AccountResponse.from(bankService.getAccount(accountId));
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public CustomerResponse registerCustomer(@Valid @RequestBody CreateCustomerRequest request, Principal principal) {
        String actor = actor(principal);
        return CustomerResponse.from(bankService.registerCustomer(actor, request.firstName(), request.lastName(), request.ownerUsername()));
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public AccountResponse openAccount(@Valid @RequestBody OpenAccountRequest request, Principal principal) {
        return AccountResponse.from(bankService.openAccount(actor(principal), request.customerId(), request.openingBalance()));
    }

    @PostMapping("/accounts/{accountId}/deposit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public AccountResponse deposit(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, Principal principal) {
        bankService.deposit(actor(principal), accountId, request.amount(), request.description());
        return AccountResponse.from(bankService.getAccount(accountId));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public AccountResponse withdraw(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, Principal principal) {
        bankService.withdraw(actor(principal), accountId, request.amount(), request.description());
        return AccountResponse.from(bankService.getAccount(accountId));
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public void transfer(@Valid @RequestBody TransferRequest request, Principal principal, Authentication authentication) {
        requireAccountAccess(request.sourceAccountId(), authentication);
        bankService.transfer(actor(principal), request.sourceAccountId(), request.targetAccountId(), request.amount(), request.description());
    }

    @GetMapping("/accounts/{accountId}/transactions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public List<TransactionResponse> transactions(@PathVariable UUID accountId, Authentication authentication) {
        requireAccountAccess(accountId, authentication);
        return bankService.getTransactions(accountId).stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/accounts/{accountId}/transactions/page")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public TransactionPageResponse transactionPage(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @Size(max = 100) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            Authentication authentication) {
        requireAccountAccess(accountId, authentication);
        var transactionPage = bankService.getTransactionPage(accountId, new TransactionQuery(type, query, from, to, page, size));
        return new TransactionPageResponse(
                transactionPage.content().stream().map(TransactionResponse::from).toList(),
                transactionPage.page(), transactionPage.size(), transactionPage.totalElements());
    }

    @PostMapping("/accounts/{accountId}/transaction-exports")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public void recordTransactionExport(
            @PathVariable UUID accountId,
            @Valid @RequestBody TransactionExportRequest request,
            Principal principal,
            Authentication authentication) {
        requireAccountAccess(accountId, authentication);
        bankService.recordTransactionExport(actor(principal), accountId, request.format(), request.filterSummary());
    }

    @GetMapping("/audit-events")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditEventResponse> auditEvents() {
        return bankService.getAuditEvents().stream().map(AuditEventResponse::from).toList();
    }

    public record CreateCustomerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            String ownerUsername) {
    }

    public record OpenAccountRequest(
            @NotNull UUID customerId,
            @NotNull @DecimalMin("0.00") BigDecimal openingBalance) {
    }

    public record MoneyRequest(
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String description) {
    }

    public record TransferRequest(
            @NotNull UUID sourceAccountId,
            @NotNull UUID targetAccountId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String description) {
    }

            public record TransactionExportRequest(
                @NotBlank @Pattern(regexp = "CSV|PDF") String format,
                @Size(max = 200) String filterSummary) {
            }

    public record CustomerResponse(UUID id, String fullName, String ownerUsername) {
        static CustomerResponse from(Customer customer) {
            return new CustomerResponse(customer.id(), customer.fullName(), customer.ownerUsername());
        }
    }

    public record AccountResponse(UUID id, String iban, UUID customerId, BigDecimal balance) {
        static AccountResponse from(Account account) {
            return new AccountResponse(account.id(), account.iban(), account.customerId(), account.balance());
        }
    }

    public record TransactionResponse(
            UUID id,
            UUID accountId,
            String type,
            BigDecimal amount,
            String timestamp,
            String description) {
        static TransactionResponse from(Transaction transaction) {
            return new TransactionResponse(
                    transaction.id(),
                    transaction.accountId(),
                    transaction.type().name(),
                    transaction.amount(),
                    transaction.timestamp().toString(),
                    transaction.description());
        }
    }

    public record TransactionPageResponse(List<TransactionResponse> content, int page, int size, long totalElements) {
    }

    public record AuditEventResponse(
            UUID id,
            String actor,
            String action,
            String resourceType,
            UUID resourceId,
            BigDecimal amount,
            String timestamp,
            String outcome,
            String description) {
        static AuditEventResponse from(AuditEvent event) {
            return new AuditEventResponse(
                    event.id(),
                    event.actor(),
                    event.action(),
                    event.resourceType(),
                    event.resourceId(),
                    event.amount(),
                    event.timestamp().toString(),
                    event.outcome(),
                    event.description());
        }
    }

    private String actor(Principal principal) {
        return principal == null ? "anonymous" : principal.getName();
    }

    private List<Account> accountsVisibleTo(Authentication authentication) {
        if (isStaff(authentication)) {
            return bankService.getAccounts();
        }
        return bankService.getAccountsForOwner(authentication.getName());
    }

    private void requireAccountAccess(UUID accountId, Authentication authentication) {
        if (isStaff(authentication)) {
            return;
        }
        if (!bankService.isAccountOwnedBy(accountId, authentication.getName())) {
            throw new AccessDeniedException("Account is not owned by current user");
        }
    }

    private boolean isStaff(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .anyMatch(authority -> authority.equals("ROLE_EMPLOYEE") || authority.equals("ROLE_ADMIN"));
    }
}
