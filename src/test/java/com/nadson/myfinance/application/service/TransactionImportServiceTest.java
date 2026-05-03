package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.GetCategoriesPort;
import com.nadson.myfinance.application.port.in.ListAccountsByUserPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionImportServiceTest {

    @Mock private TransferPort transferPort;
    @Mock private CreateTransactionPort createTransactionPort;
    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private ListAccountsByUserPort listAccountsPort;
    @Mock private GetCategoriesPort getCategoriesPort;
    @Mock private CreateCategoryPort createCategoryPort;
    @Mock private CsvRowMapperStrategy mapperStrategy;

    @Spy
    private List<CsvRowMapperStrategy> mapperStrategies = new ArrayList<>();

    @InjectMocks
    private TransactionImportService importService;

    @BeforeEach
    void setUp() {
        // Adiciona a estratégia mockada na lista de estratégias injetada no serviço
        mapperStrategies.add(mapperStrategy);
    }

    @Test
    void shouldSkipRowWhenTransactionAlreadyExistsInDatabase() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID interAccountId = UUID.randomUUID();

        // O Service exige que estas 3 contas existam, senão lança RuntimeException [1]
        Account interAcc = new Account(interAccountId, userId, AccountType.CHECKING, "Inter", BigDecimal.ZERO);
        Account mpAcc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Mercado Pago", BigDecimal.ZERO);
        Account invAcc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Investimento", BigDecimal.ZERO);

        when(listAccountsPort.execute(userId)).thenReturn(List.of(interAcc, mpAcc, invAcc));
        when(getCategoriesPort.execute(userId)).thenReturn(List.of());
        when(mapperStrategy.getBankCode()).thenReturn("Inter");

        // Simula a extração de dados do CSV pela estratégia
        when(mapperStrategy.extractDescription(any())).thenReturn("Compra Teste");
        when(mapperStrategy.extractAmount(any())).thenReturn(new BigDecimal("-100.00"));
        when(mapperStrategy.extractDate(any())).thenReturn(LocalDateTime.now());

        // Criamos o MockMultipartFile com o header que ativa a leitura (Data)
        String csvContent = "Data;Descricao;Valor\n03/05/2026;Compra Teste;-100,00";
        MockMultipartFile file = new MockMultipartFile("file", "extrato.csv", "text/csv", csvContent.getBytes());

        // Simula que o count no banco de dados retornou 1 (transação já existe)
        when(transactionRepo.count(eq(interAccountId), any(), any(), any(), any())).thenReturn(1L);

        // Act
        importService.importCsv(file, "Inter", userId);

        // Assert
        // Verifica que o createTransactionPort NUNCA foi chamado para evitar duplicidade
        verify(createTransactionPort, never()).execute(any());
    }
}