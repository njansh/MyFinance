package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteCreditCardPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import jakarta.transaction.Transactional;

import java.util.UUID;

public class DeleteCreditCardUseCase implements DeleteCreditCardPort {
    private final CreditCardRepositoryPort creditCardRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public DeleteCreditCardUseCase(CreditCardRepositoryPort creditCardRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.creditCardRepositoryPort = creditCardRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    public void execute(UUID creditCardId) {
        CreditCard creditCard = creditCardRepositoryPort.findById(creditCardId);
        if (creditCard == null) {
            throw new BusinessRuleException("Credit card not found.");
        }

        User user = userRepositoryPort.findById(creditCard.getUserId());
        if (user == null) {
            throw new BusinessRuleException("User not found.");
        }

        creditCardRepositoryPort.deleteByID(creditCardId);
    }
}
