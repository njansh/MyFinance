import { PaginatedTransactions, Account } from '../lib/api/api-client';

interface TableProps {
  data: PaginatedTransactions;
  accounts: Account[];
  isConsolidated: boolean;
}

export function TransactionsTable({ data, accounts, isConsolidated }: TableProps) {
  const incomeTotal = data.content.filter(t => t.type === 'INCOME').reduce((acc, t) => acc + t.amount, 0);
  const expenseTotal = data.content.filter(t => t.type === 'EXPENSE').reduce((acc, t) => acc + t.amount, 0);
  const currentBalance = data.content[0]?.accountBalanceAfter || 0;

  function getAccountName(accountId: string): string {
    const found = accounts.find(acc => acc.accountId === accountId);
    return found ? found.name : 'Outra Conta';
  }

  return (
    <div className="space-y-6">
      {!isConsolidated && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col gap-1">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Saldo em Conta</span>
            <span className="text-2xl font-bold tracking-tight text-slate-900">
              R$ {currentBalance.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </span>
          </div>
          <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col gap-1">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Entradas do Período</span>
            <span className="text-2xl font-bold tracking-tight text-emerald-600">
              + R$ {incomeTotal.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </span>
          </div>
          <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col gap-1">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Saídas do Período</span>
            <span className="text-2xl font-bold tracking-tight text-rose-600">
              - R$ {expenseTotal.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </span>
          </div>
        </div>
      )}

      {isConsolidated && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col gap-1">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total de Entradas Consolidadas</span>
            <span className="text-2xl font-bold tracking-tight text-emerald-600">
              + R$ {incomeTotal.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </span>
          </div>
          <div className="p-5 bg-white border border-slate-200/80 rounded-2xl shadow-sm flex flex-col gap-1">
            <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total de Saídas Consolidadas</span>
            <span className="text-2xl font-bold tracking-tight text-rose-600">
              - R$ {expenseTotal.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
            </span>
          </div>
        </div>
      )}

      <div className="overflow-hidden border border-slate-200/80 rounded-2xl bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="bg-slate-50/70 border-b border-slate-200/80 text-slate-500 font-semibold uppercase text-[11px] tracking-wider">
                <th className="p-4 pl-6">Data</th>
                <th className="p-4">Descrição</th>
                <th className="p-4">Conta Origem</th>
                <th className="p-4">Classificação</th>
                <th className="p-4 text-right">Valor</th>
                {!isConsolidated && <th className="p-4 text-right pr-6">Saldo Histórico</th>}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700 font-medium">
              {data.content.length === 0 ? (
                <tr>
                  <td colSpan={isConsolidated ? 5 : 6} className="p-12 text-center text-slate-400 font-medium">
                    Nenhum lançamento contábil identificado para o período corrente.
                  </td>
                </tr>
              ) : (
                data.content.map((tx: any, index) => (
                  <tr key={tx.id || tx.transactionId || index} className="hover:bg-slate-50/40 transition-colors group">
                    <td className="p-4 pl-6 whitespace-nowrap text-slate-400 font-normal text-xs">
                      {new Date(tx.date).toLocaleDateString('pt-BR', { timeZone: 'UTC' })}
                    </td>
                    <td className="p-4 text-slate-900 group-hover:text-slate-900 transition-colors">{tx.description}</td>
                    <td className="p-4 text-xs font-semibold text-slate-500 whitespace-nowrap">
                      {getAccountName(tx.accountId)}
                    </td>
                    <td className="p-4">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-lg text-xs font-semibold tracking-wide ${
                        tx.type === 'INCOME' ? 'bg-emerald-50 text-emerald-700' : 'bg-rose-50 text-rose-700'
                      }`}>
                        {tx.type === 'INCOME' ? 'Receita' : 'Despesa'}
                      </span>
                    </td>
                    <td className={`p-4 text-right font-semibold whitespace-nowrap text-base ${
                      tx.type === 'INCOME' ? 'text-emerald-600' : 'text-slate-900'
                    }`}>
                      {tx.type === 'INCOME' ? '+' : '-'} R$ {tx.amount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                    </td>
                    {!isConsolidated && (
                      <td className="p-4 text-right whitespace-nowrap text-slate-400 font-normal pr-6 text-xs">
                        R$ {(tx.accountBalanceAfter ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}