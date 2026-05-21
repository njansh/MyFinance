'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function getAuthToken() {
  const cookieStore = await cookies();

  const token = cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('UNAUTHORIZED');
  }

  return token;
}

/* =========================
   CREATE
========================= */

export async function createRecurringTemplateAction(data: any) {
  const token = await getAuthToken();

  const response = await fetch(`${API_BASE_URL}/recurring`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();

    throw new Error(
      `Erro ao salvar template (${response.status}): ${err}`
    );
  }

  revalidatePath('/recurring');

  return response.json();
}

/* =========================
   CONFIRM
========================= */

export async function confirmRecurringTransactionAction(
  id: string,
  amount: number
) {
  const token = await getAuthToken();

  const response = await fetch(
    `${API_BASE_URL}/recurring/${id}/confirm`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ amount }),
    }
  );

  if (!response.ok) {
    const err = await response.text();

    throw new Error(err);
  }

  revalidatePath('/dashboard');

  return response.json();
}

/* =========================
   DELETE
========================= */

export async function deleteRecurringTemplateAction(id: string) {
  const token = await getAuthToken();

  const response = await fetch(
    `${API_BASE_URL}/recurring/${id}`,
    {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  if (!response.ok) {
    const err = await response.text();

    throw new Error(
      `Erro ao deletar template (${response.status}): ${err}`
    );
  }

  revalidatePath('/recurring');

  return { success: true };
}