import { NavLink, Navigate, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

export default function Layout() {
  const { usuario, ehAgente, sair } = useAuth()
  const navigate = useNavigate()
  if (!usuario) return <Navigate to="/login" replace />

  return (
    <>
      <header className="topo">
        <div className="topo-in">
          <span className="marca">Aluguel de Carros</span>
          <nav className="menu" aria-label="Principal">
            <NavLink to="/pedidos">Pedidos</NavLink>
            <NavLink to="/automoveis">Automóveis</NavLink>
            {ehAgente
              ? <NavLink to="/clientes">Clientes</NavLink>
              : <NavLink to="/meu-cadastro">Meu cadastro</NavLink>}
          </nav>
          <div className="quem">
            <span>{usuario.nome}</span>
            <small>{ehAgente ? (usuario.tipoAgente === 'BANCO' ? 'Banco' : 'Empresa') : 'Cliente'}</small>
            <button className="btn-link" onClick={() => { sair(); navigate('/login') }}>Sair</button>
          </div>
        </div>
      </header>
      <main className="pagina"><Outlet /></main>
    </>
  )
}
