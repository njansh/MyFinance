'use server'

import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'
import { revalidatePath } from 'next/cache'
import { getAuthToken } from './auth'

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

// Necessário porque a rota GET do backend exige o ID na URL: /users/{id}
function getUserIdFromToken(token: string): string {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(Buffer.from(payload, 'base64').toString('utf-8'));
    return decoded.sub || '';
  } catch { return ''; }
}

export async function getUserProfileAction() {
  try {
    const token = await getAuthToken()
    const userId = getUserIdFromToken(token)

    // Rota alinhada com: @GetMapping("/{id}")
    const response = await fetch(`${API_URL}/users/${userId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    return response.ok ? await response.json() : null
  } catch (error) {
    console.error("Erro ao buscar perfil:", error)
    return null
  }
}

export async function updateUserProfileAction(prevState: any, formData: FormData) {
  try {
    const token = await getAuthToken()
    const name = formData.get('name')
    const email = formData.get('email')

    // Rota alinhada com: @PutMapping("/profile")
    const response = await fetch(`${API_URL}/users/profile`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name, email })
    })

    if (!response.ok) {
      const err = await response.text()
      return { success: false, error: `Erro do servidor: ${err}` }
    }

    revalidatePath('/profile')
    return { success: true, message: 'Perfil atualizado com sucesso!', error: null }
  } catch (err: any) {
    return { success: false, error: err.message }
  }
}

export async function changePasswordAction(prevState: any, formData: FormData) {
  try {
    const token = await getAuthToken()

    // Alinhado com o ChangePasswordRequest do Java (oldPassword e newPassword)
    const oldPassword = formData.get('currentPassword')
    const newPassword = formData.get('newPassword')

    // Rota alinhada com: @PutMapping("/profile/password")
    const response = await fetch(`${API_URL}/users/profile/password`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ oldPassword, newPassword })
    })

    if (!response.ok) {
      const err = await response.text()
      return { success: false, error: `Erro do servidor: ${err}` }
    }

    return { success: true, message: 'Senha alterada com sucesso!', error: null }
  } catch (err: any) {
    return { success: false, error: err.message }
  }
}

export async function deleteUserAccountAction() {
  try {
    const token = await getAuthToken()

    // Rota alinhada com: @DeleteMapping("/me")
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

    const cookieStore = await cookies()
    cookieStore.delete('accessToken')

  } catch (error) {
    console.error("Falha ao deletar usuário:", error)
    throw error
  }

  redirect("/login")
}