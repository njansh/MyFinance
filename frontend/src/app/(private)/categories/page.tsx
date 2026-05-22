import { getCategoriesAction, deleteCategoryAction } from "@/app/(private)/actions/category-actions";
import { CategoryForm } from "@/components/categories/category-form";
import { revalidatePath } from "next/cache";
import { Tag } from "lucide-react"; // Importação do símbolo

export default async function CategoriesPage() {
  const categories = await getCategoriesAction();

  async function handleDelete(id: string) {
    'use server';
    await deleteCategoryAction(id);
    revalidatePath("/categories");
  }

  return (
    <div className="p-6">
      {/* Título com o símbolo */}
      <div className="flex items-center gap-3 mb-6">
        <Tag className="text-emerald-600" size={28} />
        <h1 className="text-2xl font-bold">Categorias</h1>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Formulário de Criação */}
        <div className="border p-4 rounded shadow bg-white">
          <h2 className="font-semibold mb-4">Nova Categoria</h2>
          <CategoryForm />
        </div>

        {/* Lista de Categorias */}
        <div>
          <h2 className="font-semibold mb-4">Suas Categorias</h2>
          <ul className="space-y-3">
            {categories.map((c: any) => (
              <li
                key={c.id}
                className="flex items-center justify-between p-4 bg-white border border-slate-100 rounded-2xl shadow-sm hover:shadow-md transition-all duration-200 group"
              >
                <div className="flex items-center gap-4">
                  <div
                    className="w-10 h-10 rounded-full flex items-center justify-center shadow-inner"
                    style={{ backgroundColor: c.colorHex }}
                  />
                  <div>
                    <p className="font-semibold text-slate-800">{c.name}</p>
                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                      {c.type === 'INCOME' ? 'Receita' : 'Despesa'}
                    </p>
                  </div>
                </div>

                <form action={handleDelete.bind(null, c.id)}>
                  <button
                    type="submit"
                    className="text-slate-300 hover:text-rose-500 transition-colors opacity-0 group-hover:opacity-100"
                    title="Excluir Categoria"
                  >
                    <span className="text-xs font-medium">Excluir</span>
                  </button>
                </form>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}