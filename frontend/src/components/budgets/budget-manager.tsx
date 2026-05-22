'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { createBudgetAction, updateBudgetLimitAction, deleteBudgetAction } from '@/app/(private)/actions/budget-actions';

interface BudgetView {
  id: string;
  categoryId: string;
  categoryName: string;
  limitAmount: number;
  spentAmount: number;
  usagePercentage: number;
}

// 1. Interface atualizada com o campo 'type'
interface Category {
  id?: string;
  categoryId?: string;
  name: string;
  type?: string;
}

interface BudgetManagerProps {
  budgets: BudgetView[];
  categories: Category[];
  currentMonth: number;
  currentYear: number;
}

export function BudgetManager({ budgets, categories, currentMonth, currentYear }: BudgetManagerProps) {
  const router = useRouter();

  // Estados do Formulário de Criação
  const [selectedCategory, setSelectedCategory] = useState('');
  const [limitAmount, setLimitAmount] = useState('');
  const [isCreating, setIsCreating] = useState(false);

  // Estados do Modal de Edição
  const [editingBudget, setEditingBudget] = useState<BudgetView | null>(null);
  const [editLimit, setEditLimit] = useState('');
  const [isUpdating, setIsUpdating] = useState(false);

  // Formatações visuais
  const formatCurrency = (val: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);

  const getProgressColor = (pct: number) => {
    if (pct < 0.5) return 'bg-emerald-500';
    if (pct < 0.8) return 'bg-amber-500';
    return 'bg-rose-500';
  };

  // Ação de Criar Orçamento
  async function handleCreateBudget(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedCategory || !limitAmount) return;

    try {
      setIsCreating(true);
      await createBudgetAction({
        categoryId: selectedCategory,
        month: currentMonth,
        year: currentYear,
        limitAmount: parseFloat(limitAmount)
      });

      setSelectedCategory('');
      setLimitAmount('');
      router.refresh();
    } catch (err) {
      alert('Erro ao criar o orçamento. Certifica-te de que já não existe um orçamento para esta categoria neste mês.');
    } finally {
      setIsCreating(false);
    }
  }

  // Ação de Atualizar Limite
  async function handleUpdateLimit(e: React.FormEvent) {
    e.preventDefault();
    if (!editingBudget || !editLimit) return;

    try {
      setIsUpdating(true);
      await updateBudgetLimitAction(editingBudget.id, parseFloat(editLimit));
      setEditingBudget(null);
      router.refresh();
    } catch (err) {
      alert('Erro ao atualizar limite.');
    } finally {
      setIsUpdating(false);
    }
  }

  // Ação de Eliminar Orçamento
  async function handleDeleteBudget(id: string) {
    if (!confirm('Tens a certeza de que desejas eliminar este orçamento?')) return;

    try {
      await deleteBudgetAction(id);
      router.refresh();
    } catch (err) {
      alert('Erro ao eliminar orçamento.');
    }
  }

  // 2. Filtro Inteligente: Apenas DESPESAS que ainda não têm orçamento
  const availableCategories = categories.filter(cat => {
    // Confirma se é despesa (ajusta 'EXPENSE' se o teu backend devolver em minúsculas ou noutro idioma)
    const isExpense = cat.type === 'EXPENSE';

    // Confirma se ainda não está na lista de orçamentos deste mês
    const catId = cat.categoryId || cat.id;
    const notAlreadyBudgeted = !budgets.some(b => b.categoryId === catId);

    return isExpense && notAlreadyBudgeted;
  });

  return (
    <div className="space-y-8">
      {/* Bloco 1: Formulário de Criação */}
      <div className="p-6 bg-white border border-slate-200/80 rounded-2xl shadow-sm">
        <h3 className="text-base font-bold text-slate-900 mb-1">Definir Novo Limite Mensal</h3>
        <p className="text-xs font-medium text-slate-400 mb-5">Escolha uma categoria de despesa e defina a meta de gastos para este período.</p>

        <form onSubmit={handleCreateBudget} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <div className="flex flex-col gap-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Categoria</label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="bg-white border border-slate-200 rounded-xl px-3 py-2.5 text-sm font-semibold text-slate-700 focus:outline-none focus:ring-1 focus:ring-black"
              required
            >
              <option value="">Selecione uma categoria...</option>
              {availableCategories.map(cat => {
                const id = cat.categoryId || cat.id;
                return <option key={id} value={id}>{cat.name}</option>;
              })}
            </select>
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Limite Estimado (R$)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={limitAmount}
              onChange={(e) => setLimitAmount(e.target.value)}
              placeholder="Ex: 500,00"
              className="bg-white border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold text-slate-700 focus:outline-none focus:ring-1 focus:ring-black"
              required
            />
          </div>

          <button
            type="submit"
            disabled={isCreating}
            className="w-full bg-slate-900 hover:bg-slate-800 text-white rounded-xl py-2.5 text-sm font-bold shadow-sm transition-colors cursor-pointer disabled:opacity-50"
          >
            {isCreating ? 'A guardar...' : 'Criar Orçamento'}
          </button>
        </form>
      </div>

      {/* Bloco 2: Listagem Completa de Orçamentos */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm">
        <h3 className="text-base font-bold text-slate-900 mb-4">Orçamentos Ativos no Período</h3>
        
        {budgets.length === 0 ? (
          <p className="text-sm font-medium text-slate-400 py-4 text-center">Nenhum orçamento configurado para este mês.</p>
        ) : (
          <div className="space-y-6">
            {budgets.map((budget) => {
              const isDanger = budget.usagePercentage >= 0.8;
              return (
                <div key={budget.id} className="group flex flex-col gap-2 p-3 hover:bg-slate-50 rounded-xl transition-all">
                  <div className="flex justify-between items-end">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-bold text-slate-700">{budget.categoryName}</span>
                      {isDanger && (
                        <span className="px-2 py-0.5 rounded-md bg-rose-100 text-rose-700 text-[10px] font-bold uppercase tracking-wider">
                          {budget.usagePercentage >= 1 ? 'Esgotado' : 'Atenção'}
                        </span>
                      )}
                    </div>
                    <div className="text-right flex items-center gap-4">
                      <span className="text-xs font-semibold text-slate-500">
                        {formatCurrency(budget.spentAmount)} / <span className="text-slate-900 font-bold">{formatCurrency(budget.limitAmount)}</span>
                      </span>
                      <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        <button
                          onClick={() => { setEditingBudget(budget); setEditLimit(budget.limitAmount.toString()); }}
                          className="text-xs font-bold text-slate-600 hover:text-blue-600 bg-white border border-slate-200 px-2.5 py-1 rounded-lg shadow-sm cursor-pointer"
                        >
                          ✏️ Editar
                        </button>
                        <button
                          onClick={() => handleDeleteBudget(budget.id)}
                          className="text-xs font-bold text-rose-600 hover:text-white hover:bg-rose-600 bg-white border border-rose-100 px-2.5 py-1 rounded-lg shadow-sm cursor-pointer transition-colors"
                        >
                          🗑️ Eliminar
                        </button>
                      </div>
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
        )}
      </div>

      {/* Modal de Alteração de Limite */}
      {editingBudget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
          <div className="bg-white p-6 rounded-3xl shadow-2xl w-full max-w-sm border border-slate-100 animate-in fade-in zoom-in duration-200">
            <h3 className="text-xl font-black text-slate-900 mb-1">Ajustar Limite</h3>
            <p className="text-sm font-medium text-slate-500 mb-6">
              Categoria: <span className="text-slate-800 font-bold">{editingBudget.categoryName}</span>
            </p>

            <form onSubmit={handleUpdateLimit} className="space-y-5">
              <div>
                <label className="block text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-2">Novo Valor (R$)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={editLimit}
                  onChange={(e) => setEditLimit(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-base font-bold text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                  autoFocus
                />
              </div>

              <div className="flex gap-3 justify-end pt-2">
                <button
                  type="button"
                  onClick={() => setEditingBudget(null)}
                  disabled={isUpdating}
                  className="px-5 py-2.5 text-sm font-bold text-slate-500 hover:bg-slate-100 rounded-xl"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={isUpdating}
                  className="px-5 py-2.5 text-sm font-bold text-white bg-slate-900 hover:bg-slate-800 rounded-xl shadow-md"
                >
                  {isUpdating ? 'A salvar...' : 'Confirmar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}