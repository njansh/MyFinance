package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetBillingCycleDetailsPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import com.nadson.myfinance.domain.records.BillingCycleDetailsDTO;
import com.nadson.myfinance.domain.records.InstallmentDTO;

import java.util.List;
import java.util.UUID;

public class GetBillingCycleDetailsUseCase implements GetBillingCycleDetailsPort {

    private final BillingCycleRepositoryPort billingCycleRepository;
    private final CreditCardRepositoryPort creditCardRepository;
    private final CreditCardInstallmentRepositoryPort installmentRepository;
    private final CreditCardPurchaseRepositoryPort purchaseRepository;

    public GetBillingCycleDetailsUseCase(
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardRepositoryPort creditCardRepository,
            CreditCardInstallmentRepositoryPort installmentRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository) {
        this.billingCycleRepository = billingCycleRepository;
        this.creditCardRepository = creditCardRepository;
        this.installmentRepository = installmentRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public BillingCycleDetailsDTO execute(UUID userId, UUID creditCardId, UUID billingCycleId) {
        // 1. Validação de segurança: O cartão pertence ao usuário?
        CreditCard card = creditCardRepository.findById(creditCardId);
        if (card == null || !card.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Cartão não encontrado ou acesso negado");
        }

        // 2. Buscar a Fatura
        BillingCycle cycle = billingCycleRepository.findById(billingCycleId);
        if (cycle == null || !cycle.getCreditCardId().equals(creditCardId)) {
            throw new ResourceNotFoundException("Fatura não encontrada");
        }

        // 3. Buscar as Parcelas desta fatura
        List<CreditCardInstallment> installments = installmentRepository.findByBillingCycleId(billingCycleId);

        // 4. Mapear para o DTO (Buscando o nome da compra original para cada parcela)
        List<InstallmentDTO> items = installments.stream().map(inst -> {
            CreditCardPurchase purchase = purchaseRepository.findById(inst.getPurchaseId());
            
            return new InstallmentDTO(
                    inst.getId(),
                    purchase != null ? purchase.getDescription() : "Compra Desconhecida",
                    inst.getInstallmentNumber(),
                    purchase != null ? purchase.getTotalInstallments() : 0,
                    inst.getAmount(),
                    inst.getStatus()
            );
        }).toList();

        // 5. Retornar a Fatura explodida
        return new BillingCycleDetailsDTO(
                cycle.getId(),
                cycle.getDueDate(),
                cycle.getTotalAmount(),
                items
        );
    }
}