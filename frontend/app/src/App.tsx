import Login from './pages/Login';
import Operations from './pages/Operations';
import Categories from './pages/Categories';
import Archive from './pages/Archive';
import Dynamics from './pages/Dynamics';
import Expenses from './pages/Expenses';

function Home() {
  return (
    <main>
      <h1>OverMoney</h1>
      <p>React + Vite + TypeScript</p>
    </main>
  );
}

function App() {
  const path = window.location.pathname;
  if (/\/login\/?$/.test(path)) return <Login />;
  if (/\/operations\/?$/.test(path)) return <Operations />;
  if (/\/categories\/?$/.test(path)) return <Categories />;
  if (/\/archive\/?$/.test(path)) return <Archive />;
  if (/\/dynamics\/?$/.test(path)) return <Dynamics />;
  if (/\/expenses\/?$/.test(path)) return <Expenses />;
  return <Home />;
}

export default App;
