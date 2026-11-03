import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api.js'
import ClienteForm from '../components/ClienteForm.jsx'

export default function ClienteEditar() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [cliente, setCliente] = useState(null)
  const [erro, setErro] = useState('')
  useEffect(() => { api.buscarCliente(id).then(setCliente).catch((e) => setErro(e.message)) }, [id])

  if (erro) return <p className="aviso erro-caixa">{erro}</p>
  if (!cliente) return <p className="meta">Carregando…</p>

  return (
    <div className="estreita">
      <h1>Editar cliente</h1>
      <p className="meta">Login: {cliente.login} · <Link to="/clientes">Voltar à lista</Link></p>
      <ClienteForm
        modo="editar"
        inicial={cliente}
        rotuloSalvar="Salvar alterações"
        onSalvar={async (d) => { await api.atualizarCliente(id, d); navigate('/clientes') }}
        onExcluir={async () => {
          if (!confirm('Excluir este cliente e seus pedidos?')) return
          await api.removerCliente(id); navigate('/clientes')
        }}
      />
    </div>
  )
}
