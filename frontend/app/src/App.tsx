import Login from './pages/Login';
import Operations from './pages/Operations';
import Categories from './pages/Categories';

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
  return <Home />;
}

export default App;
