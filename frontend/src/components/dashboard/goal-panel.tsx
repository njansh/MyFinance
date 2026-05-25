'use client';

import { Goal } from '@/lib/api/api-server';

interface GoalPanelProps {
  goals: Goal[];
}

export function GoalPanel({ goals }: GoalPanelProps) {
  function formatCurrency(value: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  if (goals.length === 0) return null;

  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm col-span-1 md:col-span-3">
      <div className="mb-6">
        <h3 className="text-base font-bold text-slate-900">Metas em Andamento</h3>
        <p className="text-xs font-medium text-slate-400">Progresso dos seus objetivos de economia.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {goals.map((goal) => {
          const percentage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
          return (
            <div key={goal.id} className="p-4 border border-slate-100 rounded-xl bg-slate-50/50 hover:border-emerald-200 transition-colors">
              <div className="flex justify-between items-start mb-2">
                <span className="text-sm font-bold text-slate-800">{goal.description}</span>
                <span className="text-[10px] font-black uppercase bg-emerald-100 text-emerald-700 px-2 py-0.5 rounded-md">
                  {percentage.toFixed(0)}%
                </span>
              </div>
              <p className="text-[11px] font-semibold text-slate-500">
                {formatCurrency(goal.currentAmount)} / {formatCurrency(goal.targetAmount)}
              </p>
              <div className="h-2 w-full bg-slate-200 rounded-full mt-2 overflow-hidden">
                <div 
                  className="h-full bg-emerald-500 rounded-full transition-all duration-500"
                  style={{ width: `${percentage}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}