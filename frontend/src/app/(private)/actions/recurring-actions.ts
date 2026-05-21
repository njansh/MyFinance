'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  'http://localhost:8080';

export async function createRecurringTemplateAction(
  data: any
) {
  const cookieStore = await cookies();

  const token =
    cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('Usuário não autenticado');
  }

  const response = await fetch(
    `${API_BASE_URL}/recurring`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        accountId: data.accountId,
        categoryId: data.categoryId,
        description: data.description,
        expectedAmount: Number(
          data.expectedAmount
        ),
        type: data.type,
        frequencyDay: Number(
          data.frequencyDay
        ),
      }),
    }
  );

  if (!response.ok) {
    const err = await response.text();

    throw new Error(
      `Erro ao salvar template (${response.status}): ${err}`
    );
  }

  revalidatePath('/recurring');

  return response.json();
}

export async function confirmRecurringTransactionAction(
  transactionId: string,
  amount: number
) {
  const cookieStore = await cookies();

  const token =
    cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('Usuário não autenticado');
  }

  const response = await fetch(
    `${API_BASE_URL}/transactions/${transactionId}/confirm?actualAmount=${amount}`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }
  );

  if (!response.ok) {
    const err = await response.text();

    throw new Error(
      `Falha ao confirmar (${response.status}): ${err}`
    );
  }

  revalidatePath('/dashboard');
  revalidatePath('/recurring');

  return response.json();
}