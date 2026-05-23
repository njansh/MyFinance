    package com.nadson.myfinance.infrastructure.adapter.web.controller;

    import com.nadson.myfinance.application.port.in.CreateAccountPort;
    import com.nadson.myfinance.application.port.in.UpdateAccountPort;
    import com.nadson.myfinance.application.port.in.DeleteAccountPort;
    import com.nadson.myfinance.application.port.in.GetAccountport;
    import com.nadson.myfinance.application.port.in.ListTransactionsPort;
    import com.nadson.myfinance.domain.entity.Account;
    import com.nadson.myfinance.domain.entity.Transaction;
    import com.nadson.myfinance.domain.enums.AccountType;
    import com.nadson.myfinance.infrastructure.adapter.web.dto.response.AccountResponse;
    import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateAccountRequest;
    import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UpdateAccountRequest;
    import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
    import org.springframework.data.domain.Sort;
    import org.springframework.data.web.PageableDefault;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;

    import java.time.LocalDateTime;
    import java.time.YearMonth;
    import java.util.UUID;



    @RestController
    @RequestMapping("/accounts")
    public class AccountController {
        private final ListTransactionsPort listTransactionsPort;
        private final GetAccountport getAccountport;
        private final CreateAccountPort createAccountPort;
        private final UpdateAccountPort updateAccountPort;
        private final DeleteAccountPort deleteAccountPort;

        public AccountController(ListTransactionsPort listTransactionsPort, GetAccountport getAccountport, CreateAccountPort createAccountPort, UpdateAccountPort updateAccountPort, DeleteAccountPort deleteAccountPort) {
            this.listTransactionsPort = listTransactionsPort;
            this.getAccountport = getAccountport;
            this.createAccountPort = createAccountPort;
            this.updateAccountPort = updateAccountPort;
            this.deleteAccountPort = deleteAccountPort;
        }
        @GetMapping("/{id}")
        public ResponseEntity<AccountResponse> getById(@PathVariable UUID id) {
            Account account = getAccountport.execute(id);
            return ResponseEntity.ok(AccountResponse.fromDomain(account));
        }

        @PostMapping
        public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request) {
            Account account = createAccountPort.execute(
                    request.getUserId(),
                    request.getName(),
                    AccountType.valueOf(request.getType().toUpperCase())
            );

            return ResponseEntity.status(201).body(AccountResponse.fromDomain(account));
        }
        @PutMapping("/{id}")
        public ResponseEntity<AccountResponse> update(@PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
            String userIdString = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = UUID.fromString(userIdString);

            Account account = updateAccountPort.execute(
                    id,
                    userId,
                    request.name(),
                    request.balance(),
                    request.type()
            );
            return ResponseEntity.ok(AccountResponse.fromDomain(account));
        }
        @GetMapping("/{id}/transactions")
        public ResponseEntity<Page<TransactionResponse>> listTransactions(
                @PathVariable UUID id,
                @RequestParam(required = false) Integer month,
                @RequestParam(required = false) Integer year,
                @RequestParam(required = false) String desc,
                @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

            LocalDateTime startDate = null;
            LocalDateTime endDate = null;

            if (month != null && year != null) {
                YearMonth yearMonth = YearMonth.of(year, month);
                startDate = yearMonth.atDay(1).atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            }

            Page<Transaction> transactionsPage = listTransactionsPort.execute(id, startDate, endDate, desc, pageable);

            Page<TransactionResponse> response = transactionsPage.map(t -> TransactionResponse.fromDomain(t, null));

            return ResponseEntity.ok(response);
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
            String userIdString = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = UUID.fromString(userIdString);
            deleteAccountPort.execute(id, userId);
            return ResponseEntity.noContent().build();
        }
    }
