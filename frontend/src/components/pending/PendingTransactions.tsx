'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';

import {
  confirmRecurringTransactionAction
} from '@/app/(private)/actions/recurring-actions';

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

  const [editingId, setEditingId] =
    useState<string | null>(null);

  const [customAmount, setCustomAmount] =
    useState<number>(0);

  const [customDate, setCustomDate] =
    useState<string>('');

  async function handleConfirm(
    id: string
  ) {
    setConfirmingId(id);

    try {
      await confirmRecurringTransactionAction(
        id,
        customAmount,
        customDate
      );

      setEditingId(null);

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

  function openEditor(
    transaction: PendingTransaction
  ) {
    setEditingId(transaction.id);

    setCustomAmount(transaction.amount);

    const formattedDate =
      new Date().toISOString().slice(0, 16);

    setCustomDate(formattedDate);
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

      <div className="space-y-4">
        {transactions.map((t) => {
          const isEditing =
            editingId === t.id;

          const isLoading =
            confirmingId === t.id;

          return (
            <div
              key={t.id}
              className="p-4 bg-slate-50 rounded-xl border border-slate-100 space-y-4"
            >
              <div className="flex justify-between items-start gap-4">
                <div>
                  <p className="text-sm font-semibold text-slate-800">
                    {t.description}
                  </p>

                  <p className="text-xs text-slate-500">
                    Vencimento:{' '}
                    {new Date(
                      t.date
                    ).toLocaleDateString()}
                  </p>

                  <p className="text-sm font-bold text-emerald-700 mt-1">
                    R$ {t.amount.toFixed(2)}
                  </p>
                </div>

                {!isEditing && (
                  <button
                    onClick={() =>
                      openEditor(t)
                    }
                    className="text-xs font-bold bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition"
                  >
                    Pagar
                  </button>
                )}
              </div>

              {isEditing && (
                <div className="space-y-3 border-t border-slate-200 pt-4">
                  {/* VALOR */}
                  <div>
                    <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">
                      Valor Pago
                    </label>

                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={customAmount}
                      onChange={(e) =>
                        setCustomAmount(
                          Number(
                            e.target.value
                          )
                        )
                      }
                      className="w-full p-3 border border-slate-200 rounded-xl text-sm"
                    />
                  </div>

                  {/* DATA */}
                  <div>
                    <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">
                      Data do Pagamento
                    </label>

                    <input
                      type="datetime-local"
                      value={customDate}
                      onChange={(e) =>
                        setCustomDate(
                          e.target.value
                        )
                      }
                      className="w-full p-3 border border-slate-200 rounded-xl text-sm"
                    />
                  </div>

                  {/* BOTÕES */}
                  <div className="flex gap-2 pt-2">
                    <button
                      onClick={() =>
                        handleConfirm(
                          t.id
                        )
                      }
                      disabled={isLoading}
                      className="flex-1 bg-emerald-600 text-white py-3 rounded-xl font-bold text-sm hover:bg-emerald-700 transition disabled:opacity-50"
                    >
                      {isLoading
                        ? 'Processando...'
                        : 'Confirmar Pagamento'}
                    </button>

                    <button
                      onClick={() =>
                        setEditingId(null)
                      }
                      disabled={isLoading}
                      className="px-4 py-3 rounded-xl bg-slate-200 text-slate-700 font-bold text-sm hover:bg-slate-300 transition"
                    >
                      Cancelar
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}