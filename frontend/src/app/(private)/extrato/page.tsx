import { Suspense } from 'react';
import { getTransactions, getAccounts, getCategories } from '../../../lib/api/api-server';
import { TransactionsTable } from '../../../components/transactions/transactions-table';
import { TransactionsFilter } from '../../../components/transactions/transactions-filter';
import Link from 'next/link';
import { ChevronLeft, ChevronRight } from 'lucide-react';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

export default async function ExtratoPage({ searchParams }: { searchParams: any }) {
  const params = searchParams instanceof Promise ? await searchParams : searchParams;

  let accounts = [];
  let categories = [];

  try {
    const [accRes, catRes] = await Promise.all([getAccounts(), getCategories()]);
    accounts = accRes;
    categories = catRes;
  } catch (error: any) {
    return (
      <div className="container mx-auto p-8">
        <p className="text-rose-700">Falha ao comunicar com o servidor.</p>
      </div>
    );
  }

  const accountId = params?.accountId || 'all';
  const now = new Date();

  const currentMonth = params?.month ? Number(params.month) : now.getMonth() + 1;
  const currentYear = params?.year ? Number(params.year) : now.getFullYear();

  const filters = {
    month: currentMonth,
    year: currentYear,
    desc: params?.desc || undefined,
    page: params?.page ? Number(params.page) : 0,
    size: params?.size ? Number(params.size) : 10000,
  };

  const createDateUrl = (month: number, year: number) => {
    let newMonth = month;
    let newYear = year;

    if (newMonth < 1) {
      newMonth = 12;
      newYear -= 1;
    } else if (newMonth > 12) {
      newMonth = 1;
      newYear += 1;
    }

    const p = new URLSearchParams(params);
    p.set('month', String(newMonth));
    p.set('year', String(newYear));
    p.set('page', '0');
    return `?${p.toString()}`;
  };

  const monthName = new Date(currentYear, currentMonth - 1).toLocaleString('pt-BR', { month: 'long' });

  return (
    <div className="p-6 md:p-10 max-w-7xl mx-auto space-y-8 antialiased text-slate-900">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div className="flex flex-col gap-1">
          <h1 className="text-3xl font-black tracking-tight text-slate-900">Extrato Consolidado</h1>
          <p className="text-sm text-slate-500 font-medium mt-1">Gestão e auditoria de lançamentos e fluxos de caixa.</p>
        </div>

        <div className="flex items-center gap-3 bg-white border border-slate-200 p-1.5 rounded-xl shadow-sm">
          <Link
            href={createDateUrl(currentMonth - 1, currentYear)}
            className="p-1.5 hover:bg-slate-100 rounded-lg transition-colors text-slate-600"
          >
            <ChevronLeft className="w-5 h-5" />
          </Link>
          <span className="text-sm font-bold px-2 text-slate-900 capitalize">
            {monthName} {currentYear}
          </span>
          <Link
            href={createDateUrl(currentMonth + 1, currentYear)}
            className="p-1.5 hover:bg-slate-100 rounded-lg transition-colors text-slate-600"
          >
            <ChevronRight className="w-5 h-5" />
          </Link>
        </div>
      </div>

      <TransactionsFilter accounts={accounts} />

      <Suspense fallback={<div className="h-96 w-full bg-slate-50 border border-slate-200 rounded-2xl animate-pulse" />}>
        <TransactionsDataWrapper accountId={accountId} filters={filters} accounts={accounts} categories={categories} />
      </Suspense>
    </div>
  );
}

async function TransactionsDataWrapper({ accountId, filters, accounts, categories }: any) {
  try {
    let data;
    if (accountId === 'all') {
      const promises = accounts.map((acc: any) => getTransactions(acc.accountId, filters).catch(() => ({ content: [] })));
      const results = await Promise.all(promises);
      const allTransactions = results.flatMap((r: any) => r.content);
      allTransactions.sort((a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime());

      data = { content: allTransactions, totalPages: 1, totalElements: allTransactions.length, size: allTransactions.length, number: 0 };
    } else {
      data = await getTransactions(accountId, filters);
    }

    return <TransactionsTable data={data} accounts={accounts} categories={categories} isConsolidated={accountId === 'all'} />;
  } catch (error: any) {
    return <div className="p-4 bg-rose-50 border border-rose-100 text-rose-700 rounded-xl text-sm font-medium">Não foi possível recuperar os lançamentos.</div>;
  }
}