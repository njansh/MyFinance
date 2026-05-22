'use client';

import { useState } from 'react';
import { updateBudgetLimitAction } from '@/app/(private)/actions/budget-actions';

interface BudgetView {
  id: string;
  categoryName: string;
  limitAmount: number;
  spentAmount: number;
  usagePercentage: number;
}

interface BudgetPanelProps {
  budgets: BudgetView[];
}

export function BudgetPanel({ budgets }: BudgetPanelProps) {
  const [editingBudget, setEditingBudget] = useState<BudgetView | null>(null);
  const [newLimit, setNewLimit] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  function formatCurrency(value: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  function getProgressColor(percentage: number) {
    if (percentage < 0.5) return 'bg-emerald-500';
    if (percentage < 0.8) return 'bg-amber-500';
    return 'bg-rose-500';
  }

  function openEditModal(budget: BudgetView) {
    setEditingBudget(budget);
    setNewLimit(budget.limitAmount.toString());
  }

  function closeEditModal() {
    setEditingBudget(null);
    setNewLimit('');
  }

  async function handleSaveEdit(e: React.FormEvent) {
    e.preventDefault();
    if (!editingBudget) return;

    try {
      setIsSubmitting(true);
      const limitValue = parseFloat(newLimit);

      if (isNaN(limitValue) || limitValue <= 0) {
         alert('Por favor, insira um valor válido maior que zero.');
         return;
      }

      // Chama a Server Action (comunicação servidor-servidor)
      await updateBudgetLimitAction(editingBudget.id, limitValue);

      closeEditModal();

    } catch (error) {
      console.error("Erro ao atualizar o limite:", error);
      alert('Erro ao atualizar o orçamento. Verifique o console para mais detalhes.');
    } finally {
      setIsSubmitting(false);
    }
  }

  if (budgets.length === 0) {
    return null;
  }

  return (
    <>
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm col-span-1 md:col-span-3 relative">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <h3 className="text-base font-bold text-slate-900">Monitor de Orçamentos</h3>
            <p className="text-xs font-medium text-slate-400">Acompanhe seus limites de gastos por categoria.</p>
          </div>
        </div>

        <div className="space-y-5 mt-4">
          {budgets.map((budget) => {
            const isDanger = budget.usagePercentage >= 0.8;

            return (
              <div key={budget.id} className="group flex flex-col gap-2">
                <div className="flex justify-between items-end">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-slate-700">{budget.categoryName}</span>
                    {isDanger && (
                      <span className="px-2 py-0.5 rounded-md bg-rose-100 text-rose-700 text-[10px] font-bold uppercase tracking-wider">
                        Atenção
                      </span>
                    )}
                  </div>
                  <div className="text-right flex items-center gap-3">
                    <span className="text-xs font-semibold text-slate-500">
                      {formatCurrency(budget.spentAmount)} / <span className="text-slate-900">{formatCurrency(budget.limitAmount)}</span>
                    </span>

                    {/* Botão de edição */}
                    <button
                      onClick={() => openEditModal(budget)}
                      className="text-slate-400 hover:text-blue-600 transition-colors opacity-0 group-hover:opacity-100"
                      title="Editar Limite"
                    >
                      ✏️
                    </button>
                  </div>
                </div>

                <div className="h-2.5 w-full bg-slate-100 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${getProgressColor(budget.usagePercentage)}`}
                    style={{ width: `${Math.min(budget.usagePercentage * 100, 100)}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Modal de Edição */}
      {editingBudget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
          <div className="bg-white p-6 rounded-3xl shadow-2xl w-full max-w-sm border border-slate-100 animate-in fade-in zoom-in duration-200">
            <h3 className="text-xl font-black text-slate-900 mb-1">Ajustar Limite</h3>
            <p className="text-sm font-medium text-slate-500 mb-6">
              Categoria: <span className="text-slate-800 font-bold">{editingBudget.categoryName}</span>
            </p>

            <form onSubmit={handleSaveEdit} className="space-y-5">
              <div>
                <label className="block text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                  Novo Valor (R$)
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={newLimit}
                  onChange={(e) => setNewLimit(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-base font-bold text-slate-800 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all"
                  required
                  autoFocus
                />
              </div>

              <div className="flex gap-3 justify-end pt-2">
                <button
                  type="button"
                  onClick={closeEditModal}
                  disabled={isSubmitting}
                  className="px-5 py-2.5 text-sm font-bold text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-5 py-2.5 text-sm font-bold text-white bg-slate-900 hover:bg-slate-800 rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-md"
                >
                  {isSubmitting ? 'Salvando...' : 'Confirmar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}