import { deleteUserAccountAction } from "@/app/(private)/actions/user-actions";
import { Trash2 } from "lucide-react";

export default function ProfilePage() {
  return (
    <div className="p-8 max-w-2xl">
      <h1 className="text-2xl font-bold mb-6">Configurações da Conta</h1>
      
      <div className="bg-white p-6 border border-rose-100 rounded-2xl">
        <h2 className="text-lg font-bold text-rose-600 mb-2">Zona de Perigo</h2>
        <p className="text-slate-500 mb-6">
          Ao excluir sua conta, todos os seus dados, categorias, orçamentos e transações serão removidos permanentemente.
        </p>

        <form action={deleteUserAccountAction}>
          <button 
            type="submit"
            className="flex items-center gap-2 bg-rose-50 text-rose-600 px-4 py-2 rounded-lg font-bold hover:bg-rose-100 transition-colors"
          >
            <Trash2 size={18} />
            Excluir minha conta permanentemente
          </button>
        </form>
      </div>
    </div>
  );
}