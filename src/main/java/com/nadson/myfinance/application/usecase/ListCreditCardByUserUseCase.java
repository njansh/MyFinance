package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListCreditCardByUserPort;
import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ListCreditCardByUserUseCase implements ListCreditCardByUserPort {
    private final CreditCardRepositoryPort repositoryPort;
    private final UserRepositoryPort userRepository;
    private final BillingCycleRepositoryPort billingCycleRepository;

    public ListCreditCardByUserUseCase(CreditCardRepositoryPort repositoryPort,
                                       UserRepositoryPort userRepository,
                                       BillingCycleRepositoryPort billingCycleRepository) {
        this.repositoryPort = repositoryPort;
        this.userRepository = userRepository;
        this.billingCycleRepository = billingCycleRepository;
    }

    @Override
    public List<CreditCardWithBalanceDTO> execute(UUID userId) {
        validate(userId);

        List<CreditCard> cards = repositoryPort.findByUserId(userId);

        return cards.stream().map(card -> {
            List<BillingCycle> unpaidCycles = billingCycleRepository.findUnpaidCyclesByCardId(card.getId());

            BigDecimal usedLimit = unpaidCycles.stream()
                    .map(BillingCycle::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal availableLimit = card.getCreditLimit().subtract(usedLimit);

            return new CreditCardWithBalanceDTO(
                    card.getId(),
                    card.getAccountId(),
                    card.getUserId(),
                    card.getName(),
                    card.getCreditLimit(),
                    availableLimit
            );
        }).toList();
    }

    void validate(UUID userId) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }
    }
}