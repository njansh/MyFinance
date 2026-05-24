'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function getAuthToken() {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;
  if (!token) throw new Error('Usuário não autenticado');
  return token;
}

function getUserIdFromToken(token: string): string {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
    return decoded.sub || '';
  } catch { return ''; }
}

export async function createAccountAction(prevState: any, formData: FormData) {
  try {
    const token = await getAuthToken();
    const userId = getUserIdFromToken(token);

    const name = formData.get('name');
    const type = formData.get('type');

    const response = await fetch(`${API_BASE_URL}/accounts`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name, type, userId }),
    });

    if (!response.ok) {
      const err = await response.text();
      return { success: false, error: `Erro: ${err}` };
    }

    revalidatePath('/contas');
    return { success: true, error: null };
  } catch (err: any) {
    return { success: false, error: err.message };
  }
}

export async function getAccountsAction() {
  const token = await getAuthToken();
  const userId = getUserIdFromToken(token);
  const response = await fetch(`${API_BASE_URL}/users/${userId}/accounts`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.ok ? response.json() : [];
}

export async function deleteAccountAction(accountId: string) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/accounts/${accountId}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao deletar conta (${response.status}): ${err}`);
  }

  revalidatePath('/contas');
}

// NOVA ACTION: Adicionada de forma isolada apenas para a edição
export async function updateAccountAction(prevState: any, formData: FormData) {
  try {
    const token = await getAuthToken();
    const userId = getUserIdFromToken(token);

    const accountId = formData.get('accountId');
    const name = formData.get('name');
    const type = formData.get('type');
    const balance = formData.get('balance');

    const payload: any = { name, type, userId };

    // Adiciona o saldo ao payload se ele for enviado na edição
    if (balance !== null && balance !== undefined) {
      payload.balance = parseFloat(balance as string);
    }

    const response = await fetch(`${API_BASE_URL}/accounts/${accountId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const err = await response.text();
      return { success: false, error: `Erro: ${err}` };
    }

    revalidatePath('/contas');
    return { success: true, error: null };
  } catch (err: any) {
    return { success: false, error: err.message };
  }
}