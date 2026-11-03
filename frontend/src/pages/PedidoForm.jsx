import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api, moeda } from '../api.js'
import Campo from '../components/Campo.jsx'

export default function PedidoForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [carros, setCarros] = useState([])
  const [f, setF] = useState({ automovelId: '', dataInicio: '', dataFim: '', valorTotal: '' })
  const [erros, setErros] = useState({})
  const [msg, setMsg] = useState('')

  useEffect(() => {
    api.listarAutomoveis().then(setCarros).catch((e) => setMsg(e.message))
    if (id) {
      api.buscarPedido(id).then((p) =>
        setF({ automovelId: p.automovel.id, dataInicio: p.dataInicio, dataFim: p.dataFim, valorTotal: p.valorTotal }))
        .catch((e) => setMsg(e.message))
    }
  }, [id])

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  async function enviar(e) {
    e.preventDefault()
    setErros({}); setMsg('')
    const corpo = { ...f, automovelId: Number(f.automovelId), valorTotal: Number(f.valorTotal) }
    try {
      if (id) await api.modificarPedido(id, corpo)
      else await api.criarPedido(corpo)
      navigate('/pedidos')
    } catch (err) { setMsg(err.message); setErros(err.campos ?? {}) }
  }

  return (
    <div className="estreita">
      <h1>{id ? 'Modificar pedido' : 'Novo pedido'}</h1>
      {carros.length === 0 && !msg && (
        <p className="aviso">Não há automóveis cadastrados. <Link to="/automoveis">Cadastre um automóvel</Link> para continuar.</p>
      )}
      <form onSubmit={enviar} className="form" noValidate>
        <Campo rotulo="Automóvel" erro={erros.automovelId}>
          <select value={f.automovelId} onChange={set('automovelId')} required>
            <option value="">Escolha um automóvel</option>
            {carros.map((c) => (
              <option key={c.id} value={c.id}>{c.marca} {c.modelo} {c.ano} · {c.placa}</option>
            ))}
          </select>
        </Campo>
        <div className="grade">
          <Campo rotulo="Retirada" erro={erros.dataInicio}>
            <input type="date" value={f.dataInicio} onChange={set('dataInicio')} required />
          </Campo>
          <Campo rotulo="Devolução" erro={erros.dataFim}>
            <input type="date" value={f.dataFim} onChange={set('dataFim')} required />
          </Campo>
        </div>
        <Campo rotulo="Valor total do aluguel (R$)" erro={erros.valorTotal} dica={f.valorTotal ? moeda(f.valorTotal) : ''}>
          <input type="number" min="0.01" step="0.01" value={f.valorTotal} onChange={set('valorTotal')} required />
        </Campo>
        {msg && <p className="aviso erro-caixa" role="alert">{msg}</p>}
        <div className="acoes">
          <button className="btn">{id ? 'Salvar alterações' : 'Enviar pedido'}</button>
          <Link className="btn-link" to="/pedidos">Voltar</Link>
        </div>
      </form>
    </div>
  )
}
