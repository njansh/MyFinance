package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransactionImportServiceTest {

    @Mock private CategorizationEngine categorizationEngine;
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

    private final UUID userId = UUID.randomUUID();
    private final UUID interId = UUID.randomUUID();
    private final UUID mpId = UUID.randomUUID();
    private final UUID invId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mapperStrategies.add(mapperStrategy);
        lenient().when(mapperStrategy.getBankCode()).thenReturn("MP");

        Account inter = new Account(interId, userId, AccountType.CHECKING, "Inter", BigDecimal.ZERO);
        Account mp = new Account(mpId, userId, AccountType.CHECKING, "Mercado Pago", BigDecimal.ZERO);
        Account inv = new Account(invId, userId, AccountType.CHECKING, "Investimento", BigDecimal.ZERO);

        lenient().when(listAccountsPort.execute(userId)).thenReturn(List.of(inter, mp, inv));
        lenient().when(getCategoriesPort.execute(userId)).thenReturn(List.of());

        Category cat = new Category(UUID.randomUUID(), userId, "Geral", "#FFFFFF", TransactionType.INCOME);
        lenient().when(createCategoryPort.execute(any(), any(), any(), any())).thenReturn(cat);
    }

    // Testa o processamento de um extrato real do Mercado Pago com transações comuns, transferências e investimentos
    @Test
    void shouldProcessRealWorldStatementSuccessfully() throws Exception {
        String csvContent = "RELEASE_DATE;TRANSACTION_TYPE;REFERENCE_ID;TRANSACTION_NET_AMOUNT;PARTIAL_BALANCE\n" +
                "01-12-2025;Rendimentos ;123;0,06;74,25\n" +
                "04-12-2025;Transferência Pix recebida NADSON JHONY ALVES NASCIMENTO SANTOS;456;791,10;821,85\n" +
                "11-12-2025;Reserva programada Moto;789;-200,00;4.223,48\n" +
                "26-12-2025;Pagamento com QR Pix ATACADAO S.A.;101;-688,90;111,34";

        MockMultipartFile file = new MockMultipartFile("file", "statement.csv", "text/csv", csvContent.getBytes());

        when(mapperStrategy.extractDate(any())).thenReturn(LocalDateTime.of(2025, 12, 1, 10, 0));

        when(mapperStrategy.extractAmount(any())).thenReturn(
                new BigDecimal("0.06"),
                new BigDecimal("791.10"),
                new BigDecimal("-200.00"),
                new BigDecimal("-688.90")
        );

        when(mapperStrategy.extractDescription(any())).thenReturn(
                "Rendimentos",
                "Transferência Pix recebida NADSON JHONY ALVES NASCIMENTO SANTOS",
                "Reserva programada Moto",
                "Pagamento com QR Pix ATACADAO S.A."
        );

        when(transactionRepo.count(any(), any(), any(), any(), any())).thenReturn(0L);

        lenient().when(transactionRepo.findFirstUnmatchedTransaction(any(), any(), any(), any(), any())).thenReturn(null);

        when(categorizationEngine.process(anyString())).thenReturn("Geral");

        importService.importCsv(file, "MP", userId);

        verify(createTransactionPort, times(2)).execute(any(Transaction.class));
        verify(transferPort, times(1)).execute(eq(interId), eq(mpId), any(), any(), any(), any(), any());
        verify(transferPort, times(1)).execute(eq(mpId), eq(invId), any(), any(), any(), any(), any());
    }
    // Testa o processamento de um extrato do banco Inter, validando transferências automáticas e transações comuns
    @Test
    void shouldProcessInterBankStatementSuccessfully() throws Exception {
        String csvContent = "Conta ;74628909\n" +
                "Período ;01/01/2026 a 31/01/2026\n" +
                "Saldo ;0,00\n" +
                "\n" +
                "Data Lançamento;Histórico;Descrição;Valor;Saldo\n" +
                "23/01/2026;Pix enviado ;Nadson Jhony Alves Nascimento Santos;-80,00;0,00\n" +
                "17/01/2026;Pix recebido;Walison Sousa De Sa;80,00;80,00\n" +
                "11/01/2026;Pix enviado ;Maria Z Da Silva Medicamentos;-7,99;-240,00\n" +
                "10/01/2026;Pagamento efetuado;Fatura cartão Inter;-81,21;0,00";

        MockMultipartFile file = new MockMultipartFile("file", "extrato_inter.csv", "text/csv", csvContent.getBytes());

        when(mapperStrategy.getBankCode()).thenReturn("INTER");

        when(mapperStrategy.extractDate(any())).thenReturn(LocalDateTime.of(2026, 1, 10, 0, 0));

        when(mapperStrategy.extractAmount(any())).thenReturn(
                new BigDecimal("-80.00"),
                new BigDecimal("80.00"),
                new BigDecimal("-7.99"),
                new BigDecimal("-81.21")
        );

        when(mapperStrategy.extractDescription(any())).thenReturn(
                "Nadson Jhony Alves Nascimento Santos",
                "Walison Sousa De Sa",
                "Maria Z Da Silva Medicamentos",
                "Fatura cartão Inter"
        );

        when(transactionRepo.count(any(), any(), any(), any(), any())).thenReturn(0L);
        when(categorizationEngine.process(anyString())).thenReturn("Geral");

        importService.importCsv(file, "INTER", userId);

        verify(transferPort, times(1)).execute(eq(interId), eq(mpId), eq(new BigDecimal("80.00")), any(), any(), any(), any());
        verify(createTransactionPort, times(3)).execute(any(Transaction.class));
    }
    // Testa se o sistema ignora transações duplicadas e reutiliza categorias já criadas
    @Test
    void shouldIgnoreDuplicateTransactionsAndUseCategoryCache() throws Exception {
        String csvContent = "RELEASE_DATE;TRANSACTION_TYPE;REFERENCE_ID;TRANSACTION_NET_AMOUNT;PARTIAL_BALANCE\n" +
                "01-12-2025;Compra;1; -10.00; 100.00\n" +
                "02-12-2025;Compra;2; -20.00; 80.00\n" +
                "03-12-2025;Compra;3; -20.00; 60.00";

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        when(mapperStrategy.getBankCode()).thenReturn("MP");
        when(mapperStrategy.extractDate(any())).thenReturn(LocalDateTime.now());

        when(mapperStrategy.extractAmount(any())).thenReturn(
                new BigDecimal("-10.00"), new BigDecimal("-20.00"), new BigDecimal("-20.00")
        );
        when(mapperStrategy.extractDescription(any())).thenReturn("LOJA TESTE", "LOJA TESTE", "LOJA TESTE");

        when(transactionRepo.count(eq(mpId), any(), eq(new BigDecimal("10.00")), eq("LOJA TESTE"), any()))
                .thenReturn(1L);

        when(transactionRepo.count(eq(mpId), any(), eq(new BigDecimal("20.00")), eq("LOJA TESTE"), any()))
                .thenReturn(0L);

        when(categorizationEngine.process("LOJA TESTE")).thenReturn("Compras");

        importService.importCsv(file, "MP", userId);

        verify(createTransactionPort, times(2)).execute(any());
        verify(createCategoryPort, times(1)).execute(eq(userId), eq("Compras"), anyString(), any());
    }
    // Testa se o sistema lança uma exceção quando uma das contas obrigatórias (Inter, Mercado Pago ou Investimento) não está cadastrada
    @Test
    void shouldThrowExceptionWhenRequiredAccountIsMissing() {
        Account inter = new Account(interId, userId, AccountType.CHECKING, "Inter", BigDecimal.ZERO);

        when(listAccountsPort.execute(userId)).thenReturn(List.of(inter));

        String csvContent = "RELEASE_DATE;TRANSACTION_NET_AMOUNT\n01-01-2026;10.00";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> importService.importCsv(file, "MP", userId)
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                exception.getMessage().contains("Você precisa criar uma conta chamada 'Mercado Pago'")
        );

        verifyNoInteractions(createTransactionPort);
    }
}