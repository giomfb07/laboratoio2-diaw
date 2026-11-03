import { createContext, useContext, useState } from 'react'
import { api, gravarSessao, lerSessao } from './api.js'

const Ctx = createContext(null)
export const useAuth = () => useContext(Ctx)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(lerSessao())

  const entrar = (resposta) => { gravarSessao(resposta); setUsuario(resposta) }
  const sair = () => { gravarSessao(null); setUsuario(null) }
  const login = async (dados) => entrar(await api.login(dados))

  const value = {
    usuario,
    ehAgente: usuario?.perfil === 'AGENTE',
    ehBanco: usuario?.tipoAgente === 'BANCO',
    login, entrar, sair,
    atualizarNome: (nome) => entrar({ ...usuario, nome }),
  }
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>
}
