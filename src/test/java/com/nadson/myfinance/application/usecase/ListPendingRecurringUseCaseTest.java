package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPendingRecurringUseCaseTest {

    @Mock
    private UserRepositoryPort userRepo;

    @Mock
    private RecurringTemplateRepositoryPort recurringRepo;

    @InjectMocks
    private ListPendingRecurringUseCase useCase;

    @Test
    void shouldReturnPendingTemplatesForValidUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User fakeUser = new User(userId, "Nadson", "nadson@test.com");

        RecurringTemplate template = new RecurringTemplate(
                UUID.randomUUID(), userId, UUID.randomUUID(), UUID.randomUUID(),
                "Assinatura Netflix", new BigDecimal("45.90"), TransactionType.EXPENSE, 10, true
        );

        when(userRepo.findById(userId)).thenReturn(fakeUser);

        // Simula que o repositório encontrou 1 template pendente
        when(recurringRepo.findPendingTemplates(eq(userId), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(template));

        // Act
        List<RecurringTemplate> pendingList = useCase.execute(userId);

        // Assert
        assertEquals(1, pendingList.size(), "Deveria retornar 1 pendência");
        assertEquals("Assinatura Netflix", pendingList.get(0).getDescription(), "A descrição deve corresponder ao mock");
    }
}