package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.BillingProcessPaymentPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BillingProcessPaymentUseCase implements BillingProcessPaymentPort {

    private final CreditCardInstallmentRepositoryPort installmentRepository;
    private final BillingPaymentRepositoryPort paymentRepository;
    private final AccountRepositoryPort accountRepository;
    private final BillingCycleRepositoryPort billingCycleRepository;

    public BillingProcessPaymentUseCase(CreditCardInstallmentRepositoryPort installmentRepository,
                                        BillingPaymentRepositoryPort paymentRepository,
                                        AccountRepositoryPort accountRepository,
                                        BillingCycleRepositoryPort billingCycleRepository) {
        this.installmentRepository = installmentRepository;
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.billingCycleRepository = billingCycleRepository;
    }

    @Transactional
    @Override
    public void billingProcessPayment(UUID cycleId, UUID accountId, BigDecimal amountToPay) {
        accountRepository.debit(accountId, amountToPay);


        BillingCycle cycle = billingCycleRepository.findById(cycleId);
        cycle.registerPayment(amountToPay);
        billingCycleRepository.save(cycle);

        List<CreditCardInstallment> installments = installmentRepository.findByBillingCycleId(cycleId).stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING || i.getStatus() == InstallmentStatus.PARTIAL)
                .sorted(Comparator.comparing(CreditCardInstallment::getAmount).reversed())
                .toList();

        BigDecimal remaining = amountToPay;

        for (CreditCardInstallment inst : installments) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal paymentForThisOne = remaining.min(inst.getAmount());

            inst.setAmount(inst.getAmount().subtract(paymentForThisOne));

            if (inst.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                inst.setStatus(InstallmentStatus.PAID);
            } else {
                inst.setStatus(InstallmentStatus.PARTIAL);
            }

            installmentRepository.save(inst);
            remaining = remaining.subtract(paymentForThisOne);
        }

        paymentRepository.save(new BillingPayment(UUID.randomUUID(), cycleId, accountId, amountToPay, LocalDateTime.now()));
    }
}