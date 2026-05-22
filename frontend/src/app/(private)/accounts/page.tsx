'use client';

import { useState, useEffect, useActionState } from 'react';
import { Trash2, Plus, Landmark, X } from 'lucide-react';
import { getAccountsAction, deleteAccountAction, createAccountAction } from '@/app/(private)/actions/accounts-actions';

export default function AccountsPage() {
  const [accounts, setAccounts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Hook que integra o formulário com a Server Action
  const [state, formAction, pending] = useActionState(createAccountAction, { success: false, error: null });

  async function loadData() {
    setLoading(true);
    const data = await getAccountsAction();
    setAccounts(data || []);
    setLoading(false);
  }

  useEffect(() => {
    if (state?.success) {
      setIsModalOpen(false);
      loadData();
    }
  }, [state]);

  useEffect(() => { loadData(); }, []);

  async function handleDelete(id: string) {
    if (!confirm('Deseja excluir esta conta?')) return;
    await deleteAccountAction(id);
    loadData();
  }

  return (
    <div className="p-8 max-w-6xl mx-auto space-y-8">
      <div className="flex justify-between items-end border-b border-slate-100 pb-6">
        <div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">Minhas Contas</h1>
          <p className="text-slate-500 font-medium mt-1">Gestão centralizada do seu patrimônio</p>
        </div>
        <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-700 text-white px-6 py-3 rounded-2xl font-bold transition-all active:scale-95">
          <Plus size={20} /> Nova Conta
        </button>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 animate-pulse">
           {[1,2,3].map(i => <div key={i} className="h-40 bg-slate-100 rounded-3xl" />)}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {accounts.map(acc => (
            <div key={acc.accountId} className="group relative p-7 bg-white border border-slate-100 rounded-3xl shadow-sm hover:shadow-xl transition-all">
              <div className="flex justify-between items-start mb-8">
                <div className="p-3 bg-emerald-50 rounded-2xl text-emerald-600"><Landmark size={24} /></div>
                <button onClick={() => handleDelete(acc.accountId)} className="opacity-0 group-hover:opacity-100 p-2 text-slate-300 hover:text-rose-500 transition-all"><Trash2 size={18} /></button>
              </div>
              <h3 className="font-bold text-slate-900 text-lg">{acc.name}</h3>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">{acc.type}</p>
              <p className={`text-3xl font-black mt-3 ${acc.balance < 0 ? 'text-rose-600' : 'text-slate-900'}`}>
                R$ {acc.balance.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
              </p>
            </div>
          ))}
        </div>
      )}

      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50">
          <form action={formAction} className="bg-white p-8 rounded-3xl w-full max-w-sm space-y-6 shadow-2xl">
            <div className="flex justify-between"><h2 className="font-bold">Nova Conta</h2><button type="button" onClick={() => setIsModalOpen(false)}><X/></button></div>
            <input name="name" placeholder="Ex: Nubank" required className="w-full p-4 border rounded-xl" />
            <select name="type" className="w-full p-4 border rounded-xl">
              <option value="CHECKING">Conta Corrente</option>
              <option value="INVESTMENT">Investimento</option>
              <option value="CASH">Dinheiro</option>
            </select>
            {state?.error && <p className="text-rose-500 text-sm">{state.error}</p>}
            <button disabled={pending} className="w-full p-4 bg-emerald-600 text-white rounded-2xl font-bold">
              {pending ? 'Criando...' : 'Criar Conta'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}