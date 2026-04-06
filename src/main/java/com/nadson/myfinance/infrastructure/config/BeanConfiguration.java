package com.nadson.myfinance.infrastructure.config;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            AccountRepositoryPort accountRepo) {
        return new CreateTransactionUseCase(transactionRepo, accountRepo);
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
    // deixe tudo bem organizado

    @Bean
    public GetAccountBalancePort getAccountBalancePort(AccountRepositoryPort repository) {
        return new GetAccountBalanceUseCase(repository);
    }
}