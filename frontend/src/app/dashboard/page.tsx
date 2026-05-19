import { cookies } from 'next/headers';
import { getAccounts, getExpensesReport, getIncomesReport } from '../../lib/api/api-client';
import { DashboardCharts } from '../../components/dashboard/dashboard-charts';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value || 0);
}

export default async function DashboardPage() {
  const cookieStore = await cookies();
  const token = cookieStore.get('accessToken')?.value;

  const now = new Date();
  const currentMonth = now.getMonth() + 1;
  const currentYear = now.getFullYear();

  let accounts: any[] = [];
  let kpis = { netWorth: 0, monthlyIncome: 0, monthlyExpense: 0 };
  const expensesReport: Record<string, number> = {};
  const incomesReport: Record<string, number> = {};

  try {
    accounts = await getAccounts();

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
    <div className="min-h-screen bg-slate-50/50 p-4 md:p-8 text-slate-900 antialiased">
      <div className="max-w-6xl mx-auto space-y-8">
        <header className="flex flex-col sm:flex-row justify-between sm:items-center bg-white p-6 border border-slate-200/80 rounded-2xl shadow-sm gap-4">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-slate-900">Visão Geral Financeira</h1>
            <p className="text-sm font-medium text-slate-400">Acompanhe seu fluxo de caixa de {currentMonth}/{currentYear}.</p>
          </div>
          <a href="/login" className="px-4 py-2 text-center text-sm font-semibold text-rose-600 bg-rose-50 border border-rose-100 rounded-xl hover:bg-rose-100 transition active:scale-[0.98]">
            Sair do Sistema
          </a>
        </header>

        <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Saldo Atual (Patrimônio)</h3>
            <p className="text-3xl font-bold tracking-tight text-slate-900 mt-1">{formatCurrency(kpis.netWorth)}</p>
          </div>
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Receitas do Mês</h3>
            <p className="text-3xl font-bold tracking-tight text-emerald-600 mt-1">{formatCurrency(kpis.monthlyIncome)}</p>
          </div>
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Despesas do Mês</h3>
            <p className="text-3xl font-bold tracking-tight text-rose-600 mt-1">{formatCurrency(kpis.monthlyExpense)}</p>
          </div>
        </div>

        <DashboardCharts expensesData={expensesReport} incomesData={incomesReport} />
      </div>
    </div>
  );
}