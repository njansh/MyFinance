import Link from 'next/link';

import {
  getRecurringTemplates
} from '@/lib/api/api-server';

import { RecurringList } from './RecurringList';

export const dynamic = 'force-dynamic';

export default async function RecurringPage() {
  const templates =
    await getRecurringTemplates().catch(
      () => []
    );

  return (
    <div className="p-6 md:p-10 max-w-6xl mx-auto space-y-8 text-slate-900 antialiased">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-black">
            Despesas Recorrentes
          </h1>
        </div>

        <Link
          href="/recurring/new"
          className="bg-slate-900 text-white px-5 py-2.5 rounded-xl text-sm font-bold hover:bg-slate-800 transition"
        >
          + Novo Template
        </Link>
      </div>

      <RecurringList
        initialTemplates={templates}
      />
    </div>
  );
}