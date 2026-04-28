import { useEffect, useMemo, useState } from 'react';
import TopBar from './TopBar';
import { apiFetch } from '../apiFetch';
import './Operations.css';
import './Categories.css';
import './Expenses.css';

const MONTH_RU = [
  'Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь',
  'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь',
];

type TotalRow = {
  categoryName: string;
  categoryId: number;
  monthlyAnalytics: Record<string, number>;
  shareOfMonthlyExpenses?: Record<string, number>;
};

type Transaction = {
  id: string;
  message: string;
  amount: number;
  date: string;
};

const formatAmount = (n: number) => new Intl.NumberFormat('ru-RU').format(Math.round(n));

export default function Expenses() {
  const [years, setYears] = useState<number[]>([]);
  const [year, setYear] = useState<number | null>(null);
  const [data, setData] = useState<TotalRow[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modalCell, setModalCell] = useState<{
    month: number;
    categoryId: number;
    categoryName: string;
  } | null>(null);

  useEffect(() => {
    apiFetch('/analytics/available-years', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<number[]>;
      })
      .then((ys) => {
        setYears(ys);
        if (ys.length > 0) setYear(Math.max(...ys));
      })
      .catch((e: Error) => setError(e.message));
  }, []);

  useEffect(() => {
    if (year === null) return;
    setLoading(true);
    setError(null);
    fetch(`/analytics/total/${year}`, { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<TotalRow[]>;
      })
      .then(setData)
      .catch((e: Error) => {
        setData([]);
        setError(e.message);
      })
      .finally(() => setLoading(false));
  }, [year]);

  const monthTotals = useMemo(() => {
    const totals = Array(13).fill(0) as number[];
    if (!data) return totals;
    for (const r of data) {
      for (let m = 1; m <= 12; m++) {
        totals[m] += Number(r.monthlyAnalytics[String(m)] ?? 0);
      }
    }
    return totals;
  }, [data]);

  const top3 = useMemo(() => {
    const map = new Map<number, Map<number, number>>();
    if (!data) return map;
    for (let m = 1; m <= 12; m++) {
      const sorted = data
        .map((r) => ({
          id: r.categoryId,
          amount: Number(r.monthlyAnalytics[String(m)] ?? 0),
        }))
        .filter((x) => x.amount > 0)
        .sort((a, b) => b.amount - a.amount)
        .slice(0, 3);
      const ranks = new Map<number, number>();
      sorted.forEach((x, i) => ranks.set(x.id, i + 1));
      map.set(m, ranks);
    }
    return map;
  }, [data]);

  return (
    <div className="ops-app">
      <TopBar active="exp" />
      <main className="expenses-page">
        <div className="expenses-page__header">
          <label className="year-picker">
            <span className="year-picker__label">Год:</span>
            <select
              className="merge-block__select expenses-year-select"
              value={year ?? ''}
              onChange={(e) => setYear(Number(e.target.value))}
              disabled={years.length === 0}
            >
              {years.length === 0 && <option value="">—</option>}
              {years
                .slice()
                .sort((a, b) => b - a)
                .map((y) => (
                  <option key={y} value={y}>
                    {y}
                  </option>
                ))}
            </select>
          </label>
        </div>

        {error && <div className="content__error">Не удалось загрузить: {error}</div>}

        {loading ? (
          <div className="content__placeholder">Загрузка...</div>
        ) : !data || data.length === 0 ? (
          <div className="content__placeholder">Нет данных</div>
        ) : (
          <div className="expenses-table-wrap">
            <table className="expenses-table">
              <thead>
                <tr>
                  <th className="expenses-table__corner">Категории/месяц</th>
                  {MONTH_RU.map((m, i) => (
                    <th key={i} className="expenses-table__month">
                      {m}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                <tr className="row-totals">
                  <th className="row-name">ИТОГО:</th>
                  {Array.from({ length: 12 }, (_, i) => (
                    <td key={i} className="cell">
                      {formatAmount(monthTotals[i + 1])}
                    </td>
                  ))}
                </tr>
                {data.map((r) => (
                  <tr key={r.categoryId} className="row-data">
                    <th className="row-name">{r.categoryName}</th>
                    {Array.from({ length: 12 }, (_, i) => {
                      const m = i + 1;
                      const v = Number(r.monthlyAnalytics[String(m)] ?? 0);
                      const rank = top3.get(m)?.get(r.categoryId);
                      const cls = [
                        'cell',
                        rank ? `cell--rank-${rank}` : '',
                        v === 0 ? 'cell--zero' : '',
                      ]
                        .filter(Boolean)
                        .join(' ');
                      return (
                        <td key={m} className={cls}>
                          <button
                            type="button"
                            className="cell-btn"
                            disabled={v === 0}
                            onClick={() =>
                              setModalCell({
                                month: m,
                                categoryId: r.categoryId,
                                categoryName: r.categoryName,
                              })
                            }
                          >
                            {formatAmount(v)}
                          </button>
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      {modalCell && year !== null && (
        <CellModal
          year={year}
          month={modalCell.month}
          categoryId={modalCell.categoryId}
          categoryName={modalCell.categoryName}
          onClose={() => setModalCell(null)}
        />
      )}
    </div>
  );
}

function CellModal({
  year,
  month,
  categoryId,
  categoryName,
  onClose,
}: {
  year: number;
  month: number;
  categoryId: number;
  categoryName: string;
  onClose: () => void;
}) {
  const [items, setItems] = useState<Transaction[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(`/transactions/info?year=${year}&month=${month}&categoryId=${categoryId}`, {
      credentials: 'include',
    })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<Transaction[]>;
      })
      .then(setItems)
      .catch((e: Error) => {
        setItems([]);
        setError(e.message);
      });
  }, [year, month, categoryId]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <div
      className="modal-backdrop"
      role="presentation"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="modal modal--wide" role="dialog" aria-modal="true">
        <button type="button" className="modal__close" aria-label="Закрыть" onClick={onClose}>
          ×
        </button>
        <h3 className="modal__title modal__title--center">
          {categoryName} — {MONTH_RU[month - 1]} {year}
        </h3>
        {error && <div className="content__error">Не удалось загрузить: {error}</div>}
        {!items ? (
          <div className="content__placeholder">Загрузка...</div>
        ) : items.length === 0 ? (
          <div className="content__placeholder">Нет операций</div>
        ) : (
          <ol className="cell-modal-list">
            {items.map((t, i) => (
              <li key={t.id} className="cell-modal-item">
                <span className="cell-modal-item__num">{i + 1}.</span>
                <span className="cell-modal-item__msg">{t.message}</span>
                <span className="cell-modal-item__amt">{formatAmount(t.amount)}</span>
              </li>
            ))}
          </ol>
        )}
      </div>
    </div>
  );
}
