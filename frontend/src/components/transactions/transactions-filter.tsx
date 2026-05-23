'use client';

import { useSearchParams } from 'next/navigation';

interface Account {
  accountId: string;
  name: string;
}

interface FilterProps {
  accounts: Account[];
}

export function TransactionsFilter({ accounts }: FilterProps) {
  const searchParams = useSearchParams();

  const now = new Date();
  const currentAccountId = searchParams.get('accountId') || 'all';
  const currentMonth = searchParams.get('month') || String(now.getMonth() + 1);
  const currentYear = searchParams.get('year') || String(now.getFullYear());
  const currentDesc = searchParams.get('desc') || '';

  function handleChange(name: string, value: string) {
    const params = new URLSearchParams(window.location.search);
    params.set(name, value);
    params.set('page', '0');

    if (!params.get('accountId') && currentAccountId) {
      params.set('accountId', currentAccountId);
    }
    if (!params.get('month')) params.set('month', currentMonth);
    if (!params.get('year')) params.set('year', currentYear);

    window.location.href = `?${params.toString()}`;
  }

  return (
    <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm gap-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 items-end">
      <div className="space-y-1.5">
        <label className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Conta Alvo</label>
        <select name="accountId" value={currentAccountId} onChange={(e) => handleChange('accountId', e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm bg-slate-50 text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 h-10 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
          <option value="all">Todas as Contas (Consolidado)</option>
          {accounts.map((acc) => (
            <option key={acc.accountId} value={acc.accountId}>
              {acc.name}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-1.5">
        <label className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Mês de Referência</label>
        <select name="month" value={currentMonth} onChange={(e) => handleChange('month', e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm bg-slate-50 text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 h-10 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
          {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
            <option key={m} value={m}>
              {new Date(2000, m - 1).toLocaleString('pt-BR', { month: 'long' })}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-1.5">
        <label className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Ano</label>
        <select name="year" value={currentYear} onChange={(e) => handleChange('year', e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm bg-slate-50 text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 h-10 appearance-none bg-[url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%2364748B%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E')] bg-[length:0.65rem_auto] bg-[right_0.75rem_center] bg-no-repeat pr-8">
          {[2025, 2026, 2027].map((y) => (
            <option key={y} value={y}>
              {y}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-1.5">
        <label className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Filtro por Termo</label>
        <input name="desc" placeholder="Filtrar lançamento..." value={currentDesc} onChange={(e) => handleChange('desc', e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm bg-slate-50 placeholder-slate-400 text-slate-800 font-medium focus:outline-none focus:ring-2 focus:ring-slate-900 transition-all h-10" />
      </div>
    </div>
  );
}