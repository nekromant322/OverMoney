import { useEffect, useState } from 'react';
import TopBar from './TopBar';
import { apiFetch } from '../apiFetch';
import './Operations.css';
import './Categories.css';
import './Dynamics.css';

const MONTH_ORDER = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

const MONTH_RU: Record<string, string> = {
  January: 'Январь', February: 'Февраль', March: 'Март', April: 'Апрель',
  May: 'Май', June: 'Июнь', July: 'Июль', August: 'Август',
  September: 'Сентябрь', October: 'Октябрь', November: 'Ноябрь', December: 'Декабрь',
};

const MONTH_RU_SHORT = ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек'];

const PALETTE = [
  '#ff6b6b', '#5ec07a', '#ffa94d', '#ffd43b', '#74c0fc', '#9775fa',
  '#f783ac', '#69db7c', '#fcc419', '#3bc9db', '#da77f2', '#ff8787',
  '#ff922b', '#94d82d', '#22b8cf', '#845ef7',
];

type CategoryAvgData = { categoryId: number; categoryName: string; mediumAmountOfTransactions: number };

type MonthData = { month: string; totalIncome: number; totalExpense: number };

type CategoryYearData = {
  categoryName: string;
  monthlyAnalytics: Record<string, number>;
};

export default function Dynamics() {
  const [years, setYears] = useState<number[]>([]);
  const [yearsError, setYearsError] = useState<string | null>(null);

  useEffect(() => {
    apiFetch('/analytics/available-years', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<number[]>;
      })
      .then(setYears)
      .catch((e: Error) => setYearsError(e.message));
  }, []);

  return (
    <div className="ops-app">
      <TopBar active="dyn" />
      <main className="dynamics-page">
        <div className="dynamics-page__inner">
          {yearsError && (
            <div className="content__error">Не удалось загрузить года: {yearsError}</div>
          )}
          <ExpenseByCategoryPieCard years={years} />
          <IncomeOutcomeCard years={years} />
          <IncomeByCategoryCard years={years} />
          <IncomeOutcomePerYearCard />
        </div>
      </main>
    </div>
  );
}

function YearSelect({
  years,
  value,
  onChange,
}: {
  years: number[];
  value: number | null;
  onChange: (y: number) => void;
}) {
  return (
    <select
      className="merge-block__select chart-year-select"
      value={value ?? ''}
      onChange={(e) => onChange(Number(e.target.value))}
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
  );
}

/* ---------------- Card 0: Expense by category pie ---------------- */

function ExpenseByCategoryPieCard({ years }: { years: number[] }) {
  const [year, setYear] = useState<number | null>(null);
  const [data, setData] = useState<CategoryAvgData[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    if (years.length > 0 && year === null) setYear(Math.max(...years));
  }, [years, year]);

  useEffect(() => {
    if (year === null) return;
    setLoading(true);
    setError(null);
    apiFetch(`/analytics/totalCategorySums/EXPENSE/${year}`, { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<CategoryAvgData[]>;
      })
      .then((rows) => setData(rows.filter((r) => r.mediumAmountOfTransactions > 0)))
      .catch((e: Error) => {
        setData([]);
        setError(e.message);
      })
      .finally(() => setLoading(false));
  }, [year]);

  return (
    <section className={`chart-card ${collapsed ? 'is-collapsed' : ''}`}>
      <header className="chart-card__head">
        <CollapseTitle collapsed={collapsed} onToggle={() => setCollapsed((v) => !v)}>
          Расходы по категориям
        </CollapseTitle>
      </header>

      {!collapsed && (
        <>
          <div className="chart-card__controls">
            <YearSelect years={years} value={year} onChange={setYear} />
          </div>

          {error && <div className="content__error">Не удалось загрузить: {error}</div>}

          {loading ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : !data ? (
            <div className="content__placeholder">Выбери год</div>
          ) : data.length === 0 ? (
            <div className="content__placeholder">Нет данных</div>
          ) : (
            <PieChart data={data} />
          )}
        </>
      )}
    </section>
  );
}

