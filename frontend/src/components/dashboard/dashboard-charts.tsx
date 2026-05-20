'use client';

interface KpiCardsProps {
  netWorth: number;
  monthlyIncome: number;
  monthlyExpense: number;
  lastMonthBalance?: number;     // Novo: Saldo acumulado que veio do mês anterior
  nextMonthForecast?: number;    // Novo: Saldo previsto (Patrimônio Atual + Receitas Previstas - Despesas Previstas)
}

export function KpiCards({
  netWorth,
  monthlyIncome,
  monthlyExpense,
  lastMonthBalance = 0,
  nextMonthForecast = 0
}: KpiCardsProps) {

  function formatCurrency(value: number) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value || 0);
  }

  return (
    <div className="space-y-6">
      {/* Grid Principal de 3 Colunas */}
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

      {/* Grid Secundário: Métricas Comparativas e Preditivas (Mês Anterior e Próximo Mês) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl flex justify-between items-center shadow-sm">
          <div>
            <h4 className="text-xs font-bold text-slate-500">Acumulado do Mês Anterior</h4>
            <p className="text-xs text-slate-400 mt-0.5">Saldo final trazido do fechamento passado.</p>
          </div>
          <span className="text-lg font-bold text-slate-700">
            {formatCurrency(lastMonthBalance)}
          </span>
        </div>

        <div className="p-4 bg-blue-50/50 border border-blue-100 rounded-xl flex justify-between items-center shadow-sm">
          <div>
            <h4 className="text-xs font-bold text-blue-950">Previsão para o Próximo Mês</h4>
            <p className="text-xs text-blue-600/80 mt-0.5">Estimativa de patrimônio baseado nas recorrências futuras.</p>
          </div>
          <span className="text-lg font-black text-blue-900">
            {formatCurrency(nextMonthForecast)}
          </span>
        </div>
      </div>
    </div>
  );
}