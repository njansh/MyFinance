'use server';
import { getAuthToken } from '@/lib/auth'; // Ajuste conforme seu projeto
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export async function getAccountsAction() {
  const token = await getAuthToken();
  const response = await fetch(`${API_BASE_URL}/accounts`, { // Ajuste conforme seu Controller
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.json();
}