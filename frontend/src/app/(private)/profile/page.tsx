'use client';

import { useState, useEffect, useActionState } from 'react';
import { deleteUserAccountAction, getUserProfileAction, updateUserProfileAction, changePasswordAction } from "@/app/(private)/actions/user-actions";
import { Trash2, Save, User, Lock } from "lucide-react";

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  // Isolamento das Actions de Edição e Senha
  const [profileState, profileAction, pendingProfile] = useActionState(updateUserProfileAction, { success: false, error: null, message: null });
  const [passwordState, passwordAction, pendingPassword] = useActionState(changePasswordAction, { success: false, error: null, message: null });

  // Função de carregamento isolada para ser reutilizada no refresh
  async function loadProfile() {
    const data = await getUserProfileAction();
    setUser(data);
  }

  // Carregamento inicial do componente
  useEffect(() => {
    async function init() {
      setLoading(true);
      await loadProfile();
      setLoading(false);
    }
    init();
  }, []);

  // Intervenção Cirúrgica: Dispara o refresh dos dados assim que o perfil é salvo com sucesso
  useEffect(() => {
    if (profileState?.success) {
      loadProfile();
    }
  }, [profileState]);

  return (
    <div className="p-8 max-w-5xl mx-auto space-y-8">
      <div className="border-b border-slate-100 pb-6">
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">Configurações da Conta</h1>
        <p className="text-slate-500 font-medium mt-1">Gerencie seus dados pessoais e preferências de segurança</p>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 animate-pulse">
           <div className="h-80 bg-slate-100 rounded-3xl" />
           <div className="h-80 bg-slate-100 rounded-3xl" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">

          {/* Formulário de Dados Pessoais */}
          <div className="bg-white p-8 border border-slate-100 rounded-3xl shadow-sm hover:shadow-lg transition-all">
            <div className="flex items-center gap-3 mb-8">
              <div className="p-3 bg-emerald-50 rounded-2xl text-emerald-600"><User size={24} /></div>
              <h2 className="text-xl font-bold text-slate-900">Dados Pessoais</h2>
            </div>

            <form action={profileAction} className="space-y-5">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Nome Completo</label>
                <input
                  name="name"
                  defaultValue={user?.name || ''}
                  required
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">E-mail</label>
                <input
                  name="email"
                  type="email"
                  defaultValue={user?.email || ''}
                  required
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-emerald-500 outline-none transition-all"
                />
              </div>

              {profileState?.error && <p className="text-rose-500 text-sm font-medium">{profileState.error}</p>}
              {profileState?.success && <p className="text-emerald-500 text-sm font-medium">{profileState.message}</p>}

              <button disabled={pendingProfile} className="w-full flex justify-center items-center gap-2 p-4 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl font-bold transition-all disabled:opacity-50">
                <Save size={18} /> {pendingProfile ? 'Salvando...' : 'Salvar Alterações'}
              </button>
            </form>
          </div>

          {/* Formulário de Alteração de Senha */}
          <div className="bg-white p-8 border border-slate-100 rounded-3xl shadow-sm hover:shadow-lg transition-all">
            <div className="flex items-center gap-3 mb-8">
              <div className="p-3 bg-slate-50 rounded-2xl text-slate-600"><Lock size={24} /></div>
              <h2 className="text-xl font-bold text-slate-900">Segurança da Senha</h2>
            </div>

            <form action={passwordAction} className="space-y-5">
              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Senha Atual</label>
                <input
                  name="currentPassword"
                  type="password"
                  required
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-slate-500 outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-slate-700 mb-2">Nova Senha</label>
                <input
                  name="newPassword"
                  type="password"
                  required
                  className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-slate-500 outline-none transition-all"
                />
              </div>

              {passwordState?.error && <p className="text-rose-500 text-sm font-medium">{passwordState.error}</p>}
              {passwordState?.success && <p className="text-emerald-500 text-sm font-medium">{passwordState.message}</p>}

              <button disabled={pendingPassword} className="w-full flex justify-center items-center gap-2 p-4 bg-slate-800 hover:bg-slate-900 text-white rounded-2xl font-bold transition-all disabled:opacity-50">
                <Save size={18} /> {pendingPassword ? 'Atualizando...' : 'Atualizar Senha'}
              </button>
            </form>
          </div>

        </div>
      )}

      {/* Zona de Perigo Isolada */}
      <div className="bg-white p-8 border border-rose-100 rounded-3xl shadow-sm hover:shadow-lg transition-all mt-8">
        <h2 className="text-lg font-bold text-rose-600 mb-2">Zona de Perigo</h2>
        <p className="text-slate-500 mb-6">
          Ao excluir sua conta, todos os seus dados, categorias, orçamentos e transações serão removidos permanentemente.
        </p>

        <form action={deleteUserAccountAction}>
          <button
            type="submit"
            className="flex items-center gap-2 bg-rose-50 text-rose-600 px-6 py-4 rounded-2xl font-bold hover:bg-rose-100 transition-colors"
          >
            <Trash2 size={18} />
            Excluir minha conta permanentemente
          </button>
        </form>
      </div>
    </div>
  );
}