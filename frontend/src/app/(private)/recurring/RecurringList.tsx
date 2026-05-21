'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { deleteRecurringTemplateAction } from '../actions/recurring-actions';

export function RecurringList({ initialTemplates }: any) {
  const router = useRouter();
  const [loadingId, setLoadingId] = useState<string | null>(null);

  async function handleDelete(id: string) {
    setLoadingId(id);

    try {
      await deleteRecurringTemplateAction(id);
      router.refresh();
    } catch (err) {
      console.error(err);
      alert('Erro ao deletar template');
    } finally {
      setLoadingId(null);
    }
  }

  return (
    <div className="space-y-3">
      {initialTemplates.map((t: any) => (
        <div
          key={t.id}
          className="flex justify-between p-4 bg-white border rounded-xl"
        >
          <div>
            <p className="font-semibold">{t.description}</p>
            <p className="text-sm text-slate-500">
              R$ {t.expectedAmount}
            </p>
          </div>

          <button
            onClick={() => handleDelete(t.id)}
            disabled={loadingId === t.id}
            className="text-red-600 text-sm font-bold"
          >
            {loadingId === t.id ? 'Excluindo...' : 'Excluir'}
          </button>
        </div>
      ))}
    </div>
  );
}