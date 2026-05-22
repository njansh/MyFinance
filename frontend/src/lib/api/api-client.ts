const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  'http://localhost:8080';

/* =========================
   GENERIC CLIENT FETCH
========================= */

async function clientFetch(
  endpoint: string,
  options: RequestInit = {}
) {
  const response = await fetch(endpoint, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    const errorText = await response.text();

   console.error(
     'STATUS:',
     response.status
   );

   console.error(
     'TEXTO:',
     errorText
   );

   throw new Error(
     `STATUS ${response.status}: ${errorText}`
   );
  }

  return response;
}

/* =========================
   RECURRING
========================= */

export async function createRecurringTemplate(
  payload: {
    description: string;
    expectedAmount: number;
    type: 'INCOME' | 'EXPENSE';
    accountId: string;
    categoryId: string;
    frequencyDay: number;
  }
) {
  const response = await clientFetch(
    '/api/recurring',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );

  return response.json();
}

export async function deleteRecurringTemplate(
  templateId: string
) {
  const response = await clientFetch(
    `/api/recurring/${templateId}`,
    {
      method: 'DELETE',
    }
  );

  return response.json();
}

/* =========================
   TRANSFERS
========================= */

export async function createTransfer(payload: {
  fromId: string;
  toId: string;
  amount: number;
  date: string;
}) {
  const response = await clientFetch(
    '/api/transactions/transfer',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );

  return response.json();
}

/* =========================
   CREDIT CARDS
========================= */

export async function createCreditCard(
  payload: {
    name: string;
    limit: number;
    closingDay: number;
    dueDay: number;
    accountId: string;
  }
) {
  const response = await clientFetch(
    '/api/credit-cards',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    }
  );

  return response.json();
}export async function confirmTransaction(
   transactionId: string,
   actualAmount: number
 ) {
   const response = await clientFetch(
     `/api/transactions/${transactionId}/confirm`,
     {
       method: 'POST',
       body: JSON.stringify({
         actualAmount,
       }),
     }
   );

   return response.json();
 }
/* =========================
   BUDGETS (ORÇAMENTOS)
========================= */

export async function createBudget(payload: {
  categoryId: string;
  month: number;
  year: number;
  limitAmount: number;
}) {
  const response = await clientFetch('/api/budgets', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  return response.json();
}

export async function updateBudgetLimit(id: string, newLimit: number) {
  const response = await clientFetch(`/api/budgets/${id}/limit`, {
    method: 'PATCH',
    body: JSON.stringify(newLimit),
  });
  return response.json();
}

export async function deleteBudget(id: string) {
  const response = await clientFetch(`/api/budgets/${id}`, {
    method: 'DELETE',
  });
  return response;
}
