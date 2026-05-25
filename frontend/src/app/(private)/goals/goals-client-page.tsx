'use client';

import { useState } from 'react';
import { Goal, Account } from '@/lib/api/api-server';
import { GoalForm } from './goal-form';
import { createGoalAction, updateGoalAction, deleteGoalAction } from '../actions/goal-actions';

export function GoalsClientPage({ initialGoals, accounts }: { initialGoals: Goal[], accounts: Account[] }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGoal, setEditingGoal] = useState<Goal | null>(null);

  const handleOpenNew = () => {
    setEditingGoal(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (goal: Goal) => {
    setEditingGoal(goal);
    setIsModalOpen(true);
  };

  const handleDelete = async (goalId: string) => {
    if (!confirm('Deseja realmente excluir esta meta?')) return;
    try {
      await deleteGoalAction(goalId);
    } catch (error) {
      alert('Erro ao excluir meta.');
    }
  };

  const handleSave = async (data: { description: string; targetAmount: number; accountIds: string[] }) => {
    if (editingGoal) {
      await updateGoalAction(editingGoal.id, data);
    } else {
      await createGoalAction(data);
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Metas Financeiras</h1>
          <p className="text-slate-500">Acompanhe seus objetivos de poupança.</p>
        </div>
        <button 
          onClick={handleOpenNew}
          className="bg-emerald-600 text-white px-5 py-2.5 rounded-xl font-bold hover:bg-emerald-700 transition shadow-sm"
        >
          Nova Meta
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {initialGoals.length === 0 ? (
          <div className="col-span-full p-12 border-2 border-dashed border-slate-200 rounded-2xl text-center text-slate-400">
            Você ainda não criou nenhuma meta.
          </div>
        ) : (
          initialGoals.map((goal) => {
            const percentage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
            return (
              <div key={goal.id} className="p-6 bg-white border border-slate-200 rounded-2xl shadow-sm group">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-bold text-lg text-slate-900">{goal.description}</h3>
                  <div className="opacity-0 group-hover:opacity-100 transition flex gap-2">
                    <button onClick={() => handleOpenEdit(goal)} className="text-blue-500 hover:text-blue-700 text-sm font-bold">Editar</button>
                    <button onClick={() => handleDelete(goal.id)} className="text-rose-500 hover:text-rose-700 text-sm font-bold">Excluir</button>
                  </div>
                </div>
                
                <p className="text-sm text-slate-500 mb-1">
                  R$ {goal.currentAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} de R$ {goal.targetAmount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                </p>
                
                <div className="w-full bg-slate-100 h-3 rounded-full mt-4 overflow-hidden relative">
                  <div 
                    className={`h-full transition-all duration-500 ${percentage >= 100 ? 'bg-emerald-500' : 'bg-blue-500'}`} 
                    style={{ width: `${percentage}%` }}
                  />
                </div>
                <p className="text-xs text-right mt-1 font-medium text-slate-400">{percentage.toFixed(1)}% alcançado</p>
              </div>
            );
          })
        )}
      </div>

      {isModalOpen && (
        <GoalForm 
          accounts={accounts} 
          initialData={editingGoal} 
          onClose={() => setIsModalOpen(false)} 
          onSave={handleSave} 
        />
      )}
    </div>
  );
}