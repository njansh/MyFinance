import { getCreditCards, getAccounts } from '../../lib/api/api-server';
import { CreditCardsPageContent } from './CreditCardsPageContent';

export default async function CreditCardsPage() {
  const [cards, accounts] = await Promise.all([getCreditCards(), getAccounts()]);
  return <CreditCardsPageContent cards={cards} accounts={accounts} />;
}