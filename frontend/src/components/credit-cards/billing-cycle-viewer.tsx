'use client';
import { useState, useEffect, useCallback } from 'react';
import { fetchBillingCycleAction } from '../../app/(private)/actions/credit-card-actions';
import { CreditCardPaymentForm } from './credit-card-payment-form';

interface ViewerProps {
  cardId: string;
  cardName: string;
  accounts: any[]; // <-- Adicionamos as contas aqui
  onClose: () => void;
}

export function BillingCycleViewer({ cardId, cardName, accounts, onClose }: ViewerProps) {
  const now = new Date();
  const currentYear = now.getFullYear();

  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(currentYear);

  const [cycle, setCycle] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false); // Estado do Modal

  const yearsRange = Array.from({ length: 9 }, (_, i) => (currentYear - 2) + i);

  const loadCycle = useCallback(async () => {
    setLoading(true);
    setCycle(null);
    try {
      const data = await fetchBillingCycleAction(cardId, month, year);
      setCycle(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [cardId, month, year]);

  useEffect(() => {
    loadCycle();
  }, [loadCycle]);

  function handlePreviousMonth() {
    if (month === 1) { setMonth(12); setYear(year - 1); }
    else { setMonth(month - 1); }
  }

  function handleNextMonth() {
    if (month === 12) { setMonth(1); setYear(year + 1); }
    else { setMonth(month + 1); }
  }

  return (
    <div className="mt-6 p-6 border border-slate-200 rounded-2xl bg-slate-50 shadow-inner w-full relative">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-xl font-bold text-slate-900">Fatura: {cardName}</h2>
          <p className="text-sm text-slate-500 font-medium">Lançamentos e parcelas do período</p>
        </div>
        <button onClick={onClose} className="text-slate-400 hover:text-rose-600 transition font-medium text-sm">✕ Fechar Painel</button>
      </div>

      <div className="flex justify-between items-center mb-6 bg-white p-2 rounded-xl border border-slate-200 shadow-sm w-full md:w-96 md:mx-auto">
        <button onClick={handlePreviousMonth} className="p-2 text-slate-400 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M15 19l-7-7 7-7" /></svg>
        </button>
        <div className="flex gap-2">
          <select value={month} onChange={(e) => setMonth(Number(e.target.value))} className="p-1 border-none bg-transparent font-bold text-slate-700 outline-none cursor-pointer hover:bg-slate-50 rounded text-center appearance-none">
            {Array.from({ length: 12 }, (_, i) => i + 1).map(m => (
               <option key={m} value={m}>{new Date(2000, m - 1).toLocaleString('pt-BR', { month: 'long' }).toUpperCase()}</option>
            ))}
          </select>
          <span className="text-slate-300 font-bold self-center">/</span>
          <select value={year} onChange={(e) => setYear(Number(e.target.value))} className="p-1 border-none bg-transparent font-bold text-slate-700 outline-none cursor-pointer hover:bg-slate-50 rounded text-center appearance-none">
            {yearsRange.map(y => <option key={y} value={y}>{y}</option>)}
          </select>
        </div>
        <button onClick={handleNextMonth} className="p-2 text-slate-400 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M9 5l7 7-7 7" /></svg>
        </button>
      </div>

      {loading ? (
        <div className="animate-pulse h-32 bg-white rounded-xl border border-slate-200"></div>
      ) : cycle ? (
        <div className="space-y-6">
          <div className="flex justify-between items-center p-5 bg-white rounded-xl border border-slate-200 shadow-sm">
            <div>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Total da Fatura</p>
              <div className="flex items-center gap-4 mt-1">
                <p className="text-3xl font-bold text-rose-600">R$ {cycle.totalAmount?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</p>
                {/* BOTÃO DE PAGAR SÓ APARECE SE TIVER SALDO > 0 E NÃO ESTIVER PAGA */}
                {cycle.totalAmount > 0 && cycle.status !== 'PAID' && (
                  <button
                    onClick={() => setIsPaymentModalOpen(true)}
                    className="bg-emerald-100 text-emerald-700 hover:bg-emerald-200 transition px-4 py-1.5 rounded-lg text-xs font-bold tracking-wide"
                  >
                    Pagar Fatura
                  </button>
                )}
              </div>
            </div>
            <div className="text-right">
              <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Status | Vencimento</p>
              <div className="flex items-center justify-end gap-2 mt-1">
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold tracking-wider ${cycle.status === 'PAID' ? 'bg-emerald-100 text-emerald-700' : 'bg-rose-100 text-rose-700'}`}>
                  {cycle.status === 'PAID' ? 'PAGA' : 'ABERTA'}
                </span>
                <p className="text-sm font-semibold text-slate-700">{new Date(cycle.dueDate).toLocaleDateString('pt-BR', { timeZone: 'UTC' })}</p>
              </div>
            </div>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-sm">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr className="text-slate-500 font-semibold uppercase text-[11px] tracking-wider">
                  <th className="p-3 pl-4">Descrição</th>
                  <th className="p-3 text-center">Parcela</th>
                  <th className="p-3 text-center">Status</th>
                  <th className="p-3 text-right pr-4">Valor</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {cycle.items?.length === 0 ? (
                  <tr><td colSpan={4} className="p-8 text-center text-slate-400 font-medium">Nenhum lançamento registrado nesta fatura.</td></tr>
                ) : (
                  cycle.items?.map((item: any) => (
                    <tr key={item.id} className="hover:bg-slate-50/50 transition-colors">
                      <td className="p-3 pl-4 font-semibold text-slate-800">{item.description}</td>
                      <td className="p-3 text-slate-500 font-medium text-center">{item.currentInstallment} / {item.totalInstallments}</td>
                      <td className="p-3 text-center">
                        <span className={`inline-flex items-center px-2 py-1 rounded text-[10px] font-bold tracking-wider ${
                          item.status === 'PAID' ? 'bg-emerald-100 text-emerald-700' :
                          item.status === 'PARTIAL' ? 'bg-amber-100 text-amber-700' : 'bg-slate-100 text-slate-600'
                        }`}>
                          {item.status}
                        </span>
                      </td>
                      <td className="p-3 text-right pr-4 font-semibold text-slate-900">
                        R$ {item.amount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="p-10 text-center bg-white border border-dashed border-slate-300 rounded-xl">
          <p className="text-slate-500 font-medium">Nenhuma fatura encontrada para este período.</p>
        </div>
      )}

      {/* RENDERIZA O MODAL DE PAGAMENTO SE CLICADO */}
      {isPaymentModalOpen && cycle && (
        <CreditCardPaymentForm
          cardId={cardId}
          cycleId={cycle.id}
          totalAmount={cycle.totalAmount}
          accounts={accounts}
          onClose={() => setIsPaymentModalOpen(false)}
          onSuccess={() => {
            setIsPaymentModalOpen(false);
            loadCycle(); // Recarrega a tela instantaneamente
          }}
        />
      )}
    </div>
  );
}