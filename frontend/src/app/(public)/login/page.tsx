'use client'

import { useActionState } from 'react'
import { loginAction } from '../../(private)/actions/auth'
import Link from 'next/link'

export default function LoginPage() {
  const [state, formAction, isPending] = useActionState(loginAction, null)

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 p-4 antialiased text-slate-900">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-2xl border border-slate-200/80 shadow-xl">
        <div className="space-y-2 text-center">
          <h2 className="text-3xl font-black tracking-tight text-slate-900">My<span className="text-emerald-600">Finance</span></h2>
          <p className="text-sm font-medium text-slate-400">Entre para gerenciar seu dinheiro com inteligência</p>
        </div>

        <form action={formAction} className="space-y-5">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-bold uppercase tracking-wider text-slate-400">E-mail</label>
            <input
              type="email"
              name="email"
              required
              placeholder="seu@email.com"
              className="w-full px-4 py-2.5 mt-1 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-700 shadow-sm focus:outline-none focus:ring-2 focus:ring-slate-900 focus:border-slate-900 transition duration-200"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-bold uppercase tracking-wider text-slate-400">Senha</label>
            <input
              type="password"
              name="password"
              required
              placeholder="••••••••"
              className="w-full px-4 py-2.5 mt-1 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-700 shadow-sm focus:outline-none focus:ring-2 focus:ring-slate-900 focus:border-slate-900 transition duration-200"
            />
          </div>

          {state?.error && (
            <div className="p-3.5 text-xs font-semibold text-rose-700 bg-rose-50 border border-rose-100 rounded-xl animate-in fade-in duration-200">
              {state.error}
            </div>
          )}

          <button
            type="submit"
            disabled={isPending}
            className="w-full px-4 py-3 font-bold text-white bg-slate-900 rounded-xl hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-sm shadow-slate-900/10"
          >
            {isPending ? 'Autenticando...' : 'Acessar Conta'}
          </button>
        </form>

        <p className="text-xs font-semibold text-center text-slate-400">
          Ainda não tem uma conta?{' '}
          <Link href="/signup" className="text-emerald-600 hover:text-emerald-700 hover:underline transition">
            Crie uma agora
          </Link>
        </p>
      </div>
    </div>
  )
}