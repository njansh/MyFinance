import { getBudgets, getCategories } from '@/lib/api/api-server';
import { DashboardFilter } from '@/components/dashboard/dashboard-filter';
import { BudgetManager } from '@/components/budgets/budget-manager';

export const dynamic = 'force-dynamic';
export const revalidate = 0;

interface PageProps {
  searchParams: any;
}

export default async function BudgetsPage({ searchParams }: PageProps) {
  const params = searchParams instanceof Promise ? await searchParams : searchParams;

  const now = new Date();
  const currentMonth = params?.month ? Number(params.month) : now.getMonth() + 1;
  const currentYear = params?.year ? Number(params.year) : now.getFullYear();

  let categories: any[] = [];
  let budgets: any[] = [];

  try {
    // Executa as consultas em paralelo no servidor
    const [categoriesRes, budgetsRes] = await Promise.allSettled([
      getCategories(),
      getBudgets(currentMonth, currentYear)
    ]);

    if (categoriesRes.status === 'fulfilled') categories = categoriesRes.value;
    if (budgetsRes.status === 'fulfilled') budgets = budgetsRes.value;
  } catch (error) {
    console.error("Erro ao carregar dados na página de orçamentos:", error);
  }

  // Faz a junção segura das categorias com as metas vindas do backend
  const budgetsView = budgets.map((b: any) => {
    const targetCategoryId = b.categoryId || b.category_id;
    const cat = categories.find((c: any) => c.id === targetCategoryId || c.categoryId === targetCategoryId);

    return {
      id: b.id,
      categoryId: targetCategoryId,
      categoryName: cat ? cat.name : 'Categoria Desconhecida',
      limitAmount: b.limitAmount,
      spentAmount: b.spentAmount,
      usagePercentage: b.usagePercentage
    };
  });

  return (
    <div className="p-6 md:p-10 max-w-6xl mx-auto space-y-8 text-slate-900 antialiased">
      {/* Cabeçalho da Página */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-black tracking-tight text-slate-900">Planeamento de Orçamentos</h1>
          <p className="text-sm font-medium text-slate-400 mt-1">
            Gere tetos de despesas controlados para o mês de {String(currentMonth).padStart(2, '0')}/{currentYear}.
          </p>
        </div>
        {/* Reutiliza o filtro consolidado de mês/ano do teu projeto */}
        <DashboardFilter />
      </div>

      {/* Painel Centralizador de Ações */}
      <BudgetManager
        budgets={budgetsView}
        categories={categories}
        currentMonth={currentMonth}
        currentYear={currentYear}
      />
    </div>
  );
}