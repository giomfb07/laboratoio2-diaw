import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api, dataBR, moeda } from '../api.js'
import { useAuth } from '../AuthContext.jsx'
import StatusTrilha from './StatusTrilha.jsx'
import Campo from './Campo.jsx'

export default function PedidoCard({ pedido: p, onMudou }) {
  const { ehAgente, ehBanco } = useAuth()
  const [painel, setPainel] = useState(null) // 'avaliar' | 'contrato'
  const [erro, setErro] = useState('')
  const [aprovado, setAprovado] = useState('true')
  const [parecer, setParecer] = useState('')
  const [comCredito, setComCredito] = useState(false)
  const [cred, setCred] = useState({ valorFinanciado: p.valorTotal, taxaJurosMensal: '1.5', parcelas: 12 })

  const executar = async (fn) => {
    setErro('')
    try { await fn(); setPainel(null); onMudou() } catch (e) { setErro(e.message) }
  }

  const cancelar = () => {
    if (confirm('Cancelar este pedido? Esta ação não pode ser desfeita.')) executar(() => api.cancelarPedido(p.id))
  }
  const avaliar = (e) => {
    e.preventDefault()
    executar(() => api.avaliarPedido(p.id, { aprovado: aprovado === 'true', parecer }))
  }
  const contratar = (e) => {
    e.preventDefault()
    const credito = comCredito
      ? { valorFinanciado: Number(cred.valorFinanciado), taxaJurosMensal: Number(cred.taxaJurosMensal), parcelas: Number(cred.parcelas) }
      : null
    executar(() => api.executarContrato(p.id, { credito }))
  }

  const editavel = ehAgente ? !['CANCELADO', 'CONTRATADO'].includes(p.status) : p.status === 'PENDENTE'
  const cancelavel = !ehAgente && !['CANCELADO', 'CONTRATADO'].includes(p.status)

  return (
    <article className="pedido">
      <div className="pedido-topo">
        <div>
          <h3>{p.automovel.marca} {p.automovel.modelo} <span className="ano">{p.automovel.ano}</span></h3>
          <p className="meta">Placa {p.automovel.placa}{ehAgente && <> · Cliente: {p.clienteNome}</>}</p>
        </div>
        <div className="valor">{moeda(p.valorTotal)}</div>
      </div>

      <p className="periodo">{dataBR(p.dataInicio)} a {dataBR(p.dataFim)}</p>
      <StatusTrilha status={p.status} />

      {p.parecerFinanceiro && (
        <p className="parecer"><strong>Parecer{p.avaliadoPor ? ` de ${p.avaliadoPor}` : ''}:</strong> {p.parecerFinanceiro}</p>
      )}
      {p.credito && (
        <p className="parecer">
          <strong>Crédito {p.credito.banco}:</strong> {moeda(p.credito.valorFinanciado)} em {p.credito.parcelas}x,
          juros de {p.credito.taxaJurosMensal}% ao mês.
        </p>
      )}

      <div className="acoes">
        {editavel && <Link className="btn sec" to={`/pedidos/${p.id}/editar`}>Modificar</Link>}
        {cancelavel && <button className="btn perigo" onClick={cancelar}>Cancelar pedido</button>}
        {ehAgente && p.status === 'PENDENTE' && (
          <button className="btn" onClick={() => setPainel('avaliar')}>Avaliar</button>
        )}
        {ehAgente && p.status === 'APROVADO' && (
          <button className="btn" onClick={() => setPainel('contrato')}>Executar contrato</button>
        )}
      </div>

      {painel === 'avaliar' && (
        <form className="painel" onSubmit={avaliar}>
          <div className="radios">
            <label><input type="radio" checked={aprovado === 'true'} onChange={() => setAprovado('true')} /> Parecer positivo</label>
            <label><input type="radio" checked={aprovado === 'false'} onChange={() => setAprovado('false')} /> Parecer negativo</label>
          </div>
          <Campo rotulo="Parecer financeiro">
            <textarea rows="3" value={parecer} onChange={(e) => setParecer(e.target.value)} maxLength={500} required />
          </Campo>
          <div className="acoes">
            <button className="btn">Registrar parecer</button>
            <button type="button" className="btn-link" onClick={() => setPainel(null)}>Fechar</button>
          </div>
        </form>
      )}

      {painel === 'contrato' && (
        <form className="painel" onSubmit={contratar}>
          {ehBanco && (
            <label className="radios">
              <input type="checkbox" checked={comCredito} onChange={(e) => setComCredito(e.target.checked)} />
              Associar contrato de crédito deste banco
            </label>
          )}
          {comCredito && (
            <div className="grade tres">
              <Campo rotulo="Valor financiado (R$)">
                <input type="number" min="1" step="0.01" max={p.valorTotal} value={cred.valorFinanciado}
                  onChange={(e) => setCred({ ...cred, valorFinanciado: e.target.value })} required />
              </Campo>
              <Campo rotulo="Juros ao mês (%)">
                <input type="number" min="0" step="0.01" value={cred.taxaJurosMensal}
                  onChange={(e) => setCred({ ...cred, taxaJurosMensal: e.target.value })} required />
              </Campo>
              <Campo rotulo="Parcelas">
                <input type="number" min="1" value={cred.parcelas}
                  onChange={(e) => setCred({ ...cred, parcelas: e.target.value })} required />
              </Campo>
            </div>
          )}
          {!ehBanco && <p className="meta">Confirme a execução do contrato de aluguel.</p>}
          <div className="acoes">
            <button className="btn">Confirmar contrato</button>
            <button type="button" className="btn-link" onClick={() => setPainel(null)}>Fechar</button>
          </div>
        </form>
      )}

      {erro && <p className="aviso erro-caixa" role="alert">{erro}</p>}
    </article>
  )
}