function PieChart({ data }: { data: CategoryAvgData[] }) {
  const { tip, show, hide } = useTooltip();
  const total = data.reduce((s, d) => s + d.mediumAmountOfTransactions, 0);

  const R = 160;
  const CX = 210;
  const CY = 210;
  const W = 900;
  const H = 420;

  const slices: Array<{ d: string; color: string; item: CategoryAvgData; midAngle: number }> = [];
  let angle = -Math.PI / 2;
  for (let i = 0; i < data.length; i++) {
    const item = data[i];
    const sweep = (item.mediumAmountOfTransactions / total) * 2 * Math.PI;
    const endAngle = angle + sweep;
    const mid = angle + sweep / 2;
    const lx1 = CX + R * Math.cos(angle);
    const ly1 = CY + R * Math.sin(angle);
    const lx2 = CX + R * Math.cos(endAngle);
    const ly2 = CY + R * Math.sin(endAngle);
    const large = sweep > Math.PI ? 1 : 0;
    const path = `M ${CX} ${CY} L ${lx1} ${ly1} A ${R} ${R} 0 ${large} 1 ${lx2} ${ly2} Z`;
    slices.push({ d: path, color: PALETTE[i % PALETTE.length], item, midAngle: mid });
    angle = endAngle;
  }

  const legendX = CX + R + 40;
  const legendRowH = 22;
  const maxLegendRows = Math.floor((H - 20) / legendRowH);
  const legendItems = data.slice(0, maxLegendRows);

  return (
    <>
      <ChartTooltip tip={tip} />
      <svg className="bar-chart" viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="xMidYMid meet">
        {slices.map((s) => (
          <path
            key={s.item.categoryId}
            d={s.d}
            fill={s.color}
            stroke="#fff"
            strokeWidth={1.5}
            onMouseEnter={(e) =>
              show(e, `${s.item.categoryName}: ${formatTick(s.item.mediumAmountOfTransactions)} / мес`)
            }
            onMouseMove={(e) =>
              show(e, `${s.item.categoryName}: ${formatTick(s.item.mediumAmountOfTransactions)} / мес`)
            }
            onMouseLeave={hide}
          />
        ))}

        {legendItems.map((item, i) => {
          const color = PALETTE[i % PALETTE.length];
          const y = 20 + i * legendRowH;
          return (
            <g key={item.categoryId}>
              <rect x={legendX} y={y} width={14} height={14} fill={color} rx={2} />
              <text x={legendX + 22} y={y + 11} className="axis-label" fontSize={13}>
                {item.categoryName}
              </text>
              <text x={legendX + 200} y={y + 11} textAnchor="start" className="axis-label" fontSize={13} fontWeight="600">
                {formatTick(item.mediumAmountOfTransactions)}
              </text>
            </g>
          );
        })}
      </svg>
    </>
  );
}

/* ---------------- Card 1: Income/Outcome bars ---------------- */

function IncomeOutcomeCard({ years }: { years: number[] }) {
  const [year, setYear] = useState<number | null>(null);
  const [data, setData] = useState<MonthData[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    if (years.length > 0 && year === null) setYear(Math.max(...years));
  }, [years, year]);

  useEffect(() => {
    if (year === null) return;
    setLoading(true);
    setError(null);
    fetch(`/analytics/totalIncomeOutcome/${year}`, { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<MonthData[]>;
      })
      .then(setData)
      .catch((e: Error) => {
        setData([]);
        setError(e.message);
      })
      .finally(() => setLoading(false));
  }, [year]);

  return (
    <section className={`chart-card ${collapsed ? 'is-collapsed' : ''}`}>
      <header className="chart-card__head">
        <CollapseTitle
          collapsed={collapsed}
          onToggle={() => setCollapsed((v) => !v)}
        >
          Расходы / Доходы по месяцам
        </CollapseTitle>
        {!collapsed && (
          <div className="chart-legend">
            <span className="legend-item">
              <span className="legend-dot legend-dot--expense" />
              Расходы
            </span>
            <span className="legend-item">
              <span className="legend-dot legend-dot--income" />
              Доходы
            </span>
          </div>
        )}
      </header>

      {!collapsed && (
        <>
          <div className="chart-card__controls">
            <YearSelect years={years} value={year} onChange={setYear} />
          </div>

          {error && <div className="content__error">Не удалось загрузить: {error}</div>}

          {loading ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : !data ? (
            <div className="content__placeholder">Выбери год</div>
          ) : (
            <BarChart data={data} />
          )}
        </>
      )}
    </section>
  );
}

