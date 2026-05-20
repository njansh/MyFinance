'use client';

interface KpiCardsProps {
  netWorth: number;
  monthlyIncome: number;
  monthlyExpense: number;
}

export function KpiCards({ netWorth, monthlyIncome, monthlyExpense }: KpiCardsProps) {

  function formatCurrency(value: number) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value || 0);
  }

  return (
    <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
      {/* Card: Patrimônio Total */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1 hover:shadow-md transition-shadow duration-200">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Saldo Atual (Patrimônio)</h3>
        <p className="text-3xl font-black tracking-tight text-slate-900 mt-1">
          {formatCurrency(netWorth)}
        </p>
      </div>

      {/* Card: Receitas do Mês */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1 hover:shadow-md transition-shadow duration-200">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Receitas do Mês</h3>
        <p className="text-3xl font-black tracking-tight text-emerald-600 mt-1">
          {formatCurrency(monthlyIncome)}
        </p>
      </div>

      {/* Card: Despesas do Mês */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm flex flex-col gap-1 hover:shadow-md transition-shadow duration-200">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Despesas do Mês</h3>
        <p className="text-3xl font-black tracking-tight text-rose-600 mt-1">
          {formatCurrency(monthlyExpense)}
        </p>
      </div>
    </div>
  );
}