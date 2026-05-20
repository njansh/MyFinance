'use client';
import { useState } from 'react';
import { payBillingCycleAction } from '../../app/actions/credit-card-actions';

interface PaymentFormProps {
  cardId: string;
  cycleId: string;
  totalAmount: number;
  accounts: any[];
  onClose: () => void;
  onSuccess: () => void;
}

export function CreditCardPaymentForm({ cardId, cycleId, totalAmount, accounts, onClose, onSuccess }: PaymentFormProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    const formData = new FormData(e.currentTarget);

    const rawAmount = formData.get('amount') as string;
    const formattedAmount = Number(rawAmount.replace(',', '.'));

    try {
      await payBillingCycleAction(cardId, cycleId, {
        accountId: formData.get('accountId') as string,
        amount: formattedAmount,
      });
      onSuccess(); // Recarrega a fatura
    } catch (err: any) {
      setError(err.message || 'Erro ao processar o pagamento.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden">
        <div className="p-5 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Pagar Fatura</h2>
            <p className="text-xs font-medium text-slate-500">Abater saldo devedor</p>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600 transition">✕</button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {error && (
             <div className="p-3 bg-rose-50 text-rose-700 text-xs font-semibold rounded-lg border border-rose-100 break-words">
               {error}
             </div>
          )}

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Conta de Origem (Debitar)</label>
            <select required name="accountId" className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-slate-900">
              {accounts.map((acc: any) => (
                <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
              ))}
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-xs font-bold text-slate-500 uppercase tracking-wider">Valor do Pagamento (R$)</label>
            <input
              required
              name="amount"
              type="text"
              defaultValue={totalAmount.toFixed(2).replace('.', ',')}
              className="w-full h-10 px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-900 outline-none focus:ring-2 focus:ring-slate-900"
            />
            <p className="text-[10px] text-slate-400 font-medium pt-1">O valor padrão é o total da fatura, mas você pode editar para um pagamento parcial.</p>
          </div>

          <button disabled={loading} className="w-full h-11 mt-2 bg-emerald-600 text-white font-bold tracking-wide rounded-xl text-sm hover:bg-emerald-700 transition disabled:opacity-50">
            {loading ? 'Processando...' : 'Confirmar Pagamento'}
          </button>
        </form>
      </div>
    </div>
  );
}