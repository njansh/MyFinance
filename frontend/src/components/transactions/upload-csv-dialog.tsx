'use client';

import { useState } from 'react';
import { Upload, Loader2, CheckCircle2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogTitle,
  DialogDescription,
  DialogClose
} from '@/components/ui/dialog';
import { toast } from 'sonner';

export function UploadCsvDialog({ onUpload }: { onUpload: (formData: FormData) => Promise<{ success: boolean; message: string }> }) {
  const [file, setFile] = useState<File | null>(null);
  const [bankCode, setBankCode] = useState('INTER');
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);

  async function handleUpload() {
    if (!file) return;
    setLoading(true);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('bankCode', bankCode);

    try {
      const result = await onUpload(formData);
      if (result.success) {
        toast.success('Sucesso!', {
          description: 'O arquivo foi enviado e está sendo processado.',
          icon: <CheckCircle2 className="text-emerald-500" />
        });
        setFile(null);
        setOpen(false);
      } else {
        toast.error('Erro', { description: result.message });
      }
    } catch (e: any) {
      toast.error('Erro ao enviar arquivo', { description: e.message });
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      {/* Removido o asChild para evitar nesting de botões inválido */}
      <DialogTrigger>
        <Button variant="outline" size="sm" type="button">
          <Upload className="size-4 mr-2" /> Importar CSV
        </Button>
      </DialogTrigger>

      <DialogContent>
        <DialogTitle className="text-lg font-bold">Importar Extrato</DialogTitle>
        <DialogDescription>
          Selecione o banco e o arquivo para processamento assíncrono.
        </DialogDescription>

        <select
          value={bankCode}
          onChange={(e) => setBankCode(e.target.value)}
          className="w-full p-2 mt-2 border rounded-md text-sm"
        >
          <option value="INTER">Banco Inter</option>
          <option value="MP">Mercado Pago</option>
        </select>

        <input
          type="file"
          accept=".csv"
          onChange={(e) => setFile(e.target.files?.[0] || null)}
          className="w-full text-sm mt-4"
        />

        <div className="flex justify-end gap-2 mt-6">
          <DialogClose asChild>
            <Button variant="outline" type="button">Cancelar</Button>
          </DialogClose>
          <Button onClick={handleUpload} disabled={!file || loading} type="button">
            {loading ? (
              <>
                <Loader2 className="size-4 animate-spin mr-2" /> Enviando...
              </>
            ) : (
              'Confirmar Envio'
            )}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}