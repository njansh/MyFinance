'use server'

import { cookies } from 'next/headers'
import { revalidatePath } from 'next/cache'

export async function createTransactionAction(prevState: any, formData: FormData) {
  const cookieStore = await cookies()
  const token = cookieStore.get('accessToken')?.value

  if (!token) {
    return { error: 'Usuário não autenticado.' }
  }

  const description = formData.get('description')
  const amount = formData.get('amount')
  const type = formData.get('type')
  const accountId = formData.get('accountId')
  const categoryId = formData.get('categoryId') // Recuperando a categoria

  const dateStr = formData.get('date') as string
  let timeStr = formData.get('time') as string

  // Se o usuário não preencheu o horário, resgatamos a hora atual do sistema
  if (!timeStr) {
    const now = new Date()
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    timeStr = `${hours}:${minutes}:${seconds}`
  } else if (timeStr.length === 5) {
    timeStr += ':00' // Adiciona os segundos para garantir que o Spring Boot aceite (HH:mm:ss)
  }

  // Combina a data escolhida com a hora para gerar o formato ISO 8601
  const finalDateTime = new Date(`${dateStr}T${timeStr}`).toISOString()

  try {
    const res = await fetch('http://localhost:8080/transactions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        description,
        amount: parseFloat(amount as string),
        date: finalDateTime,
        type,
        accountId,
        categoryId, // Passando a categoria corretamente para a API
        isTransfer: false
      })
    })

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}))
      return { error: errorData.message || 'Erro ao registrar lançamento.' }
    }

    // Força o Next.js a limpar o cache do Dashboard para atualizar os saldos
    revalidatePath('/dashboard')
    revalidatePath('/transactions')

    return { success: 'Lançamento registrado com sucesso!' }
  } catch (error) {
    return { error: 'Erro de rede ao conectar com a API.' }
  }
}