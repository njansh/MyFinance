"use client";

import React, { useState, useMemo } from "react";
import * as LucideIcons from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";

const CURATED_ICONS = [
  {
    category: "Finanças & Negócios",
    icons: [
      { name: "Wallet", tags: ["carteira", "dinheiro", "saldo"] },
      { name: "Banknote", tags: ["nota", "dinheiro", "cédula", "espécie"] },
      { name: "CreditCard", tags: ["cartão", "crédito", "débito"] },
      { name: "Landmark", tags: ["banco", "instituição", "prédio"] },
      { name: "PiggyBank", tags: ["cofre", "economia", "poupança", "guardar"] },
      { name: "TrendingUp", tags: ["investimento", "lucro", "alta", "renda"] },
      { name: "TrendingDown", tags: ["prejuízo", "queda", "baixa"] },
      { name: "DollarSign", tags: ["dólar", "dinheiro", "moeda"] },
      { name: "Percent", tags: ["porcentagem", "juros", "desconto"] },
      { name: "Briefcase", tags: ["trabalho", "emprego", "maleta", "negócios"] },
      { name: "Building2", tags: ["empresa", "escritório"] },
      { name: "Coins", tags: ["moedas", "troco", "metal"] },
      { name: "Receipt", tags: ["recibo", "comprovante", "nota fiscal"] },
      { name: "Calculator", tags: ["calculadora", "contas", "matemática"] },
      { name: "BarChart3", tags: ["gráfico", "estatística", "análise"] },
    ]
  },
  {
    category: "Moradia & Contas",
    icons: [
      { name: "Home", tags: ["casa", "lar", "moradia", "aluguel"] },
      { name: "Lightbulb", tags: ["luz", "energia", "eletricidade", "conta"] },
      { name: "Droplet", tags: ["água", "saneamento", "gota", "conta"] },
      { name: "Wifi", tags: ["internet", "wifi", "conexão", "rede", "conta"] },
      { name: "Phone", tags: ["telefone", "celular", "conta", "fatura"] },
      { name: "Sofa", tags: ["móveis", "mobilia", "decoração", "sala"] },
      { name: "Bed", tags: ["quarto", "cama", "descanso"] },
      { name: "Bath", tags: ["banheiro", "banho"] },
      { name: "PaintBucket", tags: ["reforma", "pintura", "casa"] },
      { name: "Key", tags: ["chave", "casa", "segurança"] },
      { name: "Trash2", tags: ["lixo", "limpeza", "condomínio"] },
      { name: "Hammer", tags: ["ferramenta", "conserto", "manutenção"] },
      { name: "ShieldCheck", tags: ["seguro", "proteção", "segurança"] },
    ]
  },
  {
    category: "Alimentação",
    icons: [
      { name: "Utensils", tags: ["comida", "restaurante", "alimentação", "garfo"] },
      { name: "ShoppingCart", tags: ["compras", "mercado", "supermercado", "feira"] },
      { name: "Coffee", tags: ["café", "bebida", "lanchonete", "padaria"] },
      { name: "Pizza", tags: ["pizza", "fast food", "lanche", "ifood"] },
      { name: "GlassWater", tags: ["bebida", "água", "bar"] },
      { name: "Beer", tags: ["cerveja", "bar", "bebida"] },
      { name: "Cake", tags: ["bolo", "doce", "aniversário"] },
      { name: "Apple", tags: ["fruta", "saudável", "comida"] },
      { name: "ChefHat", tags: ["cozinha", "chef", "restaurante"] },
      { name: "IceCream", tags: ["sorvete", "sobremesa", "doce"] },
      { name: "Wine", tags: ["vinho", "bebida", "álcool", "jantar"] },
      { name: "Martini", tags: ["drink", "coquetel", "festa"] },
    ]
  },
  {
    category: "Transporte & Viagem",
    icons: [
      { name: "Car", tags: ["carro", "veículo", "transporte", "combustível"] },
      { name: "Bus", tags: ["ônibus", "transporte público", "passagem"] },
      { name: "Train", tags: ["trem", "metrô"] },
      { name: "Plane", tags: ["avião", "viagem", "voo", "férias", "passagem"] },
      { name: "Fuel", tags: ["posto", "gasolina", "álcool", "combustível"] },
      { name: "Bike", tags: ["bicicleta", "pedal", "transporte"] },
      { name: "Anchor", tags: ["barco", "mar", "viagem"] },
      { name: "Ship", tags: ["navio", "transporte"] },
      { name: "Map", tags: ["mapa", "viagem", "destino"] },
      { name: "Navigation", tags: ["gps", "rota", "direção"] },
      { name: "Mountain", tags: ["trilha", "natureza", "viagem", "aventura"] },
      { name: "Umbrella", tags: ["praia", "férias", "descanso"] },
    ]
  },
  {
    category: "Saúde, Bem-estar & Pets",
    icons: [
      { name: "HeartPulse", tags: ["saúde", "médico", "coração", "hospital", "plano"] },
      { name: "Pill", tags: ["remédio", "farmácia", "medicamento"] },
      { name: "Dumbbell", tags: ["academia", "treino", "musculação", "exercício"] },
      { name: "Stethoscope", tags: ["médico", "hospital", "saúde"] },
      { name: "Syringe", tags: ["vacina", "injeção", "saúde"] },
      { name: "Dog", tags: ["pet", "cachorro", "animal", "veterinário"] },
      { name: "Cat", tags: ["pet", "gato", "animal"] },
      { name: "PawPrint", tags: ["pet", "banho", "tosa"] },
      { name: "Flower2", tags: ["beleza", "estética", "flor", "perfume"] },
      { name: "Scissors", tags: ["corte", "cabelo", "barbearia", "salão"] },
    ]
  },
  {
    category: "Lazer & Tecnologia",
    icons: [
      { name: "Gamepad2", tags: ["jogo", "videogame", "lazer", "diversão"] },
      { name: "Music", tags: ["música", "show", "festa", "spotify"] },
      { name: "Tv", tags: ["tv", "televisão", "streaming", "filme", "cinema", "netflix"] },
      { name: "Laptop", tags: ["computador", "pc", "tecnologia", "trabalho"] },
      { name: "Smartphone", tags: ["celular", "telefone", "app"] },
      { name: "Headphones", tags: ["fone", "música", "podcast"] },
      { name: "Camera", tags: ["foto", "fotografia", "recordação"] },
      { name: "Ticket", tags: ["cinema", "ingresso", "show", "teatro"] },
      { name: "Mic2", tags: ["microfone", "podcast", "evento"] },
    ]
  },
  {
    category: "Educação & Outros",
    icons: [
      { name: "GraduationCap", tags: ["educação", "faculdade", "escola", "curso", "estudo"] },
      { name: "BookOpen", tags: ["livro", "leitura", "material", "papelaria"] },
      { name: "Files", tags: ["documentos", "arquivos", "trabalho"] },
      { name: "Folder", tags: ["pasta", "arquivos"] },
      { name: "Baby", tags: ["bebê", "criança", "família"] },
      { name: "Gift", tags: ["presente", "aniversário", "festa"] },
      { name: "Trophy", tags: ["conquista", "prêmio", "jogo"] },
      { name: "Shirt", tags: ["roupa", "vestuário", "shopping", "camisa"] },
      { name: "ShoppingBag", tags: ["compras", "presentes", "loja"] },
      { name: "Sun", tags: ["sol", "praia", "férias"] },
      { name: "Moon", tags: ["noite", "descanso"] },
      { name: "Circle", tags: ["círculo", "outro"] },
      { name: "MoreHorizontal", tags: ["outros", "mais"] },
    ]
  }
];

