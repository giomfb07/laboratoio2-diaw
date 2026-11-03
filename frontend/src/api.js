const BASE = import.meta.env.VITE_API_URL ?? ''
const CHAVE = 'aluguel.sessao'

export function lerSessao() {
  try { return JSON.parse(localStorage.getItem(CHAVE)) } catch { return null }
}
export function gravarSessao(s) {
  if (s) localStorage.setItem(CHAVE, JSON.stringify(s))
  else localStorage.removeItem(CHAVE)
}

export class ApiError extends Error {
  constructor(status, mensagem, campos = {}) {
    super(mensagem)
    this.status = status
    this.campos = campos
  }
}

async function req(metodo, caminho, corpo) {
  const headers = { 'Content-Type': 'application/json' }
  const sessao = lerSessao()
  if (sessao?.token) headers.Authorization = `Bearer ${sessao.token}`

  let resp
  try {
    resp = await fetch(`${BASE}/api${caminho}`, {
      method: metodo,
      headers,
      body: corpo === undefined ? undefined : JSON.stringify(corpo),
    })
  } catch {
    throw new ApiError(0, 'Não foi possível conectar ao servidor. Verifique se o back-end está em execução.')
  }

  if (resp.status === 401 && sessao && caminho !== '/auth/login') {
    gravarSessao(null)
    window.location.assign('/login')
  }
  if (resp.status === 204) return null

  const dados = await resp.json().catch(() => null)
  if (!resp.ok) {
    throw new ApiError(resp.status, dados?.mensagem ?? 'Algo deu errado. Tente novamente.', dados?.campos)
  }
  return dados
}

export const api = {
  login: (d) => req('POST', '/auth/login', d),
  registrarAgente: (d) => req('POST', '/auth/agentes', d),

  criarCliente: (d) => req('POST', '/clientes', d),
  listarClientes: () => req('GET', '/clientes'),
  meuCadastro: () => req('GET', '/clientes/me'),
  buscarCliente: (id) => req('GET', `/clientes/${id}`),
  atualizarCliente: (id, d) => req('PUT', `/clientes/${id}`, d),
  removerCliente: (id) => req('DELETE', `/clientes/${id}`),

  listarAutomoveis: () => req('GET', '/automoveis'),
  criarAutomovel: (d) => req('POST', '/automoveis', d),
  atualizarAutomovel: (id, d) => req('PUT', `/automoveis/${id}`, d),
  removerAutomovel: (id) => req('DELETE', `/automoveis/${id}`),

  listarPedidos: (status) => req('GET', `/pedidos${status ? `?status=${status}` : ''}`),
  buscarPedido: (id) => req('GET', `/pedidos/${id}`),
  criarPedido: (d) => req('POST', '/pedidos', d),
  modificarPedido: (id, d) => req('PUT', `/pedidos/${id}`, d),
  cancelarPedido: (id) => req('POST', `/pedidos/${id}/cancelar`),
  avaliarPedido: (id, d) => req('POST', `/pedidos/${id}/avaliacao`, d),
  executarContrato: (id, d) => req('POST', `/pedidos/${id}/contrato`, d ?? {}),
}

export const moeda = (v) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v ?? 0)

export const dataBR = (iso) => (iso ? iso.slice(0, 10).split('-').reverse().join('/') : '')
