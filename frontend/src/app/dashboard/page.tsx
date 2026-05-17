async function TransactionsDataWrapper({ accountId, filters, accounts }: { accountId: string; filters: any; accounts: any[] }) {
  try {
    if (accountId === 'all') {
      const promises = accounts.map(acc =>
        getTransactions(acc.accountId, filters)
          .catch(() => ({ content: [] }))
      );

      const results = await Promise.all(promises);
      const allTransactions = results.flatMap(r => r.content);

      allTransactions.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

      const normalizedData = {
        content: allTransactions,
        totalPages: 1,
        totalElements: allTransactions.length,
        size: allTransactions.length,
        number: 0
      };

      return <TransactionsTable data={normalizedData} accounts={accounts} isConsolidated={true} />;
    }

    const data = await getTransactions(accountId, filters);
    return <TransactionsTable data={data} accounts={accounts} isConsolidated={false} />;
  } catch (error: any) {
    console.error('=== ERRO CRÍTICO NA BUSCA DE TRANSAÇÕES ===', error);
    return (
      <div className="p-4 bg-rose-50 border border-rose-100 text-rose-700 rounded-xl text-sm font-medium">
        Não foi possível recuperar os lançamentos para a seleção atual.
      </div>
    );
  }
}
