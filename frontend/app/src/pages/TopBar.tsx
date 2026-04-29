import { useState, useEffect, useRef } from 'react';
import { apiFetch } from '../apiFetch';

type TabKey = 'ops' | 'cats' | 'arch' | 'dyn' | 'exp' | 'settings';

const navigate = (sub: string) => {
  window.location.href = `${import.meta.env.BASE_URL}${sub}`;
};

interface UserInfo {
  username: string;
  photoBase64Format: string | null;
}

const BADGE_TTL_MS = 60_000;
let badgeCache: { value: number; at: number } | null = null;

async function fetchBadge(): Promise<number> {
  if (badgeCache && Date.now() - badgeCache.at < BADGE_TTL_MS) {
    return badgeCache.value;
  }
  const r = await apiFetch('/transactions');
  if (!r.ok) throw new Error(`HTTP ${r.status}`);
  const data: unknown[] = await r.json();
  const value = data.length;
  badgeCache = { value, at: Date.now() };
  return value;
}

export default function TopBar({ active }: { active: TabKey }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [user, setUser] = useState<UserInfo | null>(null);
  const [opsBadge, setOpsBadge] = useState<number | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  const [bugOpen, setBugOpen] = useState(false);
  const [bugText, setBugText] = useState('');
  const [bugBusy, setBugBusy] = useState(false);

  useEffect(() => {
    apiFetch('/users/current')
      .then(r => r.ok ? r.json() : null)
      .then((data: UserInfo | null) => { if (data) setUser(data); })
      .catch(() => {});

    fetchBadge()
      .then(v => setOpsBadge(v))
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (!menuOpen) return;
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [menuOpen]);

  useEffect(() => {
    if (!bugOpen) return;
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !bugBusy) closeBugModal();
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [bugOpen, bugBusy]);

  const openBugModal = () => {
    setMenuOpen(false);
    setBugText('');
    setBugOpen(true);
  };

  const closeBugModal = () => {
    setBugOpen(false);
    setBugText('');
  };

  const handleSendBugReport = async () => {
    if (!bugText.trim()) return;
    setBugBusy(true);
    try {
      await apiFetch('/bugreport', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ report: bugText.trim(), localDateTime: new Date().toISOString() }),
      });
      closeBugModal();
    } finally {
      setBugBusy(false);
    }
  };

  const handleLogout = async () => {
    await apiFetch('/auth/logout', { method: 'POST' });
    window.location.href = `${import.meta.env.BASE_URL}login`;
  };

  return (
    <>
      <header className="topbar">
        <div className="topbar__brand">
          <a
            className="topbar__tg-link"
            href="https://t.me/OverMoneyBot"
            target="_blank"
            rel="noopener noreferrer"
            aria-label="Открыть бот в Telegram"
          >
            <TelegramIcon />
          </a>
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
          <button
            className={`tab ${active === 'exp' ? 'is-active' : ''}`}
            onClick={() => navigate('expenses')}
          >
            <CoinsIcon />
            <span>Расходы</span>
          </button>
        </nav>

        <div className="topbar__user">
          <button
            className="avatar"
            aria-label="Профиль"
            onClick={() => setMenuOpen(o => !o)}
            style={user?.photoBase64Format ? {
              backgroundImage: `url(${user.photoBase64Format})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
            } : undefined}
          />
        </div>
      </header>

      {menuOpen && (
        <div className="profile-backdrop" onClick={() => setMenuOpen(false)} />
      )}

      <div ref={menuRef} className={`profile-menu ${menuOpen ? 'profile-menu--open' : ''}`}>
        <div className="profile-menu__header">
          <div
            className="profile-menu__avatar"
            style={user?.photoBase64Format ? {
              backgroundImage: `url(${user.photoBase64Format})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
            } : undefined}
          />
          <span className="profile-menu__username">{user?.username ?? '—'}</span>
          <button className="profile-menu__close" onClick={() => setMenuOpen(false)} aria-label="Закрыть">
            <CloseIcon />
          </button>
        </div>

        <div className="profile-menu__divider" />

        <ul className="profile-menu__list">
          <li>
            <button className="profile-menu__item" onClick={() => { setMenuOpen(false); navigate('settings'); }}>
              <SettingsIcon />
              <span>Настройки</span>
            </button>
          </li>
          <li>
            <a
              className="profile-menu__item"
              href={`${import.meta.env.BASE_URL}privacy-policy`}
              onClick={() => setMenuOpen(false)}
            >
              <ShieldIcon />
              <span>Политика конфиденциальности</span>
            </a>
          </li>
          <li>
            <button className="profile-menu__item" onClick={openBugModal}>
              <MailIcon />
              <span>Написать в поддержку</span>
            </button>
          </li>
        </ul>

        <div className="profile-menu__divider" />

        <ul className="profile-menu__list">
          <li>
            <button className="profile-menu__item profile-menu__item--logout" onClick={handleLogout}>
              <LogoutIcon />
              <span>Выйти</span>
            </button>
          </li>
        </ul>
      </div>

      {bugOpen && (
        <div
          className="modal-backdrop"
          role="presentation"
          onClick={e => { if (e.target === e.currentTarget && !bugBusy) closeBugModal(); }}
        >
          <div className="modal" role="dialog" aria-modal="true">
            <h3 className="modal__title">Написать в поддержку</h3>
            <div className="modal__form">
              <div className="modal__field">
                <label className="modal__label" htmlFor="bug-report-text">Сообщение</label>
                <textarea
                  id="bug-report-text"
                  className="modal__textarea"
                  placeholder="Опишите проблему или вопрос..."
                  rows={5}
                  value={bugText}
                  onChange={e => setBugText(e.target.value)}
                  autoFocus
                  disabled={bugBusy}
                />
              </div>
            </div>
            <div className="modal__actions">
              <button className="ghost-btn" onClick={closeBugModal} disabled={bugBusy}>
                Отмена
              </button>
              <button
                className="primary-btn"
                onClick={handleSendBugReport}
                disabled={bugBusy || !bugText.trim()}
              >
                {bugBusy ? '...' : 'Отправить'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

function TelegramIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21.5 2 2 9.9l7.4 2.7L21.5 2Z" />
      <path d="m9.4 12.6 8.1-8.1" />
      <path d="M9.4 12.6 12 21l3-6.4" />
    </svg>
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

function CoinsIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <ellipse cx="9" cy="6" rx="6" ry="2.5" />
      <path d="M3 6v4c0 1.4 2.7 2.5 6 2.5s6-1.1 6-2.5V6" />
      <path d="M3 10v4c0 1.4 2.7 2.5 6 2.5" />
      <ellipse cx="15" cy="14" rx="6" ry="2.5" />
      <path d="M9 17.5c0 1.4 2.7 2.5 6 2.5s6-1.1 6-2.5V14" />
    </svg>
  );
}

function CloseIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M18 6 6 18" />
      <path d="m6 6 12 12" />
    </svg>
  );
}

function SettingsIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="3" />
      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
    </svg>
  );
}

function ShieldIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
    </svg>
  );
}

function MailIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="4" width="20" height="16" rx="2" />
      <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
    </svg>
  );
}

function LogoutIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16 17 21 12 16 7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </svg>
  );
}
