'use client';

import { useState } from 'react';
import { X } from 'lucide-react';
// Importe a sua action de criar transação do cartão, ajuste o caminho se necessário
// import { createCreditCardPurchaseAction } from '@/app/(private)/actions/credit-card-actions';

export function CreditCardPurchaseForm({ cardId, cardName, categories, onClose }: any) {
  const [loading, setLoading] = useState(false);

  // A MÁGICA ACONTECE AQUI: Filtra a lista para mostrar APENAS categorias de SAÍDA (EXPENSE)
  const expenseCategories = categories.filter((cat: any) => cat.type === 'EXPENSE');

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData(e.currentTarget);
    // const data = Object.fromEntries(formData);

    try {
      // Aqui você chama a sua Server Action para salvar a compra
      // await createCreditCardPurchaseAction(cardId, data);

      alert('Compra registrada com sucesso!');
      onClose();
    } catch (error) {
      alert('Erro ao registrar compra.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in duration-200">

        {/* Cabeçalho do Modal */}
        <div className="bg-slate-900 p-6 flex justify-between items-center">
          <div>
            <h2 className="text-xl font-bold text-white">Nova Compra</h2>
            <p className="text-slate-400 text-sm mt-1">Cartão: {cardName}</p>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white bg-white/10 hover:bg-white/20 p-2 rounded-full transition"
          >
            <X size={20} />
          </button>
        </div>

        {/* Formulário */}
        <div className="p-6">
          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-1">Descrição</label>
              <input
                required
                type="text"
                name="description"
                placeholder="Ex: Mercado, Uber, Ifood..."
                className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900 transition"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">Valor (R$)</label>
                <input
                  required
                  type="number"
                  step="0.01"
                  name="amount"
                  placeholder="0.00"
                  className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900 transition"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">Parcelas</label>
                <input
                  required
                  type="number"
                  min="1"
                  defaultValue="1"
                  name="installments"
                  className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900 transition"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">Data da Compra</label>
                <input
                  required
                  type="date"
                  name="date"
                  defaultValue={new Date().toISOString().split('T')[0]}
                  className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900 transition"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1">Categoria</label>
                <select
                  required
                  name="categoryId"
                  className="w-full h-11 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900 transition"
                >
                  <option value="">Selecione...</option>

                  {/* MAPEANDO APENAS AS CATEGORIAS FILTRADAS (DESPESAS) */}
                  {expenseCategories.map((cat: any) => (
                    <option key={cat.categoryId || cat.id} value={cat.categoryId || cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full h-11 mt-4 bg-black text-white font-bold rounded-xl hover:bg-slate-800 active:scale-[0.98] transition disabled:opacity-50"
            >
              {loading ? 'Registrando...' : 'Registrar Compra'}
            </button>
          </form>
        </div>

      </div>
    </div>
  );
}