'use client';

import { useState } from 'react';
import { CreditCardForm } from '../../components/credit-card-form';

export default function CreditCardsPage({ accounts, cards }: { accounts: any[], cards: any[] }) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <div className="container mx-auto p-8 max-w-5xl space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Cartões de Crédito</h1>
          <p className="text-sm text-slate-500">Gerencie seus limites e datas de fechamento.</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="px-4 py-2 bg-slate-900 text-white rounded-xl text-sm font-semibold hover:bg-slate-800 transition"
        >
          + Adicionar Cartão
        </button>
      </div>

      {/* Grid de Cartões */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {cards.map((card) => (
          <div key={card.id} className="p-6 bg-white border border-slate-200 rounded-2xl shadow-sm space-y-4 hover:border-slate-300 transition">
            <h3 className="font-bold text-lg text-slate-900">{card.name}</h3>
            <div className="text-sm text-slate-500 space-y-1">
              <p>Limite: <span className="font-medium text-slate-900">R$ {card.limit.toFixed(2)}</span></p>
              <div className="flex justify-between pt-2 text-xs border-t">
                <span>Fechamento: <b>{card.closingDay}</b></span>
                <span>Vencimento: <b>{card.dueDay}</b></span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal Overlay */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="w-full max-w-md animate-in fade-in zoom-in duration-200">
            <CreditCardForm accounts={accounts} onClose={() => setIsModalOpen(false)} />
          </div>
        </div>
      )}
    </div>
  );
}