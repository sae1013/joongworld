(() => {
    const state = {
        activeTab: 'users',
        syncedAt: null,
        data: {
            users: [
                {
                    id: 1,
                    name: '김지원',
                    email: 'jiwon@example.com',
                    nickname: '분당장터지기',
                    role: 'ADMIN',
                    status: 'ACTIVE',
                    joinedAt: '2025-01-03T08:24:00+09:00'
                },
                {
                    id: 2,
                    name: '박서준',
                    email: 'seojun@example.com',
                    nickname: '중고탐험가',
                    role: 'USER',
                    status: 'SUSPENDED',
                    joinedAt: '2024-12-26T13:15:00+09:00'
                }
            ],
            categories: [
                {
                    id: 101,
                    name: '디지털/가전',
                    displayOrder: 1,
                    productCount: 328,
                    active: true
                },
                {
                    id: 102,
                    name: '패션/잡화',
                    displayOrder: 2,
                    productCount: 214,
                    active: true
                },
                {
                    id: 103,
                    name: '반려동물용품',
                    displayOrder: 7,
                    productCount: 48,
                    active: false
                },

            ],
            reports: [
                {
                    id: 'RPT-240112-001',
                    reporter: '이하늘',
                    target: '상품 #59321',
                    reason: '허위 매물 의심',
                    status: 'IN_PROGRESS',
                    reportedAt: '2025-01-12T09:12:00+09:00'
                },
                {
                    id: 'RPT-240111-004',
                    reporter: '정다은',
                    target: '유저 @중고장터왕',
                    reason: '욕설/비방',
                    status: 'RESOLVED',
                    reportedAt: '2025-01-11T20:30:00+09:00'
                }
            ]
        }
    };

    const DEFAULT_DATA = structuredClone ? structuredClone(state.data)
        : JSON.parse(JSON.stringify(state.data));

    const escapeHtml = (value) => {
        if (value == null) {
            return '';
        }
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    };

    const formatDateTime = (value) => {
        if (!value) {
            return '-';
        }
        const dt = new Date(value);
        if (Number.isNaN(dt.getTime())) {
            return '-';
        }
        return dt.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const statusLabels = {
        ACTIVE: {text: '활성', className: 'status-active'},
        SUSPENDED: {text: '정지', className: 'status-suspended'},
        LEAVED: {text: '탈퇴', className: 'status-suspended'},
        PENDING: {text: '대기', className: 'status-pending'},
        IN_PROGRESS: {text: '처리 중', className: 'status-pending'},
        RESOLVED: {text: '완료', className: 'status-active'}
    };

    function badgeStatus(value) {
        const status = statusLabels[value] || {text: value || '-', className: ''};
        return `<span class="status-pill ${status.className || ''}">${escapeHtml(
            status.text)}</span>`;
    }

    function renderUsers() {
        const $tbody = $('#userTableBody');
        const keyword = ($('#userKeyword').val() || '').trim().toLowerCase();
        const role = $('#userRole').val();
        const status = $('#userStatus').val();

        const users = state.data.users.filter((user) => {
            const matchesKeyword = !keyword ||
                [user.name, user.email, user.nickname]
                    .some((field) => field?.toLowerCase().includes(keyword));
            const matchesRole = !role || user.role === role;
            const matchesStatus = !status || user.status === status;
            return matchesKeyword && matchesRole && matchesStatus;
        });

        if (!users.length) {
            $tbody.html(`
                <tr class="table-empty">
                    <td colspan="7">
                        <p>표시할 유저가 없습니다.</p>
                        <span>검색 조건을 조정하거나 새로고침해 주세요.</span>
                    </td>
                </tr>
            `);
            return;
        }

        const rows = users.map((user) => `
            <tr data-id="${escapeHtml(user.id)}">
                <td>${escapeHtml(user.name)}</td>
                <td>${escapeHtml(user.email)}</td>
                <td>${escapeHtml(user.nickname)}</td>
                <td>${user.role === 'ADMIN' ? '관리자' : '일반회원'}</td>
                <td>${badgeStatus(user.status)}</td>
                <td>${formatDateTime(user.joinedAt)}</td>
                <td class="cell-actions">
                    <div class="table-actions">
                        <button type="button" class="table-action-btn" data-action="view-user">보기</button>
                        <button type="button" class="table-action-btn" data-action="suspend-user">정지</button>
                    </div>
                </td>
            </tr>
        `);

        $tbody.html(rows.join(''));
    }

    function renderCategories() {
        const $tbody = $('#categoryTableBody');
        const categories = state.data.categories;

        if (!categories.length) {
            $tbody.html(`
                <tr class="table-empty">
                    <td colspan="5">
                        <p>등록된 카테고리가 없습니다.</p>
                        <span>카테고리를 추가하면 이곳에 표시됩니다.</span>
                    </td>
                </tr>
            `);
            return;
        }

        const rows = categories
            .sort((a, b) => a.displayOrder - b.displayOrder)
            .map((category) => `
                <tr data-id="${escapeHtml(category.id)}">
                    <td>${escapeHtml(category.name)}</td>
                    <td>${escapeHtml(category.displayOrder)}</td>
                    <td>${escapeHtml(category.productCount)}</td>
                    <td>${badgeStatus(category.active ? 'ACTIVE' : 'SUSPENDED')}</td>
                    <td class="cell-actions">
                        <div class="table-actions">
                            <button type="button" class="table-action-btn" data-action="edit-category">편집</button>
                            <button type="button" class="table-action-btn" data-action="toggle-category">${category.active
                ? '숨기기' : '노출'}</button>
                        </div>
                    </td>
                </tr>
            `);

        $tbody.html(rows.join(''));
    }

    function renderReports() {
        const $tbody = $('#reportTableBody');
        const statusFilter = $('#reportStatus').val();
        const period = Number($('#reportPeriod').val() || 30);
        const now = Date.now();
        const ms = period * 24 * 60 * 60 * 1000;

        const reports = state.data.reports.filter((report) => {
            const reportedTime = new Date(report.reportedAt).getTime();
            const matchesStatus = !statusFilter || report.status === statusFilter;
            const matchesPeriod = Number.isNaN(reportedTime) || now - reportedTime
                <= ms;
            return matchesStatus && matchesPeriod;
        });

        if (!reports.length) {
            $tbody.html(`
                <tr class="table-empty">
                    <td colspan="7">
                        <p>최근 신고 내역이 없습니다.</p>
                        <span>새로운 신고가 접수되면 자동으로 업데이트됩니다.</span>
                    </td>
                </tr>
            `);
            return;
        }

        const rows = reports
            .sort((a, b) => new Date(b.reportedAt) - new Date(a.reportedAt))
            .map((report) => `
                <tr data-id="${escapeHtml(report.id)}">
                    <td>${escapeHtml(report.id)}</td>
                    <td>${escapeHtml(report.reporter)}</td>
                    <td>${escapeHtml(report.target)}</td>
                    <td>${escapeHtml(report.reason)}</td>
                    <td>${badgeStatus(report.status)}</td>
                    <td>${formatDateTime(report.reportedAt)}</td>
                    <td class="cell-actions">
                        <div class="table-actions">
                            <button type="button" class="table-action-btn" data-action="view-report">보기</button>
                            <button type="button" class="table-action-btn" data-action="resolve-report">처리</button>
                        </div>
                    </td>
                </tr>
            `);

        $tbody.html(rows.join(''));
    }

    function updateSyncedTime() {
        state.syncedAt = new Date();
        const $time = $('#dashboardSyncedAt');
        if ($time.length) {
            $time.attr('datetime', state.syncedAt.toISOString());
            $time.text(formatDateTime(state.syncedAt));
        }
    }

    function setActiveTab(tabId) {
        if (state.activeTab === tabId) {
            return;
        }
        state.activeTab = tabId;

        $('.dashboard-tab').each(function () {
            const $tab = $(this);
            const isActive = $tab.data('tab') === tabId;
            $tab.toggleClass('is-active', isActive)
                .attr('aria-selected', String(isActive));
        });

        $('.dashboard-panel').each(function () {
            const $panel = $(this);
            const isActive = $panel.data('panel') === tabId;
            $panel.toggleClass('is-active', isActive)
                .attr('hidden', isActive ? null : 'hidden');
        });
    }

    function hydrateFromWindow() {
        if (window.__ADMIN_DASHBOARD__) {
            try {
                state.data = {
                    users: window.__ADMIN_DASHBOARD__.users ?? DEFAULT_DATA.users,
                    categories: window.__ADMIN_DASHBOARD__.categories
                        ?? DEFAULT_DATA.categories,
                    reports: window.__ADMIN_DASHBOARD__.reports ?? DEFAULT_DATA.reports
                };
            } catch (error) {
                console.warn(
                    '[admin-dashboard] failed to load external data, fallback to defaults',
                    error);
                state.data = DEFAULT_DATA;
            }
        }
    }

    $(function () {
        hydrateFromWindow();

        renderUsers();
        renderCategories();
        renderReports();
        updateSyncedTime();

        $('.dashboard-tab').on('click', function () {
            const target = $(this).data('tab');
            if (!target) {
                return;
            }
            setActiveTab(target);
        });

        $('#userKeyword, #userRole, #userStatus').on('input change',
            () => renderUsers());
        $('#reportStatus, #reportPeriod').on('change', () => renderReports());

        $('#dashboardRefreshBtn').on('click', () => {
            updateSyncedTime();
            renderUsers();
            renderCategories();
            renderReports();
        });
    });
})();
