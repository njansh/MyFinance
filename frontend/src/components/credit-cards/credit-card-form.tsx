'use client';

import { useState } from 'react';
import { createCreditCard } from '../lib/api/api-client';
import { useRouter } from 'next/navigation';

interface CreditCardFormProps {
  accounts: any[];
  onClose: () => void;
}

export function CreditCardForm({ accounts, onClose }: CreditCardFormProps) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const formData = new FormData(e.currentTarget);

    try {
      await createCreditCard({
        name: formData.get('name'),
        limit: Number(formData.get('limit')),
        closingDay: Number(formData.get('closingDay')),
        dueDay: Number(formData.get('dueDay')),
        accountId: formData.get('accountId'),
      });

      router.refresh();
      onClose(); // Fecha o modal após sucesso
    } catch (err) {
      setError('Erro ao cadastrar cartão. Verifique os dados.');
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="p-6 bg-white border border-slate-200 rounded-2xl shadow-2xl space-y-4">
      <div className="flex justify-between items-center mb-2">
        <h2 className="font-bold text-lg text-slate-900">Novo Cartão de Crédito</h2>
        <button type="button" onClick={onClose} className="text-slate-400 hover:text-slate-900 font-bold">✕</button>
      </div>

      {error && <p className="text-red-500 text-sm font-medium">{error}</p>}

      <input name="name" placeholder="Ex: Nubank Platinum" className="w-full p-3 border border-slate-200 rounded-lg focus:ring-2 focus:ring-slate-900 outline-none" required />

      <input name="limit" type="number" step="0.01" placeholder="Limite Total (R$)" className="w-full p-3 border border-slate-200 rounded-lg focus:ring-2 focus:ring-slate-900 outline-none" required />

      <div className="grid grid-cols-2 gap-4">
        <input name="closingDay" type="number" min="1" max="31" placeholder="Dia Fechamento" className="p-3 border border-slate-200 rounded-lg focus:ring-2 focus:ring-slate-900 outline-none" required />
        <input name="dueDay" type="number" min="1" max="31" placeholder="Dia Vencimento" className="p-3 border border-slate-200 rounded-lg focus:ring-2 focus:ring-slate-900 outline-none" required />
      </div>

      <select name="accountId" className="w-full p-3 border border-slate-200 rounded-lg bg-white focus:ring-2 focus:ring-slate-900 outline-none" required>
        <option value="">Selecione a conta de pagamento</option>
        {accounts.map(acc => (
          <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
        ))}
      </select>

      <div className="flex gap-3 pt-2">
        <button type="button" onClick={onClose} className="flex-1 py-3 text-slate-600 font-semibold hover:bg-slate-50 rounded-lg transition">
          Cancelar
        </button>
        <button disabled={loading} className="flex-1 py-3 bg-slate-900 text-white font-semibold rounded-lg hover:bg-slate-800 transition disabled:opacity-50">
          {loading ? 'Salvando...' : 'Criar Cartão'}
        </button>
      </div>
    </form>
  );
}