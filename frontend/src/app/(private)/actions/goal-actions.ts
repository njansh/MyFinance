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

export async function createGoalAction(data: { description: string; targetAmount: number; accountIds: string[] }) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/goals`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao criar meta: ${err}`);
  }

  revalidatePath('/goals');
  revalidatePath('/dashboard');
  return response.json();
}

export async function updateGoalAction(goalId: string, data: { description: string; targetAmount: number; accountIds: string[] }) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/goals/${goalId}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao atualizar meta: ${err}`);
  }

  revalidatePath('/goals');
  revalidatePath('/dashboard');
  return response.json();
}

export async function deleteGoalAction(goalId: string) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/goals/${goalId}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro ao excluir meta: ${err}`);
  }

  revalidatePath('/goals');
  revalidatePath('/dashboard');
  return { success: true };
}