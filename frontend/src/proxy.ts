import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

export function proxy(request: NextRequest) {
  // Tenta extrair o token persistido anteriormente na Action de login
  const token = request.cookies.get('accessToken')?.value

  // Se não existir token, bloqueia a requisição e redireciona para login
  if (!token) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  // Se o token estiver presente, permite o acesso à rota solicitada
  return NextResponse.next()
}

// Configura o proxy para agir apenas nas rotas privadas
export const config = {
  matcher: [
    '/dashboard/:path*', 
    '/transactions/:path*', 
    '/accounts/:path*'
  ],
}