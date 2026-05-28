package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionUsecaseTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private GetTransactionUsecase useCase;

    @Test
    @DisplayName("Deve retornar transação por ID com sucesso")
    void shouldGetTransactionById() {
        UUID txId = UUID.randomUUID();
        Transaction tx = mock(Transaction.class);
        when(transactionRepositoryPort.findById(txId)).thenReturn(tx);

        Transaction result = useCase.execute(txId);

        assertThat(result).isEqualTo(tx);
    }

    @Test
    @DisplayName("Deve lançar exceção se transação por ID não existir")
    void shouldFailWhenTransactionNotFound() {
        UUID txId = UUID.randomUUID();
        when(transactionRepositoryPort.findById(txId)).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(txId));
    }

    @Test
    @DisplayName("Deve retornar todas as transações da conta quando descrição é nula")
    void shouldGetAllTransactionsByAccountId() {
        UUID accId = UUID.randomUUID();
        List<Transaction> list = List.of(mock(Transaction.class));
        when(transactionRepositoryPort.findAllByAccountId(accId)).thenReturn(list);

        List<Transaction> result = useCase.execute(accId, null);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve filtrar transações por descrição")
    void shouldFilterTransactionsByDescription() {
        UUID accId = UUID.randomUUID();
        Transaction t1 = mock(Transaction.class);
        when(t1.getDescription()).thenReturn("Conta de Luz");

        when(transactionRepositoryPort.findAllByAccountId(accId)).thenReturn(List.of(t1));

        List<Transaction> result = useCase.execute(accId, "luz");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Conta de Luz");
    }
}