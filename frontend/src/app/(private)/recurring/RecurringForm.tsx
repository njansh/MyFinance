'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

import {
  createRecurringTemplateAction
} from '../actions/recurring-actions';

interface Account {
  accountId: string;
  name: string;
}

interface Category {
  categoryId?: string;
  id?: string;
  name: string;
  type?: string; // Adicionado para permitir o filtro
}

interface RecurringFormProps {
  accounts: Account[];
  categories: Category[];
}

export function RecurringForm({
  accounts,
  categories,
}: RecurringFormProps) {
  const router = useRouter();

  const [loading, setLoading] = useState(false);

  // Novos estados para controlar o filtro dinâmico
  const [type, setType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [categoryId, setCategoryId] = useState('');

  // Filtra as categorias com base no tipo selecionado (Receita ou Despesa)
  const filteredCategories = categories.filter(
    (cat) => cat.type === type
  );

  async function handleSubmit(
    e: React.FormEvent<HTMLFormElement>
  ) {
    e.preventDefault();

    setLoading(true);

    try {
      const formData = new FormData(
        e.currentTarget
      );

      const payload = {
        description: String(
          formData.get('description') || ''
        ).trim(),

        expectedAmount: Number(
          formData.get('amount')
        ),

        frequencyDay: Number(
          formData.get('day')
        ),

        accountId: String(
          formData.get('accountId') || ''
        ),

        categoryId: String(
          formData.get('categoryId') || ''
        ),

        type: String(
          formData.get('type') || ''
        ) as 'INCOME' | 'EXPENSE',
      };

      console.log(
        'Payload enviado:',
        JSON.stringify(payload, null, 2)
      );

      // VALIDAÇÕES

      if (!payload.description) {
        throw new Error(
          'Descrição obrigatória'
        );
      }

      if (!payload.accountId) {
        throw new Error(
          'Conta obrigatória'
        );
      }

      if (!payload.categoryId) {
        throw new Error(
          'Categoria obrigatória'
        );
      }

      if (
        Number.isNaN(payload.expectedAmount) ||
        payload.expectedAmount <= 0
      ) {
        throw new Error(
          'Valor inválido'
        );
      }

      if (
        Number.isNaN(payload.frequencyDay) ||
        payload.frequencyDay < 1 ||
        payload.frequencyDay > 31
      ) {
        throw new Error(
          'Dia de vencimento inválido'
        );
      }

      await createRecurringTemplateAction(
        payload
      );

      router.push('/recurring');

      router.refresh();
    } catch (err) {
      console.error(
        'Erro ao salvar template:',
        err
      );

      if (err instanceof Error) {
        alert(err.message);
      } else {
        alert(
          'Erro ao salvar template recorrente'
        );
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4"
    >
      {/* DESCRIÇÃO */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Descrição
        </label>

        <input
          name="description"
          type="text"
          placeholder="Ex: Diárias"
          className="w-full p-3 border rounded-xl"
          required
        />
      </div>

      {/* VALOR */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Valor
        </label>

        <input
          name="amount"
          type="number"
          step="0.01"
          min="0"
          placeholder="0.00"
          className="w-full p-3 border rounded-xl"
          required
        />
      </div>

      {/* TIPO */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Tipo
        </label>

        <select
          name="type"
          value={type}
          onChange={(e) => {
            setType(e.target.value as 'INCOME' | 'EXPENSE');
            setCategoryId(''); // Limpa a categoria ao mudar de tipo
          }}
          className="w-full p-3 border rounded-xl"
          required
        >
          <option value="EXPENSE">
            Saída (Despesa)
          </option>

          <option value="INCOME">
            Entrada (Receita)
          </option>
        </select>
      </div>

      {/* DIA */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Dia do Vencimento
        </label>

        <input
          name="day"
          type="number"
          min="1"
          max="31"
          placeholder="Ex: 20"
          className="w-full p-3 border rounded-xl"
          required
        />
      </div>

      {/* CONTA */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Conta
        </label>

        <select
          name="accountId"
          className="w-full p-3 border rounded-xl"
          required
        >
          <option value="">Selecione uma conta...</option>
          {accounts?.map(
            (account, index) => (
              <option
                key={
                  account.accountId ||
                  `acc-${index}`
                }
                value={account.accountId}
              >
                {account.name}
              </option>
            )
          )}
        </select>
      </div>

      {/* CATEGORIA */}
      <div>
        <label className="block text-sm font-medium mb-1">
          Categoria
        </label>

        <select
          name="categoryId"
          value={categoryId}
          onChange={(e) => setCategoryId(e.target.value)}
          className="w-full p-3 border rounded-xl"
          required
        >
          <option value="">Selecione uma categoria...</option>
          {filteredCategories?.map(
            (category, index) => (
              <option
                key={
                  category.categoryId ||
                  category.id ||
                  `cat-${index}`
                }
                value={
                  category.categoryId ||
                  category.id
                }
              >
                {category.name}
              </option>
            )
          )}
        </select>
      </div>

      {/* BOTÃO */}
      <button
        type="submit"
        disabled={loading}
        className="w-full bg-emerald-600 text-white p-3 rounded-xl font-bold hover:bg-emerald-700 transition disabled:opacity-50"
      >
        {loading
          ? 'Salvando...'
          : 'Criar Template'}
      </button>
    </form>
  );
}