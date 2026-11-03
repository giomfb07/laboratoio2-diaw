// Trilha simples do pedido: três etapas (Enviado, Aprovado, Contratado).
const ETAPAS = ['Enviado', 'Aprovado', 'Contratado']
const POSICAO = { PENDENTE: 0, APROVADO: 1, CONTRATADO: 2, REPROVADO: 1, CANCELADO: 0 }
const ROTULO_FALHA = { REPROVADO: 'Reprovado', CANCELADO: 'Cancelado' }
const TEXTO = {
  PENDENTE: 'Aguardando análise financeira',
  APROVADO: 'Aprovado. Aguardando contrato',
  CONTRATADO: 'Contrato executado',
  REPROVADO: 'Reprovado na análise',
  CANCELADO: 'Cancelado pelo cliente',
}

export default function StatusTrilha({ status }) {
  const atual = POSICAO[status] ?? 0
  const falha = status in ROTULO_FALHA
  return (
    <div className={`trilha${falha ? ' falha' : ''}`} role="img" aria-label={`Status: ${TEXTO[status]}`}>
      <ol>
        {ETAPAS.map((e, i) => {
          const feito = i < atual || (i === atual && !falha)
          const erro = falha && i === atual
          return (
            <li key={e} className={`${feito ? 'feito' : ''}${erro ? ' erro' : ''}${i === atual ? ' atual' : ''}`}>
              <span className="marco" />
              <span className="nome">{erro ? ROTULO_FALHA[status] : e}</span>
            </li>
          )
        })}
      </ol>
      <p className="trilha-texto">{TEXTO[status]}</p>
    </div>
  )
}
