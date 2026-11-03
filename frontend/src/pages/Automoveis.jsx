import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../AuthContext.jsx'
import Campo from '../components/Campo.jsx'

const VAZIO = { matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '' }
const DONO = { CLIENTE: 'Cliente', EMPRESA: 'Empresa', BANCO: 'Banco' }

export default function Automoveis() {
  const { usuario, ehAgente } = useAuth()
  const [lista, setLista] = useState(null)
  const [editando, setEditando] = useState(null) // null | 'novo' | id
  const [f, setF] = useState(VAZIO)
  const [erros, setErros] = useState({})
  const [msg, setMsg] = useState('')

  const carregar = useCallback(() => { api.listarAutomoveis().then(setLista).catch((e) => setMsg(e.message)) }, [])
  useEffect(carregar, [carregar])

  // Dono = quem cadastrou (cliente logado ou agente logado).
  const meu = (c) => (ehAgente ? c.tipoProprietario !== 'CLIENTE' : c.tipoProprietario === 'CLIENTE') && c.proprietarioId === usuario.id

  const abrir = (c) => { setEditando(c ? c.id : 'novo'); setF(c ?? VAZIO); setErros({}); setMsg('') }

  async function salvar(e) {
    e.preventDefault()
    setErros({}); setMsg('')
    const corpo = { ...f, ano: Number(f.ano) }
    try {
      if (editando === 'novo') await api.criarAutomovel(corpo)
      else await api.atualizarAutomovel(editando, corpo)
      setEditando(null); carregar()
    } catch (err) { setMsg(err.message); setErros(err.campos ?? {}) }
  }

  async function excluir(c) {
    if (!confirm(`Excluir ${c.marca} ${c.modelo} (${c.placa})?`)) return
    try { await api.removerAutomovel(c.id); carregar() } catch (err) { setMsg(err.message) }
  }

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  return (
    <>
      <div className="titulo-linha">
        <h1>Automóveis</h1>
        {!editando && <button className="btn" onClick={() => abrir(null)}>Cadastrar automóvel</button>}
      </div>

      {editando && (
        <form className="form painel" onSubmit={salvar} noValidate>
          <div className="grade tres">
            <Campo rotulo="Marca" erro={erros.marca}><input value={f.marca} onChange={set('marca')} required /></Campo>
            <Campo rotulo="Modelo" erro={erros.modelo}><input value={f.modelo} onChange={set('modelo')} required /></Campo>
            <Campo rotulo="Ano" erro={erros.ano}><input type="number" value={f.ano} onChange={set('ano')} required /></Campo>
            <Campo rotulo="Placa" erro={erros.placa}><input value={f.placa} onChange={set('placa')} maxLength={8} required /></Campo>
            <Campo rotulo="Matrícula" erro={erros.matricula}><input value={f.matricula} onChange={set('matricula')} required /></Campo>
          </div>
          {msg && <p className="aviso erro-caixa" role="alert">{msg}</p>}
          <div className="acoes">
            <button className="btn">Salvar automóvel</button>
            <button type="button" className="btn-link" onClick={() => setEditando(null)}>Cancelar</button>
          </div>
        </form>
      )}
      {!editando && msg && <p className="aviso erro-caixa" role="alert">{msg}</p>}

      {!lista && <p className="meta">Carregando…</p>}
      {lista?.length === 0 && <div className="vazio"><p>Nenhum automóvel cadastrado ainda.</p></div>}
      {lista?.length > 0 && (
        <div className="tabela-rolavel">
          <table>
            <thead>
              <tr><th>Veículo</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Proprietário</th><th /></tr>
            </thead>
            <tbody>
              {lista.map((c) => (
                <tr key={c.id}>
                  <td>{c.marca} {c.modelo}</td>
                  <td>{c.ano}</td>
                  <td>{c.placa}</td>
                  <td>{c.matricula}</td>
                  <td>{DONO[c.tipoProprietario]}</td>
                  <td className="acoes-linha">
                    {meu(c) && (<>
                      <button className="btn-link" onClick={() => abrir(c)}>Editar</button>
                      <button className="btn-link perigo" onClick={() => excluir(c)}>Excluir</button>
                    </>)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
