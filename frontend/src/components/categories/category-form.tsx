"use client";

import { useForm } from "react-hook-form";
import { createCategoryAction, updateCategoryAction } from "@/app/(private)/actions/category-actions";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useState, useEffect } from "react";
import { IconPicker } from "@/components/categories/icon-picker";

export function CategoryForm({ initialData, onSuccess }: { initialData?: any, onSuccess?: () => void }) {
  const { register, handleSubmit, setValue, watch, reset } = useForm({
    defaultValues: initialData || { name: "", colorHex: "#000000", type: "EXPENSE", icon: "Circle" }
  });

  useEffect(() => {
    if (initialData) reset(initialData);
  }, [initialData, reset]);

  const [loading, setLoading] = useState(false);
  const selectedIcon = watch("icon");

  const onSubmit = async (data: any) => {
    setLoading(true);
    try {
      if (initialData?.id) {
        await updateCategoryAction(initialData.id, data);
        alert("Categoria atualizada com sucesso!"); // <--- AVISO DE ATUALIZAÇÃO AQUI
      } else {
        await createCategoryAction(data);
        reset();
        alert("Categoria criada com sucesso!"); // <--- AVISO DE CRIAÇÃO AQUI
      }

      if (onSuccess) {
        onSuccess();
      }
    } catch (error) {
      console.error(error);
      alert("Erro ao salvar categoria.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm font-medium">Nome</label>
        <Input {...register("name", { required: true })} placeholder="Ex: Alimentação" required />
      </div>

      <div>
        <label className="text-sm font-medium">Cor</label>
        <Input {...register("colorHex")} type="color" className="w-full h-10 p-1 cursor-pointer" />
      </div>

      <div>
        <label className="text-sm font-medium">Tipo</label>
        <select {...register("type")} className="w-full border p-2 rounded-md bg-white">
          <option value="EXPENSE">Despesa</option>
          <option value="INCOME">Receita</option>
        </select>
      </div>

      <div>
        <label className="text-sm font-medium">Ícone</label>
        <IconPicker
          value={selectedIcon}
          onChange={(icon) => setValue("icon", icon)}
        />
      </div>

      <Button type="submit" disabled={loading} className="w-full">
        {loading ? "Salvando..." : initialData ? "Atualizar Categoria" : "Criar Categoria"}
      </Button>
    </form>
  );
}