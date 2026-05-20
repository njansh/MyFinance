'use client';
import { useState } from 'react';
import { createCreditCardTransactionAction } from '../../app/actions/credit-card-actions';

interface PurchaseFormProps {
  cardId: string;
  cardName: string;
  categories: any[];
  onClose: () => void;
}

export function CreditCardPurchaseForm({ cardId, cardName, categories, onClose }: PurchaseFormProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    const formData = new FormData(e.currentTarget);

    // 1. CORREÇÃO DA VÍRGULA
    const rawAmount = formData.get('amount') as string;
    const formattedAmount = Number(rawAmount.replace(',', '.'));

    // 2. CORREÇÃO DA DATA
    const dateStr = formData.get('date') as string;

    try {
      await createCreditCardTransactionAction(cardId, {
        description: formData.get('description'),
        amount: formattedAmount,
        installments: Number(formData.get('installments')),
        date: dateStr,
        categoryId: formData.get('categoryId'),
      });
      onClose();
    } catch (err: any) {
      setError(err.message || 'Erro ao registrar compra no cartão.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Nova Compra</h2>
            <p className="text-xs font-medium text-slate-500">Cartão: {cardName}</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition">✕</button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
             <div className="p-3 bg-rose-50 text-rose-700 text-xs font-semibold rounded-lg border border-rose-100 break-words">
               {error}
             </div>
          )}

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Descrição</label>
            <input required name="description" placeholder="Ex: Notebook Dell" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900" />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Valor Total (R$)</label>
              <input required name="amount" type="text" placeholder="200,33" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900" />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Parcelas</label>
              <input required name="installments" type="number" min="1" max="72" defaultValue="1" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Data</label>
              <input required name="date" type="date" defaultValue={new Date().toISOString().split('T')[0]} className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900" />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Categoria</label>
              <select required name="categoryId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900">
                {categories.map((cat: any) => (
                  <option key={cat.categoryId || cat.id} value={cat.categoryId || cat.id}>{cat.name}</option>
                ))}
              </select>
            </div>
          </div>

          <button disabled={loading} className="w-full h-11 mt-2 bg-slate-900 text-white font-semibold rounded-xl text-sm hover:bg-slate-800 transition disabled:opacity-50">
            {loading ? 'Processando...' : 'Confirmar Compra'}
          </button>
        </form>
      </div>
    </div>
  );
}