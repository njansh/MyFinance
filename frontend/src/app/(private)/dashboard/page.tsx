import { cookies } from 'next/headers';
// CORREÇÃO DOS PATHS: Subindo três níveis (../../../) para encontrar as pastas corretas
import { getAccounts, getExpensesReport, getIncomesReport } from '../../../lib/api/api-server';
import { DashboardCharts } from '../../../components/dashboard/dashboard-charts';

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
    <div className="p-6 md:p-10 max-w-6xl mx-auto space-y-8 text-slate-900 antialiased">
      {/* REFATORADO: Removemos o box branco com o botão de sair duplicado, mantendo um header limpo e moderno */}
      <div>
        <h1 className="text-3xl font-black tracking-tight text-slate-900">Visão Geral</h1>
        <p className="text-sm font-medium text-slate-400 mt-1">Acompanhe o fluxo de caixa consolidado de {String(currentMonth).padStart(2, '0')}/{currentYear}.</p>
      </div>

      {/* Grid de KPIs Cards */}
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

      {/* Gráficos de Distribuição */}
      <DashboardCharts expensesData={expensesReport} incomesData={incomesReport} />
    </div>
  );
}