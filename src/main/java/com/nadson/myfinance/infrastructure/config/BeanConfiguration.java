package com.nadson.myfinance.infrastructure.config;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.application.service.CategorizationEngine;
import com.nadson.myfinance.application.service.TransactionImportService;
import com.nadson.myfinance.application.usecase.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfiguration {

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
            AccountRepositoryPort accountRepo, CategoryRepositoryPort categoryRepo, ApplicationEventPublisher eventPublisher) {
        return new CreateTransactionUseCase(transactionRepo, accountRepo,categoryRepo, eventPublisher);
    }
    @Bean
    public CreateUserPort createUserUseCase(UserRepositoryPort repository, CreateCategoryPort createCategoryPort, PasswordEncoderPort passwordEncoderPort) {
        return new CreateUserUseCase(repository, createCategoryPort, passwordEncoderPort);
    }

    @Bean
    public GetAccountport getAccountUseCase(AccountRepositoryPort accountRepositoryPort) {
        return new GetAccountUseCase(accountRepositoryPort);
    }
    @Bean
    public GetCategoriesPort getCategoriesUseCase(CategoryRepositoryPort categoryRepositoryPort){
        return new GetCategoriesUseCase(categoryRepositoryPort);
    }
    @Bean
    public GetTransactionPort getTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        return new GetTransactionUsecase(transactionRepositoryPort);

    }
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
    public GetUserPort getUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserUseCase(userRepositoryPort);
    }
    @Bean
    public TransferPort transferPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transRepo) {
        return new TransferUseCase(accountRepo, transRepo);
    }

    @Bean
    public ListTransactionsPort listTransactionsPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transRepo) {
        return new ListTransactionsUseCase(accountRepo, transRepo);
    }

    @Bean
    public GetAccountBalancePort getAccountBalancePort(TransactionRepositoryPort repository,AccountRepositoryPort accountRepositoryPort) {
        return new GetAccountBalanceUseCase(repository,accountRepositoryPort );
    }
    @Bean
    public CategorizeTransactionPort categorizeTransactionPort(TransactionRepositoryPort transactionRepositoryPort, CategoryRepositoryPort categoryRepository){
        return new CategorizeTransactionUseCase(transactionRepositoryPort, categoryRepository);

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
    public UpdateTransactionPort updateTransactionPort(TransactionRepositoryPort transactionRepositoryPort, AccountRepositoryPort accountRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        return new UpdateTransactionUseCase(transactionRepositoryPort, categoryRepositoryPort, accountRepositoryPort);
    }
    @Bean
    public DeleteTransactionPort deleteTransactionPort(TransactionRepositoryPort transactionRepo, AccountRepositoryPort accountRepo) {
        return new DeleteTransactionUseCase(transactionRepo, accountRepo);
    }
    @Bean
    public ListAccountsByUserPort listAccountsByUserPort(AccountRepositoryPort accountRepositoryPort, UserRepositoryPort userRepository){
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
    public ListPendingRecurringPort listPendingRecurringPort(UserRepositoryPort userRepositoryPort, RecurringTemplateRepositoryPort recurringTemplateRepository){
        return new ListPendingRecurringUseCase(userRepositoryPort, recurringTemplateRepository);
    }

    @Bean
    public ConfirmRecurringPort confirmRecurringPort(RecurringTemplateRepositoryPort repository, CreateTransactionPort createTransactionPort) {
        return new ConfirmRecurringUseCase(repository, createTransactionPort);
    }
    @Bean
    public CreateCreditCardPort createCreditCardUseCase(CreditCardRepositoryPort creditCardRepository,AccountRepositoryPort accountRepository) {
        return new CreateCreditCardUseCase(creditCardRepository, accountRepository);
    }
    @Bean
    public CreateBudgetPort createBudgetPort(BudgetRepositoryPort repository,UserRepositoryPort userRepository, CategoryRepositoryPort categoryRepository) {
        return new CreateBudgetUseCase(repository, userRepository, categoryRepository);
    }

    @Bean
    public CreateGoalPort createGoalPort(GoalRepositoryPort repository, UserRepositoryPort userRepository) {
        return new CreateGoalUseCase(repository, userRepository);
    }
    @Bean
    public DeleteUserPort deleteUserPort(UserRepositoryPort userRepo, AccountRepositoryPort accountRepo,
                                         CategoryRepositoryPort categoryRepo, BudgetRepositoryPort budgetRepo,
                                         GoalRepositoryPort goalRepo, RecurringTemplateRepositoryPort recurringRepo,
                                         DeleteAccountPort deleteAccountPort) {
        return new DeleteUserUseCase(
                userRepo, accountRepo, categoryRepo, budgetRepo, goalRepo, recurringRepo, deleteAccountPort
        );
    }
    @Bean
    public DeleteAccountPort deleteAccountPort(AccountRepositoryPort accountRepo, TransactionRepositoryPort transactionRepo,
                                               RecurringTemplateRepositoryPort recurringRepo, CreditCardRepositoryPort creditCardRepo){
        return new DeleteAccountUseCase(accountRepo, transactionRepo, recurringRepo, creditCardRepo);
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
            CreditCardRepositoryPort creditCardRepository) {
        return new BillingProcessPaymentUseCase(
                installmentRepository,
                paymentRepository,
                accountRepository,
                billingCycleRepository,
                creditCardRepository
        );
    }

}
