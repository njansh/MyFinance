'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function getAuthToken() {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('UNAUTHORIZED');
  }

  return token;
}

/* =========================
   ATUALIZAR LIMITE (PATCH)
========================= */
export async function updateBudgetLimitAction(id: string, newLimit: number) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/budgets/${id}/limit`, {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(newLimit),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao atualizar orçamento (${response.status}): ${err}`);
  }

  // Atualiza o cache do Next.js para refletir as mudanças nas páginas em tempo real
  revalidatePath('/dashboard');
  revalidatePath('/budgets');

  return response.json();
}

/* =========================
   CRIAR ORÇAMENTO (POST)
========================= */
export async function createBudgetAction(data: {
  categoryId: string;
  month: number;
  year: number;
  limitAmount: number;
}) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/budgets`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao criar orçamento (${response.status}): ${err}`);
  }

  revalidatePath('/dashboard');
  revalidatePath('/budgets');

  return response.json();
}

/* =========================
   ELIMINAR ORÇAMENTO (DELETE)
========================= */
export async function deleteBudgetAction(id: string) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/budgets/${id}`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao eliminar orçamento (${response.status}): ${err}`);
  }

  revalidatePath('/dashboard');
  revalidatePath('/budgets');

  return { success: true };
}