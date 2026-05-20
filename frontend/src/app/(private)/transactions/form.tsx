'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';

interface Account {
  accountId: string;
  name: string;
}

interface Category {
  categoryId?: string;
  id?: string;
  name: string;
}

interface FormProps {
  accounts: Account[];
  categories: Category[];
  onSuccessAction: (formData: any) => Promise<void>;
}

export function TransactionForm({ accounts, categories, onSuccessAction }: FormProps) {
  const router = useRouter();
  const [isTransferMode, setIsTransferMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const formData = new FormData(event.currentTarget);
    const data: any = Object.fromEntries(formData.entries());

    try {
      data.amount = Number(data.amount);
      data.isTransfer = isTransferMode;

      if (isTransferMode) {
        if (data.fromId === data.toId) {
          throw new Error('As contas de origem e destino devem ser diferentes.');
        }
      }

      await onSuccessAction(data);
      router.push('/extrato');
      router.refresh();
    } catch (err: any) {
      setError(err.message || 'Ocorreu um erro ao processar a operação.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="w-full max-w-xl mx-auto p-6 bg-white border border-slate-200 rounded-2xl shadow-sm space-y-6 antialiased text-slate-900">
      <div className="flex justify-between items-center border-b border-slate-100 pb-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Novo Lançamento</h2>
          <p className="text-xs text-slate-500 font-medium">Insira movimentações financeiras no seu fluxo de caixa.</p>
        </div>
        <div className="flex bg-slate-100 p-1 rounded-xl border border-slate-200/40">
          <button type="button" onClick={() => setIsTransferMode(false)} className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${!isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'}`}>
            Comum
          </button>
          <button type="button" onClick={() => setIsTransferMode(true)} className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'}`}>
            Transferência
          </button>
        </div>
      </div>

      {error && (
        <div className="p-3.5 bg-rose-50 border border-rose-100 text-rose-700 text-xs font-semibold rounded-xl">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Descrição</label>
          <input required name="description" placeholder="Ex: Transferência Reserva" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 transition-all" />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Valor (R$)</label>
            <input required name="amount" type="number" step="0.01" min="0.01" placeholder="0,00" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 transition-all" />
          </div>
          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Data do Lançamento</label>
            <input required name="date" type="datetime-local" defaultValue={new Date().toISOString().slice(0, 16)} className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 transition-all" />
          </div>
        </div>

        {!isTransferMode ? (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Tipo</label>
              <select name="type" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Conta Alvo</label>
              <select name="accountId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
                {accounts.map(acc => (
                  <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Categoria</label>
              <select name="categoryId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
                {categories.map(cat => (
                  <option key={cat.categoryId || cat.id} value={cat.categoryId || cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Conta de Origem (Debitar)</label>
              <select name="fromId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
                {accounts.map(acc => (
                  <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Conta de Destino (Creditar)</label>
              <select name="toId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
                {accounts.map(acc => (
                  <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
                ))}
              </select>
            </div>
          </div>
        )}

        <button type="submit" disabled={loading} className="w-full h-11 bg-slate-950 text-white font-semibold rounded-xl text-sm hover:bg-slate-850 active:scale-[0.99] transition-all shadow-sm disabled:opacity-50 mt-2">
          {loading ? 'Salvando Lançamento...' : 'Confirmar e Registrar'}
        </button>
      </form>
    </div>
  );
}