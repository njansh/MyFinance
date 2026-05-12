"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Wallet } from "lucide-react"

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center p-4 bg-zinc-50 dark:bg-zinc-950">
      <Card className="w-full max-w-sm shadow-lg border-zinc-200 dark:border-zinc-800">
        <CardHeader className="space-y-2 text-center pb-4">
          <div className="flex justify-center mb-2">
            <div className="p-3 bg-zinc-900 dark:bg-zinc-100 rounded-full text-zinc-50 dark:text-zinc-900">
              <Wallet className="w-8 h-8" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold tracking-tight">MyFinance</CardTitle>
          <CardDescription className="text-zinc-500">
            Acesse sua conta ou cadastre-se gratuitamente.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue="login" className="w-full">

            {/* Botões para alternar as abas */}
            <TabsList className="grid w-full grid-cols-2 mb-6">
              <TabsTrigger value="login">Entrar</TabsTrigger>
              <TabsTrigger value="register">Criar Conta</TabsTrigger>
            </TabsList>

            {/* FORMULÁRIO DE LOGIN */}
            <TabsContent value="login">
              <form className="space-y-4">
                <div className="space-y-2 text-left">
                  <Label htmlFor="email">E-mail</Label>
                  <Input id="email" type="email" placeholder="seu@email.com" required />
                </div>
                <div className="space-y-2 text-left">
                  <Label htmlFor="password">Senha</Label>
                  <Input id="password" type="password" required />
                </div>
                <Button className="w-full font-semibold" type="submit">
                  Entrar no Painel
                </Button>
              </form>
            </TabsContent>

            {/* FORMULÁRIO DE CADASTRO */}
            <TabsContent value="register">
              <form className="space-y-4">
                <div className="space-y-2 text-left">
                  <Label htmlFor="name-register">Nome Completo</Label>
                  <Input id="name-register" type="text" placeholder="João da Silva" required />
                </div>
                <div className="space-y-2 text-left">
                  <Label htmlFor="email-register">E-mail</Label>
                  <Input id="email-register" type="email" placeholder="seu@email.com" required />
                </div>
                <div className="space-y-2 text-left">
                  <Label htmlFor="password-register">Criar Senha</Label>
                  <Input id="password-register" type="password" required />
                </div>
                <Button className="w-full font-semibold" type="submit">
                  Concluir Cadastro
                </Button>
              </form>
            </TabsContent>

          </Tabs>
        </CardContent>
      </Card>
    </div>
  )
}