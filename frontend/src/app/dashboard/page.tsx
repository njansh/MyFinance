import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'

function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value || 0)
}

export default async function DashboardPage() {
  const cookieStore = await cookies()
  const token = cookieStore.get('accessToken')?.value

  if (!token) {
    redirect('/login')
  }

  const now = new Date()
  const currentMonth = now.getMonth() + 1
  const currentYear = now.getFullYear()

  let kpis = { netWorth: 0, monthlyIncome: 0, monthlyExpense: 0 }

  try {
    const res = await fetch(`http://localhost:8080/api/dashboard/kpis?month=${currentMonth}&year=${currentYear}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      cache: 'no-store'
    })

    if (res.ok) {
      kpis = await res.json()
    } else if (res.status === 403 || res.status === 401) {
      redirect('/login')
    }
  } catch (error) {
    console.error("Erro ao conectar com a API de KPIs:", error)
  }

  return (
    <div className="min-h-screen bg-gray-100 p-4 md:p-8">
      <div className="max-w-6xl mx-auto space-y-6">

        <header className="flex justify-between items-center bg-white p-6 rounded-xl shadow-sm">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Visão Geral Financeira</h1>
            <p className="text-gray-500">Acompanhe seu fluxo de caixa de {currentMonth}/{currentYear}.</p>
          </div>

          <button className="px-4 py-2 text-sm font-medium text-red-600 bg-red-50 rounded-lg hover:bg-red-100 transition">
            Sair
          </button>
        </header>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="bg-white p-6 rounded-xl shadow-sm border-l-4 border-gray-800">
            <h3 className="text-sm font-medium text-gray-500">Saldo Atual (Patrimônio)</h3>
            <p className="text-3xl font-bold text-gray-900 mt-2">{formatCurrency(kpis.netWorth)}</p>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border-l-4 border-green-500">
            <h3 className="text-sm font-medium text-gray-500">Receitas do Mês</h3>
            <p className="text-3xl font-bold text-green-600 mt-2">{formatCurrency(kpis.monthlyIncome)}</p>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border-l-4 border-red-500">
            <h3 className="text-sm font-medium text-gray-500">Despesas do Mês</h3>
            <p className="text-3xl font-bold text-red-600 mt-2">{formatCurrency(kpis.monthlyExpense)}</p>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm min-h-[300px] flex items-center justify-center border border-dashed border-gray-300">
          <p className="text-gray-400">Os Cards acima agora exibem os dados reais do seu usuário!</p>
        </div>

      </div>
    </div>
  )
}