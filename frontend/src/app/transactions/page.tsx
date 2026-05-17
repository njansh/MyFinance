import { Suspense } from 'react';
import { getTransactions, getAccounts } from '@/lib/api-client';
import { TransactionsTable } from '@/components/transactions-table';
import { TransactionsFilter } from '@/components/transactions-filter';

interface PageProps {
  searchParams: Promise<{
    accountId?: string;
    startDate?: string;
    endDate?: string;
    description?: string;
    page?: string;
  }>;
}

export default async function TransactionsPage({ searchParams }: PageProps) {
  const resolvedParams = await searchParams;

  let accounts = [];
  try {
    accounts = await getAccounts();
  } catch {
    return (
      <div className="container mx-auto p-8">
        <div className="p-4 bg-rose-50 border border-rose-100 text-rose-700 rounded-xl text-sm font-medium">
          Falha ao conectar com o serviço de contas.
        </div>
      </div>
    );
  }

  const accountId = resolvedParams.accountId || accounts[0]?.accountId || '';
  const filters = {
    startDate: resolvedParams.startDate,
    endDate: resolvedParams.endDate,
    description: resolvedParams.description,
    page: resolvedParams.page ? Number(resolvedParams.page) : 0,
    size: 10,
  };

  return (
    <div className="container mx-auto p-8 max-w-7xl space-y-8 antialiased text-slate-900">
      <div className="flex flex-col gap-1">
        <h1 className="text-3xl font-bold tracking-tight text-slate-900">Extrato Consolidado</h1>
        <p className="text-sm text-slate-500 font-medium">Gestão e auditoria de lançamentos e fluxos de caixa.</p>
      </div>

      <TransactionsFilter accounts={accounts} />

      <Suspense fallback={<div className="h-96 w-full bg-slate-50 border border-slate-100 animate-pulse rounded-2xl" />}>
        {accountId ? (
          <TransactionsDataWrapper accountId={accountId} filters={filters} />
        ) : (
          <div className="p-12 border border-dashed border-slate-200 rounded-2xl text-center text-sm text-slate-400 font-medium bg-white shadow-sm">
            Nenhuma conta bancária ativa localizada para este perfil.
          </div>
        )}
      </Suspense>
    </div>
  );
}

async function TransactionsDataWrapper({ accountId, filters }: { accountId: string; filters: any }) {
  try {
    const data = await getTransactions(accountId, filters);
    return <TransactionsTable data={data} />;
  } catch {
    return (
      <div className="p-4 bg-rose-50 border border-rose-100 text-rose-700 rounded-xl text-sm font-medium">
        Não foi possível recuperar os lançamentos para a conta selecionada.
      </div>
    );
  }
}