function BarChart({ data }: { data: MonthData[] }) {
  const { tip, show, hide } = useTooltip();
  const byMonth = new Map<string, MonthData>();
  for (const d of data) {
    const key = d.month.trim();
    byMonth.set(key, { ...d, month: key });
  }
  const sorted = MONTH_ORDER.map(
    (m) => byMonth.get(m) ?? { month: m, totalIncome: 0, totalExpense: 0 },
  );

  const max = Math.max(...sorted.flatMap((d) => [d.totalIncome, d.totalExpense]), 1);
  const { ticks, niceMax } = niceScale(max, 6);

  const W = 1100;
  const H = 380;
  const PAD = { top: 16, right: 16, bottom: 60, left: 90 };
  const innerW = W - PAD.left - PAD.right;
  const innerH = H - PAD.top - PAD.bottom;

  const slotW = innerW / 12;
  const barW = Math.min(28, slotW * 0.32);
  const gap = 4;

  const yToPx = (v: number) => PAD.top + innerH - (v / niceMax) * innerH;

  return (
    <>
      <ChartTooltip tip={tip} />
      <svg className="bar-chart" viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="xMidYMid meet">
        {ticks.map((t, i) => (
          <g key={i}>
            <line x1={PAD.left} x2={W - PAD.right} y1={yToPx(t)} y2={yToPx(t)} className="grid-line" />
            <text x={PAD.left - 10} y={yToPx(t) + 4} textAnchor="end" className="axis-label">
              {formatTick(t)}
            </text>
          </g>
        ))}

        {sorted.map((d, i) => {
          const slotX = PAD.left + i * slotW;
          const expX = slotX + slotW / 2 - barW - gap / 2;
          const incX = slotX + slotW / 2 + gap / 2;
          const expY = yToPx(d.totalExpense);
          const incY = yToPx(d.totalIncome);
          const ruMonth = MONTH_RU[d.month] ?? d.month;
          return (
            <g key={d.month}>
              <rect
                x={expX}
                y={expY}
                width={barW}
                height={Math.max(0, PAD.top + innerH - expY)}
                className="bar bar--expense"
                rx="2"
                onMouseEnter={(e) => show(e, `${ruMonth}: расход ${formatTick(d.totalExpense)}`)}
                onMouseMove={(e) => show(e, `${ruMonth}: расход ${formatTick(d.totalExpense)}`)}
                onMouseLeave={hide}
              />
              <rect
                x={incX}
                y={incY}
                width={barW}
                height={Math.max(0, PAD.top + innerH - incY)}
                className="bar bar--income"
                rx="2"
                onMouseEnter={(e) => show(e, `${ruMonth}: доход ${formatTick(d.totalIncome)}`)}
                onMouseMove={(e) => show(e, `${ruMonth}: доход ${formatTick(d.totalIncome)}`)}
                onMouseLeave={hide}
              />
              <text x={slotX + slotW / 2} y={H - PAD.bottom + 22} textAnchor="middle" className="axis-label axis-label--month">
                {ruMonth}
              </text>
            </g>
          );
        })}
      </svg>
    </>
  );
}

/* ---------------- Card 2: Income by category lines ---------------- */

