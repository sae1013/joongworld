document.addEventListener('DOMContentLoaded', () => {
    const table = document.querySelector('.approval-table');
    if (!table) {
        return;
    }
    if (!window.apiService || typeof window.apiService.post !== 'function') {
        console.error('[approve] apiService is not available.');
        return;
    }

    const pendingCountEl = document.getElementById('pendingCount');

    const updatePendingCount = () => {
        if (!pendingCountEl) {
            return;
        }
        const rows = table.querySelectorAll('tbody tr[data-user-row]');
        const next = Math.max(rows.length, 0);
        pendingCountEl.dataset.pendingCount = String(next);
        pendingCountEl.textContent = `${next}명`;
    };

    const showMessage = (title, message) => {
        if (window.Popup && typeof window.Popup.show === 'function') {
            window.Popup.show({
                title,
                message
            });
        } else {
            const plain = message ? message.replace(/<br\s*\/?>/gi, '\n') : '';
            window.alert(`${title}\n${plain}`);
        }
    };

    const confirmAction = (title, message, onConfirm) => {
        if (window.Popup && typeof window.Popup.show === 'function') {
            window.Popup.show({
                title,
                message,
                actions: [
                    { label: '취소', variant: 'secondary' },
                    {
                        label: '확인',
                        variant: 'primary',
                        handler: () => onConfirm()
                    }
                ]
            });
            return;
        }
        if (window.confirm(message.replace(/<br\s*\/?>/gi, '\n'))) {
            onConfirm();
        }
    };

    const removeRow = (row) => {
        if (!row) {
            return;
        }
        row.remove();
        const hasRows = table.querySelectorAll('tbody tr[data-user-row]').length > 0;
        if (!hasRows) {
            window.location.reload();
            return;
        }
        updatePendingCount();
    };

    const handleAction = async (button, onRequest) => {
        if (!button || typeof onRequest !== 'function') {
            return false;
        }
        const originalLabel = button.textContent;
        button.disabled = true;
        button.textContent = '처리 중...';
        try {
            await onRequest();
            return true;
        } catch (error) {
            console.error('[approve] failed', error);
            const message = error?.message || '요청 처리 중 오류가 발생했습니다.';
            showMessage('처리 실패', message);
            return false;
        } finally {
            button.disabled = false;
            button.textContent = originalLabel;
        }
    };

    const requestApproval = (button) => {
        const row = button.closest('tr[data-user-row]');
        const userId = button.dataset.userId;
        const name = row?.querySelector('td')?.textContent?.trim() || '해당 사용자';
        if (!userId) {
            return;
        }
        const confirmMessage = `${name}님의 매니저 가입을 승인할까요?<br/>승인 즉시 관리자 권한이 활성화됩니다.`;

        const execute = async () => {
            const success = await handleAction(button, () => window.apiService.post(`/api/admin/users/${userId}/approve`, {}));
            if (success) {
                removeRow(row);
            }
        };

        confirmAction('가입 승인', confirmMessage, execute);
    };

    const requestRejection = (button) => {
        const row = button.closest('tr[data-user-row]');
        const userId = button.dataset.userId;
        const name = row?.querySelector('td')?.textContent?.trim() || '해당 사용자';
        if (!userId) {
            return;
        }
        const confirmMessage = `${name}님의 가입 요청을 거절할까요?<br/>거절 시 다시 승인을 요청할 때까지 관리자 기능을 사용하지 못합니다.`;

        const execute = async () => {
            const success = await handleAction(button, () => window.apiService.post(`/api/admin/users/${userId}/reject`, {}));
            if (success) {
                removeRow(row);
            }
        };

        confirmAction('가입 거절', confirmMessage, execute);
    };

    table.addEventListener('click', (event) => {
        const approveBtn = event.target.closest('.approval-btn--approve');
        if (approveBtn) {
            requestApproval(approveBtn);
            return;
        }
        const rejectBtn = event.target.closest('.approval-btn--reject');
        if (rejectBtn) {
            requestRejection(rejectBtn);
        }
    });
});
