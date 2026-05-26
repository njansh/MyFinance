import { Sidebar } from '../../components/sidebar';
import { Toaster } from 'sonner';

export default function PrivateLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex h-screen w-full bg-slate-50 overflow-hidden font-sans antialiased">
      <Sidebar />
      <main className="flex-1 overflow-y-auto relative">
        {children}
      </main>
      <Toaster richColors position="top-right" />
    </div>
  );
}