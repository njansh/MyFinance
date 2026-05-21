import { Suspense } from 'react';
// CORREÇÃO DOS PATHS: Subindo exatamente 3 níveis para encontrar a lib e os components
import { getTransactions, getAccounts } from '../../../lib/api/api-server';
import { TransactionsTable } from '../../../components/transactions/transactions-table';
import { TransactionsFilter } from '../../../components/transactions/transactions-filter';
export const dynamic = 'force-dynamic';
export const revalidate = 0;

interface PageProps {
  searchParams: any;
}

export default async function ExtratoPage({ searchParams }: PageProps) {
  const params = searchParams instanceof Promise ? await searchParams : searchParams;

  let accounts = [];
  try {
    accounts = await getAccounts();
  } catch (error: any) {
    return (
      <div className="container mx-auto p-8 max-w-2xl mt-12 antialiased">
        <div className="p-6 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl space-y-3 shadow-sm">
          <h2 className="text-base font-bold text-rose-900">Falha Geral de Comunicação</h2>
          <p className="text-sm font-medium text-rose-700">O servidor do Next.js não conseguiu completar a chamada para o backend Spring Boot.</p>
          <div className="p-3 bg-rose-950 text-rose-200 font-mono text-xs rounded-xl overflow-x-auto border border-rose-900">
            {error.message || String(error)}
          </div>
        </div>
      </div>
    );
  }

  const accountId = params?.accountId || 'all';
  const now = new Date();

  const filters = {
    month: params?.month ? Number(params.month) : now.getMonth() + 1,
    year: params?.year ? Number(params.year) : now.getFullYear(),
    desc: params?.desc || undefined,
    page: params?.page ? Number(params.page) : 0,
    size: 10,
  };

  return (
    <div className="p-6 md:p-10 max-w-7xl mx-auto space-y-8 antialiased text-slate-900">
      <div className="flex flex-col gap-1">
        <h1 className="text-3xl font-black tracking-tight text-slate-900">Extrato Consolidado</h1>
        <p className="text-sm text-slate-500 font-medium mt-1">Gestão e auditoria de lançamentos e fluxos de caixa.</p>
      </div>

      <TransactionsFilter accounts={accounts} />

      <Suspense fallback={<div className="h-96 w-full bg-slate-50 border border-slate-200 rounded-2xl animate-pulse" />}>
        <TransactionsDataWrapper accountId={accountId} filters={filters} accounts={accounts} />
      </Suspense>
    </div>
  );
}

async function TransactionsDataWrapper({ accountId, filters, accounts }: { accountId: string; filters: any; accounts: any[] }) {
  try {
    if (accountId === 'all') {
      const promises = accounts.map(acc =>
        getTransactions(acc.accountId, filters)
          .catch(() => ({ content: [] }))
      );

      const results = await Promise.all(promises);
      const allTransactions = results.flatMap(r => r.content);

      allTransactions.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

      const normalizedData = {
        content: allTransactions,
        totalPages: 1,
        totalElements: allTransactions.length,
        size: allTransactions.length,
        number: 0
      };

      return <TransactionsTable data={normalizedData} accounts={accounts} isConsolidated={true} />;
    }

    const data = await getTransactions(accountId, filters);
    return <TransactionsTable data={data} accounts={accounts} isConsolidated={false} />;
  } catch (error: any) {
    console.error('=== ERRO CRÍTICO NA BUSCA DE TRANSAÇÕES ===', error);
    return (
      <div className="p-4 bg-rose-50 border border-rose-100 text-rose-700 rounded-xl text-sm font-medium">
        Não foi possível recuperar os lançamentos para a seleção atual.
      </div>
    );
  }
}