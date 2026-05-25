package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateAccountPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UpdateAccountUseCase implements UpdateAccountPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final GoalRepositoryPort goalRepositoryPort;

    public UpdateAccountUseCase(AccountRepositoryPort accountRepositoryPort, GoalRepositoryPort goalRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.goalRepositoryPort = goalRepositoryPort;
    }

    @Override
    public Account execute(UUID accountId, UUID userId, String name, BigDecimal newBalance, String type) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) throw new RuntimeException("Account not found");
        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("User does not have permission to update this account");
        }

        BigDecimal currentBalance = account.getBalance();
        BigDecimal delta = newBalance.subtract(currentBalance);

        AccountType accountType = (type != null) ? AccountType.valueOf(type.toUpperCase()) : null;

        // Atualiza os dados da conta
        account.update(name, newBalance, accountType);
        Account updatedAccount = accountRepositoryPort.save(account);

        // Se houve alteração no saldo, sincroniza com as metas vinculadas
        if (delta.compareTo(BigDecimal.ZERO) != 0) {
            List<Goal> affectedGoals = goalRepositoryPort.findByAccountId(accountId);
            System.out.println("DEBUG: Para a conta " + accountId + " encontrei " + affectedGoals.size() + " metas.");

            for (Goal goal : affectedGoals) {
                System.out.println("DEBUG: Atualizando meta " + goal.getDescription() + " com delta: " + delta);

                if (delta.compareTo(BigDecimal.ZERO) > 0) {
                    goal.addAmount(delta); // Corrigido: adiciona o delta positivo
                } else {
                    goal.subtractAmount(delta.abs()); // Subtrai o valor absoluto do delta negativo
                }

                goalRepositoryPort.save(goal);
            }
        }

        return updatedAccount;
    }
}