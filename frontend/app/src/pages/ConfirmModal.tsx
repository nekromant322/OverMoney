import { useEffect } from 'react';

type Props = {
  open: boolean;
  title: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
  busy?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
};

export default function ConfirmModal({
  open,
  title,
  message,
  confirmText = 'Удалить',
  cancelText = 'Отмена',
  busy = false,
  onConfirm,
  onCancel,
}: Props) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !busy) onCancel();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [open, busy, onCancel]);

  if (!open) return null;

  return (
    <div
      className="modal-backdrop"
      role="presentation"
      onClick={(e) => {
        if (e.target === e.currentTarget && !busy) onCancel();
      }}
    >
      <div className="modal modal--confirm" role="alertdialog" aria-modal="true">
        <h3 className="modal__title modal__title--center">{title}</h3>
        {message && <p className="modal__hint modal__hint--center">{message}</p>}
        <div className="modal__actions modal__actions--split">
          <button
            type="button"
            className="ghost-btn"
            onClick={onCancel}
            disabled={busy}
          >
            {cancelText}
          </button>
          <button
            type="button"
            className="primary-btn primary-btn--danger"
            onClick={onConfirm}
            disabled={busy}
          >
            {busy ? '...' : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
