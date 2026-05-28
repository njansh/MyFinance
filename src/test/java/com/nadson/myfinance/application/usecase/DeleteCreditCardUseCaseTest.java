package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteCreditCardUseCaseTest {

    @Mock private CreditCardRepositoryPort creditCardRepo;
    @Mock private UserRepositoryPort userRepo;
    @InjectMocks private DeleteCreditCardUseCase useCase;

    @Test
    @DisplayName("Deve deletar cartão com sucesso")
    void shouldDeleteCreditCardSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        User user = mock(User.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepo.findById(cardId)).thenReturn(card);
        when(userRepo.findById(userId)).thenReturn(user);

        useCase.execute(cardId, userId);

        verify(creditCardRepo).deleteByID(cardId);
    }

    @Test
    @DisplayName("Deve falhar se o cartão não existir")
    void shouldFailWhenCardNotFound() {
        when(creditCardRepo.findById(any())).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve falhar se o usuário não existir")
    void shouldFailWhenUserNotFound() {
        when(creditCardRepo.findById(any())).thenReturn(mock(CreditCard.class));
        when(userRepo.findById(any())).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for dono do cartão")
    void shouldFailWhenUserIsNotOwner() {
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        CreditCard card = mock(CreditCard.class);
        when(card.getUserId()).thenReturn(ownerId);

        when(creditCardRepo.findById(cardId)).thenReturn(card);
        when(userRepo.findById(intruderId)).thenReturn(mock(User.class));

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(cardId, intruderId));
    }
}