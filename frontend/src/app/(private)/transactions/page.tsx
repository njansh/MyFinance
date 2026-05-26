import {
  getAccounts,
  getCategories,
} from '../../../lib/api/api-server';
import { TransactionForm } from './form';
import { UploadCsvDialog } from '@/components/transactions/upload-csv-dialog';
import { revalidatePath } from 'next/cache';
import { cookies } from 'next/headers';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

export default async function TransactionsPage() {
  // 1. Busca os dados iniciais
  const [accounts, categories] = await Promise.all([
    getAccounts(),
    getCategories(),
  ]);

  // Função para processar o upload do CSV via Server Action
  async function handleImportAction(formData: FormData) {
    'use server';

    const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    const cookieStore = await cookies();
    const token = cookieStore.get('accessToken')?.value;
    const bankCode = formData.get('bankCode');

    const response = await fetch(`${API_BASE_URL}/api/import/${bankCode}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Falha na importação (${response.status}): ${errorText}`);
    }

    revalidatePath('/extrato');
    revalidatePath('/dashboard');
    return { success: true, message: 'Processamento iniciado!' };
  }

  // Função para criar transação manual
  async function handleCreateTransaction(formData: any) {
    'use server';

    const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
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
          description: formData.description,
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

  return (
    <div className="container mx-auto p-8 min-h-screen bg-slate-50/30">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold">Transações</h1>
        {/* Botão de Importação CSV integrado */}
        <UploadCsvDialog onUpload={handleImportAction} />
      </div>

      <TransactionForm
        accounts={accounts}
        categories={categories}
        onSuccessAction={handleCreateTransaction}
      />
    </div>
  );
}