function IncomeByCategoryCard({ years }: { years: number[] }) {
  const [year, setYear] = useState<number | null>(null);
  const [data, setData] = useState<CategoryYearData[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [hidden, setHidden] = useState<Set<string>>(new Set());
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    if (years.length > 0 && year === null) setYear(Math.max(...years));
  }, [years, year]);

  useEffect(() => {
    if (year === null) return;
    setLoading(true);
    setError(null);
    setHidden(new Set());
    fetch(`/analytics/income/${year}`, { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<CategoryYearData[]>;
      })
      .then(setData)
      .catch((e: Error) => {
        setData([]);
        setError(e.message);
      })
      .finally(() => setLoading(false));
  }, [year]);

  const toggle = (name: string) => {
    setHidden((prev) => {
      const next = new Set(prev);
      if (next.has(name)) next.delete(name);
      else next.add(name);
      return next;
    });
  };

  return (
    <section className={`chart-card ${collapsed ? 'is-collapsed' : ''}`}>
      <header className="chart-card__head">
        <CollapseTitle
          collapsed={collapsed}
          onToggle={() => setCollapsed((v) => !v)}
        >
          Доходы за год по категориям
        </CollapseTitle>
      </header>

      {!collapsed && (
        <>
          <div className="chart-card__controls">
            <YearSelect years={years} value={year} onChange={setYear} />
          </div>

          {data && data.length > 0 && (
            <div className="line-legend">
              {data.map((c, i) => {
                const color = PALETTE[i % PALETTE.length];
                const isHidden = hidden.has(c.categoryName);
                return (
                  <button
                    key={c.categoryName}
                    type="button"
                    className={`line-legend__item ${isHidden ? 'is-hidden' : ''}`}
                    onClick={() => toggle(c.categoryName)}
                  >
                    <span
                      className="line-legend__dot"
                      style={{ background: isHidden ? 'transparent' : color, borderColor: color }}
                    />
                    <span>{c.categoryName}</span>
                  </button>
                );
              })}
            </div>
          )}

          {error && <div className="content__error">Не удалось загрузить: {error}</div>}

          {loading ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : !data ? (
            <div className="content__placeholder">Выбери год</div>
          ) : data.length === 0 ? (
            <div className="content__placeholder">Нет данных</div>
          ) : (
            <LineChart
              data={data}
              hidden={hidden}
              lastMonth={
                years.length > 0 && year === Math.max(...years)
                  ? lastNonZeroMonth(data)
                  : 12
              }
            />
          )}
        </>
      )}
    </section>
  );
}

function lastNonZeroMonth(data: CategoryYearData[]): number {
  let last = 0;
  for (let m = 1; m <= 12; m++) {
    const has = data.some((c) => Number(c.monthlyAnalytics[String(m)] ?? 0) > 0);
    if (has) last = m;
  }
  return last;
}

function LineChart({
  data,
  hidden,
  lastMonth,
}: {
  data: CategoryYearData[];
  hidden: Set<string>;
  lastMonth: number;
}) {
  const months = Math.max(1, lastMonth);
  const visible = data.filter((c) => !hidden.has(c.categoryName));
  const allValues = visible.flatMap((c) =>
    Array.from({ length: months }, (_, i) => Number(c.monthlyAnalytics[String(i + 1)] ?? 0)),
  );
  const max = Math.max(1, ...allValues);
  const { ticks, niceMax } = niceScale(max, 6);

  const W = 1100;
  const H = 380;
  const PAD = { top: 16, right: 16, bottom: 50, left: 90 };
  const innerW = W - PAD.left - PAD.right;
  const innerH = H - PAD.top - PAD.bottom;

  const slotW = months > 1 ? innerW / (months - 1) : innerW;
  const yToPx = (v: number) => PAD.top + innerH - (v / niceMax) * innerH;
  const xToPx = (i: number) => PAD.left + i * slotW;

  const { tip, show, hide } = useTooltip();

  return (
    <>
      <ChartTooltip tip={tip} />
      <svg className="bar-chart" viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="xMidYMid meet">
        {ticks.map((t, i) => (
          <g key={i}>
            <line x1={PAD.left} x2={W - PAD.right} y1={yToPx(t)} y2={yToPx(t)} className="grid-line" />
            <text x={PAD.left - 10} y={yToPx(t) + 4} textAnchor="end" className="axis-label">
              {formatTick(t)}
            </text>
          </g>
        ))}

        {data.map((c, ci) => {
          if (hidden.has(c.categoryName)) return null;
          const color = PALETTE[ci % PALETTE.length];
          const points = Array.from({ length: months }, (_, i) => {
            const v = Number(c.monthlyAnalytics[String(i + 1)] ?? 0);
            return { x: xToPx(i), y: yToPx(v), v };
          });
          const path = points.map((p) => `${p.x},${p.y}`).join(' ');
          return (
            <g key={c.categoryName}>
              <polyline points={path} fill="none" stroke={color} strokeWidth={2.5} strokeLinejoin="round" strokeLinecap="round" />
              {points.map((p, i) => (
                <circle
                  key={i}
                  cx={p.x}
                  cy={p.y}
                  r={3.5}
                  fill={color}
                  onMouseEnter={(e) => show(e, `${MONTH_RU_SHORT[i]}: ${c.categoryName} — ${formatTick(p.v)}`)}
                  onMouseMove={(e) => show(e, `${MONTH_RU_SHORT[i]}: ${c.categoryName} — ${formatTick(p.v)}`)}
                  onMouseLeave={hide}
                />
              ))}
            </g>
          );
        })}

        {MONTH_RU_SHORT.slice(0, months).map((m, i) => (
          <text
            key={m}
            x={xToPx(i)}
            y={H - PAD.bottom + 22}
            textAnchor="middle"
            className="axis-label axis-label--month"
          >
            {m}
          </text>
        ))}
      </svg>
    </>
  );
}

