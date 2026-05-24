package com.nadson.myfinance.infrastructure.config;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.application.service.CategorizationEngine;
import com.nadson.myfinance.application.service.TransactionImportService;
import com.nadson.myfinance.application.usecase.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfiguration {

    /* =========================
       CRIAÇÃO
    ========================= */

    @Bean
    public CreateAccountPort createAccountUseCase(AccountRepositoryPort repository) {
        return new CreateAccountUseCase(repository);
    }

    @Bean
    public CreateCategoryPort createCategoryUseCase(CategoryRepositoryPort repository) {
        return new CreateCategoryUseCase(repository);
    }

    @Bean
    public CreateTransactionPort createTransactionUseCase(
            TransactionRepositoryPort transactionRepo,
            AccountRepositoryPort accountRepo,
            CategoryRepositoryPort categoryRepo,
            ProcessTransactionInBudgetPort processTransactionInBudget) {
        return new CreateTransactionUseCase(transactionRepo, accountRepo, categoryRepo, processTransactionInBudget);
    }

    @Bean
    public CreateUserPort createUserUseCase(UserRepositoryPort repository, CreateCategoryPort createCategoryPort, PasswordEncoderPort passwordEncoderPort) {
        return new CreateUserUseCase(repository, createCategoryPort, passwordEncoderPort);
    }

    @Bean
    public CreateCreditCardPort createCreditCardUseCase(CreditCardRepositoryPort creditCardRepository, AccountRepositoryPort accountRepository) {
        return new CreateCreditCardUseCase(creditCardRepository, accountRepository);
    }

    @Bean
    public CreateBudgetPort createBudgetPort(BudgetRepositoryPort repository,
                                             UserRepositoryPort userRepository,
                                             CategoryRepositoryPort categoryRepository,
                                             TransactionRepositoryPort transactionRepository) {
        return new CreateBudgetUseCase(repository, userRepository, categoryRepository, transactionRepository);
    }

    @Bean
    public CreateGoalPort createGoalPort(GoalRepositoryPort repository, UserRepositoryPort userRepository) {
        return new CreateGoalUseCase(repository, userRepository);
    }

    @Bean
    public CreateRecurringTemplatePort createRecurringTemplatePort(RecurringTemplateRepositoryPort repository) {
        return new CreateRecurringTemplateUseCase(repository);
    }

    /* =========================
       LISTAS / BUSCAS
    ========================= */

    @Bean
    public GetAccountport getAccountUseCase(AccountRepositoryPort accountRepositoryPort) {
        return new GetAccountUseCase(accountRepositoryPort);
    }

    @Bean
    public GetCategoriesPort getCategoriesUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new GetCategoriesUseCase(categoryRepositoryPort);
    }

    @Bean
    public GetTransactionPort getTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        return new GetTransactionUsecase(transactionRepositoryPort);
    }

    @Bean
    public GetUserPort getUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserUseCase(userRepositoryPort);
    }

    @Bean
    public ListTransactionsPort listTransactionsPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transRepo) {
        return new ListTransactionsUseCase(accountRepo, transRepo);
    }

    @Bean
    public GetAccountBalancePort getAccountBalancePort(TransactionRepositoryPort repository, AccountRepositoryPort accountRepositoryPort) {
        return new GetAccountBalanceUseCase(repository, accountRepositoryPort);
    }

    @Bean
    public GetExpensesByCategoryPort getExpensesByCategoryPort(TransactionRepositoryPort transactionRepo) {
        return new GetExpensesByCategoryUseCase(transactionRepo);
    }

    @Bean
    public GetIncomesByCategoryPort getIncomesByCategoryPort(TransactionRepositoryPort transactionRepo) {
        return new GetIncomesByCategoryUseCase(transactionRepo);
    }

    @Bean
    public GetTotalBalancePort getTotalBalancePort(AccountRepositoryPort accountRepositoryPort, UserRepositoryPort userRepositoryPort) {
        return new GetTotalBalanceUserCase(accountRepositoryPort, userRepositoryPort);
    }

    @Bean
    public ListAccountsByUserPort listAccountsByUserPort(AccountRepositoryPort accountRepositoryPort, UserRepositoryPort userRepository) {
        return new ListAccountsByUserUseCase(accountRepositoryPort, userRepository);
    }

    @Bean
    public ListCreditCardByUserPort listCreditCardByUserPort(CreditCardRepositoryPort creditCardRepository, UserRepositoryPort userRepository, BillingCycleRepositoryPort billingCycleRepository) {
        return new ListCreditCardByUserUseCase(creditCardRepository, userRepository, billingCycleRepository);
    }

    @Bean
    public GetCreditCardPort getCreditCardPort(CreditCardRepositoryPort repository, BillingCycleRepositoryPort billingCycleRepository) {
        return new GetCreditCardUseCase(repository, billingCycleRepository);
    }

    @Bean
    public GetFinancialDashboardKpisPort getFinancialDashboardKpisPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transRepo, RecurringTemplateRepositoryPort recurringRepo) {
        return new GetFinancialDashboardKpisUseCase(accountRepo, transRepo, recurringRepo);
    }

    @Bean
    public ListPendingRecurringPort listPendingRecurringPort(UserRepositoryPort userRepositoryPort, RecurringTemplateRepositoryPort recurringTemplateRepository, TransactionRepositoryPort transactionRepositoryPort) {
        return new ListPendingRecurringUseCase(userRepositoryPort, recurringTemplateRepository, transactionRepositoryPort);
    }

    @Bean
    public ListRecurringTemplatesPort listRecurringTemplatesPort(RecurringTemplateRepositoryPort repository) {
        return new ListRecurringTemplatesUseCase(repository);
    }

    @Bean
    public ListBudgetsPort listBudgetsPort(BudgetRepositoryPort repository) {
        return new ListBudgetsUseCase(repository);
    }

    @Bean
    public GetBudgetPort getBudgetPort(BudgetRepositoryPort repository) {
        return new GetBudgetUseCase(repository);
    }

    /* =========================
       PROCESSAMENTO / OPERAÇÕES
    ========================= */

    @Bean
    public TransactionImportService transactionImportService(
            CategorizationEngine categorizationEngine,
            TransferPort transferPort,
            CreateTransactionPort createTransactionPort,
            TransactionRepositoryPort transactionRepositoryPort,
            ListAccountsByUserPort listAccountsByUserPort,
            GetCategoriesPort getCategoriesPort,
            CreateCategoryPort createCategoryPort,
            List<CsvRowMapperStrategy> mapperStrategies) {
        return new TransactionImportService(
                categorizationEngine, transferPort, createTransactionPort, transactionRepositoryPort,
                listAccountsByUserPort, getCategoriesPort, createCategoryPort, mapperStrategies);
    }

    @Bean
    public TransferPort transferPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transRepo) {
        return new TransferUseCase(accountRepo, transRepo);
    }

    @Bean
    public CategorizeTransactionPort categorizeTransactionPort(TransactionRepositoryPort transactionRepositoryPort, CategoryRepositoryPort categoryRepository) {
        return new CategorizeTransactionUseCase(transactionRepositoryPort, categoryRepository);
    }

    @Bean
    public UpdateTransactionPort updateTransactionPort(
            TransactionRepositoryPort transactionRepositoryPort,
            AccountRepositoryPort accountRepositoryPort,
            BudgetRepositoryPort budgetRepositoryPort,
            ProcessTransactionInBudgetPort processTransactionInBudgetPort) {
        return new UpdateTransactionUseCase(
                transactionRepositoryPort,
                accountRepositoryPort,
                budgetRepositoryPort,
                processTransactionInBudgetPort
        );
    }
    @Bean
    public UpdateAccountPort updateAccountPort(AccountRepositoryPort repository) {
        return new UpdateAccountUseCase(repository);
    }

    @Bean
    public UpdateUserUsePort updateUserUsePort(UserRepositoryPort repository) {
        return new UpdateUserUseCase(repository);
    }

    @Bean
    public UpdateCategoryPort updateCategoryPort(CategoryRepositoryPort categoryRepositoryPort) {
        return new UpdateCategoryUseCase(categoryRepositoryPort);
    }

    @Bean
    public ChangePasswordPort changePasswordPort(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        return new ChangePasswordUseCase(userRepositoryPort, passwordEncoder);
    }

    @Bean
    public ConfirmRecurringPort confirmRecurringPort(TransactionRepositoryPort transactionRepositoryPort, AccountRepositoryPort accountRepositoryPort) {
        return new ConfirmRecurringUseCase(transactionRepositoryPort, accountRepositoryPort);
    }

    @Bean
    public ProcessCreditCardTransactionPort processCreditCardTransactionPort(
            CreditCardRepositoryPort creditCardRepository,
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository,
            CreditCardInstallmentRepositoryPort installmentRepository) {
        return new ProcessCreditCardTransactionUseCase(
                creditCardRepository, billingCycleRepository, purchaseRepository, installmentRepository);
    }

    @Bean
    public GetBillingCycleDetailsPort getBillingCycleDetailsPort(
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardRepositoryPort creditCardRepository,
            CreditCardInstallmentRepositoryPort installmentRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository) {
        return new GetBillingCycleDetailsUseCase(
                billingCycleRepository, creditCardRepository, installmentRepository, purchaseRepository);
    }

    @Bean
    public GetBillingCycleByDatePort getBillingCycleByDatePort(
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardRepositoryPort creditCardRepository,
            CreditCardInstallmentRepositoryPort installmentRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository) {
        return new GetBillingCycleByDateUseCase(
                billingCycleRepository, creditCardRepository, installmentRepository, purchaseRepository);
    }

    @Bean
    public BillingProcessPaymentPort processPaymentPort(
            CreditCardInstallmentRepositoryPort installmentRepository,
            BillingPaymentRepositoryPort paymentRepository,
            AccountRepositoryPort accountRepository,
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardRepositoryPort creditCardRepository,
            TransactionRepositoryPort transactionRepositoryPort,
            CategoryRepositoryPort categoryRepositoryPort) {
        return new BillingProcessPaymentUseCase(
                installmentRepository,
                paymentRepository,
                accountRepository,
                billingCycleRepository,
                creditCardRepository,
                transactionRepositoryPort,
                categoryRepositoryPort
        );
    }

    @Bean
    public UpdateBudgetLimitPort updateBudgetLimitPort(BudgetRepositoryPort repository) {
        return new UpdateBudgetLimitUseCase(repository);
    }

    @Bean
    public ProcessTransactionInBudgetPort processTransactionInBudgetPort(BudgetRepositoryPort budgetRepository, AccountRepositoryPort accountRepository) {
        return new ProcessTransactionInBudgetUseCase(budgetRepository, accountRepository);
    }

    /* =========================
       DELEÇÃO
    ========================= */

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(
            TransactionRepositoryPort transactionRepository,
            AccountRepositoryPort accountRepository,
            BudgetRepositoryPort budgetRepository) {
        return new DeleteTransactionUseCase(transactionRepository, accountRepository, budgetRepository);
    }

    @Bean
    public DeleteTransactionPort deleteTransactionPort(DeleteTransactionUseCase deleteTransactionUseCase) {
        return deleteTransactionUseCase;
    }

    @Bean
    public DeleteAccountPort deleteAccountPort(
            AccountRepositoryPort accountRepo,
            TransactionRepositoryPort transactionRepo,
            RecurringTemplateRepositoryPort recurringRepo,
            CreditCardRepositoryPort creditCardRepo,
            DeleteTransactionUseCase deleteTransactionUseCase) {
        return new DeleteAccountUseCase(accountRepo, transactionRepo, recurringRepo, creditCardRepo, deleteTransactionUseCase);
    }

    @Bean
    public DeleteUserPort deleteUserPort(UserRepositoryPort userRepo, AccountRepositoryPort accountRepo,
                                         CategoryRepositoryPort categoryRepo, BudgetRepositoryPort budgetRepo,
                                         GoalRepositoryPort goalRepo, RecurringTemplateRepositoryPort recurringRepo,
                                         TransactionRepositoryPort transactionRepo, BillingCycleRepositoryPort billingCycleRepo,
                                         BillingPaymentRepositoryPort billingPaymentRepo, CreditCardRepositoryPort creditCardRepo) {
        return new DeleteUserUseCase(
                userRepo, accountRepo, categoryRepo, budgetRepo, goalRepo, recurringRepo,
                transactionRepo, billingCycleRepo, billingPaymentRepo, creditCardRepo
        );
    }

    @Bean
    public DeleteRecurringTemplatePort deleteRecurringTemplatePort(RecurringTemplateRepositoryPort recurringTemplateRepositoryPort, TransactionRepositoryPort transactionRepositoryPort) {
        return new DeleteRecurringTemplateUseCase(recurringTemplateRepositoryPort, transactionRepositoryPort);
    }

    @Bean
    public DeleteBudgetPort deleteBudgetPort(BudgetRepositoryPort repository) {
        return new DeleteBudgetUseCase(repository);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(CategoryRepositoryPort repository) {
        return new DeleteCategoryUseCase(repository);
    }

    @Bean
    public DeleteCategoryPort deleteCategoryPort(DeleteCategoryUseCase deleteCategoryUseCase) {
        return deleteCategoryUseCase;
    }
    @Bean
    public DeleteCreditCardPort deleteCreditCardPort(CreditCardRepositoryPort creditCardRepository, UserRepositoryPort userRepository) {
        return new DeleteCreditCardUseCase(creditCardRepository, userRepository);
    }
}
