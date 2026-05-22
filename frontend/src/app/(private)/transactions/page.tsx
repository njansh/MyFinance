import {
  getAccounts,
  getCategories,
} from '../../../lib/api/api-server';
import { createTransfer } from '../../../lib/api/api-client';
import { TransactionForm } from './form';
import { revalidatePath } from 'next/cache';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

export default async function TransactionsPage() {
  // 1. Busca os dados iniciais
  const [accounts, categories] = await Promise.all([
    getAccounts(),
    getCategories(),
  ]);

async function handleCreateTransaction(formData: any) {
    'use server';

    const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    const { cookies } = await import('next/headers');
    const cookieStore = await cookies();
    const token = cookieStore.get('accessToken')?.value;

    if (formData.isTransfer) {
      const response = await fetch(`${API_BASE_URL}/transactions/transfer`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          fromId: formData.fromId,
          toId: formData.toId,
          amount: formData.amount,
          date: formData.date,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Falha na transferência (${response.status}): ${errorText}`);
      }
    } else {
      const response = await fetch(`${API_BASE_URL}/transactions`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          description: formData.description,
          amount: formData.amount,
          date: formData.date,
          type: formData.type,
          accountId: formData.accountId,
          categoryId: formData.categoryId,
          isTransfer: false,
          transferID: null,
          accountBalanceAfter: null
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Falha ao salvar transação (${response.status}): ${errorText}`);
      }
    }

    revalidatePath('/extrato');
    revalidatePath('/dashboard');
  }
  // 3. O RETURN da renderização da página (fora da função handleCreateTransaction)
  return (
    <div className="container mx-auto p-8 min-h-screen bg-slate-50/30 flex items-center justify-center">
      <TransactionForm
        accounts={accounts}
        categories={categories}
        onSuccessAction={handleCreateTransaction}
      />
    </div>
  );
}