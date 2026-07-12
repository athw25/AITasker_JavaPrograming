import Modal from './Modal';

export default function ConfirmDialog({ open, onClose, onConfirm, title, message, danger, loading }) {
  return (
    <Modal open={open} onClose={onClose} title={title} size="sm">
      <p className="text-sm text-slate-600">{message}</p>
      <div className="mt-6 flex justify-end gap-3">
        <button className="btn-secondary" onClick={onClose}>Hủy</button>
        <button className={danger ? 'btn-danger' : 'btn-primary'} onClick={onConfirm} disabled={loading}>
          {loading ? 'Đang xử lý...' : 'Xác nhận'}
        </button>
      </div>
    </Modal>
  );
}
