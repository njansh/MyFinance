'use client';

import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

interface ChartsProps {
  expensesData: Record<string, number>;
  incomesData: Record<string, number>;
}

// Cores refinadas e mais harmônicas com o design moderno do app
const EXPENSE_COLORS = ['#f43f5e', '#f97316', '#fbbf24', '#a3e635', '#22d3ee', '#6366f1', '#c084fc', '#f472b6'];
const INCOME_COLORS = ['#10b981', '#059669', '#047857', '#065f46', '#0f766e', '#115e59', '#14532d', '#166534'];

export function DashboardCharts({ expensesData, incomesData }: ChartsProps) {
  const formattedExpenses = Object.entries(expensesData)
    .filter(([_, value]) => value > 0)
    .map(([key, value]) => ({ name: key, value }));

  const formattedIncomes = Object.entries(incomesData)
    .filter(([_, value]) => value > 0)
    .map(([key, value]) => ({ name: key, value }));

  function renderCustomTooltip({ active, payload }: any) {
    if (active && payload && payload.length) {
      return (
        <div className="bg-slate-950 text-white px-3 py-2 text-xs font-semibold rounded-xl shadow-md border border-slate-800 animate-in fade-in duration-200">
          <p>{`${payload[0].name}: R$ ${payload[0].value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`}</p>
        </div>
      );
    }
    return null;
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* Card Gráfico de Despesas */}
      <div className="p-6 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col h-[400px] hover:shadow-md transition-shadow duration-200">
        <div className="mb-4">
          <h3 className="text-base font-bold text-slate-900">Distribuição de Despesas</h3>
          <p className="text-xs font-medium text-slate-400">Análise de saídas proporcionais por categoria.</p>
        </div>
        <div className="flex-1 w-full h-full min-h-0">
          {formattedExpenses.length === 0 ? (
            <div className="w-full h-full flex items-center justify-center text-sm font-medium text-slate-400">
              Nenhuma despesa registrada neste período.
            </div>
          ) : (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={formattedExpenses}
                  cx="50%"
                  cy="50%"
                  innerRadius={70}
                  outerRadius={95}
                  paddingAngle={4}
                  dataKey="value"
                  isAnimationActive={true} // Ativa animações fluidas de abertura
                  animationDuration={600}
                  animationEasing="ease-out"
                >
                  {formattedExpenses.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={EXPENSE_COLORS[index % EXPENSE_COLORS.length]} className="focus:outline-none" />
                  ))}
                </Pie>
                <Tooltip content={renderCustomTooltip} cursor={false} />
                <Legend verticalAlign="bottom" height={44} iconType="circle" wrapperStyle={{ fontSize: '12px', fontWeight: 500, paddingTop: '10px' }} />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      {/* Card Gráfico de Receitas */}
      <div className="p-6 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col h-[400px] hover:shadow-md transition-shadow duration-200">
        <div className="mb-4">
          <h3 className="text-base font-bold text-slate-900">Origem de Receitas</h3>
          <p className="text-xs font-medium text-slate-400">Análise de entradas proporcionais por categoria.</p>
        </div>
        <div className="flex-1 w-full h-full min-h-0">
          {formattedIncomes.length === 0 ? (
            <div className="w-full h-full flex items-center justify-center text-sm font-medium text-slate-400">
              Nenhuma receita registrada neste período.
            </div>
          ) : (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={formattedIncomes}
                  cx="50%"
                  cy="50%"
                  innerRadius={70}
                  outerRadius={95}
                  paddingAngle={4}
                  dataKey="value"
                  isAnimationActive={true} // Ativa animações fluidas de abertura
                  animationDuration={600}
                  animationEasing="ease-out"
                >
                  {formattedIncomes.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={INCOME_COLORS[index % INCOME_COLORS.length]} className="focus:outline-none" />
                  ))}
                </Pie>
                <Tooltip content={renderCustomTooltip} cursor={false} />
                <Legend verticalAlign="bottom" height={44} iconType="circle" wrapperStyle={{ fontSize: '12px', fontWeight: 500, paddingTop: '10px' }} />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  );
}