'use client';
import { useState } from 'react';
import { CreditCardForm } from '../../components/credit-cards/credit-card-form';

export function CreditCardsPageContent({ cards, accounts }: any) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const cardList = Array.isArray(cards) ? cards : (cards?.content || []);

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Meus Cartões</h1>
        <button onClick={() => setIsModalOpen(true)} className="bg-black text-white px-4 py-2 rounded-lg font-medium hover:bg-slate-800 transition shadow-sm">
          Adicionar Novo
        </button>
      </div>

      {isModalOpen && (
        <div className="mb-8">
          <CreditCardForm accounts={accounts} onClose={() => setIsModalOpen(false)} />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
        {cardList.length > 0 ? (
          cardList.map((c: any) => (
            <div key={c.id} className="p-5 bg-white border border-slate-200 rounded-xl shadow-sm hover:shadow-md transition">
              <h3 className="font-bold text-lg text-slate-900">{c.name}</h3>
              {/* O Backend agora devolve creditLimit e availableLimit */}
              <p className="text-sm text-slate-500 mt-2">Limite Total: <span className="font-medium text-slate-800">R$ {c.creditLimit}</span></p>
              <p className="text-sm text-slate-500">Disponível: <span className="font-bold text-emerald-600">R$ {c.availableLimit || c.creditLimit}</span></p>
              <div className="mt-4 flex gap-4 text-xs font-semibold text-slate-400">
                <span>Fechamento: Dia {c.closingDay}</span>
                <span>Vencimento: Dia {c.dueDay}</span>
              </div>
            </div>
          ))
        ) : (
          <p className="text-slate-500 col-span-full">Nenhum cartão cadastrado. Adicione seu primeiro cartão!</p>
        )}
      </div>
    </div>
  );
}