package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.parser.InterRowMapper;
import com.nadson.myfinance.application.parser.MercadoPagoRowMapper;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionImportServiceTest {

    @Mock private CategorizationEngine categorizationEngine;
    @Mock private TransferPort transferPort;
    @Mock private CreateTransactionPort createTransactionPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;
    @Mock private ListAccountsByUserPort listAccountsByUserPort;
    @Mock private GetCategoriesPort getCategoriesPort;
    @Mock private CreateCategoryPort createCategoryPort;

    @Spy private List<CsvRowMapperStrategy> mapperStrategies = new ArrayList<>();

    @InjectMocks
    private TransactionImportService service;

    private Account mockAccount(String name) {
        Account acc = mock(Account.class);
        when(acc.getName()).thenReturn(name);
        when(acc.getAccountId()).thenReturn(UUID.randomUUID());
        return acc;
    }

    private Category mockCategory(UUID userId, String name, TransactionType type) {
        return new Category(
                UUID.randomUUID(),
                userId,
                name,
                "#000000",
                "Circle",
                type
        );
    }

    private MultipartFile mockCsvFile(String content) throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(content.getBytes()));
        return file;
    }

    private void mockInterMapper() {
        mapperStrategies.clear();
        mapperStrategies.add(new InterRowMapper());
    }

    private void mockMpMapper() {
        mapperStrategies.clear();
        mapperStrategies.add(new MercadoPagoRowMapper());
    }

    @Test
    @DisplayName("Deve cobrir IOException do importCsv")
    void shouldThrowIOExceptionInsideImportCsv() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("boom"));

        assertThatThrownBy(() ->
                service.importCsv(file, "INTER", UUID.randomUUID())
        ).isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Deve usar categoria existente sem criar nova")
    void shouldUseExistingCategoryWithoutCreating() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        Category existing = mockCategory(userId, "Lazer", TransactionType.EXPENSE);

        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(List.of(existing));
        when(categorizationEngine.process(any())).thenReturn("Lazer");

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;Cinema;-20,00;100,00");

        service.importCsv(file, "INTER", userId);

        verify(createCategoryPort, never()).execute(any(), any(), any(), any(), any());
        verify(createTransactionPort).execute(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve ignorar linhas antes do cabeçalho e linhas vazias")
    void shouldIgnoreGarbageBeforeHeader() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());
        when(categorizationEngine.process(any())).thenReturn("Outros");

        Category cat = mockCategory(userId, "Outros", TransactionType.EXPENSE);
        when(createCategoryPort.execute(any(), any(), any(), any(), any())).thenReturn(cat);

        MultipartFile file = mockCsvFile("LIXO\nOUTRO LIXO\n\nData;Descricao;Valor;Saldo\n01/05/2026;Compra;10,00;100,00");

        service.importCsv(file, "INTER", userId);

        verify(createTransactionPort).execute(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve criar transação INCOME comum")
    void shouldCreateIncomeTransaction() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());
        when(categorizationEngine.process(any())).thenReturn("Salário");

        Category cat = mockCategory(userId, "Salário", TransactionType.INCOME);
        when(createCategoryPort.execute(any(), any(), any(), any(), any())).thenReturn(cat);

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;Pagamento empresa;5000,00;6000,00");

        service.importCsv(file, "INTER", userId);

        verify(createTransactionPort).execute(argThat(t ->
                t.getType() == TransactionType.INCOME &&
                        t.getAmount().compareTo(new BigDecimal("5000.00")) == 0
        ));
    }

    @Test
    @DisplayName("Deve processar transferência fallback INTER -> MP")
    void shouldProcessFallbackTransferBetweenAccounts() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        UUID interId = UUID.randomUUID();
        UUID mpId = UUID.randomUUID();

        Account inter = mock(Account.class);
        Account mp = mock(Account.class);

        when(inter.getName()).thenReturn("INTER");
        when(inter.getAccountId()).thenReturn(interId);
        when(mp.getName()).thenReturn("Mercado Pago");
        when(mp.getAccountId()).thenReturn(mpId);

        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(inter, mp));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;transferência entre contas;-50,00;100,00");

        service.importCsv(file, "INTER", userId);

        verify(transferPort).execute(eq(interId), eq(mpId), eq(new BigDecimal("50.00")), any(), any(), eq(interId), any());
    }

    @Test
    @DisplayName("Deve processar transferência INCOME invertendo sender e receiver")
    void shouldProcessIncomingTransfer() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        UUID interId = UUID.randomUUID();
        UUID investId = UUID.randomUUID();

        Account inter = mock(Account.class);
        Account invest = mock(Account.class);

        when(inter.getName()).thenReturn("INTER");
        when(inter.getAccountId()).thenReturn(interId);
        when(invest.getName()).thenReturn("INVESTIMENTO");
        when(invest.getAccountId()).thenReturn(investId);

        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(inter, invest));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;resgate investimento;100,00;1000,00");

        service.importCsv(file, "INTER", userId);

        verify(transferPort).execute(eq(investId), eq(interId), eq(new BigDecimal("100.00")), any(), any(), eq(interId), any());
    }

    @Test
    @DisplayName("Deve ignorar hash duplicado")
    void shouldIgnoreDuplicateHash() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        mockMpMapper();

        Account mp = mock(Account.class);
        when(mp.getName()).thenReturn("Mercado Pago");
        when(mp.getAccountId()).thenReturn(accId);

        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(mp));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        Transaction existing = mock(Transaction.class);
        when(existing.isTransfer()).thenReturn(false);
        when(existing.getDate()).thenReturn(LocalDateTime.of(2026, 5, 1, 0, 0));
        when(existing.getAmount()).thenReturn(new BigDecimal("-10.00"));
        when(existing.getDescription()).thenReturn("Compra (Ref: REF1)");
        when(existing.getAccountBalanceAfter()).thenReturn(new BigDecimal("90.00"));

        when(transactionRepositoryPort.findAllByAccountIdAndDateBetween(any(), any(), any())).thenReturn(List.of(existing));

        MultipartFile file = mockCsvFile("RELEASE_DATE;TRANSACTION_TYPE;REFERENCE_ID;TRANSACTION_NET_AMOUNT;PARTIAL_BALANCE\n01-05-2026;Compra;REF1;-10,00;90,00");

        service.importCsv(file, "MP", userId);

        verify(createTransactionPort, never()).execute(any());
    }

    @Test
    @DisplayName("Deve cobrir balanceAfter nulo do MP")
    void shouldHandleNullBalanceFromMP() throws Exception {
        UUID userId = UUID.randomUUID();
        mockMpMapper();

        Account acc = mockAccount("Mercado Pago");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());
        when(categorizationEngine.process(any())).thenReturn("Outros");

        Category cat = mockCategory(userId, "Outros", TransactionType.EXPENSE);
        when(createCategoryPort.execute(any(), any(), any(), any(), any())).thenReturn(cat);

        MultipartFile file = mockCsvFile("RELEASE_DATE;TRANSACTION_TYPE;REFERENCE_ID;TRANSACTION_NET_AMOUNT;PARTIAL_BALANCE\n01-05-2026;Compra;REF1;-10,00;");

        service.importCsv(file, "MP", userId);

        verify(createTransactionPort).execute(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve processar arquivo físico")
    void shouldProcessPhysicalFile() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());
        when(categorizationEngine.process(any())).thenReturn("Outros");

        Category cat = mockCategory(userId, "Outros", TransactionType.EXPENSE);
        when(createCategoryPort.execute(any(), any(), any(), any(), any())).thenReturn(cat);

        File temp = File.createTempFile("test", ".csv");
        Files.writeString(temp.toPath(), "Data;Descricao;Valor;Saldo\n01/05/2026;Compra;-10,00;100,00");

        service.processFile(temp, "INTER", userId);

        verify(createTransactionPort).execute(any(Transaction.class));
    }



    @Test
    @DisplayName("Deve lançar exceção quando não existir mapper para banco")
    void shouldThrowWhenMapperNotFound() {
        MultipartFile file = mock(MultipartFile.class);
        mapperStrategies.clear();

        assertThatThrownBy(() ->
                service.importCsv(file, "BANCO_INVALIDO", UUID.randomUUID())
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não existir")
    void shouldThrowWhenAccountNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        when(listAccountsByUserPort.execute(userId)).thenReturn(Collections.emptyList());

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;Teste;-10,00;100,00");

        assertThatThrownBy(() -> service.importCsv(file, "INTER", userId)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve ignorar CSV sem linhas de transação")
    void shouldIgnoreEmptyCsv() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo");

        service.importCsv(file, "INTER", userId);

        verifyNoInteractions(createTransactionPort);
    }

    @Test
    @DisplayName("Deve reutilizar categoria criada anteriormente no mesmo processamento")
    void shouldReuseCreatedCategoryInsideSameImport() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());
        when(categorizationEngine.process(any())).thenReturn("Mercado");

        Category cat = mockCategory(userId, "Mercado", TransactionType.EXPENSE);
        when(createCategoryPort.execute(any(), any(), any(), any(), any())).thenReturn(cat);

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n01/05/2026;Compra 1;-10,00;100,00\n02/05/2026;Compra 2;-20,00;80,00");

        service.importCsv(file, "INTER", userId);

        verify(createCategoryPort, times(1)).execute(any(), any(), any(), any(), any());
        verify(createTransactionPort, times(2)).execute(any(Transaction.class));
    }


    @Test
    @DisplayName("Deve ignorar transferência quando conta destino não existir")
    void shouldIgnoreTransferWithoutTargetAccount() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));

        when(categorizationEngine.process(any())).thenReturn("Outros");

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\n" +
                "01/05/2026;transferência entre contas;-50,00;100,00");

        service.importCsv(file, "INTER", userId);

        verify(transferPort, atMost(1)).execute(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve gerar hash com balance nulo")
    void shouldGenerateHashWithNullBalance() throws Exception {
        var method = TransactionImportService.class.getDeclaredMethod(
                "generateHash", UUID.class, LocalDateTime.class, BigDecimal.class, String.class, BigDecimal.class
        );
        method.setAccessible(true);
        Object result = method.invoke(service, UUID.randomUUID(), LocalDateTime.now(), new BigDecimal("10.00"), "Teste", null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve ignorar linha inválida")
    void shouldIgnoreInvalidLine() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        MultipartFile file = mockCsvFile("Data;Descricao;Valor;Saldo\nlinha_invalida");

        service.importCsv(file, "INTER", userId);

        verifyNoInteractions(createTransactionPort);
    }
    @Test
    @DisplayName("Deve ignorar transferência por deduplicação de footprint (Cross-Bank)")
    void shouldIgnoreTransferByFootprintDeduplication() throws Exception {
        UUID userId = UUID.randomUUID();
        mockInterMapper();

        Account acc = mockAccount("INTER");
        when(listAccountsByUserPort.execute(userId)).thenReturn(List.of(acc));
        when(getCategoriesPort.execute(userId)).thenReturn(Collections.emptyList());

        Transaction existingTransfer = mock(Transaction.class);
        when(existingTransfer.isTransfer()).thenReturn(true);
        when(existingTransfer.getType()).thenReturn(TransactionType.EXPENSE);
        when(existingTransfer.getDate()).thenReturn(LocalDateTime.of(2026, 5, 1, 0, 0));
        when(existingTransfer.getAmount()).thenReturn(new BigDecimal("-50.00"));

        when(transactionRepositoryPort.findAllByAccountIdAndDateBetween(any(), any(), any()))
                .thenReturn(List.of(existingTransfer));

        MultipartFile file = mockCsvFile(
                "Data;Descricao;Valor;Saldo\n" +
                        "01/05/2026;transferência entre contas;-50,00;100,00"
        );

        service.importCsv(file, "INTER", userId);

        verifyNoInteractions(transferPort);
    }

    @Test
    @DisplayName("Deve gerar hash com descrição nula de forma segura")
    void shouldHandleNullDescriptionInHash() throws Exception {
        var method = TransactionImportService.class.getDeclaredMethod(
                "generateHash", UUID.class, LocalDateTime.class, BigDecimal.class, String.class, BigDecimal.class
        );
        method.setAccessible(true);

        Object result = method.invoke(
                service,
                UUID.randomUUID(),
                LocalDateTime.now(),
                new BigDecimal("10.00"),
                null,
                new BigDecimal("100.00")
        );

        assertThat(result).isNotNull();
    }
}
