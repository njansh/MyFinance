package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetCreditCardPort;
import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.exception.CreditCardNotFoundException;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GetCreditCardUseCase implements GetCreditCardPort {
    private final CreditCardRepositoryPort repositoryPort;
    private final BillingCycleRepositoryPort billingCycleRepository;

    public GetCreditCardUseCase(CreditCardRepositoryPort repositoryPort,
                                BillingCycleRepositoryPort billingCycleRepository) {
        this.repositoryPort = repositoryPort;
        this.billingCycleRepository = billingCycleRepository;
    }

    @Override
    public CreditCardWithBalanceDTO execute(UUID userId, UUID id) {
        CreditCard creditCard = repositoryPort.findById(id);

        if (creditCard == null || !creditCard.getUserId().equals(userId)) {
            throw new CreditCardNotFoundException(id);
        }

        List<BillingCycle> unpaidCycles = billingCycleRepository.findUnpaidCyclesByCardId(id);

        BigDecimal usedLimit = unpaidCycles.stream()
                .map(BillingCycle::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableLimit = creditCard.getCreditLimit().subtract(usedLimit);

        return new CreditCardWithBalanceDTO(
                creditCard.getId(),
                creditCard.getAccountId(),
                creditCard.getUserId(),
                creditCard.getName(),
                creditCard.getCreditLimit(),
                availableLimit
        );
    }
}
