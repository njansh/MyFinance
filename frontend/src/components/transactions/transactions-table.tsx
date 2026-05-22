'use client';

import { useState } from 'react';
import { PaginatedTransactions, Account, Category } from '../../../lib/api/api-client';
import { deleteTransactionAction, updateTransactionAction } from '@/app/(private)/actions/transactions'; // Certifique-se deste caminho

interface TableProps {
  data: PaginatedTransactions;
  accounts: Account[];
  categories: Category[];
  isConsolidated: boolean;
}

export function TransactionsTable({ data, accounts, categories, isConsolidated }: TableProps) {
  const [editingTx, setEditingTx] = useState<any | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  // Estados do Modal de Edição
  const [editDesc, setEditDesc] = useState('');
  const [editAmount, setEditAmount] = useState('');
  const [editType, setEditType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [editCategoryId, setEditCategoryId] = useState('');
  const [editDate, setEditDate] = useState('');

  // Filtra categorias dinamicamente no Modal
  const filteredCategories = categories.filter((cat: any) => cat.type === editType);

  function getAccountName(accountId: string): string {
    const found = accounts.find(acc => acc.accountId === accountId);
    return found ? found.name : 'Outra Conta';
  }

  function openEditModal(tx: any) {
    setEditingTx(tx);
    setEditDesc(tx.description);
    setEditAmount(tx.amount.toString());
    setEditType(tx.type);
    setEditCategoryId(tx.categoryId);
    setEditDate(new Date(tx.date).toISOString().slice(0, 16));
  }

  async function handleDelete(txId: string) {
    if (!confirm('Deseja realmente excluir este lançamento?')) return;
    try {
      await deleteTransactionAction(txId);
    } catch (err) {
      alert('Erro ao excluir transação.');
    }
  }

  async function handleSaveEdit(e: React.FormEvent) {
    e.preventDefault();
    if (!editingTx) return;
    try {
      setIsSaving(true);
      await updateTransactionAction(editingTx.transactionId || editingTx.id, {
        description: editDesc,
        amount: parseFloat(editAmount),
        date: new Date(editDate).toISOString(),
        type: editType,
        categoryId: editCategoryId,
        accountId: editingTx.accountId,
      });
      setEditingTx(null);
    } catch (err) {
      alert('Erro ao atualizar.');
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <div className="space-y-6">
      <div className="overflow-hidden border border-slate-200/80 rounded-2xl bg-white shadow-sm">
        <table className="w-full text-left border-collapse text-sm">
          <thead>
            <tr className="bg-slate-50/70 border-b border-slate-200/80 text-slate-500 font-semibold uppercase text-[11px] tracking-wider">
              <th className="p-4 pl-6">Data</th>
              <th className="p-4">Descrição</th>
              <th className="p-4">Conta</th>
              <th className="p-4">Tipo</th>
              <th className="p-4 text-right">Valor</th>
              <th className="p-4 text-center">Ações</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100 text-slate-700 font-medium">
            {data.content.map((tx: any) => (
              <tr key={tx.id || tx.transactionId} className="hover:bg-slate-50/40 transition-colors group">
                <td className="p-4 pl-6 text-slate-400">{new Date(tx.date).toLocaleDateString('pt-BR')}</td>
                <td className="p-4 text-slate-900">{tx.description}</td>
                <td className="p-4">{getAccountName(tx.accountId)}</td>
                <td className="p-4">
                  <span className={`px-2 py-1 rounded text-xs ${tx.type === 'INCOME' ? 'bg-emerald-50 text-emerald-700' : 'bg-rose-50 text-rose-700'}`}>
                    {tx.type === 'INCOME' ? 'Receita' : 'Despesa'}
                  </span>
                </td>
                <td className="p-4 text-right">R$ {tx.amount.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</td>
                <td className="p-4 text-center">
                  <div className="flex gap-2 justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <button onClick={() => openEditModal(tx)} className="text-blue-600 hover:text-blue-800 font-bold text-xs">Editar</button>
                    <button onClick={() => handleDelete(tx.transactionId || tx.id)} className="text-rose-600 hover:text-rose-800 font-bold text-xs">Excluir</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal de Edição (deve ser inserido aqui no JSX) */}
      {editingTx && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 p-4">
          <form onSubmit={handleSaveEdit} className="bg-white p-6 rounded-2xl w-full max-w-sm space-y-4">
            <h3 className="font-bold text-lg">Editar Lançamento</h3>
            <input value={editDesc} onChange={e => setEditDesc(e.target.value)} className="w-full p-2 border rounded" placeholder="Descrição" />
            <input type="number" value={editAmount} onChange={e => setEditAmount(e.target.value)} className="w-full p-2 border rounded" placeholder="Valor" />
            <select value={editType} onChange={e => setEditType(e.target.value as any)} className="w-full p-2 border rounded">
              <option value="EXPENSE">Despesa</option>
              <option value="INCOME">Receita</option>
            </select>
            <select value={editCategoryId} onChange={e => setEditCategoryId(e.target.value)} className="w-full p-2 border rounded">
              {filteredCategories.map((cat: any) => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
            </select>
            <div className="flex justify-end gap-2">
              <button type="button" onClick={() => setEditingTx(null)} className="px-4 py-2 text-slate-500">Cancelar</button>
              <button type="submit" className="px-4 py-2 bg-slate-900 text-white rounded">Salvar</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}