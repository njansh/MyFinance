import { cookies } from 'next/headers';

export interface Transaction {
  transactionId: string;
  description: string;
  amount: number;
  date: string;
  type: 'INCOME' | 'EXPENSE';
}

export async function getTransactions(userId: string, month: number, year: number): Promise<Transaction> {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users/${userId}/transactions?month=${month}&year=${year}`, {
    headers: {
      Cookie: `accessToken=${token}`,
    },
  });

  if (!response.ok) {
    throw new Error('Falha ao buscar transações');
  }

  return response.json();
}