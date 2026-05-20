'use client'

import { useActionState } from 'react'
import { signupAction } from '@/app/actions/auth'
import Link from 'next/link'

export default function SignupPage() {
  // Hook nativo para conectar o formulário com a action do servidor
  const [state, formAction, isPending] = useActionState(signupAction, null)

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-xl shadow-lg">
        <h2 className="text-3xl font-bold text-center text-gray-900">MyFinance</h2>
        <p className="text-center text-gray-500">Crie sua conta e controle seu dinheiro</p>

        <form action={formAction} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700">Nome Completo</label>
            <input
              type="text"
              name="name"
              required
              placeholder="Seu nome completo"
              className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">E-mail</label>
            <input
              type="email"
              name="email"
              required
              placeholder="seu@email.com"
              className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Senha</label>
            <input
              type="password"
              name="password"
              required
              placeholder="••••••••"
              className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition"
            />
          </div>

          {/* Exibição de erros retornados pela API */}
          {state?.error && (
            <div className="p-3 text-sm text-red-700 bg-red-100 rounded-lg">
              {state.error}
            </div>
          )}

          <button
            type="submit"
            disabled={isPending}
            className="w-full px-4 py-3 font-bold text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            {isPending? 'Criando conta e logando...' : 'Cadastrar'}
          </button>
        </form>

        <p className="text-sm text-center text-gray-600">
          Já tem uma conta?{' '}
          <Link href="/login" className="font-medium text-blue-600 hover:underline">
            Faça login aqui
          </Link>
        </p>
      </div>
    </div>
  )
}