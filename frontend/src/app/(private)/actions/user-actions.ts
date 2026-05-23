'use server'

import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'
import { getAuthToken } from './auth'

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export async function deleteUserAccountAction() {
  try {
    const token = await getAuthToken()

    const response = await fetch(`${API_URL}/users/me`, {
      method: "DELETE",
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      throw new Error("Erro ao excluir conta")
    }

    // 1. Limpa o cookie de autenticação para matar a sessão no frontend
    const cookieStore = await cookies()
    cookieStore.delete('accessToken')

  } catch (error) {
    console.error("Falha ao deletar usuário:", error)
    throw error // Repassa o erro se algo realmente falhou na API
  }

  // 2. O redirect do Next.js deve ficar SEMPRE fora do try/catch,
  // pois ele funciona lançando uma exceção "NEXT_REDIRECT" por baixo dos panos.
  redirect("/login")
}