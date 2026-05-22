'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function getAuthToken() {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;
  if (!token) throw new Error('UNAUTHORIZED');
  return token;
}

export async function deleteTransactionAction(transactionId: string) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/transactions/${transactionId}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    throw new Error(`Erro ao eliminar transação: ${response.status}`);
  }

  revalidatePath('/extrato');
  revalidatePath('/dashboard');
  return { success: true };
}

export async function updateTransactionAction(transactionId: string, data: any) {
  const token = await getAuthToken();

  // Usa PUT ou PATCH dependendo de como o teu backend Spring Boot foi desenhado
  const response = await fetch(`${API_BASE_URL}/transactions/${transactionId}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao atualizar transação: ${err}`);
  }

  revalidatePath('/extrato');
  revalidatePath('/dashboard');
  return response.json();
}