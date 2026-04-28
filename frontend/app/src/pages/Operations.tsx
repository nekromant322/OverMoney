import { useEffect, useRef, useState } from 'react';
import TopBar from './TopBar';
import ConfirmModal from './ConfirmModal';
import './Operations.css';

type CategoryType = 'INCOME' | 'EXPENSE';

type Category = {
  id: number;
  name: string;
  type: CategoryType;
};

type Transaction = {
  id: string;
  message: string;
  amount: number;
  date: string;
  categoryName?: string;
  type?: CategoryType;
  suggestedCategoryId?: number | null;
};

const formatAmount = (n: number) => new Intl.NumberFormat('ru-RU').format(Math.abs(n));

const formatDate = (iso: string) => {
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${pad(d.getDate())}.${pad(d.getMonth() + 1)}.${d.getFullYear()} / ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

type CategoryListProps = {
  items: Category[];
  dropTargetId: number | null;
  suggestedId?: number | null;
  onCategoryDragOver: (e: React.DragEvent, catId: number) => void;
  onCategoryDrop: (e: React.DragEvent, catId: number) => void;
};

function CategoryList({ items, dropTargetId, suggestedId, onCategoryDragOver, onCategoryDrop }: CategoryListProps) {
  return (
    <ul className="category-list">
      {items.map((c) => (
        <li
          key={c.id}
          className={`category-row ${dropTargetId === c.id ? 'is-drop-target' : ''} ${suggestedId === c.id ? 'is-suggested' : ''}`}
          onDragOver={(e) => onCategoryDragOver(e, c.id)}
          onDrop={(e) => onCategoryDrop(e, c.id)}
        >
          <span className="category-name">{c.name}</span>
        </li>
      ))}
    </ul>
  );
}

type OperationCardProps = {
  tx: Transaction;
  isDragging: boolean;
  onDragStart: (e: React.DragEvent, txId: string) => void;
  onDragEnd: () => void;
};

function OperationCard({ tx, isDragging, onDragStart, onDragEnd }: OperationCardProps) {
  return (
    <article
      className={`op-card ${isDragging ? 'is-dragging' : ''}`}
      draggable
      onDragStart={(e) => onDragStart(e, tx.id)}
      onDragEnd={onDragEnd}
    >
      <header className="op-card__head">
        <h3 className="op-card__title">{tx.message}</h3>
        <button type="button" className="icon-btn" aria-label="Редактировать">
          <PenIcon />
        </button>
      </header>
      <footer className="op-card__foot">
        <span className="op-card__date">{formatDate(tx.date)}</span>
        <span className="op-card__amount">{formatAmount(tx.amount)}</span>
      </footer>
    </article>
  );
}

export default function Operations() {
  const [query, setQuery] = useState('');
  const [categories, setCategories] = useState<Category[] | null>(null);
  const [categoriesError, setCategoriesError] = useState<string | null>(null);
  const [transactions, setTransactions] = useState<Transaction[] | null>(null);
  const [transactionsError, setTransactionsError] = useState<string | null>(null);
  const [draggingId, setDraggingId] = useState<string | null>(null);
  const [draggingSuggestedId, setDraggingSuggestedId] = useState<number | null>(null);
  const [dropTargetId, setDropTargetId] = useState<number | null>(null);
  const [trashHover, setTrashHover] = useState(false);
  const [pendingDeleteTxId, setPendingDeleteTxId] = useState<string | null>(null);
  const [toast, setToast] = useState<{ tx: Transaction; categoryId: number; categoryName: string } | null>(null);
  const toastTimerRef = useRef<number | null>(null);

  useEffect(() => {
    let cancelled = false;
    fetch('/categories/', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<Category[]>;
      })
      .then((data) => {
        if (!cancelled) setCategories(data);
      })
      .catch((e: Error) => {
        if (!cancelled) {
          setCategories([]);
          setCategoriesError(e.message);
        }
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    return () => {
      if (toastTimerRef.current !== null) {
        window.clearTimeout(toastTimerRef.current);
      }
    };
  }, []);

  const loadTransactions = () => {
    return fetch('/transactions', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<Transaction[]>;
      })
      .then((data) => {
        setTransactions(data);
        setTransactionsError(null);
      })
      .catch((e: Error) => {
        setTransactions((prev) => prev ?? []);
        setTransactionsError(e.message);
      });
  };

  useEffect(() => {
    loadTransactions();
  }, []);

  const all = categories ?? [];
  const expenses = all.filter((c) => c.type === 'EXPENSE');
  const incomes = all.filter((c) => c.type === 'INCOME');

  const q = query.trim().toLowerCase();
  const filteredExpenses = q
    ? expenses.filter((c) => c.name.toLowerCase().includes(q))
    : expenses;
  const filteredIncomes = q ? incomes.filter((c) => c.name.toLowerCase().includes(q)) : incomes;

  const handleDragStart = (e: React.DragEvent, txId: string) => {
    e.dataTransfer.setData('text/plain', txId);
    e.dataTransfer.effectAllowed = 'move';
    setDraggingId(txId);
    const tx = transactions?.find((t) => t.id === txId);
    setDraggingSuggestedId(tx?.suggestedCategoryId ?? null);
  };

  const handleDragEnd = () => {
    setDraggingId(null);
    setDropTargetId(null);
    setTrashHover(false);
    setDraggingSuggestedId(null);
  };

  const handleCategoryDragOver = (e: React.DragEvent, catId: number) => {
    if (!draggingId) return;
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    if (dropTargetId !== catId) setDropTargetId(catId);
  };

  const showToast = (next: { tx: Transaction; categoryId: number; categoryName: string }) => {
    if (toastTimerRef.current !== null) {
      window.clearTimeout(toastTimerRef.current);
    }
    setToast(next);
    toastTimerRef.current = window.setTimeout(() => {
      setToast(null);
      toastTimerRef.current = null;
    }, 6000);
  };

  const hideToast = () => {
    if (toastTimerRef.current !== null) {
      window.clearTimeout(toastTimerRef.current);
      toastTimerRef.current = null;
    }
    setToast(null);
  };

  const handleCategoryDrop = async (e: React.DragEvent, catId: number) => {
    e.preventDefault();
    const txId = e.dataTransfer.getData('text/plain');
    setDraggingId(null);
    setDropTargetId(null);
    if (!txId) return;

    const previous = transactions;
    const droppedTx = previous?.find((t) => t.id === txId);
    const targetCategory = categories?.find((c) => c.id === catId);
    setTransactions((prev) => prev?.filter((t) => t.id !== txId) ?? null);

    try {
      const r = await fetch('/transaction/define', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ transactionId: txId, categoryId: catId }),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      if (droppedTx && targetCategory) {
        showToast({ tx: droppedTx, categoryId: catId, categoryName: targetCategory.name });
      }
    } catch (err) {
      console.error('Failed to define transaction category', err);
      setTransactions(previous);
    }
  };

  const handleUndoDefine = async () => {
    if (!toast) return;
    const { tx, categoryId } = toast;
    hideToast();
    setTransactions((prev) => {
      if (!prev) return [tx];
      if (prev.some((t) => t.id === tx.id)) return prev;
      return [...prev, tx].sort((a, b) => a.date.localeCompare(b.date));
    });
    try {
      const r = await fetch('/transaction/undefine', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ transactionId: tx.id, categoryId }),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
    } catch (err) {
      console.error('Failed to undefine transaction', err);
    }
  };

  const handleTrashDragOver = (e: React.DragEvent) => {
    if (!draggingId) return;
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    if (!trashHover) setTrashHover(true);
  };

  const handleTrashDragLeave = () => {
    setTrashHover(false);
  };

  const handleTrashDrop = (e: React.DragEvent) => {
    e.preventDefault();
    const txId = e.dataTransfer.getData('text/plain');
    setDraggingId(null);
    setTrashHover(false);
    if (!txId) return;
    setPendingDeleteTxId(txId);
  };

  const confirmDelete = async () => {
    const txId = pendingDeleteTxId;
    if (!txId) return;
    const previous = transactions;
    setTransactions((prev) => prev?.filter((t) => t.id !== txId) ?? null);
    setPendingDeleteTxId(null);

    try {
      const r = await fetch(`/transaction/${encodeURIComponent(txId)}`, {
        method: 'DELETE',
        credentials: 'include',
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
    } catch (err) {
      console.error('Failed to delete transaction', err);
      setTransactions(previous);
    }
  };

  return (
    <div className="ops-app">
      <TopBar active="ops" opsBadge={transactions?.length} />

      <div className="layout">
        <aside className="sidebar">
          <div className="search">
            <SearchIcon />
            <input
              type="text"
              placeholder="Найти категорию..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>

          <section className="cat-section">
            <h2 className="cat-section__title">Расходы</h2>
            <CategoryList
              items={filteredExpenses}
              dropTargetId={dropTargetId}
              suggestedId={draggingSuggestedId}
              onCategoryDragOver={handleCategoryDragOver}
              onCategoryDrop={handleCategoryDrop}
            />
          </section>

          <section className="cat-section">
            <h2 className="cat-section__title">Доходы</h2>
            <CategoryList
              items={filteredIncomes}
              dropTargetId={dropTargetId}
              suggestedId={draggingSuggestedId}
              onCategoryDragOver={handleCategoryDragOver}
              onCategoryDrop={handleCategoryDrop}
            />
          </section>

          {categoriesError && (
            <div className="sidebar__error" role="alert">
              Не удалось загрузить категории: {categoriesError}
            </div>
          )}
        </aside>

        <main className="content">
          {transactionsError ? (
            <div className="content__error" role="alert">
              Не удалось загрузить транзакции: {transactionsError}
            </div>
          ) : transactions === null ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : transactions.length === 0 ? (
            <div className="content__placeholder">Нет операций</div>
          ) : (
            <div className="op-grid">
              {transactions.map((tx) => (
                <OperationCard
                  key={tx.id}
                  tx={tx}
                  isDragging={draggingId === tx.id}
                  onDragStart={handleDragStart}
                  onDragEnd={handleDragEnd}
                />
              ))}
            </div>
          )}

          <button
            type="button"
            className={`fab-trash ${trashHover ? 'is-drop-target' : ''}`}
            aria-label="Удалить"
            onDragOver={handleTrashDragOver}
            onDragLeave={handleTrashDragLeave}
            onDrop={handleTrashDrop}
          >
            <TrashIcon />
          </button>
        </main>
      </div>

      <ConfirmModal
        open={pendingDeleteTxId !== null}
        title="Удалить операцию?"
        message="Это действие нельзя отменить."
        onConfirm={confirmDelete}
        onCancel={() => setPendingDeleteTxId(null)}
      />

      {toast && (
        <div className="toast" role="status">
          <span className="toast__text">
            {toast.tx.message} {formatAmount(toast.tx.amount)} → {toast.categoryName}
          </span>
          <button type="button" className="toast__undo" onClick={handleUndoDefine}>
            Отменить
          </button>
        </div>
      )}
    </div>
  );
}

function SearchIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-3.5-3.5" strokeLinecap="round" />
    </svg>
  );
}

function PenIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 20h9" />
      <path d="M16.5 3.5a2.121 2.121 0 1 1 3 3L7 19l-4 1 1-4 12.5-12.5z" />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 6h18" />
      <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
      <path d="m19 6-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
      <path d="M10 11v6" />
      <path d="M14 11v6" />
    </svg>
  );
}
