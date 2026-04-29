import Login from './pages/Login';
import Operations from './pages/Operations';
import Categories from './pages/Categories';
import Archive from './pages/Archive';
import Dynamics from './pages/Dynamics';
import Expenses from './pages/Expenses';
import PrivacyPolicy from './pages/PrivacyPolicy';
import Settings from './pages/Settings';

function App() {
  const path = window.location.pathname;
  if (/\/login\/?$/.test(path)) return <Login />;
  if (/\/operations\/?$/.test(path)) return <Operations />;
  if (/\/categories\/?$/.test(path)) return <Categories />;
  if (/\/archive\/?$/.test(path)) return <Archive />;
  if (/\/dynamics\/?$/.test(path)) return <Dynamics />;
  if (/\/expenses\/?$/.test(path)) return <Expenses />;
  if (/\/privacy-policy\/?$/.test(path)) return <PrivacyPolicy />;
  if (/\/settings\/?$/.test(path)) return <Settings />;
  window.location.replace(window.location.origin + '/finances/operations');
  return null;
}

export default App;
