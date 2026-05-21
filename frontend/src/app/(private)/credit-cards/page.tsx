import { getCreditCards, getAccounts, getCategories } from '../../../lib/api/api-server';
import { CreditCardsPageContent } from './CreditCardsPageContent';

export default async function CreditCardsPage() {
  const [cards, accounts, categories] = await Promise.all([
    getCreditCards(),
    getAccounts(),
    getCategories()
  ]);

  return <CreditCardsPageContent cards={cards} accounts={accounts} categories={categories} />;
}