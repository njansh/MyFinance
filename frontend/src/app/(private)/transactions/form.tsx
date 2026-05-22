'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowRightLeft, Wallet } from 'lucide-react';

interface Account {
  accountId: string;
  name: string;
  balance?: number;
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

  // Estados para capturar seleções
  const [fromId, setFromId] = useState('');
  const [toId, setToId] = useState('');
  const [accountId, setAccountId] = useState('');

  const filteredCategories = categories.filter((cat) => cat.type === type);
  const getAccount = (id: string) => accounts.find(a => a.accountId === id);

  // Função para aplicar cor baseada no saldo
  const getBalanceStyle = (balance: number) => {
    return balance < 0 ? "text-rose-600 font-bold" : "text-emerald-600 font-bold";
  };

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const formData = new FormData(event.currentTarget);
    const data: any = Object.fromEntries(formData.entries());

    try {
      data.amount = Number(data.amount);
      data.isTransfer = isTransferMode;

      if (isTransferMode && data.fromId === data.toId) {
        throw new Error('As contas de origem e destino devem ser diferentes.');
      }

      await onSuccessAction(data);
      alert(isTransferMode ? 'Transferência realizada com sucesso!' : 'Lançamento registrado com sucesso!');
      router.push('/extrato');
      router.refresh();
    } catch (err: any) {
      setError(err.message || 'Erro ao processar a operação.');
    } finally {
      setLoading(false);
    }
  }

  const selectClass = "w-full h-12 px-4 bg-white border border-slate-200 rounded-xl text-sm font-medium focus:ring-2 focus:ring-emerald-500 shadow-sm appearance-none select-arrow";

  return (
    <div className="w-full max-w-2xl mx-auto p-8 bg-white border border-slate-100 rounded-3xl shadow-xl shadow-slate-200/50 space-y-8 antialiased text-slate-900">
      <div className="text-center space-y-1">
        <h2 className="text-2xl font-black text-slate-900">Novo Lançamento</h2>
      </div>

      <div className="flex bg-slate-100 p-1.5 rounded-2xl border border-slate-200">
        <button type="button" onClick={() => setIsTransferMode(false)} className={`flex-1 flex items-center justify-center gap-2 py-3 text-sm font-bold rounded-xl transition-all ${!isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}>
          <Wallet size={18} /> Transação
        </button>
        <button type="button" onClick={() => setIsTransferMode(true)} className={`flex-1 flex items-center justify-center gap-2 py-3 text-sm font-bold rounded-xl transition-all ${isTransferMode ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}>
          <ArrowRightLeft size={18} /> Transferência
        </button>
      </div>

      {error && <div className="p-3.5 bg-rose-50 border border-rose-100 text-rose-700 text-xs font-semibold rounded-xl">{error}</div>}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Descrição</label>
            <input required name="description" placeholder="Ex: Pagamento Aluguel" className="w-full h-12 px-4 bg-slate-50 border border-slate-200 rounded-xl" />
          </div>
          <div className="space-y-2">
            <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Data</label>
            <input required name="date" type="datetime-local" defaultValue={new Date().toISOString().slice(0, 16)} className="w-full h-12 px-4 bg-slate-50 border border-slate-200 rounded-xl" />
          </div>
        </div>

        <div className="space-y-2">
          <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Valor (R$)</label>
          <input required name="amount" type="number" step="0.01" min="0.01" placeholder="0,00" className="w-full h-12 px-4 bg-slate-50 border border-slate-200 rounded-xl" />
        </div>

        {!isTransferMode ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Tipo</label>
              <select name="type" value={type} onChange={(e) => { setType(e.target.value as any); setCategoryId(''); }} className={selectClass}>
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
              </select>
            </div>
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Conta</label>
              <select name="accountId" required onChange={(e) => setAccountId(e.target.value)} className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name} (R$ {acc.balance?.toFixed(2)})</option>)}
              </select>
              {accountId && (
                <p className={`text-xs ${getBalanceStyle(getAccount(accountId)?.balance || 0)}`}>
                  Saldo: R$ {getAccount(accountId)?.balance?.toFixed(2) || '0,00'}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Categoria</label>
              <select name="categoryId" value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required className={selectClass}>
                <option value="">Selecione...</option>
                {filteredCategories.map(cat => <option key={cat.categoryId || cat.id} value={cat.categoryId || cat.id}>{cat.name}</option>)}
              </select>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Origem</label>
              <select name="fromId" required onChange={(e) => setFromId(e.target.value)} className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name} (R$ {acc.balance?.toFixed(2)})</option>)}
              </select>
              {fromId && (
                <p className={`text-xs ${getBalanceStyle(getAccount(fromId)?.balance || 0)}`}>
                  Saldo: R$ {getAccount(fromId)?.balance?.toFixed(2) || '0,00'}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <label className="text-xs font-bold text-slate-400 uppercase tracking-widest">Destino</label>
              <select name="toId" required onChange={(e) => setToId(e.target.value)} className={selectClass}>
                <option value="">Selecione...</option>
                {accounts.map(acc => <option key={acc.accountId} value={acc.accountId}>{acc.name} (R$ {acc.balance?.toFixed(2)})</option>)}
              </select>
              {toId && (
                <p className={`text-xs ${getBalanceStyle(getAccount(toId)?.balance || 0)}`}>
                  Saldo: R$ {getAccount(toId)?.balance?.toFixed(2) || '0,00'}
                </p>
              )}
            </div>
          </div>
        )}

        <button type="submit" disabled={loading} className="w-full h-14 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-2xl transition-all active:scale-95">
           {loading ? 'A processar...' : 'Confirmar Lançamento'}
        </button>
      </form>
    </div>
  );
}