/* ---------------- Card 3: Income/Expense per year bars ---------------- */

type YearRow = { year: number; income: number; expense: number };

function IncomeOutcomePerYearCard() {
  const [rows, setRows] = useState<YearRow[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [collapsed, setCollapsed] = useState(true);

  useEffect(() => {
    setLoading(true);
    apiFetch('/analytics/v2/years/amounts', { credentials: 'include' })
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json() as Promise<{
          data: Array<{
            year: number;
            users: Array<{
              categoryIncome?: Array<{ sum?: number | null }>;
              categoryExpense?: Array<{ sum?: number | null }>;
            }>;
          }>;
        }>;
      })
      .then((resp) => {
        const totals = (resp.data ?? [])
          .map((yr) => {
            let income = 0;
            let expense = 0;
            for (const u of yr.users ?? []) {
              for (const c of u.categoryIncome ?? []) income += c.sum ?? 0;
              for (const c of u.categoryExpense ?? []) expense += c.sum ?? 0;
            }
            return { year: yr.year, income, expense };
          })
          .sort((a, b) => a.year - b.year);
        setRows(totals);
      })
      .catch((e: Error) => {
        setRows([]);
        setError(e.message);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className={`chart-card ${collapsed ? 'is-collapsed' : ''}`}>
      <header className="chart-card__head">
        <CollapseTitle
          collapsed={collapsed}
          onToggle={() => setCollapsed((v) => !v)}
        >
          Доходы / Расходы по годам
        </CollapseTitle>
        {!collapsed && (
          <div className="chart-legend">
            <span className="legend-item">
              <span className="legend-dot legend-dot--expense" />
              Расходы
            </span>
            <span className="legend-item">
              <span className="legend-dot legend-dot--income" />
              Доходы
            </span>
          </div>
        )}
      </header>

      {!collapsed && (
        <>
          {error && <div className="content__error">Не удалось загрузить: {error}</div>}

          {loading ? (
            <div className="content__placeholder">Загрузка...</div>
          ) : !rows || rows.length === 0 ? (
            <div className="content__placeholder">Нет данных</div>
          ) : (
            <YearBarChart rows={rows} />
          )}
        </>
      )}
    </section>
  );
}

function YearBarChart({ rows }: { rows: YearRow[] }) {
  const { tip, show, hide } = useTooltip();
  const max = Math.max(1, ...rows.flatMap((r) => [r.income, r.expense]));
  const { ticks, niceMax } = niceScale(max, 6);

  const W = 1100;
  const H = 380;
  const PAD = { top: 16, right: 16, bottom: 50, left: 90 };
  const innerW = W - PAD.left - PAD.right;
  const innerH = H - PAD.top - PAD.bottom;

  const slotW = innerW / Math.max(1, rows.length);
  const barW = Math.min(56, slotW * 0.32);
  const gap = 6;

  const yToPx = (v: number) => PAD.top + innerH - (v / niceMax) * innerH;

  return (
    <>
      <ChartTooltip tip={tip} />
      <svg className="bar-chart" viewBox={`0 0 ${W} ${H}`} preserveAspectRatio="xMidYMid meet">
      {ticks.map((t) => (
        <g key={t}>
          <line x1={PAD.left} x2={W - PAD.right} y1={yToPx(t)} y2={yToPx(t)} className="grid-line" />
          <text x={PAD.left - 10} y={yToPx(t) + 4} textAnchor="end" className="axis-label">
            {formatTick(t)}
          </text>
        </g>
      ))}

      {rows.map((r, i) => {
        const slotX = PAD.left + i * slotW;
        const expX = slotX + slotW / 2 - barW - gap / 2;
        const incX = slotX + slotW / 2 + gap / 2;
        const expY = yToPx(r.expense);
        const incY = yToPx(r.income);
        return (
          <g key={r.year}>
            <rect
              x={expX}
              y={expY}
              width={barW}
              height={Math.max(0, PAD.top + innerH - expY)}
              className="bar bar--expense"
              rx="2"
              onMouseEnter={(e) => show(e, `${r.year}: расход ${formatTick(r.expense)}`)}
              onMouseMove={(e) => show(e, `${r.year}: расход ${formatTick(r.expense)}`)}
              onMouseLeave={hide}
            />
            <rect
              x={incX}
              y={incY}
              width={barW}
              height={Math.max(0, PAD.top + innerH - incY)}
              className="bar bar--income"
              rx="2"
              onMouseEnter={(e) => show(e, `${r.year}: доход ${formatTick(r.income)}`)}
              onMouseMove={(e) => show(e, `${r.year}: доход ${formatTick(r.income)}`)}
              onMouseLeave={hide}
            />
            <text x={slotX + slotW / 2} y={H - PAD.bottom + 22} textAnchor="middle" className="axis-label axis-label--month">
              {r.year}
            </text>
          </g>
        );
      })}
      </svg>
    </>
  );
}

/* ---------------- CollapseTitle ---------------- */

function CollapseTitle({
  collapsed,
  onToggle,
  children,
}: {
  collapsed: boolean;
  onToggle: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      className="chart-card__title-btn"
      onClick={onToggle}
      aria-expanded={!collapsed}
    >
      <svg
        width="14"
        height="14"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        className={`collapse-chevron ${collapsed ? 'is-collapsed' : ''}`}
      >
        <path d="m6 9 6 6 6-6" />
      </svg>
      <h2 className="chart-card__title">{children}</h2>
    </button>
  );
}

/* ---------------- Tooltip ---------------- */

type TipState = { x: number; y: number; text: string } | null;

function useTooltip() {
  const [tip, setTip] = useState<TipState>(null);
  const show = (e: React.MouseEvent, text: string) => {
    const card = (e.currentTarget as Element).closest('.chart-card') as HTMLElement | null;
    if (!card) return;
    const rect = card.getBoundingClientRect();
    setTip({ x: e.clientX - rect.left, y: e.clientY - rect.top, text });
  };
  const hide = () => setTip(null);
  return { tip, show, hide };
}

function ChartTooltip({ tip }: { tip: TipState }) {
  if (!tip) return null;
  return (
    <div className="chart-tooltip" style={{ left: tip.x + 12, top: tip.y - 30 }}>
      {tip.text}
    </div>
  );
}

/* ---------------- Helpers ---------------- */

function niceStep(rough: number): number {
  if (rough <= 0) return 1;
  const exp = Math.pow(10, Math.floor(Math.log10(rough)));
  const f = rough / exp;
  let nice: number;
  if (f < 1.5) nice = 1;
  else if (f < 3) nice = 2;
  else if (f < 7) nice = 5;
  else nice = 10;
  return nice * exp;
}

function niceScale(max: number, desiredTicks: number): { ticks: number[]; niceMax: number } {
  const safeMax = Math.max(1, max);
  const step = niceStep(safeMax / desiredTicks);
  const niceMax = Math.ceil(safeMax / step) * step;
  const ticks: number[] = [];
  for (let i = 0; i * step <= niceMax + 1e-9; i++) ticks.push(i * step);
  return { ticks, niceMax };
}

function formatTick(v: number) {
  return new Intl.NumberFormat('ru-RU').format(Math.round(v));
}
