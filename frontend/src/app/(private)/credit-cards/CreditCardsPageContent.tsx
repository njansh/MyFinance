'use client';
import { useState } from 'react';
// CORREÇÃO DOS PATHS: Subindo 3 níveis para alcançar a pasta components na raiz
import { CreditCardForm } from '../../../components/credit-cards/credit-card-form';
import { BillingCycleViewer } from '../../../components/credit-cards/billing-cycle-viewer';
import { CreditCardPurchaseForm } from '../../../components/credit-cards/credit-card-purchase-form';

export function CreditCardsPageContent({ cards, accounts, categories }: any) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedCard, setSelectedCard] = useState<{ id: string, name: string } | null>(null);
  const [purchaseCard, setPurchaseCard] = useState<{ id: string, name: string } | null>(null);

  const cardList = Array.isArray(cards) ? cards : (cards?.content || []);

  return (
    <div className="p-8 max-w-5xl mx-auto">
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

      {/* Grid de Cartões */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
        {cardList.length > 0 ? (
          cardList.map((c: any) => (
            <div key={c.id} className={`p-5 bg-white border rounded-xl transition ${selectedCard?.id === c.id ? 'border-black ring-1 ring-black shadow-md' : 'border-slate-200 hover:shadow-md'}`}>
              <h3 className="font-bold text-lg text-slate-900">{c.name}</h3>
              <p className="text-sm text-slate-500 mt-2">Limite Total: <span className="font-medium text-slate-800">R$ {c.creditLimit}</span></p>
              <p className="text-sm text-slate-500">Disponível: <span className="font-bold text-emerald-600">R$ {c.availableLimit ?? c.creditLimit}</span></p>
              <div className="mt-4 flex gap-4 text-xs font-semibold text-slate-400">
                <span>Fechamento: Dia {c.closingDay}</span>
                <span>Vencimento: Dia {c.dueDay}</span>
              </div>

              {/* Botões de Ação do Cartão */}
              <div className="mt-4 flex gap-2">
                <button
                  onClick={() => setPurchaseCard({ id: c.id, name: c.name })}
                  className="w-full py-2 bg-black text-white font-semibold text-xs rounded-lg hover:bg-slate-800 transition"
                >
                  Nova Compra
                </button>
                <button
                  onClick={() => setSelectedCard(selectedCard?.id === c.id ? null : { id: c.id, name: c.name })}
                  className="w-full py-2 bg-slate-100 text-slate-700 font-semibold text-xs rounded-lg hover:bg-slate-200 transition"
                >
                  {selectedCard?.id === c.id ? 'Ocultar Fatura' : 'Ver Faturas'}
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="text-slate-500 col-span-full">Nenhum cartão cadastrado. Adicione seu primeiro cartão!</p>
        )}
      </div>

      {/* Visualizador */}
      {selectedCard && (
        <BillingCycleViewer
          cardId={selectedCard.id}
          cardName={selectedCard.name}
          accounts={accounts}
          onClose={() => setSelectedCard(null)}
        />
      )}

      {/* Modal de Nova Compra */}
      {purchaseCard && (
        <CreditCardPurchaseForm
          cardId={purchaseCard.id}
          cardName={purchaseCard.name}
          categories={categories || []}
          onClose={() => setPurchaseCard(null)}
        />
      )}
    </div>
  );
}