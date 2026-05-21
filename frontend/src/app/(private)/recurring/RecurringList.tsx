'use client';

interface Props {
  initialTemplates: any[];
}

export function RecurringList({
  initialTemplates,
}: Props) {
  if (!initialTemplates?.length) {
    return (
      <div className="bg-white p-6 rounded-2xl border border-slate-200">
        Nenhum template recorrente encontrado.
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {initialTemplates.map((template, index) => (
        <div
          key={
            template.id ||
            template.templateId ||
            index
          }
          className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm"
        >
          <div className="flex justify-between items-center">
            <div>
              <h3 className="font-bold text-slate-900">
                {template.description}
              </h3>

              <p className="text-sm text-slate-500">
                Dia {template.frequencyDay}
              </p>
            </div>

            <div className="text-right">
              <p className="font-bold text-emerald-600">
                R$ {template.expectedAmount}
              </p>

              <p className="text-xs text-slate-400">
                {template.type}
              </p>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}