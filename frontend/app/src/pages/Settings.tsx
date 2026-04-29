import { useState, useEffect, useRef } from 'react';
import TopBar from './TopBar';
import { apiFetch } from '../apiFetch';
import './Settings.css';

interface SubscriptionStatus {
  isActive: boolean;
  endDate?: string;
}

type ToastType = 'success' | 'error' | 'info';

interface Toast {
  id: number;
  message: string;
  type: ToastType;
}

export default function Settings() {
  const [subscription, setSubscription] = useState<SubscriptionStatus | null>(null);
  const [subLoading, setSubLoading] = useState(true);

  const [restoreModalOpen, setRestoreModalOpen] = useState(false);
  const [restoreFile, setRestoreFile] = useState<File | null>(null);
  const [restoring, setRestoring] = useState(false);

  const [backupBusy, setBackupBusy] = useState(false);
  const [excelBusy, setExcelBusy] = useState(false);

  const [toasts, setToasts] = useState<Toast[]>([]);
  const toastCounter = useRef(0);

  const addToast = (message: string, type: ToastType = 'info') => {
    const id = ++toastCounter.current;
    setToasts(t => [...t, { id, message, type }]);
    setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), 3500);
  };

  useEffect(() => {
    loadSubscription();
  }, []);

  async function loadSubscription() {
    setSubLoading(true);
    try {
      const chatIdRes = await apiFetch('/users/currentChatId');
      if (!chatIdRes.ok) return;
      const chatId: number = await chatIdRes.json();
      const res = await apiFetch(`/payments/subscription/${chatId}/status`);
      if (res.ok) {
        const data = await res.json();
        setSubscription(data);
      }
    } catch {
      /* ignore */
    } finally {
      setSubLoading(false);
    }
  }

  async function handleBuySubscription() {
    try {
      const chatIdRes = await apiFetch('/users/currentChatId');
      if (!chatIdRes.ok) return;
      const chatId: number = await chatIdRes.json();
      const res = await apiFetch(`/payments/pay/${chatId}`);
      if (res.ok) {
        const url = await res.text();
        window.location.href = url;
      }
    } catch {
      addToast('Ошибка при получении ссылки на оплату', 'error');
    }
  }

  async function handleDownloadBackup() {
    setBackupBusy(true);
    try {
      const res = await apiFetch('/settings/backup');
      if (!res.ok) throw new Error();
      const data = await res.json();
      const blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
      downloadBlob(blob, `${dateStr()}-overmoney.json`);
    } catch {
      addToast('Ошибка при скачивании бэкапа', 'error');
    } finally {
      setBackupBusy(false);
    }
  }

  async function handleDownloadExcel() {
    setExcelBusy(true);
    try {
      const res = await apiFetch('/settings/export/excel');
      if (!res.ok) throw new Error();
      const blob = await res.blob();
      downloadBlob(blob, `${dateStr()}-export.xlsx`);
    } catch {
      addToast('Ошибка при экспорте в Excel', 'error');
    } finally {
      setExcelBusy(false);
    }
  }

  async function handleRestore() {
    if (!restoreFile) return;
    setRestoring(true);
    try {
      const text = await restoreFile.text();
      const body = JSON.parse(text);
      const res = await apiFetch('/settings/backup/read', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      if (res.ok) {
        setRestoreModalOpen(false);
        setRestoreFile(null);
        addToast('Данные успешно восстановлены', 'success');
      } else {
        addToast('Ошибка при восстановлении данных', 'error');
      }
    } catch {
      addToast('Не удалось прочитать файл', 'error');
    } finally {
      setRestoring(false);
    }
  }

  return (
    <div className="settings-layout">
      <TopBar active="settings" />

      <main className="settings-main">
        <div className="settings-container">
          <h1 className="settings-title">Настройки</h1>

          {/* Data section */}
          <section className="settings-section">
            <h2 className="settings-section__title">Данные</h2>

            <div className="settings-card">
              <div className="settings-card__row">
                <div className="settings-card__info">
                  <span className="settings-card__label">Бэкап данных</span>
                  <span className="settings-card__desc">Скачать все ваши данные в формате JSON</span>
                </div>
                <button
                  className="settings-btn settings-btn--primary"
                  onClick={handleDownloadBackup}
                  disabled={backupBusy}
                >
                  <DownloadIcon />
                  {backupBusy ? 'Загрузка...' : 'Скачать JSON'}
                </button>
              </div>

              <div className="settings-card__divider" />

              <div className="settings-card__row">
                <div className="settings-card__info">
                  <span className="settings-card__label">Экспорт в Excel</span>
                  <span className="settings-card__desc">Выгрузить историю транзакций в .xlsx</span>
                </div>
                <button
                  className="settings-btn settings-btn--primary"
                  onClick={handleDownloadExcel}
                  disabled={excelBusy}
                >
                  <TableIcon />
                  {excelBusy ? 'Загрузка...' : 'Скачать Excel'}
                </button>
              </div>

              <div className="settings-card__divider" />

              <div className="settings-card__row">
                <div className="settings-card__info">
                  <span className="settings-card__label">Восстановить из бэкапа</span>
                  <span className="settings-card__desc settings-card__desc--danger">Все текущие данные будут перезаписаны</span>
                </div>
                <button
                  className="settings-btn settings-btn--danger"
                  onClick={() => setRestoreModalOpen(true)}
                >
                  <UploadIcon />
                  Восстановить
                </button>
              </div>
            </div>
          </section>

          {/* Subscription section */}
          <section className="settings-section">
            <h2 className="settings-section__title">Подписка</h2>

            <div className="settings-card">
              <div className="settings-card__row">
                <div className="settings-card__info">
                  <span className="settings-card__label">Статус подписки</span>
                  {subLoading ? (
                    <span className="settings-card__desc">Проверяем...</span>
                  ) : subscription?.isActive ? (
                    <span className="settings-sub-badge settings-sub-badge--active">Активна</span>
                  ) : (
                    <span className="settings-sub-badge settings-sub-badge--inactive">Не активирована</span>
                  )}
                </div>
                <div className="settings-card__actions">
                  <button
                    className="settings-btn settings-btn--ghost"
                    onClick={loadSubscription}
                    disabled={subLoading}
                    title="Обновить статус"
                  >
                    <RefreshIcon spinning={subLoading} />
                  </button>
                  {!subLoading && !subscription?.isActive && (
                    <button className="settings-btn settings-btn--primary" onClick={handleBuySubscription}>
                      Купить подписку
                    </button>
                  )}
                </div>
              </div>
            </div>
          </section>

        </div>
      </main>

      {/* Restore confirmation modal */}
      {restoreModalOpen && (
        <div className="settings-modal-backdrop" onClick={() => { setRestoreModalOpen(false); setRestoreFile(null); }}>
          <div className="settings-modal" onClick={e => e.stopPropagation()}>
            <div className="settings-modal__header">
              <h3>Восстановление из бэкапа</h3>
              <button className="settings-modal__close" onClick={() => { setRestoreModalOpen(false); setRestoreFile(null); }}>×</button>
            </div>
            <div className="settings-modal__body">
              <p className="settings-modal__warning">
                Все текущие данные будут перезаписаны. Это действие необратимо.
              </p>
              <label className="settings-file-label">
                <UploadIcon />
                {restoreFile ? restoreFile.name : 'Выбрать файл .json'}
                <input
                  type="file"
                  accept=".json"
                  className="settings-file-input"
                  onChange={e => setRestoreFile(e.target.files?.[0] ?? null)}
                />
              </label>
            </div>
            <div className="settings-modal__footer">
              <button className="settings-btn settings-btn--ghost" onClick={() => { setRestoreModalOpen(false); setRestoreFile(null); }}>
                Отмена
              </button>
              <button
                className="settings-btn settings-btn--danger"
                onClick={handleRestore}
                disabled={!restoreFile || restoring}
              >
                {restoring ? 'Загрузка...' : 'Восстановить'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toasts */}
      <div className="settings-toasts">
        {toasts.map(t => (
          <div key={t.id} className={`settings-toast settings-toast--${t.type}`}>
            {t.message}
          </div>
        ))}
      </div>
    </div>
  );
}

function dateStr() {
  const d = new Date();
  return [
    String(d.getDate()).padStart(2, '0'),
    String(d.getMonth() + 1).padStart(2, '0'),
    d.getFullYear(),
  ].join('.');
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

function DownloadIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
      <polyline points="7 10 12 15 17 10" />
      <line x1="12" y1="15" x2="12" y2="3" />
    </svg>
  );
}

function TableIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="3" width="18" height="18" rx="2" />
      <path d="M3 9h18" />
      <path d="M3 15h18" />
      <path d="M9 3v18" />
    </svg>
  );
}

function UploadIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
      <polyline points="17 8 12 3 7 8" />
      <line x1="12" y1="3" x2="12" y2="15" />
    </svg>
  );
}

function RefreshIcon({ spinning }: { spinning: boolean }) {
  return (
    <svg
      width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor"
      strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
      style={spinning ? { animation: 'settings-spin 0.8s linear infinite' } : undefined}
    >
      <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
      <path d="M21 3v5h-5" />
      <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
      <path d="M8 16H3v5" />
    </svg>
  );
}
