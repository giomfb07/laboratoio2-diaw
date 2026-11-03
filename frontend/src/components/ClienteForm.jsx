import { useState } from 'react'
import Campo from './Campo.jsx'

const VAZIO = { login: '', senha: '', rg: '', cpf: '', nome: '', endereco: '', profissao: '', rendimentos: [] }
const soDigitos = (v, n) => v.replace(/\D/g, '').slice(0, n)

/** Formulário compartilhado: cadastro (modo "criar") e edição (modo "editar") de cliente. */
export default function ClienteForm({ modo, inicial, onSalvar, onExcluir, rotuloSalvar }) {
  const [f, setF] = useState({ ...VAZIO, ...inicial, rendimentos: inicial?.rendimentos ?? [] })
  const [erros, setErros] = useState({})
  const [msg, setMsg] = useState('')
  const [ocupado, setOcupado] = useState(false)

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const setRend = (i, k, v) =>
    setF({ ...f, rendimentos: f.rendimentos.map((r, j) => (j === i ? { ...r, [k]: v } : r)) })

  async function enviar(e) {
    e.preventDefault()
    setErros({}); setMsg(''); setOcupado(true)
    const corpo = {
      ...f,
      rendimentos: f.rendimentos.map((r) => ({ entidadeEmpregadora: r.entidadeEmpregadora, valor: Number(r.valor) })),
    }
    if (modo === 'editar') { delete corpo.login; delete corpo.senha }
    try {
      await onSalvar(corpo)
    } catch (err) {
      setMsg(err.message); setErros(err.campos ?? {})
    } finally {
      setOcupado(false)
    }
  }

  return (
    <form onSubmit={enviar} className="form" noValidate>
      {modo === 'criar' && (
        <fieldset>
          <legend>Acesso</legend>
          <div className="grade">
            <Campo rotulo="Login" erro={erros.login}>
              <input value={f.login} onChange={set('login')} autoComplete="username" required />
            </Campo>
            <Campo rotulo="Senha" erro={erros.senha} dica="Mínimo de 6 caracteres">
              <input type="password" value={f.senha} onChange={set('senha')} autoComplete="new-password" required />
            </Campo>
          </div>
        </fieldset>
      )}

      <fieldset>
        <legend>Identificação</legend>
        <div className="grade">
          <Campo rotulo="Nome completo" erro={erros.nome}>
            <input value={f.nome} onChange={set('nome')} required />
          </Campo>
          <Campo rotulo="Profissão" erro={erros.profissao}>
            <input value={f.profissao} onChange={set('profissao')} required />
          </Campo>
          <Campo rotulo="CPF" erro={erros.cpf} dica="Somente números">
            <input inputMode="numeric" value={f.cpf} onChange={(e) => setF({ ...f, cpf: soDigitos(e.target.value, 11) })} required />
          </Campo>
          <Campo rotulo="RG" erro={erros.rg}>
            <input value={f.rg} onChange={set('rg')} required />
          </Campo>
        </div>
        <Campo rotulo="Endereço" erro={erros.endereco}>
          <input value={f.endereco} onChange={set('endereco')} required />
        </Campo>
      </fieldset>

      <fieldset>
        <legend>Rendimentos (até 3)</legend>
        {erros.rendimentos && <p className="erro">{erros.rendimentos}</p>}
        {f.rendimentos.length === 0 && <p className="vazio-linha">Nenhum rendimento informado.</p>}
        {f.rendimentos.map((r, i) => (
          <div className="grade rend" key={i}>
            <Campo rotulo="Entidade empregadora">
              <input value={r.entidadeEmpregadora} onChange={(e) => setRend(i, 'entidadeEmpregadora', e.target.value)} required />
            </Campo>
            <Campo rotulo="Rendimento mensal (R$)">
              <input type="number" min="0" step="0.01" value={r.valor} onChange={(e) => setRend(i, 'valor', e.target.value)} required />
            </Campo>
            <button type="button" className="btn-link perigo"
              onClick={() => setF({ ...f, rendimentos: f.rendimentos.filter((_, j) => j !== i) })}>
              Remover
            </button>
          </div>
        ))}
        {f.rendimentos.length < 3 && (
          <button type="button" className="btn sec"
            onClick={() => setF({ ...f, rendimentos: [...f.rendimentos, { entidadeEmpregadora: '', valor: '' }] })}>
            Adicionar rendimento
          </button>
        )}
      </fieldset>

      {msg && <p className="aviso erro-caixa" role="alert">{msg}</p>}

      <div className="acoes">
        <button className="btn" disabled={ocupado}>{ocupado ? 'Salvando…' : rotuloSalvar}</button>
        {onExcluir && (
          <button type="button" className="btn perigo" onClick={onExcluir}>Excluir cadastro</button>
        )}
      </div>
    </form>
  )
}
