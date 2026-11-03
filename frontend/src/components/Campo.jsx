export default function Campo({ rotulo, erro, children, dica }) {
  return (
    <label className={`campo${erro ? ' tem-erro' : ''}`}>
      <span className="rotulo">{rotulo}</span>
      {children}
      {dica && !erro && <small className="dica">{dica}</small>}
      {erro && <small className="erro">{erro}</small>}
    </label>
  )
}
