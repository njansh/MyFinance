"use server";

import { revalidatePath } from "next/cache";
import { getAuthToken } from "./auth"; // Importação unificada

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export interface CreateCategoryData {
  name: string;
  colorHex: string;
  type: "INCOME" | "EXPENSE";
}

export async function createCategoryAction(data: CreateCategoryData) {
  const token = await getAuthToken();

  const response = await fetch(`${API_URL}/categories`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(`Erro: ${err}`);
  }

  revalidatePath("/categories");
  return response.json();
}

export async function getCategoriesAction() {
  const token = await getAuthToken();
  const response = await fetch(`${API_URL}/categories`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.ok ? response.json() : [];
}
export async function deleteCategoryAction(categoryId: string) {
  const token = await getAuthToken();

  const response = await fetch(`${API_URL}/categories/${categoryId}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error("Erro ao deletar categoria");
  }

  revalidatePath("/categories");
}