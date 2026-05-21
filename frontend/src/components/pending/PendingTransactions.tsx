'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';

import {
  confirmRecurringTransactionAction
} from '../actions/recurring-actions';

interface PendingTransaction {
  id: string;
  description: string;
  amount: number;
  date: string;
}

interface PendingTransactionsProps {
  transactions: PendingTransaction[];
}

export function PendingTransactions({
  transactions
}: PendingTransactionsProps) {
  const router = useRouter();

  const [confirmingId, setConfirmingId] =
    useState<string | null>(null);

  async function handleConfirm(
    id: string,
    amount: number
  ) {
    setConfirmingId(id);

    try {
      await confirmRecurringTransactionAction(
        id,
        amount
      );

      router.refresh();
    } catch (error) {
      console.error(
        'Erro ao confirmar:',
        error
      );

      alert(
        'Falha ao confirmar o pagamento.'
      );
    } finally {
      setConfirmingId(null);
    }
  }

  if (transactions.length === 0) {
    return (
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm mt-6 text-center text-slate-400">
        Nenhuma fatura pendente para este mês.
      </div>
    );
  }

  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm mt-6">
      <h3 className="text-base font-bold text-slate-900 mb-4">
        Faturas Pendentes
      </h3>

      <div className="space-y-3">
        {transactions.map((t) => (
          <div
            key={t.id}
            className="flex justify-between items-center p-3 bg-slate-50 rounded-xl border border-slate-100"
          >
            <div>
              <p className="text-sm font-semibold text-slate-800">
                {t.description}
              </p>

              <p className="text-xs text-slate-500">
                {new Date(t.date).toLocaleDateString()}
                {' - '}
                R$ {t.amount.toFixed(2)}
              </p>
            </div>

            <button
              onClick={() =>
                handleConfirm(
                  t.id,
                  t.amount
                )
              }
              disabled={
                confirmingId === t.id
              }
              className="text-xs font-bold bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition disabled:opacity-50"
            >
              {confirmingId === t.id
                ? 'Processando...'
                : 'Pagar'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}