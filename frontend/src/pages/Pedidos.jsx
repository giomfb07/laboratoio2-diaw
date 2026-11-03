import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import { useAuth } from '../AuthContext.jsx'
import PedidoCard from '../components/PedidoCard.jsx'

const FILTROS = [
  ['', 'Todos'], ['PENDENTE', 'Pendentes'], ['APROVADO', 'Aprovados'],
  ['CONTRATADO', 'Contratados'], ['REPROVADO', 'Reprovados'], ['CANCELADO', 'Cancelados'],
]

export default function Pedidos() {
  const { ehAgente } = useAuth()
  const [lista, setLista] = useState(null)
  const [filtro, setFiltro] = useState('')
  const [erro, setErro] = useState('')

  const carregar = useCallback(() => {
    api.listarPedidos(filtro).then(setLista).catch((e) => setErro(e.message))
  }, [filtro])
  useEffect(carregar, [carregar])

  return (
    <>
      <div className="titulo-linha">
        <h1>{ehAgente ? 'Pedidos de aluguel' : 'Meus pedidos'}</h1>
        {!ehAgente && <Link className="btn" to="/pedidos/novo">Novo pedido</Link>}
      </div>

      <div className="filtros" role="group" aria-label="Filtrar por status">
        {FILTROS.map(([v, r]) => (
          <button key={v} aria-pressed={filtro === v} onClick={() => setFiltro(v)}>{r}</button>
        ))}
      </div>

      {erro && <p className="aviso erro-caixa" role="alert">{erro}</p>}
      {!lista && !erro && <p className="meta">Carregando…</p>}
      {lista?.length === 0 && (
        <div className="vazio">
          <p>{filtro ? 'Nenhum pedido com este status.' : ehAgente ? 'Nenhum pedido recebido ainda.' : 'Você ainda não fez nenhum pedido.'}</p>
          {!ehAgente && !filtro && <Link className="btn" to="/pedidos/novo">Fazer o primeiro pedido</Link>}
        </div>
      )}
      <div className="lista">
        {lista?.map((p) => <PedidoCard key={p.id} pedido={p} onMudou={carregar} />)}
      </div>
    </>
  )
}
