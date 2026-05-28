package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.records.BillingCycleDetailsDTO;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;
import com.nadson.myfinance.domain.records.PaymentRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.*;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.*;
import com.nadson.myfinance.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock private CreateUserPort createUserPort;
    @Mock private GetUserPort getUserPort;
    @Mock private GetTotalBalancePort getTotalBalancePort;
    @Mock private ListAccountsByUserPort listAccountsByUserPort;
    @Mock private GetCategoriesPort getCategoriesPort;
    @Mock private JwtService jwtService;
    @Mock private DeleteUserPort deleteUserPort;
    @Mock private ListCreditCardByUserPort listCreditCardByUserPort;
    @Mock private ProcessCreditCardTransactionPort processTransactionPort;
    @Mock private GetBillingCycleDetailsPort getBillingCycleDetailsPort;
    @Mock private CreateCreditCardPort createCreditCardPort;
    @Mock private GetCreditCardPort getCreditCardPort;
    @Mock private GetBillingCycleByDatePort getBillingCycleByDatePort;
    @Mock private BillingProcessPaymentPort billingProcessPaymentPort;
    @Mock private DeleteCreditCardPort deleteCreditCardPort;
    @Mock private UpdateUserUsePort updateUserUsePort;
    @Mock private ChangePasswordPort changePasswordPort;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthenticatedUser(UUID userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null
                )
        );
    }

    @Test
    @DisplayName("Deve criar usuário")
    void shouldCreateUser() {

        UUID id = UUID.randomUUID();

        User user = new User(
                id,
                "Nadson",
                "nadson@gmail.com",
                "123"
        );

        UserRequest request =
                new UserRequest(
                        "Nadson",
                        "nadson@gmail.com",
                        "123"
                );

        when(createUserPort.execute(any(), any(), any()))
                .thenReturn(user);

        ResponseEntity<UserResponse> response =
                controller.create(request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();

        verify(createUserPort)
                .execute(
                        request.name(),
                        request.email(),
                        request.password()
                );
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void shouldGetUserById() {

        UUID id = UUID.randomUUID();

        User user = new User(
                id,
                "Nadson",
                "email@gmail.com",
                "123"
        );

        when(getUserPort.execute(id))
                .thenReturn(user);

        ResponseEntity<UserResponse> response =
                controller.getById(id);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();

        verify(getUserPort).execute(id);
    }

    @Test
    @DisplayName("Deve atualizar perfil")
    void shouldUpdateProfile() {

        UUID userId = UUID.randomUUID();

        mockAuthenticatedUser(userId);

        UpdateUserRequest request =
                new UpdateUserRequest(
                        "Novo Nome",
                        "novo@gmail.com"
                );

        User updated = new User(
                userId,
                "Novo Nome",
                "novo@gmail.com",
                "123"
        );

        when(updateUserUsePort.execute(any(), any(), any()))
                .thenReturn(updated);

        ResponseEntity<UserResponse> response =
                controller.updateProfile(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        verify(updateUserUsePort)
                .execute(
                        userId,
                        request.name(),
                        request.email()
                );
    }

    @Test
    @DisplayName("Deve alterar senha")
    void shouldChangePassword() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "old123",
                        "new123"
                );

        ResponseEntity<Void> response =
                controller.changePassword(request);

        assertThat(response.getStatusCode().value()).isEqualTo(204);

        verify(changePasswordPort)
                .execute("old123", "new123");
    }

    @Test
    @DisplayName("Deve deletar usuário autenticado")
    void shouldDeleteMyUser() {

        UUID userId = UUID.randomUUID();

        mockAuthenticatedUser(userId);

        ResponseEntity<Void> response =
                controller.deleteMyUser();

        assertThat(response.getStatusCode().value()).isEqualTo(204);

        verify(deleteUserPort)
                .execute(userId);
    }

    @Test
    @DisplayName("Deve gerar token")
    void shouldGenerateToken() {

        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "Nadson",
                "email@gmail.com",
                "123"
        );

        when(getUserPort.execute(userId))
                .thenReturn(user);

        when(jwtService.generateToken(any()))
                .thenReturn("token");

        ResponseEntity<String> response =
                controller.generateDevelopmentToken(userId);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("token");

        verify(jwtService)
                .generateToken(userId.toString());
    }

    @Test
    @DisplayName("Deve retornar saldo total")
    void shouldGetTotalBalance() {

        UUID userId = UUID.randomUUID();

        when(getTotalBalancePort.execute(userId))
                .thenReturn(new BigDecimal("5000"));

        ResponseEntity<BigDecimal> response =
                controller.getTotalBalance(userId);

        assertThat(response.getBody())
                .isEqualByComparingTo("5000");
    }

    @Test
    @DisplayName("Deve listar contas")
    void shouldListAccounts() {

        UUID userId = UUID.randomUUID();

        Account account = new Account(
                UUID.randomUUID(),
                userId,
                AccountType.CHECKING,
                "Carteira",
                new BigDecimal("1000")

        );

        when(listAccountsByUserPort.execute(userId))
                .thenReturn(List.of(account));

        ResponseEntity<List<AccountResponse>> response =
                controller.getAccountsByUserId(userId);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar categorias")
    void shouldListCategories() {

        UUID userId = UUID.randomUUID();

        Category category = new Category(
                UUID.randomUUID(),
                userId,
                "Lazer",
                "#000000",
                "Circle",
                com.nadson.myfinance.domain.enums.TransactionType.EXPENSE
        );

        when(getCategoriesPort.execute(userId))
                .thenReturn(List.of(category));

        ResponseEntity<List<CategoryResponse>> response =
                controller.getCategoriesByUserId(userId);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Deve criar cartão")
    void shouldCreateCreditCard() {

        UUID userId = UUID.randomUUID();

        CreditCardRequest request =
                new CreditCardRequest(
                        "Nubank",
                        new BigDecimal("10000"),
                        10,
                        20,
                        UUID.randomUUID()
                );

        ResponseEntity<Void> response =
                controller.createCreditCard(userId, request);

        assertThat(response.getStatusCode().value()).isEqualTo(201);

        verify(createCreditCardPort)
                .execute(
                        eq(userId),
                        eq(request.name()),
                        eq(request.creditLimit()),
                        eq(request.closingDay()),
                        eq(request.dueDay()),
                        eq(request.accountId())
                );
    }

    @Test
    @DisplayName("Deve listar cartões")
    void shouldListCreditCards() {

        UUID userId = UUID.randomUUID();

        CreditCardWithBalanceDTO card = mock(CreditCardWithBalanceDTO.class);

        when(listCreditCardByUserPort.execute(userId))
                .thenReturn(List.of(card));

        ResponseEntity<List<CreditCardResponse>> response =
                controller.creditCardsList(userId);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar cartão por ID")
    void shouldGetCreditCardById() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId=UUID.randomUUID();

        CreditCardWithBalanceDTO cardWithBalanceDTO = new CreditCardWithBalanceDTO(
                cardId,
                accountId,
                userId,
                "Nubank",
                new BigDecimal("2000"),
                new BigDecimal("10000"),
                10,
                20
        );

        when(getCreditCardPort.execute(userId, cardId))
                .thenReturn(cardWithBalanceDTO);

        ResponseEntity<CreditCardResponse> response =
                controller.getCreditCardById(userId, cardId);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        verify(getCreditCardPort).execute(userId, cardId);
    }

    @Test
    @DisplayName("Deve adicionar transação no cartão")
    void shouldAddTransaction() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        CreditCardTransactionRequest request =
                new CreditCardTransactionRequest(
                        UUID.randomUUID(),
                        "Compra",
                        new BigDecimal("100"),
                        LocalDate.now(),
                        1
                );

        ResponseEntity<Void> response =
                controller.addTransaction(
                        userId,
                        cardId,
                        request
                );

        assertThat(response.getStatusCode().value()).isEqualTo(201);

        verify(processTransactionPort)
                .execute(
                        eq(userId),
                        eq(cardId),
                        eq(request.categoryId()),
                        eq(request.description()),
                        eq(request.amount()),
                        eq(request.date()),
                        eq(request.installments())
                );
    }

    @Test
    @DisplayName("Deve buscar billing cycle por data")
    void shouldGetBillingCycleByDate() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        BillingCycleDetailsDTO dto =
                mock(BillingCycleDetailsDTO.class);

        when(getBillingCycleByDatePort.execute(
                userId,
                cardId,
                5,
                2026
        )).thenReturn(dto);

        ResponseEntity<BillingCycleDetailsDTO> response =
                controller.getBillingCycleByDate(
                        userId,
                        cardId,
                        5,
                        2026
                );

        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar detalhes da fatura")
    void shouldGetBillingCycleDetails() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();

        BillingCycleDetailsDTO dto =
                mock(BillingCycleDetailsDTO.class);

        when(getBillingCycleDetailsPort.execute(
                userId,
                cardId,
                cycleId
        )).thenReturn(dto);

        ResponseEntity<BillingCycleDetailsDTO> response =
                controller.getBillingCycleDetails(
                        userId,
                        cardId,
                        cycleId
                );

        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Deve pagar billing cycle")
    void shouldPayBillingCycle() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();

        PaymentRequest request =
                new PaymentRequest(
                        UUID.randomUUID(),
                        new BigDecimal("500")
                );

        ResponseEntity<Void> response =
                controller.payBillingCycle(
                        userId,
                        cardId,
                        cycleId,
                        request
                );

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        verify(billingProcessPaymentPort)
                .BillingProcessPayment(
                        eq(userId),
                        eq(cardId),
                        eq(cycleId),
                        eq(request.accountId()),
                        eq(request.amount())
                );
    }

    @Test
    @DisplayName("Deve deletar cartão quando usuário autenticado for dono")
    void shouldDeleteCreditCard() {

        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        mockAuthenticatedUser(userId);

        ResponseEntity<Void> response =
                controller.deleteCreditCard(userId, cardId);

        assertThat(response.getStatusCode().value()).isEqualTo(204);

        verify(deleteCreditCardPort)
                .execute(cardId, userId);
    }

    @Test
    @DisplayName("Deve retornar 403 ao deletar cartão de outro usuário")
    void shouldReturnForbiddenWhenDeletingCardFromAnotherUser() {

        UUID authenticated = UUID.randomUUID();
        UUID anotherUser = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        mockAuthenticatedUser(authenticated);

        ResponseEntity<Void> response =
                controller.deleteCreditCard(
                        anotherUser,
                        cardId
                );

        assertThat(response.getStatusCode().value()).isEqualTo(403);

        verify(deleteCreditCardPort, never())
                .execute(any(), any());
    }
}