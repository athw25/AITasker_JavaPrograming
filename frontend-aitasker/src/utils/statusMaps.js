export const JOB_STATUS = {
  OPEN: { label: 'Đang mở', color: 'green' },
  IN_PROGRESS: { label: 'Đang thực hiện', color: 'blue' },
  CLOSED: { label: 'Đã đóng', color: 'gray' },
  CANCELLED: { label: 'Đã hủy', color: 'red' }
};

export const PROPOSAL_STATUS = {
  PENDING: { label: 'Chờ duyệt', color: 'orange' },
  ACCEPTED: { label: 'Đã chấp nhận', color: 'green' },
  REJECTED: { label: 'Đã từ chối', color: 'red' },
  WITHDRAWN: { label: 'Đã rút', color: 'gray' }
};

export const PROJECT_STATUS = {
  ACTIVE: { label: 'Đang thực hiện', color: 'blue' },
  COMPLETED: { label: 'Hoàn thành', color: 'green' },
  CANCELLED: { label: 'Đã hủy', color: 'red' },
  DISPUTED: { label: 'Tranh chấp', color: 'orange' }
};

export const MILESTONE_STATUS = {
  PENDING: { label: 'Chưa nộp', color: 'gray' },
  SUBMITTED: { label: 'Đã nộp', color: 'blue' },
  APPROVED: { label: 'Đã duyệt', color: 'green' },
  REJECTED: { label: 'Bị từ chối', color: 'red' },
  PAID: { label: 'Đã thanh toán', color: 'purple' }
};

export const DELIVERY_STATUS = {
  SUBMITTED: { label: 'Đã nộp', color: 'blue' },
  APPROVED: { label: 'Đã duyệt', color: 'green' },
  REJECTED: { label: 'Bị từ chối', color: 'red' }
};

export const PAYMENT_STATUS = {
  PENDING: { label: 'Chờ xử lý', color: 'orange' },
  HELD: { label: 'Đang giữ (Escrow)', color: 'blue' },
  RELEASED: { label: 'Đã giải ngân', color: 'green' },
  REFUNDED: { label: 'Đã hoàn tiền', color: 'purple' },
  FAILED: { label: 'Thất bại', color: 'red' }
};

export const WITHDRAWAL_STATUS = {
  PENDING: { label: 'Chờ duyệt', color: 'orange' },
  APPROVED: { label: 'Đã duyệt', color: 'green' },
  REJECTED: { label: 'Bị từ chối', color: 'red' }
};

export const DISPUTE_STATUS = {
  OPEN: { label: 'Đang mở', color: 'orange' },
  UNDER_REVIEW: { label: 'Đang xem xét', color: 'blue' },
  RESOLVED_REFUND: { label: 'Đã hoàn tiền', color: 'purple' },
  RESOLVED_REJECTED: { label: 'Bị từ chối', color: 'gray' }
};

export const USER_STATUS = {
  ACTIVE: { label: 'Hoạt động', color: 'green' },
  INACTIVE: { label: 'Không hoạt động', color: 'gray' },
  SUSPENDED: { label: 'Tạm khóa', color: 'orange' },
  BANNED: { label: 'Bị cấm', color: 'red' }
};

export function statusBadge(map, status) {
  return map[status] || { label: status || '—', color: 'gray' };
}
