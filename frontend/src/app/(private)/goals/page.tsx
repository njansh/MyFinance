import { getGoals, getAccounts, Goal, Account } from '@/lib/api/api-server';
import { redirect } from 'next/navigation';
import { GoalsClientPage } from './goals-client-page'; // Importaremos o Client Component que criaremos a seguir

export const dynamic = 'force-dynamic';

export default async function GoalsPage() {
  let goals: Goal[] = [];
  let accounts: Account[] = [];

  try {
    // Busca Metas e Contas (para o formulário de vínculo)
    [goals, accounts] = await Promise.all([getGoals(), getAccounts()]);
  } catch (error: any) {
    if (error.message.includes('UNAUTHORIZED')) {
      redirect('/login');
    }
    console.error('Erro ao carregar dados:', error);
    return (
      <div className="p-8 text-center text-rose-600">
        <p>Erro ao carregar metas e contas. Por favor, verifique sua conexão.</p>
      </div>
    );
  }

  return <GoalsClientPage initialGoals={goals} accounts={accounts} />;
}