'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { logoutAction } from '../app/(private)/actions/auth';

export function Sidebar() {
  const pathname = usePathname() || '';

  const navItems = [
      { name: 'Dashboard', href: '/dashboard', icon: '📊' },
      { name: 'Extrato', href: '/extrato', icon: '📄' },
      { name: 'Transações', href: '/transactions', icon: '💸' },
      { name: 'Cartões', href: '/credit-cards', icon: '💳' },
      { name: 'Recorrências', href: '/recurring', icon: '🔄' },
      { name: 'Orçamentos', href: '/budgets', icon: '🎯' },
    ];

  return (
    <aside className="w-64 bg-white border-r border-slate-200 flex-col hidden md:flex h-full shadow-sm z-10">
      <div className="p-6 border-b border-slate-100 flex items-center justify-center">
        <h2 className="text-2xl font-black tracking-tight text-slate-900">
          My<span className="text-emerald-600">Finance</span>
        </h2>
      </div>

      <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
        <p className="px-4 text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2 mt-2">Menu Principal</p>

        {navItems.map((item) => {
          // A lógica isActive garante que o item fique selecionado mesmo em subrotas
          const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
          return (
            <Link
              key={item.name}
              href={item.href}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all duration-200 ${
                isActive
                  ? 'bg-slate-900 text-white shadow-md scale-[1.02]'
                  : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'
              }`}
            >
              <span>{item.icon}</span>
              {item.name}
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-slate-100">
        <form action={logoutAction} className="w-full">
          <button
            type="submit"
            className="flex items-center justify-center w-full px-4 py-2.5 text-sm font-bold text-rose-600 bg-rose-50 border border-rose-100 rounded-xl hover:bg-rose-100 hover:text-rose-700 transition-colors cursor-pointer"
          >
            Sair do Sistema
          </button>
        </form>
      </div>
    </aside>
  );
}