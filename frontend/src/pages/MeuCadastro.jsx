import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api.js'
import { useAuth } from '../AuthContext.jsx'
import ClienteForm from '../components/ClienteForm.jsx'

export default function MeuCadastro() {
  const { sair, atualizarNome } = useAuth()
  const navigate = useNavigate()
  const [cliente, setCliente] = useState(null)
  const [ok, setOk] = useState(false)
  const [erro, setErro] = useState('')

  useEffect(() => { api.meuCadastro().then(setCliente).catch((e) => setErro(e.message)) }, [])

  if (erro) return <p className="aviso erro-caixa">{erro}</p>
  if (!cliente) return <p className="meta">Carregando…</p>

  return (
    <div className="estreita">
      <h1>Meu cadastro</h1>
      <p className="meta">Login: {cliente.login}</p>
      {ok && <p className="aviso ok-caixa" role="status">Cadastro atualizado.</p>}
      <ClienteForm
        modo="editar"
        inicial={cliente}
        rotuloSalvar="Salvar alterações"
        onSalvar={async (d) => {
          const r = await api.atualizarCliente(cliente.id, d)
          setCliente(r); atualizarNome(r.nome); setOk(true)
        }}
        onExcluir={async () => {
          if (!confirm('Excluir seu cadastro? Seus pedidos também serão apagados.')) return
          await api.removerCliente(cliente.id)
          sair(); navigate('/login')
        }}
      />
    </div>
  )
}
