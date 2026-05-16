import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'
import { TransactionForm } from './form'

export default async function TransactionsPage() {
  const cookieStore = await cookies()
  const token = cookieStore.get('accessToken')?.value

  if (!token) redirect('/login')

  const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString())
  const userId = payload.sub

  // CORREÇÃO 1: Inicializando como arrays vazios para evitar erros de undefined
  let accounts: any[] = []
  let categories: any[] = []

  try {
    // CORREÇÃO 2 e 3: Desestruturando as respostas e passando as duas requisições paralelas
    const [accRes, catRes] = await Promise.all([
      fetch(`http://localhost:8080/users/${userId}/accounts`, {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` },
        cache: 'no-store'
      }),
      fetch(`http://localhost:8080/users/${userId}/categories`, {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` },
        cache: 'no-store'
      })
    ])

    if (accRes.ok) accounts = await accRes.json()
    if (catRes.ok) categories = await catRes.json()
  } catch (error) {
    console.error("Erro ao carregar dados:", error)
  }

  return (
    <div className="min-h-screen bg-gray-100 p-4 md:p-8">
      <div className="max-w-4xl mx-auto space-y-6">

        <header className="bg-white p-6 rounded-xl shadow-sm">
          <h1 className="text-2xl font-bold text-gray-900">Lançamentos</h1>
          <p className="text-gray-500">Registre suas receitas e despesas para manter seu fluxo de caixa atualizado.</p>
        </header>

        <div className="bg-white p-6 rounded-xl shadow-sm">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Novo Registro</h2>

          {accounts.length > 0 ? (
            <TransactionForm accounts={accounts} categories={categories} />
          ) : (
            <div className="text-amber-700 p-4 bg-amber-50 rounded-lg">
              Você precisa criar uma Conta Bancária antes de registrar transações.
            </div>
          )}
        </div>

      </div>
    </div>
  )
}