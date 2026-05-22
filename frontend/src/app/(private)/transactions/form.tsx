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
  type?: string;
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

  const [type, setType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [categoryId, setCategoryId] = useState('');

  const filteredCategories = categories.filter((cat) => cat.type === type);

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

        alert(isTransferMode ? 'Transferência realizada com sucesso!' : 'Lançamento registrado com sucesso!');

        router.push('/extrato');
        router.refresh();
      } catch (err: any) {
        setError(err.message || 'Ocorreu um erro ao processar a operação.');
      } finally {
        setLoading(false);
      }
    }

  const selectClass = "w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 appearance-none select-arrow";

  return (
    <div className="w-full max-w-xl mx-auto p-6 bg-white border border-slate-200 rounded-2xl shadow-sm space-y-6 antialiased text-slate-900">
      <div className="flex justify-between items-center border-b border-slate-100 pb-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Novo Lançamento</h2>
          <p className="text-xs text-slate-500 font-medium">Insira movimentações financeiras no seu fluxo de caixa.</p>
        </div>
        <div className="flex bg-slate-100 p-1 rounded-xl border border-slate-200/40">
          <button type="button" onClick={() => setIsTransferMode(false)} className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${!isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'}`}>Comum</button>
          <button type="button" onClick={() => setIsTransferMode(true)} className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-800'}`}>Transferência</button>
        </div>
      </div>

      {error && <div className="p-3.5 bg-rose-50 border border-rose-100 text-rose-700 text-xs font-semibold rounded-xl">{error}</div>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-1.5">
          <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Descrição</label>
          <input required name="description" placeholder="Ex: Mercado mensal" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 transition-all" />
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
              <select name="type" value={type} onChange={(e) => { setType(e.target.value as any); setCategoryId(''); }} className={selectClass}>
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Conta</label>
              <select name="accountId" required className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>)}
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Categoria</label>
              <select name="categoryId" value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required className={selectClass}>
                <option value="">Selecione...</option>
                {filteredCategories.map(cat => <option key={cat.categoryId || cat.id} value={cat.categoryId || cat.id}>{cat.name}</option>)}
              </select>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Origem</label>
              <select name="fromId" required className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>)}
              </select>
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Destino</label>
              <select name="toId" required className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>)}
              </select>
            </div>
          </div>
        )}

        <button type="submit" disabled={loading} className="w-full h-11 bg-slate-950 text-white font-semibold rounded-xl text-sm hover:bg-slate-850 transition-all shadow-sm disabled:opacity-50 mt-2">
          {loading ? 'A processar...' : 'Confirmar e Registar'}
        </button>
      </form>
    </div>
  );
}