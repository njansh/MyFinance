package com.nadson.myfinance.infrastructure.config;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.application.usecase.*;
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
            AccountRepositoryPort accountRepo,CategoryRepositoryPort categoryRepo) {
        return new CreateTransactionUseCase(transactionRepo, accountRepo,categoryRepo);
    }
    @Bean
    public CreateUserPort createUserUseCase(UserRepositoryPort repository) {
        return new CreateUserUseCase(repository);
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
    public ProcessCreditCardTransactionPort processCreditCardTransactionPort(
            CreditCardRepositoryPort creditCardRepository,
            BillingCycleRepositoryPort billingCycleRepository) {
        return new ProcessCreditCardTransactionUseCase(creditCardRepository, billingCycleRepository);
    }
}
