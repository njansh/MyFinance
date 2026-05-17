import { cookies } from 'next/headers';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface Account {
  accountId: string;
  name: string;
  balance: number;
  type: string;
}

export interface Transaction {
  transactionId: string;
  id?: string;
  description: string;
  amount: number;
  date: string;
  type: 'INCOME' | 'EXPENSE';
  accountId: string;
  categoryId: string;
  accountBalanceAfter: number;
}

export interface PaginatedTransactions {
  content: Transaction[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

function getUserIdFromToken(token: string): string {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
    return decoded.sub || '';
  } catch {
    throw new Error('UNAUTHORIZED');
  }
}

export async function getAccounts(): Promise<Account[]> {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('UNAUTHORIZED');
  }

  const userId = getUserIdFromToken(token);
  const targetUrl = `${API_BASE_URL}/users/${userId}/accounts`;

  const response = await fetch(targetUrl, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error(`HTTP_ERROR_${response.status} na URL: ${targetUrl}`);
  }

  return response.json();
}

export async function getTransactions(
  accountId: string,
  filters: { month?: number; year?: number; desc?: string; page?: number; size?: number } = {}
): Promise<PaginatedTransactions> {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('UNAUTHORIZED');
  }

  const url = new URL(`${API_BASE_URL}/accounts/${accountId}/transactions`);

  if (filters.month !== undefined) url.searchParams.append('month', String(filters.month));
  if (filters.year !== undefined) url.searchParams.append('year', String(filters.year));
  if (filters.desc) url.searchParams.append('desc', filters.desc);
  if (filters.page !== undefined) url.searchParams.append('page', String(filters.page));
  if (filters.size !== undefined) url.searchParams.append('size', String(filters.size));

  const response = await fetch(url.toString(), {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
    next: { revalidate: 0 }
  });

  if (!response.ok) {
    throw new Error(`HTTP_ERROR_${response.status} na URL: ${url.toString()}`);
  }

  return response.json();
}