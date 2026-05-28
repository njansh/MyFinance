package com.nadson.myfinance.application.usecase;

import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
class GetBudgetUseCaseTest {
    @Mock
    private BudgetRepositoryPort repository;
    @InjectMocks private GetBudgetUseCase useCase;

    @Test
    @DisplayName("Deve retornar orçamento se encontrado")
    void shouldReturnBudget() {
        UUID id = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(repository.findById(id)).thenReturn(Optional.of(budget));

        var result = useCase.execute(id);
        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("Deve lançar exceção se orçamento não existir")
    void shouldFailIfNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(UUID.randomUUID()));
    }
}
