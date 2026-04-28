type Category = {
  id: number;
  name: string;
};

type Transaction = {
  id: string;
  message: string;
  amount: number;
  date: string;
  categoryName?: string;
};

export type EditForm = {
  message: string;
  amount: string;
  date: string;
  categoryName: string;
};

type Props = {
  editing: Transaction | null;
  editForm: EditForm;
  saving: boolean;
  categories: Category[];
  onFormChange: (form: EditForm) => void;
  onSave: () => void;
  onClose: () => void;
};

export default function EditTransactionModal({
  editing,
  editForm,
  saving,
  categories,
  onFormChange,
  onSave,
  onClose,
}: Props) {
  if (!editing) return null;

  return (
    <div
      className="modal-backdrop"
      role="presentation"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="modal modal--wide" role="dialog" aria-modal="true">
        <button
          type="button"
          className="modal__close"
          aria-label="Закрыть"
          onClick={onClose}
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
              onChange={(e) => onFormChange({ ...editForm, message: e.target.value })}
              disabled={saving}
            />
          </div>
          <div className="modal__field">
            <span className="modal__label">Категория</span>
            <select
              className="merge-block__select"
              value={editForm.categoryName}
              onChange={(e) => onFormChange({ ...editForm, categoryName: e.target.value })}
              disabled={saving}
            >
              <option value="Нераспознанное">Нераспознанное</option>
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
              onChange={(e) => onFormChange({ ...editForm, amount: e.target.value })}
              disabled={saving}
            />
          </div>
          <div className="modal__field">
            <span className="modal__label">Дата</span>
            <input
              type="date"
              value={editForm.date}
              onChange={(e) => onFormChange({ ...editForm, date: e.target.value })}
              disabled={saving}
            />
          </div>
          <button
            type="button"
            className="primary-btn primary-btn--block"
            onClick={onSave}
            disabled={saving || !editForm.message.trim()}
          >
            {saving ? 'Сохраняю...' : 'Сохранить'}
          </button>
        </div>
      </div>
    </div>
  );
}
