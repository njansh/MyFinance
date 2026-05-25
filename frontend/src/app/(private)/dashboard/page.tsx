import { cookies } from 'next/headers';
import {
  getAccounts,
  getCategories,
  getExpensesReport,
  getIncomesReport,
  getPendingTransactions,
  getBudgets,
  getGoals // Importamos a função de metas
} from '@/lib/api/api-server';
import { DashboardCharts } from '@/components/dashboard/dashboard-charts';
import { KpiCards } from '@/components/dashboard/kpi-cards';
import { DashboardFilter } from '@/components/dashboard/dashboard-filter';
import { PendingTransactions } from '@/components/pending/PendingTransactions';
import { BudgetPanel } from '@/components/dashboard/budget-panel';
import { GoalPanel } from '@/components/dashboard/goal-panel'; // Importamos o novo componente

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
  let categories: any[] = [];
  let budgets: any[] = [];
  let goals: any[] = []; // Estado para as metas

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
    // Buscas paralelas para otimizar o tempo de carregamento
    const [accountsRes, categoriesRes, budgetsRes, pendingRes, goalsRes] = await Promise.allSettled([
      getAccounts(),
      getCategories(),
      getBudgets(currentMonth, currentYear),
      getPendingTransactions(currentMonth, currentYear),
      getGoals() // Chamada paralela para as metas
    ]);

    if (accountsRes.status === 'fulfilled') accounts = accountsRes.value;
    if (categoriesRes.status === 'fulfilled') categories = categoriesRes.value;
    if (budgetsRes.status === 'fulfilled') budgets = budgetsRes.value;
    if (pendingRes.status === 'fulfilled') pendingTransactions = pendingRes.value;
    if (goalsRes.status === 'fulfilled') goals = goalsRes.value; // Atribuição das metas

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

  const budgetsView = budgets.map((b: any) => {
    const targetCategoryId = b.categoryId || b.category_id;
    const cat = categories.find((c: any) => c.id === targetCategoryId || c.categoryId === targetCategoryId);
    return {
      id: b.id,
      categoryName: cat ? cat.name : 'Categoria Desconhecida',
      limitAmount: b.limitAmount,
      spentAmount: b.spentAmount,
      usagePercentage: b.usagePercentage
    };
  });

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

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <BudgetPanel budgets={budgetsView} />
        <GoalPanel goals={goals} /> {/* Adicionado ao layout */}
      </div>

      <DashboardCharts expensesData={expensesReport} incomesData={incomesReport} />

      <PendingTransactions transactions={pendingTransactions} />
    </div>
  );
}