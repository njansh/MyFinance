'use client';
import { useState } from 'react';
import { createCreditCardAction } from '../../app/actions/credit-card-actions';

export function CreditCardForm({ accounts, onClose }: any) {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    const formData = new FormData(e.currentTarget);

    try {
      await createCreditCardAction({
        name: formData.get('name'),
        creditLimit: Number(formData.get('limit')),
        closingDay: Number(formData.get('closingDay')),
        dueDay: Number(formData.get('dueDay')),
        accountId: formData.get('accountId'),
      });

      onClose();
    } catch (err) {
      alert('Erro ao criar cartão de crédito.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="p-6 bg-white border shadow-xl space-y-4">
      <input name="name" placeholder="Nome" className="border p-2 w-full" required />
      <input name="limit" type="number" step="0.01" placeholder="Limite" className="border p-2 w-full" required />
      <input name="closingDay" type="number" placeholder="Fechamento" className="border p-2 w-full" required />
      <input name="dueDay" type="number" placeholder="Vencimento" className="border p-2 w-full" required />
      <select name="accountId" className="border p-2 w-full">
        {accounts.map((a: any) => <option key={a.accountId} value={a.accountId}>{a.name}</option>)}
      </select>
      <button disabled={loading} className="bg-black text-white p-2 w-full rounded hover:bg-gray-800 transition">
        {loading ? 'Salvando...' : 'Salvar Cartão'}
      </button>
    </form>
  );
}