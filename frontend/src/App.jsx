import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import Cadastro from './pages/Cadastro.jsx'
import Pedidos from './pages/Pedidos.jsx'
import PedidoForm from './pages/PedidoForm.jsx'
import Automoveis from './pages/Automoveis.jsx'
import MeuCadastro from './pages/MeuCadastro.jsx'
import Clientes from './pages/Clientes.jsx'
import ClienteEditar from './pages/ClienteEditar.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route element={<Layout />}>
        <Route path="/" element={<Navigate to="/pedidos" replace />} />
        <Route path="/pedidos" element={<Pedidos />} />
        <Route path="/pedidos/novo" element={<PedidoForm />} />
        <Route path="/pedidos/:id/editar" element={<PedidoForm />} />
        <Route path="/automoveis" element={<Automoveis />} />
        <Route path="/meu-cadastro" element={<MeuCadastro />} />
        <Route path="/clientes" element={<Clientes />} />
        <Route path="/clientes/:id" element={<ClienteEditar />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
