import Login from './pages/Login';

function Home() {
  return (
    <main>
      <h1>OverMoney</h1>
      <p>React + Vite + TypeScript</p>
    </main>
  );
}

function App() {
  const isLogin = /\/login\/?$/.test(window.location.pathname);
  return isLogin ? <Login /> : <Home />;
}

export default App;
