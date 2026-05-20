import { getAccounts, getCategories, createTransfer } from '../../lib/api/api-server';
import { TransactionForm } from './form';

export const dynamic = 'force-dynamic';

export default async function TransactionsPage() {
  const [accounts, categories] = await Promise.all([
    getAccounts(),
    getCategories()
  ]);

  async function handleCreateTransaction(formData: any) {
    'use server';

    if (formData.isTransfer) {
      await createTransfer({
        fromId: formData.fromId,
        toId: formData.toId,
        amount: formData.amount,
        date: formData.date,
      });
    } else {
      const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
      const { cookies } = await import('next/headers');
      const cookieStore = await cookies();
      const token = cookieStore.get('accessToken')?.value;

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
        throw new Error(`FALHA_AO_SALVAR_${response.status}`);
      }
    }
  }

  return (
    <div className="container mx-auto p-8 min-h-screen bg-slate-50/30 flex items-center justify-center">
      <TransactionForm accounts={accounts} categories={categories} onSuccessAction={handleCreateTransaction} />
    </div>
  );
}