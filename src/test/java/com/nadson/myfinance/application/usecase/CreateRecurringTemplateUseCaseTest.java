package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRecurringTemplateUseCaseTest {

    @Mock private RecurringTemplateRepositoryPort repository;
    @InjectMocks private CreateRecurringTemplateUseCase useCase;

    @Test
    @DisplayName("Deve salvar template recorrente com sucesso")
    void shouldSaveTemplateSuccessfully() {
        RecurringTemplate template = mock(RecurringTemplate.class);

        useCase.execute(template);

        verify(repository, times(1)).save(template);
    }
}