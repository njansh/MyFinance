'use server';
import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

function getUserIdFromToken(token: string): string {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
    return decoded.sub || '';
  } catch {
    throw new Error('UNAUTHORIZED');
  }
}

export async function createCreditCardAction(data: any) {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) throw new Error('Usuário não autenticado');

  const userId = getUserIdFromToken(token);

  const response = await fetch(`${API_BASE_URL}/users/${userId}/credit-cards`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao salvar cartão: ${err}`);
  }

  revalidatePath('/credit-cards');
  return { success: true };
}
export async function fetchBillingCycleAction(cardId: string, month: number, year: number) {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) throw new Error('Usuário não autenticado');

  const payload = token.split('.')[1];
  const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
  const userId = decoded.sub;

  const response = await fetch(
    `${API_BASE_URL}/users/${userId}/credit-cards/${cardId}/billing-cycles/search?month=${month}&year=${year}`,
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      cache: 'no-store',
    }
  );

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error('Erro ao buscar detalhes da fatura.');
  }

  return response.json();
}
export async function createCreditCardTransactionAction(cardId: string, data: any) {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) throw new Error('Usuário não autenticado');

  const payload = token.split('.')[1];
  const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
  const userId = decoded.sub;

  const response = await fetch(`${API_BASE_URL}/users/${userId}/credit-cards/${cardId}/transactions`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || 'Erro ao registrar compra no cartão.');
  }

  revalidatePath('/credit-cards');
  return { success: true };
}
export async function payBillingCycleAction(cardId: string, cycleId: string, data: { accountId: string, amount: number }) {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) throw new Error('Usuário não autenticado');

  const payload = token.split('.')[1];
  const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
  const userId = decoded.sub;

  const response = await fetch(`${API_BASE_URL}/users/${userId}/credit-cards/${cardId}/billing-cycles/${cycleId}/pay`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || 'Erro ao processar o pagamento da fatura.');
  }

  return { success: true };
}
export async function deleteCreditCardAction(cardId: string) {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;
  if (!token) throw new Error('Usuário não autenticado');

  const payload = token.split('.')[1];
  const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
  const userId = decoded.sub;

  const response = await fetch(`${API_BASE_URL}/users/${userId}/credit-cards/${cardId}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` }
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || 'Erro ao excluir o cartão');
  }

  revalidatePath('/credit-cards');
}