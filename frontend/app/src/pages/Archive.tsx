import { useCallback, useEffect, useRef, useState } from 'react';
import TopBar from './TopBar';
import ConfirmModal from './ConfirmModal';
import './Operations.css';
import './Categories.css';
import './Archive.css';

const PAGE_SIZE = 50;
const INT_MAX = 2147483647;

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
};

type FilterForm = {
  amountMin: string;
  amountMax: string;
  dateBegin: string;
  dateEnd: string;
  message: string;
};

const emptyFilter: FilterForm = {
  amountMin: '',
  amountMax: '',
  dateBegin: '',
  dateEnd: '',
  message: '',
};

const formatAmount = (n: number) => new Intl.NumberFormat('ru-RU').format(Math.abs(n));

const formatDate = (iso: string) => {
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${pad(d.getDate())}.${pad(d.getMonth() + 1)}.${d.getFullYear()}`;
};

const nowLocalDate = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
};

const nowLocalDateTimeString = () => {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
};

const isFilterEmpty = (f: FilterForm) =>
  Object.values(f).every((v) => !v.trim());

const buildFilterBody = (f: FilterForm, page: number) => {
  const min = f.amountMin.trim();
  const max = f.amountMax.trim();
  const amount =
    min || max
      ? {
          begin: min ? Math.max(0, Number(min) || 0) : 0,
          end: max ? Math.min(INT_MAX, Number(max) || INT_MAX) : INT_MAX,
        }
      : null;

  const begin = f.dateBegin.trim();
  const end = f.dateEnd.trim();
  const date =
    begin || end
      ? {
          begin: begin ? `${begin}T00:00:00` : '1970-01-01T00:00:00',
          end: end ? `${end}T23:59:59` : '9999-12-31T23:59:59',
        }
      : null;

  return {
    category: null,
    amount,
    message: f.message.trim() || null,
    date,
    telegramUserIdList: null,
    pageSize: PAGE_SIZE,
    pageNumber: page,
  };
};

export default function Archive() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [pageNumber, setPageNumber] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);

  const [filter, setFilter] = useState<FilterForm>(emptyFilter);
  const [appliedFilter, setAppliedFilter] = useState<FilterForm | null>(null);

  const [selected, setSelected] = useState<Set<string>>(new Set());

  const [editing, setEditing] = useState<Transaction | null>(null);
  const [editForm, setEditForm] = useState({
    message: '',
    amount: '',
    date: '',
    categoryName: '',
  });
  const [saving, setSaving] = useState(false);

  const [showScrollTop, setShowScrollTop] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const [isAddOpen, setAddOpen] = useState(false);
  const [addForm, setAddForm] = useState({
    message: '',
    amount: '',
    date: '',
    categoryId: '' as number | '',
  });
  const [addSubmitting, setAddSubmitting] = useState(false);

  const scrollRef = useRef<HTMLElement>(null);
  const sentinelRef = useRef<HTMLDivElement>(null);
  const loadingRef = useRef(false);

  useEffect(() => {
    fetch('/categories/', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<Category[]>;
      })
      .then(setCategories)
      .catch((e) => console.error('Failed to load categories', e));
  }, []);

  const loadPage = useCallback(
    async (page: number, useFilter: FilterForm | null, append: boolean) => {
      if (loadingRef.current) return;
      loadingRef.current = true;
      setLoading(true);
      setError(null);
      try {
        let data: Transaction[];
        if (useFilter === null) {
          const r = await fetch(
            `/transactions/history?pageSize=${PAGE_SIZE}&pageNumber=${page}`,
            { credentials: 'include' },
          );
          if (!r.ok) throw new Error(`HTTP ${r.status}`);
          data = await r.json();
        } else {
          const r = await fetch('/transactions/filtered', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(buildFilterBody(useFilter, page)),
          });
          if (!r.ok) throw new Error(`HTTP ${r.status}`);
          data = await r.json();
        }
        setTransactions((prev) => (append ? [...prev, ...data] : data));
        setHasMore(data.length === PAGE_SIZE);
        setPageNumber(page);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Failed to load');
      } finally {
        loadingRef.current = false;
        setLoading(false);
      }
    },
    [],
  );

  // Initial load
  useEffect(() => {
    loadPage(0, null, false);
  }, [loadPage]);

  // Infinite scroll
  useEffect(() => {
    const sentinel = sentinelRef.current;
    if (!sentinel) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loadingRef.current) {
          loadPage(pageNumber + 1, appliedFilter, true);
        }
      },
      { root: scrollRef.current, threshold: 0.1, rootMargin: '120px' },
    );
    observer.observe(sentinel);
    return () => observer.disconnect();
  }, [pageNumber, hasMore, appliedFilter, loadPage]);

  // Track scroll for scroll-to-top button
  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;
    const onScroll = () => setShowScrollTop(el.scrollTop > 240);
    el.addEventListener('scroll', onScroll, { passive: true });
    return () => el.removeEventListener('scroll', onScroll);
  }, []);

  const applyFilter = () => {
    const next = isFilterEmpty(filter) ? null : { ...filter };
    setAppliedFilter(next);
    setSelected(new Set());
    loadPage(0, next, false);
  };

  const clearFilters = () => {
    setFilter(emptyFilter);
    if (appliedFilter !== null) {
      setAppliedFilter(null);
      setSelected(new Set());
      loadPage(0, null, false);
    }
  };

  const toggleSelect = (id: string) => {
    setSelected((prev) => {
      const n = new Set(prev);
      if (n.has(id)) n.delete(id);
      else n.add(id);
      return n;
    });
  };

  const allSelected =
    transactions.length > 0 && transactions.every((t) => selected.has(t.id));
  const toggleSelectAll = () => {
    if (allSelected) setSelected(new Set());
    else setSelected(new Set(transactions.map((t) => t.id)));
  };

  const requestDelete = () => {
    if (selected.size === 0) return;
    setConfirmOpen(true);
  };

  const handleDelete = async () => {
    if (selected.size === 0) return;
    setDeleting(true);
    const ids = Array.from(selected);
    try {
      const results = await Promise.allSettled(
        ids.map((id) =>
          fetch(`/transaction/${encodeURIComponent(id)}`, {
            method: 'DELETE',
            credentials: 'include',
          }).then((r) => {
            if (!r.ok) throw new Error(`HTTP ${r.status} for ${id}`);
            return id;
          }),
        ),
      );
      const succeeded = new Set(
        results
          .filter((r): r is PromiseFulfilledResult<string> => r.status === 'fulfilled')
          .map((r) => r.value),
      );
      const failed = results.filter((r) => r.status === 'rejected');
      if (failed.length > 0) {
        console.error('Some deletes failed', failed);
      }
      setTransactions((prev) => prev.filter((t) => !succeeded.has(t.id)));
      setSelected(new Set());
      setConfirmOpen(false);
    } catch (e) {
      console.error('Failed to delete transactions', e);
    } finally {
      setDeleting(false);
    }
  };

  const openEdit = (t: Transaction) => {
    setEditing(t);
    setEditForm({
      message: t.message,
      amount: String(t.amount ?? ''),
      date: t.date ? t.date.slice(0, 10) : '',
      categoryName: t.categoryName ?? '',
    });
  };

  const closeEdit = () => {
    if (saving) return;
    setEditing(null);
  };

  const handleEditSave = async () => {
    if (!editing) return;
    setSaving(true);
    try {
      const body = {
        ...editing,
        id: editing.id,
        message: editForm.message.trim(),
        amount: Number(editForm.amount) || 0,
        date: editForm.date
          ? `${editForm.date}T${editing.date.slice(11, 19) || '00:00:00'}`
          : editing.date,
        categoryName: editForm.categoryName || null,
      };
      const r = await fetch('/transactions', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(body),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      setEditing(null);
      setSelected(new Set());
      loadPage(0, appliedFilter, false);
    } catch (e) {
      console.error('Failed to save transaction', e);
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    if (!editing) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') closeEdit();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  });

  const scrollToTop = () => {
    scrollRef.current?.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const openAddModal = () => {
    setAddForm({ message: '', amount: '', date: nowLocalDate(), categoryId: '' });
    setAddOpen(true);
  };

  const closeAddModal = () => {
    if (addSubmitting) return;
    setAddOpen(false);
  };

  const handleAddSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const note = addForm.message.trim();
    const amount = addForm.amount.trim();
    if (!note || !amount) return;

    setAddSubmitting(true);
    try {
      const r = await fetch('/transaction', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          message: `${note} ${amount}`,
          date: addForm.date
            ? `${addForm.date}T00:00:00`
            : nowLocalDateTimeString(),
        }),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      const created = (await r.json()) as { id?: string };

      if (addForm.categoryId !== '' && created.id) {
        try {
          const r2 = await fetch('/transaction/define', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
              transactionId: created.id,
              categoryId: addForm.categoryId,
            }),
          });
          if (!r2.ok) throw new Error(`HTTP ${r2.status}`);
        } catch (defineErr) {
          console.error('Failed to assign category to new transaction', defineErr);
        }
      }

      setAddOpen(false);
      loadPage(0, appliedFilter, false);
    } catch (err) {
      console.error('Failed to add transaction', err);
    } finally {
      setAddSubmitting(false);
    }
  };

  const hasSelection = selected.size > 0;

  return (
    <div className="ops-app">
      <TopBar active="arch" />

      <main className="archive-page" ref={scrollRef}>
        <div className="archive-page__inner">
          <div className="archive-toolbar">
            <input
              type="number"
              className="filter-input"
              placeholder="Цена от"
              value={filter.amountMin}
              onChange={(e) => setFilter({ ...filter, amountMin: e.target.value })}
            />
            <input
              type="number"
              className="filter-input"
              placeholder="Цена до"
              value={filter.amountMax}
              onChange={(e) => setFilter({ ...filter, amountMax: e.target.value })}
            />
            <input
              type="date"
              className="filter-input"
              placeholder="С даты"
              value={filter.dateBegin}
              onChange={(e) => setFilter({ ...filter, dateBegin: e.target.value })}
            />
            <input
              type="date"
              className="filter-input"
              placeholder="По дату"
              value={filter.dateEnd}
              onChange={(e) => setFilter({ ...filter, dateEnd: e.target.value })}
            />
            <input
              type="text"
              className="filter-input filter-input--wide"
              placeholder="Поиск по тексту"
              value={filter.message}
              onChange={(e) => setFilter({ ...filter, message: e.target.value })}
            />
            <div className="archive-toolbar__actions">
              <button type="button" className="primary-btn" onClick={applyFilter} disabled={loading}>
                Применить
              </button>
              <button
                type="button"
                className="ghost-btn"
                onClick={clearFilters}
                style={{
                  visibility: isFilterEmpty(filter) && appliedFilter === null ? 'hidden' : 'visible',
                }}
              >
                Очистить фильтры
              </button>
              <button type="button" className="primary-btn" onClick={openAddModal}>
                Добавить операцию
              </button>
            </div>
          </div>

          {error && (
            <div className="content__error" role="alert">
              {error}
            </div>
          )}

          <div className="archive-table">
            <div className="archive-row archive-row--head">
              <span className="archive-cell archive-cell--check">
                <input
                  type="checkbox"
                  checked={allSelected}
                  onChange={toggleSelectAll}
                  aria-label="Выбрать все"
                />
              </span>
              <span className="archive-cell">Название</span>
              <span className="archive-cell">Категория</span>
              <span className="archive-cell">Дата</span>
              <span className="archive-cell archive-cell--amount">Стоимость</span>
              <span className="archive-cell archive-cell--actions" />
            </div>

            {transactions.length === 0 && !loading && (
              <div className="archive-status">Нет операций</div>
            )}

            {transactions.map((t) => (
              <div
                key={t.id}
                className={`archive-row ${selected.has(t.id) ? 'is-selected' : ''}`}
              >
                <span className="archive-cell archive-cell--check">
                  <input
                    type="checkbox"
                    checked={selected.has(t.id)}
                    onChange={() => toggleSelect(t.id)}
                    aria-label={`Выбрать ${t.message}`}
                  />
                </span>
                <span className="archive-cell archive-cell--name" title={t.message}>
                  {t.message}
                </span>
                <span className="archive-cell">{t.categoryName || '—'}</span>
                <span className="archive-cell">{formatDate(t.date)}</span>
                <span
                  className={`archive-cell archive-cell--amount ${
                    t.type === 'INCOME' ? 'is-positive' : 'is-negative'
                  }`}
                >
                  {formatAmount(t.amount)}
                </span>
                <span className="archive-cell archive-cell--actions">
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label="Редактировать"
                    onClick={() => openEdit(t)}
                  >
                    <PenIcon />
                  </button>
                </span>
              </div>
            ))}

            <div ref={sentinelRef} className="archive-sentinel" />

            {loading && <div className="archive-status">Загрузка...</div>}
            {!loading && !hasMore && transactions.length > 0 && (
              <div className="archive-status archive-status--end">Это все операции</div>
            )}
          </div>
        </div>

        <div className="archive-fabs">
          <button
            type="button"
            className={`fab-trash ${hasSelection ? 'is-active' : ''}`}
            onClick={hasSelection ? requestDelete : undefined}
            aria-disabled={!hasSelection}
            title={hasSelection ? `Удалить (${selected.size})` : 'Выбери операции для удаления'}
          >
            <TrashIcon />
            {hasSelection && <span className="fab-trash__count">{selected.size}</span>}
          </button>
          {showScrollTop && (
            <button
              type="button"
              className="fab-up"
              onClick={scrollToTop}
              aria-label="Наверх"
            >
              <ArrowUpIcon />
            </button>
          )}
        </div>
      </main>

      {isAddOpen && (
        <div
          className="modal-backdrop"
          role="presentation"
          onClick={(e) => {
            if (e.target === e.currentTarget) closeAddModal();
          }}
        >
          <div className="modal modal--wide" role="dialog" aria-modal="true">
            <button
              type="button"
              className="modal__close"
              aria-label="Закрыть"
              onClick={closeAddModal}
              disabled={addSubmitting}
            >
              ×
            </button>
            <h3 className="modal__title modal__title--center">Новая операция</h3>
            <form className="modal__form" onSubmit={handleAddSubmit}>
              <label className="modal__field">
                <span className="modal__label">Описание</span>
                <input
                  type="text"
                  placeholder="Например: продукты"
                  value={addForm.message}
                  onChange={(e) => setAddForm({ ...addForm, message: e.target.value })}
                  autoFocus
                  disabled={addSubmitting}
                />
              </label>
              <label className="modal__field">
                <span className="modal__label">Категория</span>
                <select
                  className="merge-block__select"
                  value={addForm.categoryId === '' ? '' : String(addForm.categoryId)}
                  onChange={(e) =>
                    setAddForm({
                      ...addForm,
                      categoryId: e.target.value === '' ? '' : Number(e.target.value),
                    })
                  }
                  disabled={addSubmitting}
                >
                  <option value="">Без категории</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </label>
              <label className="modal__field">
                <span className="modal__label">Сумма</span>
                <input
                  type="number"
                  inputMode="decimal"
                  placeholder="0"
                  value={addForm.amount}
                  onChange={(e) => setAddForm({ ...addForm, amount: e.target.value })}
                  disabled={addSubmitting}
                />
              </label>
              <label className="modal__field">
                <span className="modal__label">Дата</span>
                <input
                  type="date"
                  value={addForm.date}
                  onChange={(e) => setAddForm({ ...addForm, date: e.target.value })}
                  disabled={addSubmitting}
                />
              </label>
              <button
                type="submit"
                className="primary-btn primary-btn--block"
                disabled={addSubmitting || !addForm.message.trim() || !addForm.amount.trim()}
              >
                {addSubmitting ? 'Добавляю...' : 'Добавить'}
              </button>
            </form>
          </div>
        </div>
      )}

      <ConfirmModal
        open={confirmOpen}
        title={selected.size === 1 ? 'Удалить операцию?' : 'Удалить операции?'}
        message={
          selected.size === 1
            ? 'Это действие нельзя отменить.'
            : `Будет удалено: ${selected.size}. Это действие нельзя отменить.`
        }
        busy={deleting}
        onConfirm={handleDelete}
        onCancel={() => setConfirmOpen(false)}
      />

      {editing && (
        <div
          className="modal-backdrop"
          role="presentation"
          onClick={(e) => {
            if (e.target === e.currentTarget) closeEdit();
          }}
        >
          <div className="modal modal--wide" role="dialog" aria-modal="true">
            <button
              type="button"
              className="modal__close"
              aria-label="Закрыть"
              onClick={closeEdit}
              disabled={saving}
            >
              ×
            </button>
            <h3 className="modal__title modal__title--center">Редактирование операции</h3>
            <div className="modal__form">
              <div className="modal__field">
                <span className="modal__label">Описание</span>
                <input
                  type="text"
                  value={editForm.message}
                  onChange={(e) => setEditForm({ ...editForm, message: e.target.value })}
                  disabled={saving}
                />
              </div>
              <div className="modal__field">
                <span className="modal__label">Категория</span>
                <select
                  className="merge-block__select"
                  value={editForm.categoryName}
                  onChange={(e) => setEditForm({ ...editForm, categoryName: e.target.value })}
                  disabled={saving}
                >
                  <option value="">Без категории</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.name}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="modal__field">
                <span className="modal__label">Сумма</span>
                <input
                  type="number"
                  inputMode="decimal"
                  value={editForm.amount}
                  onChange={(e) => setEditForm({ ...editForm, amount: e.target.value })}
                  disabled={saving}
                />
              </div>
              <div className="modal__field">
                <span className="modal__label">Дата</span>
                <input
                  type="date"
                  value={editForm.date}
                  onChange={(e) => setEditForm({ ...editForm, date: e.target.value })}
                  disabled={saving}
                />
              </div>
              <button
                type="button"
                className="primary-btn primary-btn--block"
                onClick={handleEditSave}
                disabled={saving || !editForm.message.trim()}
              >
                {saving ? 'Сохраняю...' : 'Сохранить'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
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
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M3 6h18" />
      <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
      <path d="m19 6-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
      <path d="M10 11v6" />
      <path d="M14 11v6" />
    </svg>
  );
}

function ArrowUpIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 19V5" />
      <path d="m6 11 6-6 6 6" />
    </svg>
  );
}
