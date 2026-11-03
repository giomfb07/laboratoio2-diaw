import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'
import Campo from '../components/Campo.jsx'

export default function Login() {
  const { usuario, login } = useAuth()
  const navigate = useNavigate()
  const [f, setF] = useState({ login: '', senha: '' })
  const [erro, setErro] = useState('')
  const [ocupado, setOcupado] = useState(false)

  if (usuario) return <Navigate to="/pedidos" replace />

  async function enviar(e) {
    e.preventDefault()
    setErro(''); setOcupado(true)
    try { await login(f); navigate('/pedidos') }
    catch (err) { setErro(err.message) }
    finally { setOcupado(false) }
  }

  return (
    <div className="entrada">
      <section className="entrada-form">
        <h1>Entrar</h1>
        <form onSubmit={enviar} className="form">
          <Campo rotulo="Login">
            <input value={f.login} onChange={(e) => setF({ ...f, login: e.target.value })} autoComplete="username" autoFocus required />
          </Campo>
          <Campo rotulo="Senha">
            <input type="password" value={f.senha} onChange={(e) => setF({ ...f, senha: e.target.value })} autoComplete="current-password" required />
          </Campo>
          {erro && <p className="aviso erro-caixa" role="alert">{erro}</p>}
          <button className="btn" disabled={ocupado}>{ocupado ? 'Entrando…' : 'Entrar'}</button>
        </form>
        <p className="meta">Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link></p>
      </section>
    </div>
  )
}
