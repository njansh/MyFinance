'use client'

import { useActionState } from 'react'
import { createTransactionAction } from '@/app/actions/transactions'

interface Account {
  accountId: string;
  name: string;
}

interface Category {
  id: string;
  name: string;
}

export function TransactionForm({ accounts, categories }: { accounts: Account, categories: Category }) {
  const [state, formAction, isPending] = useActionState(createTransactionAction, null)

  return (
    <form action={formAction} className="space-y-5">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="md:col-span-2">
          <label className="block text-sm font-medium text-gray-700">Descrição</label>
          <input type="text" name="description" required placeholder="Ex: Supermercado" className="w-full mt-1 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none" />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Valor (R$)</label>
          <input type="number" step="0.01" name="amount" required placeholder="0.00" className="w-full mt-1 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none" />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Tipo</label>
          <select name="type" className="w-full mt-1 p-3 border border-gray-300 rounded-lg bg-white focus:ring-2 focus:ring-blue-600 outline-none">
            <option value="EXPENSE">Despesa (Saída)</option>
            <option value="INCOME">Receita (Entrada)</option>
          </select>
        </div>

        {/* Campo com ícone nativo de calendário */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Data <span className="text-red-500">*</span></label>
          <input type="date" name="date" required className="w-full mt-1 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none" />
        </div>

        {/* Campo de horário opcional */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Horário <span className="text-gray-400 text-xs">(Opcional)</span></label>
          <input type="time" name="time" className="w-full mt-1 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600 outline-none" />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Conta Bancária</label>
          <select name="accountId" required className="w-full mt-1 p-3 border border-gray-300 rounded-lg bg-white focus:ring-2 focus:ring-blue-600 outline-none">
            <option value="">Selecione uma conta...</option>
            {accounts.map(acc => (
              <option key={acc.accountId} value={acc.accountId}>{acc.name}</option>
            ))}
          </select>
        </div>

        {/* Novo seletor de Categoria */}
        <div>
          <label className="block text-sm font-medium text-gray-700">Categoria</label>
          <select name="categoryId" required className="w-full mt-1 p-3 border border-gray-300 rounded-lg bg-white focus:ring-2 focus:ring-blue-600 outline-none">
            <option value="">Selecione a categoria...</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>
      </div>

      {state?.error && <div className="text-red-700 text-sm p-3 bg-red-100 rounded-lg">{state.error}</div>}
      {state?.success && <div className="text-green-700 text-sm p-3 bg-green-100 rounded-lg">{state.success}</div>}

      <button type="submit" disabled={isPending} className="w-full py-3 px-4 bg-blue-600 text-white font-bold rounded-lg hover:bg-blue-700 transition disabled:opacity-50">
        {isPending? 'Processando...' : 'Registrar Lançamento'}
      </button>
    </form>
  )
}