import { RecurringForm } from '../RecurringForm';

import {
  getAccounts,
  getCategories,
} from '@/lib/api/api-server';

export default async function NewRecurringPage() {
  const [accounts, categories] =
    await Promise.all([
      getAccounts().catch(() => []),
      getCategories().catch(() => []),
    ]);

  return (
    <div className="p-6 md:p-10 max-w-2xl mx-auto">
      <h1 className="text-2xl font-black text-slate-900 mb-6">
        Configurar Nova Recorrência
      </h1>

      <RecurringForm
        accounts={accounts}
        categories={categories}
      />
    </div>
  );
}