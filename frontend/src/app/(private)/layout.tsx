import { Sidebar } from '../../components/sidebar';

export default function PrivateLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen w-full bg-slate-50 overflow-hidden font-sans antialiased">
      {/* O Menu Lateral Fixo */}
      <Sidebar />

      {/* A Área Central (onde o Next.js vai injetar os pages de Dashboard, Extrato, etc) */}
      <main className="flex-1 overflow-y-auto relative">
        {children}
      </main>
    </div>
  );
}