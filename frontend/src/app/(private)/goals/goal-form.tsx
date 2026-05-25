'use client';

import { useState } from 'react';
import { Account } from '@/lib/api/api-server';

interface GoalFormProps {
  accounts: Account[];
  initialData?: any;
  onClose: () => void;
  onSave: (data: { description: string; targetAmount: number; accountIds: string[] }) => Promise<void>;
}

export function GoalForm({ accounts, initialData, onClose, onSave }: GoalFormProps) {
  const [description, setDescription] = useState(initialData?.description || '');
  const [targetAmount, setTargetAmount] = useState(initialData?.targetAmount?.toString() || '');
  const [selectedAccounts, setSelectedAccounts] = useState<string[]>(initialData?.accountIds || []);
  const [isSaving, setIsSaving] = useState(false);

  const toggleAccount = (accountId: string) => {
    setSelectedAccounts(prev => 
      prev.includes(accountId) 
        ? prev.filter(id => id !== accountId) 
        : [...prev, accountId]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedAccounts.length === 0) {
      alert('Selecione pelo menos uma conta para vincular à meta.');
      return;
    }
    
    setIsSaving(true);
    try {
      await onSave({
        description,
        targetAmount: parseFloat(targetAmount),
        accountIds: selectedAccounts
      });
      onClose();
    } catch (error) {
      alert('Erro ao salvar meta. Verifique os dados.');
      console.error(error);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl w-full max-w-md space-y-6 shadow-xl">
        <h3 className="font-bold text-xl text-slate-900">
          {initialData ? 'Editar Meta' : 'Nova Meta'}
        </h3>
        
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Descrição do Objetivo</label>
            <input 
              required
              value={description} 
              onChange={e => setDescription(e.target.value)} 
              className="w-full p-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none transition" 
              placeholder="Ex: Viagem para o Japão, Carro Novo..." 
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Valor Alvo (R$)</label>
            <input 
              required
              type="number" 
              step="0.01"
              value={targetAmount} 
              onChange={e => setTargetAmount(e.target.value)} 
              className="w-full p-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none transition" 
              placeholder="0.00" 
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">Contas Vinculadas (Saldo)</label>
            <div className="max-h-40 overflow-y-auto space-y-2 border border-slate-100 p-2 rounded-xl bg-slate-50">
              {accounts.map(acc => (
                <label key={acc.accountId} className="flex items-center gap-3 p-2 bg-white rounded-lg border border-slate-200 cursor-pointer hover:border-emerald-400 transition">
                  <input 
                    type="checkbox" 
                    checked={selectedAccounts.includes(acc.accountId)}
                    onChange={() => toggleAccount(acc.accountId)}
                    className="w-4 h-4 text-emerald-600 rounded focus:ring-emerald-500"
                  />
                  <div className="flex flex-col">
                    <span className="text-sm font-medium text-slate-800">{acc.name}</span>
                    <span className="text-xs text-slate-500">R$ {acc.balance.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</span>
                  </div>
                </label>
              ))}
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-2">
          <button 
            type="button" 
            onClick={onClose} 
            disabled={isSaving}
            className="px-5 py-2.5 text-slate-600 font-medium hover:bg-slate-100 rounded-xl transition"
          >
            Cancelar
          </button>
          <button 
            type="submit" 
            disabled={isSaving}
            className="px-5 py-2.5 bg-emerald-600 text-white font-bold rounded-xl hover:bg-emerald-700 transition shadow-sm disabled:opacity-50"
          >
            {isSaving ? 'Salvando...' : 'Salvar Meta'}
          </button>
        </div>
      </form>
    </div>
  );
}