const ALL_LUCIDE_ICON_NAMES = Object.keys(LucideIcons).filter(key =>
  typeof (LucideIcons as any)[key] === 'function' &&
  /^[A-Z]/.test(key) &&
  !['Icon', 'LucideIcon', 'createLucideIcon', 'default'].includes(key)
);

const curatedNamesSet = new Set(CURATED_ICONS.flatMap(g => g.icons.map(i => i.name)));
const remainingIconNames = ALL_LUCIDE_ICON_NAMES.filter(name => !curatedNamesSet.has(name));

export const IconPicker = ({ value, onChange }: { value: string, onChange: (icon: string) => void }) => {
  const [search, setSearch] = useState("");
  const [isOpen, setIsOpen] = useState(false);

  const iconName = typeof value === 'string' && value ? value : 'Circle';
  const SelectedIcon = (LucideIcons as any)[iconName] || LucideIcons.Circle;

  const filteredCuratedGroups = useMemo(() => {
    const term = search.toLowerCase();
    return CURATED_ICONS.map(group => ({
      ...group,
      icons: group.icons.filter(icon =>
        icon.name.toLowerCase().includes(term) || icon.tags.some(tag => tag.includes(term))
      )
    })).filter(group => group.icons.length > 0);
  }, [search]);

  const filteredRemainingIcons = useMemo(() => {
    if (!search) return [];
    const term = search.toLowerCase();
    return remainingIconNames.filter(name => name.toLowerCase().includes(term));
  }, [search]);

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
     <DialogTrigger
       className="p-3 border rounded-lg flex items-center justify-center hover:bg-slate-50 w-full bg-white transition-colors cursor-pointer"
     >
        <SelectedIcon size={24} className="text-slate-700" />
     </DialogTrigger>

      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Pesquisar ícones</DialogTitle>
        </DialogHeader>

        <Input
          placeholder="Busque por 'casa', 'comida', 'carro'..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="bg-slate-50"
        />

        <div className="max-h-[350px] overflow-y-auto space-y-6 mt-4 pr-2">
          {filteredCuratedGroups.map((group) => (
            <div key={group.category}>
              <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
                {group.category}
              </h4>
              <div className="grid grid-cols-6 gap-2">
                {group.icons.map((iconInfo) => {
                  const Icon = (LucideIcons as any)[iconInfo.name];
                  if (!Icon) return null;
                  const isSelected = iconName === iconInfo.name;

                  return (
                    <button
                      key={iconInfo.name}
                      type="button"
                      title={iconInfo.tags.join(', ')}
                      onClick={() => {
                        onChange(iconInfo.name);
                        setIsOpen(false);
                      }}
                      className={`p-3 border rounded-xl flex items-center justify-center transition-all duration-200 ${
                        isSelected
                          ? 'bg-emerald-100 border-emerald-500 text-emerald-700 shadow-sm scale-105'
                          : 'bg-white border-transparent hover:border-slate-200 hover:bg-slate-100 text-slate-600'
                      }`}
                    >
                      <Icon size={22} strokeWidth={1.5} />
                    </button>
                  );
                })}
              </div>
            </div>
          ))}

          {filteredRemainingIcons.length > 0 && (
            <div>
              <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
                Outros resultados
              </h4>
              <div className="grid grid-cols-6 gap-2">
                {filteredRemainingIcons.map((name) => {
                  const Icon = (LucideIcons as any)[name];
                  if (!Icon) return null;
                  const isSelected = iconName === name;

                  return (
                    <button
                      key={name}
                      type="button"
                      title={name}
                      onClick={() => {
                        onChange(name);
                        setIsOpen(false);
                      }}
                      className={`p-3 border rounded-xl flex items-center justify-center transition-all duration-200 ${
                        isSelected
                          ? 'bg-emerald-100 border-emerald-500 text-emerald-700 shadow-sm scale-105'
                          : 'bg-white border-transparent hover:border-slate-200 hover:bg-slate-100 text-slate-600'
                      }`}
                    >
                      <Icon size={22} strokeWidth={1.5} />
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {filteredCuratedGroups.length === 0 && filteredRemainingIcons.length === 0 && (
            <p className="text-center text-slate-500 py-10">Nenhum ícone encontrado para "{search}"</p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};