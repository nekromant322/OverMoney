type TabKey = 'ops' | 'cats' | 'arch' | 'dyn';

const navigate = (sub: string) => {
  window.location.href = `${import.meta.env.BASE_URL}${sub}`;
};

export default function TopBar({ active, opsBadge }: { active: TabKey; opsBadge?: number }) {
  return (
    <header className="topbar">
      <div className="topbar__brand" aria-label="OverMoney">
        <span className="brand-mark" />
      </div>

      <nav className="tabs" aria-label="Разделы">
        <button
          className={`tab ${active === 'ops' ? 'is-active' : ''}`}
          onClick={() => navigate('operations')}
        >
          <SortIcon />
          <span>Операции</span>
          {typeof opsBadge === 'number' && <span className="tab__badge">{opsBadge}</span>}
        </button>
        <button
          className={`tab ${active === 'cats' ? 'is-active' : ''}`}
          onClick={() => navigate('categories')}
        >
          <GridIcon />
          <span>Категории</span>
        </button>
        <button
          className={`tab ${active === 'arch' ? 'is-active' : ''}`}
          onClick={() => navigate('archive')}
        >
          <ArchiveIcon />
          <span>Архив</span>
        </button>
        <button
          className={`tab ${active === 'dyn' ? 'is-active' : ''}`}
          onClick={() => navigate('dynamics')}
        >
          <LineChartIcon />
          <span>Динамика</span>
        </button>
      </nav>

      <div className="topbar__user">
        <div className="avatar" aria-label="Профиль" />
      </div>
    </header>
  );
}

function SortIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 6h13" />
      <path d="M3 12h9" />
      <path d="M3 18h6" />
      <path d="m17 8 4-4 4 4" transform="translate(-3 0)" />
      <path d="M18 4v16" />
    </svg>
  );
}

function GridIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="7" height="7" />
      <rect x="14" y="3" width="7" height="7" />
      <rect x="3" y="14" width="7" height="7" />
      <rect x="14" y="14" width="7" height="7" />
    </svg>
  );
}

function ArchiveIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="18" height="5" rx="1" />
      <path d="M5 8v11a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8" />
      <path d="M10 12h4" />
    </svg>
  );
}

function LineChartIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 3v18h18" />
      <path d="m7 14 4-4 4 4 5-7" />
    </svg>
  );
}
