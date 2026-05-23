"use client";

import { useState, useEffect } from "react";
import { getCategoriesAction, deleteCategoryAction } from "@/app/(private)/actions/category-actions";
import { CategoryForm } from "@/components/categories/category-form";
import { Tag } from "lucide-react";
import * as LucideIcons from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";

export default function CategoriesPage() {
  const [categories, setCategories] = useState<any[]>([]);
  const [editingCategory, setEditingCategory] = useState<any>(null);

  const loadCategories = async () => {
    const data = await getCategoriesAction();
    setCategories(data);
  };

  useEffect(() => { loadCategories(); }, []);

  async function handleDelete(id: string) {
    if (confirm("Tem certeza que deseja excluir esta categoria?")) {
      await deleteCategoryAction(id);
      alert("Categoria excluída com sucesso!");
      loadCategories();
    }
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6 flex items-center gap-2"><Tag className="text-emerald-600" /> Categorias</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="border p-4 rounded shadow bg-white">
          <h2 className="font-semibold mb-4">Nova Categoria</h2>
          <CategoryForm onSuccess={loadCategories} />
        </div>

        <div>
          <ul className="space-y-3">
            {(categories || []).map((c: any) => {
              const IconDisplay = (LucideIcons as any)[c.icon] || LucideIcons.Circle;
              return (
                <li key={c.id} className="flex items-center justify-between p-4 bg-white border rounded shadow-sm">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full flex items-center justify-center text-white" style={{ backgroundColor: c.colorHex }}>
                      <IconDisplay size={20} />
                    </div>
                    <p className="font-semibold">{c.name}</p>
                  </div>
                  <div className="flex gap-2">
                    <button onClick={() => setEditingCategory(c)} className="text-blue-500">Editar</button>
                    <button onClick={() => handleDelete(c.id)} className="text-rose-500">Excluir</button>
                  </div>
                </li>
              );
            })}
          </ul>
        </div>
      </div>

      <Dialog open={!!editingCategory} onOpenChange={() => setEditingCategory(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>Editar Categoria</DialogTitle></DialogHeader>
          <CategoryForm
            initialData={editingCategory}
            onSuccess={() => {
              setEditingCategory(null);
              loadCategories();
            }}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
}