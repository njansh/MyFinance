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