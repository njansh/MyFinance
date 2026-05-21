import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const privateRoutes = [
  '/dashboard',
  '/extrato',
  '/transactions',
  '/accounts',
  '/recurring'
];

const authRoutes = [
  '/login',
  '/register'
];

export function middleware(
  request: NextRequest
) {
  const token =
    request.cookies.get('accessToken')?.value;

  const { pathname } = request.nextUrl;

  const isPrivateRoute =
    privateRoutes.some((route) =>
      pathname.startsWith(route)
    );

  const isAuthRoute =
    authRoutes.some((route) =>
      pathname.startsWith(route)
    );

  if (isPrivateRoute && !token) {
    return NextResponse.redirect(
      new URL('/login', request.url)
    );
  }

  if (isAuthRoute && token) {
    return NextResponse.redirect(
      new URL('/dashboard', request.url)
    );
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    '/dashboard/:path*',
    '/extrato/:path*',
    '/transactions/:path*',
    '/accounts/:path*',
    '/recurring/:path*',
    '/login',
    '/register'
  ],
};