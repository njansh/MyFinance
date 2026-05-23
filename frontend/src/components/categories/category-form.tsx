"use client";

import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import { createCategoryAction } from "@/app/(private)/actions/category-actions";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useState } from "react";

export function CategoryForm() {
  const { register, handleSubmit } = useForm();
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const onSubmit = async (data: any) => {
    setLoading(true);
    try {
      await createCategoryAction({
        name: data.name,
        colorHex: data.colorHex,
        type: data.type,
      });
      // Limpa os campos após sucesso (opcional) e atualiza a página
      router.refresh();
    } catch (error) {
      console.error("Erro ao criar:", error);
      alert("Erro ao criar categoria.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm font-medium">Nome</label>
        <Input {...register("name")} placeholder="Ex: Alimentação" required />
      </div>

      <div>
        <label className="text-sm font-medium">Cor</label>
        <Input {...register("colorHex")} type="color" className="w-full h-10 p-1" />
      </div>

      <div>
        <label className="text-sm font-medium">Tipo</label>
        <select {...register("type")} className="w-full border p-2 rounded-md">
          <option value="EXPENSE">Despesa</option>
          <option value="INCOME">Receita</option>
        </select>
      </div>

      <Button type="submit" disabled={loading} className="w-full">
        {loading ? "Salvando..." : "Criar Categoria"}
      </Button>
    </form>
  );
}