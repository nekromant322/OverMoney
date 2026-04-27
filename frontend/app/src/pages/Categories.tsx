import { useEffect, useState } from 'react';
import TopBar from './TopBar';
import './Operations.css';
import './Categories.css';

type CategoryType = 'INCOME' | 'EXPENSE';

type Category = {
  id: number;
  name: string;
  type: CategoryType;
};

type Filter = 'all' | 'expense' | 'income';

type Keyword = {
  accountId?: number | null;
  name: string;
  frequency?: number | null;
};

type FullCategory = Category & { keywords: Keyword[] };

export default function Categories() {
  const [categories, setCategories] = useState<Category[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<Filter>('all');
  const [editing, setEditing] = useState<FullCategory | null>(null);
  const [editingLoading, setEditingLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [mergeOpen, setMergeOpen] = useState(false);
  const [mergeTargetId, setMergeTargetId] = useState<number | ''>('');
  const [merging, setMerging] = useState(false);

  const loadCategories = () =>
    fetch('/categories/', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<Category[]>;
      })
      .then((data) => {
        setCategories(data);
        setError(null);
      })
      .catch((e: Error) => {
        setCategories((prev) => prev ?? []);
        setError(e.message);
      });

  useEffect(() => {
    loadCategories();
  }, []);

  const openCreate = () => {
    setIsCreating(true);
    setEditing({ id: 0, name: '', type: 'EXPENSE', keywords: [] });
    setEditingLoading(false);
  };

  const openEdit = async (c: Category) => {
    setIsCreating(false);
    setEditing({ ...c, keywords: [] });
    setEditingLoading(true);
    try {
      const r = await fetch(`/categories/${c.id}`, { credentials: 'include' });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      const data = (await r.json()) as FullCategory;
      setEditing(data);
    } catch (e) {
      console.error('Failed to load category', e);
    } finally {
      setEditingLoading(false);
    }
  };

  const closeEdit = () => {
    if (saving || merging) return;
    setEditing(null);
    setIsCreating(false);
    setMergeOpen(false);
    setMergeTargetId('');
  };

  const handleMerge = async () => {
    if (!editing || mergeTargetId === '') return;
    setMerging(true);
    try {
      const r = await fetch('/categories/merge', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          categoryToChangeId: mergeTargetId,
          categoryToMergeId: editing.id,
        }),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      setEditing(null);
      setMergeOpen(false);
      setMergeTargetId('');
      await loadCategories();
    } catch (e) {
      console.error('Failed to merge categories', e);
    } finally {
      setMerging(false);
    }
  };

  const removeKeyword = (idx: number) => {
    setEditing((prev) =>
      prev ? { ...prev, keywords: prev.keywords.filter((_, i) => i !== idx) } : null,
    );
  };

  const handleSave = async () => {
    if (!editing) return;
    setSaving(true);
    try {
      const body = isCreating
        ? { name: editing.name, type: editing.type, keywords: [] }
        : editing;
      const r = await fetch('/categories/', {
        method: isCreating ? 'POST' : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(body),
      });
      if (!r.ok) throw new Error(`HTTP ${r.status}`);
      setEditing(null);
      setIsCreating(false);
      await loadCategories();
    } catch (e) {
      console.error('Failed to save category', e);
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

  const list = (categories ?? []).filter((c) => {
    if (filter === 'expense') return c.type === 'EXPENSE';
    if (filter === 'income') return c.type === 'INCOME';
    return true;
  });

  return (
    <div className="ops-app">
      <TopBar active="cats" />

      <main className="cats-page">
        <div className="cats-page__inner">
        <div className="cats-toolbar">
          <div className="filter-group" role="tablist" aria-label="Фильтр">
            <button
              type="button"
              role="tab"
              aria-selected={filter === 'all'}
              className={`filter-btn ${filter === 'all' ? 'is-active' : ''}`}
              onClick={() => setFilter('all')}
            >
              <SwapIcon />
              <span>Все</span>
            </button>
            <button
              type="button"
              role="tab"
              aria-selected={filter === 'expense'}
              className={`filter-btn ${filter === 'expense' ? 'is-active' : ''}`}
              onClick={() => setFilter('expense')}
            >
              <ArrowDownIcon />
              <span>Расходы</span>
            </button>
            <button
              type="button"
              role="tab"
              aria-selected={filter === 'income'}
              className={`filter-btn ${filter === 'income' ? 'is-active' : ''}`}
              onClick={() => setFilter('income')}
            >
              <ArrowUpIcon />
              <span>Доходы</span>
            </button>
          </div>

          <button type="button" className="primary-btn" onClick={openCreate}>
            Добавить категорию
          </button>
        </div>

        {error && (
          <div className="content__error" role="alert">
            Не удалось загрузить категории: {error}
          </div>
        )}

        <div className="cats-table">
          <div className="cats-row cats-row--head">
            <span className="cats-cell-name">Название</span>
            <span className="cats-cell-actions" />
          </div>

          {categories === null ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : list.length === 0 ? (
            <div className="content__placeholder">Нет категорий</div>
          ) : (
            list.map((c) => (
              <div key={c.id} className="cats-row">
                <span className="cats-cell-name">{c.name}</span>
                <span className="cats-cell-actions">
                  {c.type === 'EXPENSE' ? (
                    <span className="type-icon type-icon--expense" aria-label="Расход">
                      <ArrowDownIcon />
                    </span>
                  ) : (
                    <span className="type-icon type-icon--income" aria-label="Доход">
                      <ArrowUpIcon />
                    </span>
                  )}
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label="Редактировать"
                    onClick={() => openEdit(c)}
                  >
                    <PenIcon />
                  </button>
                </span>
              </div>
            ))
          )}
        </div>
        </div>
      </main>

      {editing && (
        <div
          className="modal-backdrop"
          role="presentation"
          onClick={(e) => {
            if (e.target === e.currentTarget) closeEdit();
          }}
        >
          <div className="modal modal--wide" role="dialog" aria-modal="true" aria-labelledby="edit-cat-title">
            {!isCreating && (
              <button
                type="button"
                className="modal__link"
                onClick={() => setMergeOpen((v) => !v)}
                disabled={saving || merging}
              >
                Слияние
              </button>
            )}
            <button
              type="button"
              className="modal__close"
              aria-label="Закрыть"
              onClick={closeEdit}
              disabled={saving || merging}
            >
              ×
            </button>

            <h3 id="edit-cat-title" className="modal__title modal__title--center">
              {isCreating ? 'Новая категория' : 'Информация о категории'}
            </h3>

            <div className="modal__form">
              <div className="modal__field">
                <span className="modal__label">Наименование категории</span>
                <input
                  type="text"
                  value={editing.name}
                  maxLength={40}
                  onChange={(e) => setEditing({ ...editing, name: e.target.value })}
                  disabled={saving}
                />
              </div>

              <div className="modal__field">
                <span className="modal__label">Тип категории</span>
                {isCreating ? (
                  <div className="type-toggle" role="radiogroup">
                    <button
                      type="button"
                      role="radio"
                      aria-checked={editing.type === 'EXPENSE'}
                      className={`type-toggle__btn type-toggle__btn--expense ${editing.type === 'EXPENSE' ? 'is-active' : ''}`}
                      onClick={() => setEditing({ ...editing, type: 'EXPENSE' })}
                      disabled={saving}
                    >
                      Расходы
                    </button>
                    <button
                      type="button"
                      role="radio"
                      aria-checked={editing.type === 'INCOME'}
                      className={`type-toggle__btn type-toggle__btn--income ${editing.type === 'INCOME' ? 'is-active' : ''}`}
                      onClick={() => setEditing({ ...editing, type: 'INCOME' })}
                      disabled={saving}
                    >
                      Доходы
                    </button>
                  </div>
                ) : (
                  <span className={`type-badge type-badge--${editing.type === 'EXPENSE' ? 'expense' : 'income'}`}>
                    {editing.type === 'EXPENSE' ? 'Расходы' : 'Доходы'}
                  </span>
                )}
              </div>

              {!isCreating && (
                <div className="modal__field">
                  <span className="modal__label">Ключевые слова</span>
                  {editingLoading && editing.keywords.length === 0 ? (
                    <span className="modal__hint">Загрузка...</span>
                  ) : (
                    <div className="kw-list">
                      {editing.keywords.length === 0 && (
                        <span className="modal__hint">Пусто</span>
                      )}
                      {editing.keywords.map((k, i) => (
                        <span key={`${k.name}-${i}`} className="kw-chip">
                          {k.name}
                          <button
                            type="button"
                            className="kw-chip__remove"
                            aria-label={`Удалить ${k.name}`}
                            onClick={() => removeKeyword(i)}
                            disabled={saving}
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              )}

              <button
                type="button"
                className="primary-btn primary-btn--block"
                onClick={handleSave}
                disabled={saving || !editing.name.trim()}
              >
                {saving ? 'Сохраняю...' : 'Сохранить'}
              </button>

              {!isCreating && mergeOpen && (
                <div className="merge-block">
                  <span className="modal__label">Слить в</span>
                  <p className="merge-block__hint">
                    «{editing.name}» будет удалена, её транзакции и ключевые слова перейдут в выбранную категорию.
                  </p>
                  <select
                    className="merge-block__select"
                    value={mergeTargetId === '' ? '' : String(mergeTargetId)}
                    onChange={(e) =>
                      setMergeTargetId(e.target.value === '' ? '' : Number(e.target.value))
                    }
                    disabled={merging}
                  >
                    <option value="">Выберите категорию</option>
                    {(categories ?? [])
                      .filter((c) => c.type === editing.type && c.id !== editing.id)
                      .map((c) => (
                        <option key={c.id} value={c.id}>
                          {c.name}
                        </option>
                      ))}
                  </select>
                  <button
                    type="button"
                    className="primary-btn primary-btn--block"
                    onClick={handleMerge}
                    disabled={merging || mergeTargetId === ''}
                  >
                    {merging ? 'Сливаю...' : 'Слияние категории'}
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function ArrowDownIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 5v14" />
      <path d="m6 13 6 6 6-6" />
    </svg>
  );
}

function ArrowUpIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 19V5" />
      <path d="m6 11 6-6 6 6" />
    </svg>
  );
}

function SwapIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="m17 3 4 4-4 4" />
      <path d="M21 7H7" />
      <path d="m7 21-4-4 4-4" />
      <path d="M3 17h14" />
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
