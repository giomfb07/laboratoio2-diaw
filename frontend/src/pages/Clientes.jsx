import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'

export default function Clientes() {
  const [lista, setLista] = useState(null)
  const [erro, setErro] = useState('')
  const carregar = useCallback(() => { api.listarClientes().then(setLista).catch((e) => setErro(e.message)) }, [])
  useEffect(carregar, [carregar])

  async function excluir(c) {
    if (!confirm(`Excluir ${c.nome}? Os pedidos deste cliente também serão apagados.`)) return
    try { await api.removerCliente(c.id); carregar() } catch (e) { setErro(e.message) }
  }

  return (
    <>
      <h1>Clientes</h1>
      {erro && <p className="aviso erro-caixa" role="alert">{erro}</p>}
      {!lista && !erro && <p className="meta">Carregando…</p>}
      {lista?.length === 0 && <div className="vazio"><p>Nenhum cliente cadastrado.</p></div>}
      {lista?.length > 0 && (
        <div className="tabela-rolavel">
          <table>
            <thead><tr><th>Nome</th><th>CPF</th><th>Profissão</th><th>Rendimentos</th><th /></tr></thead>
            <tbody>
              {lista.map((c) => (
                <tr key={c.id}>
                  <td>{c.nome}</td>
                  <td>{c.cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')}</td>
                  <td>{c.profissao}</td>
                  <td>{c.rendimentos.length}</td>
                  <td className="acoes-linha">
                    <Link className="btn-link" to={`/clientes/${c.id}`}>Editar</Link>
                    <button className="btn-link perigo" onClick={() => excluir(c)}>Excluir</button>
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
