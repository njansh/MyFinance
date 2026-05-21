'use server';

import { cookies } from 'next/headers';

const API_BASE_URL =
  process.env.API_URL || 'http://localhost:8080';

export interface Account {
  accountId: string;
  name: string;
  balance: number;
  type: string;
}

export interface Category {
  categoryId: string;
  name: string;
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
  isTransfer?: boolean;
  transferID?: string;
}

export interface PaginatedTransactions {
  content: Transaction[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export interface CreditCard {
  id: string;
  name: string;
  limit: number;
  closingDay: number;
  dueDay: number;
  accountId: string;
}

function getUserIdFromToken(token: string): string {
  try {
    const payload = token.split('.')[1];

    const decoded = JSON.parse(
      Buffer.from(payload, 'base64').toString('utf-8')
    );

    return decoded.sub || '';
  } catch {
    throw new Error('UNAUTHORIZED');
  }
}

async function getAuthToken() {
  const cookieStore = await cookies();

  const token = cookieStore.get('accessToken')?.value;

  if (!token) {
    throw new Error('UNAUTHORIZED');
  }

  return token;
}

async function apiFetch(
  endpoint: string,
  options: RequestInit = {}
) {
  const token = await getAuthToken();

  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      ...options,
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...(options.headers || {}),
      },
      cache: 'no-store',
    }
  );

  if (!response.ok) {
    const errorText = await response.text();

    console.error('API ERROR:', errorText);

    throw new Error(
      `HTTP_ERROR_${response.status}: ${errorText}`
    );
  }

  return response;
}

/* =========================
   ACCOUNTS
========================= */

export async function getAccounts(): Promise<Account[]> {
  const token = await getAuthToken();

  const userId = getUserIdFromToken(token);

  const response = await apiFetch(
    `/users/${userId}/accounts`
  );

  return response.json();
}

/* =========================
   CATEGORIES
========================= */

export async function getCategories(): Promise<Category[]> {
  const token = await getAuthToken();

  const userId = getUserIdFromToken(token);

  const response = await apiFetch(
    `/users/${userId}/categories`
  );

  return response.json();
}

/* =========================
   TRANSACTIONS
========================= */

export async function getTransactions(
  accountId: string,
  filters: {
    month?: number;
    year?: number;
    desc?: string;
    page?: number;
    size?: number;
  } = {}
): Promise<PaginatedTransactions> {
  const url = new URL(
    `${API_BASE_URL}/accounts/${accountId}/transactions`
  );

  if (filters.month !== undefined) {
    url.searchParams.append(
      'month',
      String(filters.month)
    );
  }

  if (filters.year !== undefined) {
    url.searchParams.append(
      'year',
      String(filters.year)
    );
  }

  if (filters.desc) {
    url.searchParams.append('desc', filters.desc);
  }

  if (filters.page !== undefined) {
    url.searchParams.append(
      'page',
      String(filters.page)
    );
  }

  if (filters.size !== undefined) {
    url.searchParams.append(
      'size',
      String(filters.size)
    );
  }

  const token = await getAuthToken();

  const response = await fetch(url.toString(), {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    const errorText = await response.text();

    throw new Error(errorText);
  }

  return response.json();
}

/* =========================
   REPORTS
========================= */

export async function getExpensesReport(
  accountId: string,
  month: number,
  year: number
): Promise<Record<string, number>> {
  const response = await apiFetch(
    `/transactions/reports/expenses-by-category/${accountId}?month=${month}&year=${year}`
  );

  return response.json();
}

export async function getIncomesReport(
  accountId: string,
  month: number,
  year: number
): Promise<Record<string, number>> {
  const response = await apiFetch(
    `/transactions/reports/incomes-by-category/${accountId}?month=${month}&year=${year}`
  );

  return response.json();
}

/* =========================
   CREDIT CARDS
========================= */

export async function getCreditCards(): Promise<
  CreditCard[]
> {
  const token = await getAuthToken();

  const userId = getUserIdFromToken(token);

  const response = await apiFetch(
    `/users/${userId}/credit-cards`
  );

  return response.json();
}

/* =========================
   RECURRING
========================= */

export async function getRecurringTemplates() {
  const response = await apiFetch('/recurring');

  return response.json();
}

export async function getPendingTransactions(
  month: number,
  year: number
) {
  const response = await apiFetch(
    `/recurring/pending?month=${month}&year=${year}`
  );

  return response.json();
}