'use client';

import { useRouter, useSearchParams } from 'next/navigation'; // Adicione useSearchParams
import { useState } from 'react';

export function PendingTransactions({ transactions }: { transactions: any[] }) {
  const router = useRouter();
  const searchParams = useSearchParams(); // Pega os parâmetros da URL
  const [confirmingId, setConfirmingId] = useState<string | null>(null);

  async function handleConfirm(id: string) {
    setConfirmingId(id);
    const token = document.cookie.replace(/(?:(?:^|.*;\s*)accessToken\s*\=\s*([^;]*).*$)|^.*$/, "$1");

    // Agora o ID está dinâmico na chamada
    await fetch(`http://localhost:8080/transactions/${id}/confirm?actualAmount=300`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    setConfirmingId(null);
    router.refresh();
  }

  // Se não tiver transações no mês filtrado, mostra uma mensagem amigável
  if (transactions.length === 0) {
    return (
      <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm mt-6 text-center text-slate-400">
        Nenhuma fatura pendente para este mês.
      </div>
    );
  }

  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm mt-6">
      <h3 className="text-base font-bold text-slate-900 mb-4">Faturas Pendentes</h3>
      <div className="space-y-3">
        {transactions.map(t => (
          <div key={t.id} className="flex justify-between items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
            <div>
              <p className="text-sm font-semibold text-slate-800">{t.description}</p>
              <p className="text-xs text-slate-500">{new Date(t.date).toLocaleDateString()}</p>
            </div>
            <button
              onClick={() => handleConfirm(t.id)}
              disabled={confirmingId === t.id}
              className="text-xs font-bold bg-emerald-600 text-white px-4 py-2 rounded-lg hover:bg-emerald-700 transition"
            >
              {confirmingId === t.id ? '...' : 'Pagar'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}