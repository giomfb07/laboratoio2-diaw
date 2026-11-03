import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { api } from '../api.js'
import { useAuth } from '../AuthContext.jsx'
import ClienteForm from '../components/ClienteForm.jsx'
import Campo from '../components/Campo.jsx'

export default function Cadastro() {
  const { usuario, login, entrar } = useAuth()
  const navigate = useNavigate()
  const [tipo, setTipo] = useState('cliente')

  if (usuario) return <Navigate to="/pedidos" replace />

  return (
    <div className="pagina estreita">
      <h1>Criar conta</h1>
      <div className="abas" role="tablist">
        <button role="tab" aria-selected={tipo === 'cliente'} onClick={() => setTipo('cliente')}>Sou cliente</button>
        <button role="tab" aria-selected={tipo === 'agente'} onClick={() => setTipo('agente')}>Sou empresa ou banco</button>
      </div>

      {tipo === 'cliente' ? (
        <ClienteForm
          modo="criar"
          rotuloSalvar="Criar conta"
          onSalvar={async (dados) => {
            await api.criarCliente(dados)
            await login({ login: dados.login, senha: dados.senha })
            navigate('/pedidos')
          }}
        />
      ) : (
        <AgenteForm onCriado={(r) => { entrar(r); navigate('/pedidos') }} />
      )}
      <p className="meta">Já tem conta? <Link to="/login">Entrar</Link></p>
    </div>
  )
}

function AgenteForm({ onCriado }) {
  const [f, setF] = useState({ login: '', senha: '', nome: '', cnpj: '', tipo: 'EMPRESA' })
  const [erros, setErros] = useState({})
  const [msg, setMsg] = useState('')
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setErros({}); setMsg('')
    try { onCriado(await api.registrarAgente(f)) }
    catch (err) { setMsg(err.message); setErros(err.campos ?? {}) }
  }

  return (
    <form onSubmit={enviar} className="form" noValidate>
      <fieldset>
        <legend>Acesso</legend>
        <div className="grade">
          <Campo rotulo="Login" erro={erros.login}><input value={f.login} onChange={set('login')} required /></Campo>
          <Campo rotulo="Senha" erro={erros.senha} dica="Mínimo de 6 caracteres">
            <input type="password" value={f.senha} onChange={set('senha')} required />
          </Campo>
        </div>
      </fieldset>
      <fieldset>
        <legend>Instituição</legend>
        <div className="grade">
          <Campo rotulo="Razão social" erro={erros.nome}><input value={f.nome} onChange={set('nome')} required /></Campo>
          <Campo rotulo="CNPJ" erro={erros.cnpj} dica="Somente números">
            <input inputMode="numeric" value={f.cnpj}
              onChange={(e) => setF({ ...f, cnpj: e.target.value.replace(/\D/g, '').slice(0, 14) })} required />
          </Campo>
          <Campo rotulo="Tipo">
            <select value={f.tipo} onChange={set('tipo')}>
              <option value="EMPRESA">Empresa</option>
              <option value="BANCO">Banco</option>
            </select>
          </Campo>
        </div>
      </fieldset>
      {msg && <p className="aviso erro-caixa" role="alert">{msg}</p>}
      <div className="acoes"><button className="btn">Criar conta</button></div>
    </form>
  )
}
