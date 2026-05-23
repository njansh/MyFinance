'use client';

import { useRouter, useSearchParams } from 'next/navigation';

export function DashboardFilter() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const now = new Date();
  const currentMonth = searchParams.get('month') || String(now.getMonth() + 1);
  const currentYear = searchParams.get('year') || String(now.getFullYear());

  const months = [
    { value: '1', label: 'Janeiro' },
    { value: '2', label: 'Fevereiro' },
    { value: '3', label: 'Março' },
    { value: '4', label: 'Abril' },
    { value: '5', label: 'Maio' },
    { value: '6', label: 'Junho' },
    { value: '7', label: 'Julho' },
    { value: '8', label: 'Agosto' },
    { value: '9', label: 'Setembro' },
    { value: '10', label: 'Outubro' },
    { value: '11', label: 'Novembro' },
    { value: '12', label: 'Dezembro' },
  ];

  // Gera um array dinâmico com 11 anos (5 anos no passado + ano atual + 5 anos no futuro)
  const years = Array.from({ length: 11 }, (_, i) => String(now.getFullYear() - 5 + i));

  function handleFilterChange(key: 'month' | 'year', value: string) {
    const params = new URLSearchParams(searchParams.toString());
    params.set(key, value);
    router.push(`?${params.toString()}`);
  }

  return (
    <div className="flex flex-wrap gap-3 items-center bg-slate-50 p-4 rounded-2xl border border-slate-200/60 shadow-sm">
      <div className="flex flex-col gap-1">
        <label className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Mês de Referência</label>
        <select
          value={currentMonth}
          onChange={(e) => handleFilterChange('month', e.target.value)}
          className="bg-white border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold text-slate-700 shadow-sm focus:outline-none focus:ring-1 focus:ring-black focus:border-black"
        >
          {months.map((m) => (
            <option key={m.value} value={m.value}>{m.label}</option>
          ))}
        </select>
      </div>

      <div className="flex flex-col gap-1">
        <label className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Ano</label>
        <select
          value={currentYear}
          onChange={(e) => handleFilterChange('year', e.target.value)}
          className="bg-white border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold text-slate-700 shadow-sm focus:outline-none focus:ring-1 focus:ring-black focus:border-black"
        >
          {years.map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>
    </div>
  );
}