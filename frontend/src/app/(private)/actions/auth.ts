'use server'

import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'

export async function loginAction(prevState: any, formData: FormData) {
  const email = formData.get('email')
  const password = formData.get('password')

  if (!email || !password) {
    return { error: 'Preencha todos os campos.' }
  }

  try {
    const res = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    })

    if (!res.ok) {
      return { error: 'Credenciais inválidas. Verifique seu e-mail e senha.' }
    }

    // Extrair os cookies emitidos pelo Spring Boot e persistir no frontend
    const setCookieHeader = res.headers.getSetCookie()
    const cookieStore = await cookies()

    setCookieHeader.forEach((cookieString) => {
      const parts = cookieString.split(';').map(part => part.trim())
      const [nameValue] = parts
      const [name, value] = nameValue.split('=')

      cookieStore.set({
        name,
        value,
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        path: '/',
        sameSite: 'lax',
      })
    })

  } catch (error) {
    return { error: 'Erro interno ao conectar com a API do backend.' }
  }

  // Redireciona o usuário para o dashboard após o cookie estar salvo
  redirect('/dashboard')
}

export async function signupAction(prevState: any, formData: FormData) {
  const name = formData.get('name')
  const email = formData.get('email')
  const password = formData.get('password')

  if (!name || !email || !password) {
    return { error: 'Preencha todos os campos para criar a conta.' }
  }

  try {
    // Chama o endpoint de criação de usuário do Spring Boot (cria usuário + categorias padrão)
    const res = await fetch('http://localhost:8080/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password }),
    })

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}))
      return { error: errorData.message || 'Erro ao criar conta. O e-mail já pode estar em uso.' }
    }

  } catch (error) {
    return { error: 'Erro de rede ao tentar comunicar com o servidor.' }
  }

  return loginAction(prevState, formData)
}

export async function getAuthToken() {
  const cookieStore = await cookies()
  const token = cookieStore.get('accessToken')
  if (!token) throw new Error('UNAUTHORIZED')
  return token.value
}

export async function logoutAction() {
  const cookieStore = await cookies()
  cookieStore.delete('accessToken')
  redirect('/login')
}