import { cookies } from 'next/headers';
import {
  getAccounts,
  getExpensesReport,
  getIncomesReport,
  getPendingTransactions
} from '../../../lib/api/api-server';
import { DashboardCharts } from '../../../components/dashboard/dashboard-charts';
import { KpiCards } from '../../../components/dashboard/kpi-cards';
import { DashboardFilter } from '../../../components/dashboard/dashboard-filter';
import { PendingTransactions } from '../../../components/pending/PendingTransactions';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

interface PageProps {
  searchParams: any;
}

export default async function DashboardPage({ searchParams }: PageProps) {
  const params = searchParams instanceof Promise ? await searchParams : searchParams;

  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  const now = new Date();
  const currentMonth = params?.month ? Number(params.month) : now.getMonth() + 1;
  const currentYear = params?.year ? Number(params.year) : now.getFullYear();

  let accounts: any[] = [];
  let pendingTransactions: any[] = [];

  let kpis = {
    netWorth: 0,
    monthlyIncome: 0,
    monthlyExpense: 0,
    lastMonthBalance: 0,
    nextMonthForecast: 0
  };

  const expensesReport: Record<string, number> = {};
  const incomesReport: Record<string, number> = {};

  try {
    accounts = await getAccounts();

    pendingTransactions = await getPendingTransactions(currentMonth, currentYear).catch(() => []);

    const resKpis = await fetch(`http://localhost:8080/api/dashboard/kpis?month=${currentMonth}&year=${currentYear}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      cache: 'no-store'
    });

    if (resKpis.ok) {
      kpis = await resKpis.json();
    }

    if (accounts.length > 0) {
      const expPromises = accounts.map(acc => getExpensesReport(acc.accountId, currentMonth, currentYear).catch(() => ({})));
      const incPromises = accounts.map(acc => getIncomesReport(acc.accountId, currentMonth, currentYear).catch(() => ({})));

      const expResults = await Promise.all(expPromises);
      const incResults = await Promise.all(incPromises);

      expResults.forEach(report => {
        Object.entries(report).forEach(([category, amount]) => {
          expensesReport[category] = (expensesReport[category] || 0) + Number(amount);
        });
      });

      incResults.forEach(report => {
        Object.entries(report).forEach(([category, amount]) => {
          incomesReport[category] = (incomesReport[category] || 0) + Number(amount);
        });
      });
    }
  } catch (error) {
    console.error("Erro ao conectar com a API do Dashboard:", error);
  }

  return (
    <div className="p-6 md:p-10 max-w-6xl mx-auto space-y-8 text-slate-900 antialiased">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black tracking-tight text-slate-900">Visão Geral</h1>
          <p className="text-sm font-medium text-slate-400 mt-1">
            Acompanhe o fluxo de caixa consolidado de {String(currentMonth).padStart(2, '0')}/{currentYear}.
          </p>
        </div>
        <DashboardFilter />
      </div>

      <KpiCards
        netWorth={kpis.netWorth}
        monthlyIncome={kpis.monthlyIncome}
        monthlyExpense={kpis.monthlyExpense}
        lastMonthBalance={kpis.lastMonthBalance}
        nextMonthForecast={kpis.nextMonthForecast}
      />

      <DashboardCharts expensesData={expensesReport} incomesData={incomesReport} />

      {/* Listagem de faturas que precisam de ação do usuário */}
      <PendingTransactions transactions={pendingTransactions} />
    </div>
  );
}