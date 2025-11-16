(() => {
  const INITIAL_REPORTS = {
    users: [
      {
        id: 'URP-240111-004',
        reporter: '정다은',
        target: '유저 @중고장터왕',
        reason: '욕설/비방',
        status: 'RESOLVED',
        reportedAt: '2025-01-11T20:30:00+09:00'
      },
      {
        id: 'URP-240110-002',
        reporter: '이민수',
        target: '유저 @안전결제희망',
        reason: '사기 의심',
        status: 'IN_PROGRESS',
        reportedAt: '2025-01-10T11:10:00+09:00'
      }
    ],
    products: [
      {
        id: 'PRP-240112-001',
        reporter: '이하늘',
        productTitle: '맥북 프로 16 M3',
        seller: '분당장터지기',
        reason: '허위 매물 의심',
        status: 'IN_PROGRESS',
        reportedAt: '2025-01-12T09:12:00+09:00'
      },
      {
        id: 'PRP-240109-003',
        reporter: '장유진',
        productTitle: '나이키 한정판 조던',
        seller: '스니커덕후',
        reason: '가격 허위 기재',
        status: 'PENDING',
        reportedAt: '2025-01-09T15:45:00+09:00'
      }
    ]
  };

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
      categories: [],
      reports: structuredClone ? structuredClone(INITIAL_REPORTS)
          : JSON.parse(JSON.stringify(INITIAL_REPORTS))
    }
  };

  const ui = {
    creatingCategory: null,
    editingCategory: null, // { id, draft }
    isSavingCategory: false
  };

  let resolveSubmitting = false;

  const CATEGORY_API_BASE = '/api/admin/categories';

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

  const reasonLabels = {
    SPAM: '스팸/광고',
    FRAUD: '사기/금전 요구',
    ABUSE: '욕설/혐오',
    ADULT: '성인/음란'
  };

  const reportStatusOptions = [
    {value: 'PENDING', label: '대기'},
    {value: 'IN_PROGRESS', label: '처리 중'},
    {value: 'RESOLVED', label: '처리 완료'}
  ];

  const userStatusOptions = [
    {value: 'ACTIVE', label: '활성'},
    {value: 'DORMANT', label: '휴면'},
    {value: 'SUSPENDED', label: '정지'},
    {value: 'WITHDRAWN', label: '탈퇴'}
  ];

  const productStatusOptions = [
    {value: 'ACTIVE', label: '활성'},
    {value: 'HIDDEN', label: '숨김'},
    {value: 'BLOCKED', label: '차단'}
  ];

  const resolutionTypeOptions = [
    {value: 'NO_ACTION', label: '조치 없음'},
    {value: 'WARNED', label: '경고'},
    {value: 'SUSPENDED_USER', label: '유저 정지'},
    {value: 'BLOCKED_PRODUCT', label: '상품 차단'},
    {value: 'OTHER', label: '기타'}
  ];

  const labelFrom = (options, value) => {
    const found = options.find(opt => opt.value === value);
    return found ? found.label : value;
  };

  function badgeStatus(value) {
    const status = statusLabels[value] || {text: value || '-', className: ''};
    return `<span class="status-pill ${status.className || ''}">${escapeHtml(
        status.text)}</span>`;
  }

  function normalizeCategoryResponse(response, fallbackCount = 0) {
    if (!response) {
      return null;
    }
    return {
      id: response.id,
      name: response.name,
      displayOrder: response.displayOrder,
      productCount: typeof response.productCount === 'number'
          ? response.productCount
          : fallbackCount,
      active: response.active
    };
  }

  function cloneReports(reports) {
    if (!reports) {
      return {users: [], products: []};
    }
    return {
      users: Array.isArray(reports.users) ? reports.users.slice() : [],
      products: Array.isArray(reports.products) ? reports.products.slice() : []
    };
  }

  function ensureApiClient() {
    if (!window.apiService
        || typeof window.apiService.post !== 'function'
        || typeof window.apiService.put !== 'function') {
      throw new Error('요청 클라이언트를 초기화하지 못했습니다.');
    }
  }

  function setCategorySaving(flag) {
    ui.isSavingCategory = flag;
    renderCategories();
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
<!--                        <button type="button" class="table-action-btn" data-action="view-user">보기</button>-->
<!--                        <button type="button" class="table-action-btn" data-action="suspend-user">정지</button>-->
                    </div>
                </td>
            </tr>
        `);

    $tbody.html(rows.join(''));
  }

  const notify = (options) => {
    if (window.Popup && typeof window.Popup.show === 'function') {
      window.Popup.show(typeof options === 'string'
          ? {message: options}
          : options);
    } else {
      alert(typeof options === 'string'
          ? options
          : options?.message || '알림');
    }
  };

  function renderCategoryRow(category) {
    const isEditing = ui.editingCategory && ui.editingCategory.id
        === category.id;
    const draft = isEditing ? ui.editingCategory.draft : category;
    const nameValue = draft.name ?? category.name ?? '';
    const orderValue = draft.displayOrder ?? category.displayOrder ?? '';
    const activeValue = draft.active !== false;
    const rowMode = isEditing ? 'edit' : 'view';
    return `
            <tr data-id="${escapeHtml(category.id)}" data-mode="${rowMode}">
                <td>
                    <input type="text"
                           class="category-input ${isEditing ? 'is-editing'
        : 'is-readonly'}"
                           data-field="name"
                           value="${escapeHtml(nameValue)}"
                           ${isEditing ? '' : 'readonly tabindex="-1"'}>
                </td>
                <td>
                    <input type="number"
                           class="category-input ${isEditing ? 'is-editing'
        : 'is-readonly'}"
                           data-field="displayOrder"
                           min="1"
                           value="${escapeHtml(orderValue)}"
                           ${isEditing ? '' : 'readonly tabindex="-1"'}>
                </td>
                <td>
                    ${isEditing
        ? `<button type="button"
                               class="category-status-toggle status-pill ${activeValue
            ? 'status-active'
            : 'status-suspended'}"
                               data-action="toggle-form-status">
                               ${activeValue ? '활성' : '숨김'}
                           </button>`
        : badgeStatus(category.active ? 'ACTIVE' : 'SUSPENDED')}
                </td>
                <td class="cell-actions">
                    <div class="table-actions">
                    ${isEditing
        ? `
                                <button type="button"
                                        class="table-action-btn table-action-btn--primary"
                                        data-action="save-category"
                                        ${ui.isSavingCategory ? 'disabled' : ''}>저장</button>
                                <button type="button"
                                        class="table-action-btn"
                                        data-action="cancel-edit-category">취소</button>
                              `
        : `
                                <button type="button"
                                        class="table-action-btn"
                                        data-action="edit-category">편집</button>
                              `}
                    </div>
                </td>
            </tr>
        `;
  }

  function renderCategoryFormRow(id, draft, mode) {
    const isNew = mode === 'create';
    return `
            <tr class="category-form-row" data-id="${escapeHtml(
        id)}" data-mode="${mode}">
                <td>
                    <input type="text"
                           class="category-input"
                           data-field="name"
                           placeholder="카테고리명"
                           value="${escapeHtml(draft.name ?? '')}">
                </td>
                <td>
                    <input type="number"
                           class="category-input"
                           data-field="displayOrder"
                           min="1"
                           placeholder="순서"
                           value="${escapeHtml(draft.displayOrder ?? '')}">
                </td>
                <td>
                    <button type="button"
                            class="category-status-toggle status-pill ${draft.active
    !== false ? 'status-active' : 'status-suspended'}"
                            data-action="toggle-form-status">
                        ${draft.active !== false ? '활성' : '숨김'}
                    </button>
                </td>
                <td class="cell-actions">
                    <div class="table-actions">
                        <button type="button"
                                class="table-action-btn table-action-btn--primary"
                                data-action="${isNew ? 'save-new-category'
        : 'save-category'}"
                                ${ui.isSavingCategory ? 'disabled' : ''}>저장</button>
                        <button type="button"
                                class="table-action-btn"
                                data-action="${isNew ? 'cancel-new-category'
        : 'cancel-edit-category'}">취소</button>
                    </div>
                </td>
            </tr>
        `;
  }

  function focusDraftInput(mode) {
    requestAnimationFrame(() => {
      const selector = mode === 'create'
          ? '#categoryTableBody tr[data-mode="create"] .category-input[data-field="name"]'
          : '#categoryTableBody tr[data-mode="edit"] .category-input[data-field="name"]';
      const el = document.querySelector(selector);
      if (el) {
        el.focus();
      }
    });
  }

  function renderCategories() {
    const $tbody = $('#categoryTableBody');
    const categories = state.data.categories.slice().sort(
        (a, b) => a.displayOrder - b.displayOrder);
    const rows = [];

    if (ui.creatingCategory) {
      rows.push(renderCategoryFormRow('new', ui.creatingCategory, 'create'));
    }

    categories.forEach((category) => {
      rows.push(renderCategoryRow(category));
    });

    if (!rows.length) {
      $tbody.html(`
                <tr class="table-empty">
                    <td colspan="5">
                        <p>등록된 카테고리가 없습니다.</p>
                        <span>카테고리를 추가하면 이곳에 표시됩니다.</span>
                    </td>
                </tr>
            `);
    } else {
      $tbody.html(rows.join(''));
    }

    $('.panel-btn[data-action="create-category"]').prop('disabled',
        !!ui.creatingCategory);
  }

  function renderUserReports() {
    const $tbody = $('#reportTableBody');
    const statusFilter = $('#reportStatus').val();
    const period = Number($('#reportPeriod').val() || 30);
    const now = Date.now();
    const ms = period * 24 * 60 * 60 * 1000;

    const reports = (state.data.reports.users || []).filter((report) => {
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
                <tr data-report-id="${escapeHtml(report.id)}" data-report-type="user">
                    <td>${escapeHtml(report.id)}</td>
                    <td>${escapeHtml(report.reporter)}</td>
                    <td>${escapeHtml(report.target)}</td>
                    <td>${escapeHtml(
        reasonLabels[report.reason] || report.reason)}</td>
                    <td>${badgeStatus(report.status)}</td>
                    <td>${formatDateTime(report.reportedAt)}</td>
                    <td class="cell-actions">
                        <div class="table-actions">
                            <button type="button" class="table-action-btn" data-action="view-report">보기</button>
                        </div>
                    </td>
                </tr>
            `);

    $tbody.html(rows.join(''));
  }

  function renderProductReports() {
    const $tbody = $('#productReportTableBody');
    const statusFilter = $('#productReportStatus').val();
    const period = Number($('#productReportPeriod').val() || 30);
    const now = Date.now();
    const ms = period * 24 * 60 * 60 * 1000;

    const reports = (state.data.reports.products || []).filter((report) => {
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
                        <p>최근 상품 신고가 없습니다.</p>
                        <span>신고가 접수되면 최신 순으로 표시됩니다.</span>
                    </td>
                </tr>
            `);
      return;
    }

    const rows = reports
    .sort((a, b) => new Date(b.reportedAt) - new Date(a.reportedAt))
    .map((report) => `
                <tr data-report-id="${escapeHtml(report.id)}" data-report-type="product">
                    <td>${escapeHtml(report.id)}</td>
                    <td>${escapeHtml(report.reporter)}</td>
                    <td>${escapeHtml(
        report.productTitle || report.product || report.target || '-')}</td>
                    <td>${escapeHtml(report.seller || '-')}</td>
                    <td>${escapeHtml(
        reasonLabels[report.reason] || report.reason)}</td>
                    <td>${badgeStatus(report.status)}</td>
                    <td>${formatDateTime(report.reportedAt)}</td>
                    <td class="cell-actions">
                        <div class="table-actions">
                            <button type="button" class="table-action-btn" data-action="view-report">보기</button>
                        </div>
                    </td>
                </tr>
            `);

    $tbody.html(rows.join(''));
  }

  function findReportById(type, id) {
    const collection = type === 'product'
        ? (state.data.reports.products || [])
        : (state.data.reports.users || []);
    return collection.find((item) => String(item.id) === String(id));
  }

  function buildReportDetailHtml(report, type) {
    const pairs = [
      ['신고자', report.reporter || '-'],
      type === 'product'
          ? ['상품명', report.productTitle || '-']
          : ['대상', report.target || '-'],
      type === 'product'
          ? ['판매자', report.seller || '-']
          : null,
      type === 'product'
          ? (report.productStatus ? ['상품 상태', labelFrom(productStatusOptions, report.productStatus)] : null)
          : (report.targetStatus ? ['유저 상태', labelFrom(userStatusOptions, report.targetStatus)] : null),
      ['처리 상태', statusLabels[report.status]?.text || report.status || '-'],
      ['사유', reasonLabels[report.reason] || report.reason || '-'],
      ['신고일', formatDateTime(report.reportedAt)],
      ['신고 내용', report.description || '-'],
      report.handlerNickname ? ['처리 운영자', report.handlerNickname] : null,
      report.processedAt ? ['처리 일시', formatDateTime(report.processedAt)] : null
    ].filter(Boolean);

    return `
            <dl class="report-detail">
                ${pairs.map(([label, value]) => `
                    <div class="report-detail__row">
                        <dt>${label}</dt>
                        <dd>${escapeHtml(value)}</dd>
                    </div>
                `).join('')}
            </dl>
        `;
  }

  function openReportPopup(type, report) {
    if (!report) {
      notify('신고 정보를 찾을 수 없습니다.');
      return;
    }
    const actions = [
      {
        label: '닫기',
        variant: 'secondary'
      }
    ];
    if (report.status !== 'RESOLVED') {
      actions.push({
        label: '처리하기',
        variant: 'primary',
        close: false,
        handler: ({close}) => {
          close();
          openResolveDialog(type, report);
        }
      });
    }
    window.Popup?.show({
      title: type === 'product' ? '상품 신고 상세' : '유저 신고 상세',
      html: buildReportDetailHtml(report, type),
      className: 'popup-md',
      actions
    });
  }

  function buildOptions(options, includeBlank = true) {
    const items = options.map(
        opt => `<option value="${opt.value}">${opt.label}</option>`);
    return includeBlank ? ['<option value="">선택하세요</option>', ...items].join('')
        : items.join('');
  }

  function buildResolveForm(type, formId) {
    const targetOptions = type === 'product' ? productStatusOptions
        : userStatusOptions;
    return `
            <form id="${formId}" class="report-resolution-form">
                <div class="report-resolution__group">
                    <label>신고 상태</label>
                    <select name="reportStatus" required>
                        ${buildOptions(reportStatusOptions, false)}
                    </select>
                </div>
                <div class="report-resolution__group">
                    <label>${type === 'product' ? '상품 상태' : '유저 상태'}</label>
                    <select name="targetStatus">
                        ${buildOptions(targetOptions)}
                    </select>
                </div>
                <div class="report-resolution__group">
                    <label>처리 유형</label>
                    <select name="resolutionType">
                        ${buildOptions(resolutionTypeOptions)}
                    </select>
                </div>
                <div class="report-resolution__group">
                    <label>메모</label>
                    <textarea name="memo" rows="3" maxlength="1000" placeholder="처리 내용을 입력해 주세요."></textarea>
                </div>
                <div class="text-danger small d-none" data-error></div>
            </form>
        `;
  }

  function openResolveDialog(type, report) {
    if (!report) {
      notify('신고 정보를 찾을 수 없습니다.');
      return;
    }
    const formId = `reportResolveForm-${Date.now()}`;
    window.Popup?.show({
      title: type === 'product' ? '상품 신고 처리' : '유저 신고 처리',
      html: buildResolveForm(type, formId),
      className: 'popup-md',
      actions: [
        {label: '취소', variant: 'secondary'},
        {
          label: '저장',
          variant: 'primary',
          close: false,
          handler: ({close}) => submitResolveForm(type, report, formId, close)
        }
      ]
    });
  }

  async function submitResolveForm(type, report, formId, closePopup) {
    if (resolveSubmitting) {
      return;
    }
    const form = document.getElementById(formId);
    if (!form) {
      return;
    }
    const getValue = (name) => (form.elements.namedItem(name)?.value
        || '').trim();
    const reportStatus = getValue('reportStatus');
    const targetStatus = getValue('targetStatus');
    const resolutionType = getValue('resolutionType');
    const memo = getValue('memo');
    const errorEl = form.querySelector('[data-error]');

    const showError = (message) => {
      if (errorEl) {
        errorEl.textContent = message;
        errorEl.classList.remove('d-none');
      } else {
        notify(message);
      }
    };

    if (!reportStatus) {
      showError('신고 상태를 선택해 주세요.');
      return;
    }

    if (!window.apiService) {
      showError('요청 클라이언트를 초기화하지 못했습니다.');
      return;
    }

    if (errorEl) {
      errorEl.classList.add('d-none');
      errorEl.textContent = '';
    }

    resolveSubmitting = true;
    try {
      await window.apiService.put(`/api/admin/reports/${report.id}/resolve`, {
        reportStatus,
        targetStatus: targetStatus || null,
        resolutionType: resolutionType || null,
        memo
      });
      report.status = reportStatus;
      renderUserReports();
      renderProductReports();
      if (typeof closePopup === 'function') {
        closePopup();
      }
      notify({message: '신고 처리가 완료되었습니다.'});
    } catch (error) {
      showError(error?.message || '신고 처리 중 오류가 발생했습니다.');
    } finally {
      resolveSubmitting = false;
    }
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

  function ensureNoDrafts() {
    ui.creatingCategory = null;
    ui.editingCategory = null;
  }

  function startCreateCategory() {
    if (ui.creatingCategory) {
      notify('이미 추가 중인 카테고리가 있습니다.');
      return;
    }
    ui.creatingCategory = {
      name: '',
      displayOrder: state.data.categories.length + 1,
      productCount: 0,
      active: true
    };
    renderCategories();
    focusDraftInput('create');
  }

  function startEditCategory(id) {
    const numericId = Number(id);
    const category = state.data.categories.find(
        (item) => item.id === numericId);
    if (!category) {
      notify('카테고리를 찾을 수 없습니다.');
      return;
    }
    ui.editingCategory = {
      id: numericId,
      draft: {
        ...category
      }
    };
    renderCategories();
    focusDraftInput('edit');
  }

  function cancelCreateCategory() {
    ui.creatingCategory = null;
    renderCategories();
  }

  function cancelEditCategory() {
    ui.editingCategory = null;
    renderCategories();
  }

  function parseDraft(draft) {
    const name = (draft.name || '').trim();
    if (!name) {
      notify('카테고리명을 입력해 주세요.');
      return null;
    }
    const order = Number(draft.displayOrder);
    if (!Number.isFinite(order) || order < 1) {
      notify('표시 순서는 1 이상의 숫자여야 합니다.');
      return null;
    }
    return {
      name,
      displayOrder: Math.floor(order),
      active: draft.active !== false
    };
  }

  async function saveNewCategory() {
    if (!ui.creatingCategory) {
      return;
    }
    if (ui.isSavingCategory) {
      return;
    }
    const parsed = parseDraft(ui.creatingCategory);
    if (!parsed) {
      return;
    }
    try {
      ensureApiClient();
      setCategorySaving(true);
      const payload = {
        name: parsed.name,
        displayOrder: parsed.displayOrder,
        active: parsed.active
      };
      console.log(payload)

      const response = await window.apiService.post(CATEGORY_API_BASE, payload);
      const normalized = normalizeCategoryResponse(response, 0)
          ?? {
            id: Date.now(),
            name: parsed.name,
            displayOrder: parsed.displayOrder,
            productCount: 0,
            active: parsed.active
          };
      state.data.categories.push(normalized);
      ui.creatingCategory = null;
      notify({message: '카테고리가 추가되었습니다.'});
      renderCategories();
    } catch (error) {
      console.error('[admin-dashboard] failed to create category', error);
      notify(error?.message || '카테고리 생성 중 오류가 발생했습니다.');
    } finally {
      setCategorySaving(false);
    }
  }

  async function saveExistingCategory() {
    if (!ui.editingCategory) {
      return;
    }
    if (ui.isSavingCategory) {
      return;
    }
    const parsed = parseDraft(ui.editingCategory.draft);
    if (!parsed) {
      return;
    }
    const target = state.data.categories.find(
        (item) => item.id === ui.editingCategory.id);
    if (!target) {
      notify('카테고리를 찾을 수 없습니다.');
      return;
    }
    try {
      ensureApiClient();
      setCategorySaving(true);
      const payload = {
        name: parsed.name,
        displayOrder: parsed.displayOrder,
        active: parsed.active
      };
      const response = await window.apiService.put(
          `${CATEGORY_API_BASE}/${ui.editingCategory.id}`,
          payload
      );
      const normalized = normalizeCategoryResponse(response,
              target.productCount)
          ?? {
            id: target.id,
            name: parsed.name,
            displayOrder: parsed.displayOrder,
            productCount: target.productCount ?? 0,
            active: parsed.active
          };
      target.name = normalized.name;
      target.displayOrder = normalized.displayOrder;
      target.productCount = normalized.productCount;
      target.active = normalized.active;
      ui.editingCategory = null;
      notify({message: '카테고리가 수정되었습니다.'});
      renderCategories();
    } catch (error) {
      console.error('[admin-dashboard] failed to update category', error);
      notify(error?.message || '카테고리 수정 중 오류가 발생했습니다.');
    } finally {
      setCategorySaving(false);
    }
  }

  function toggleCategoryActive(id) {
    const numericId = Number(id);
    const target = state.data.categories.find((item) => item.id === numericId);
    if (!target) {
      notify('카테고리를 찾을 수 없습니다.');
      return;
    }
    target.active = !target.active;
    renderCategories();
  }

  function toggleDraftStatus(mode) {
    if (mode === 'create' && ui.creatingCategory) {
      ui.creatingCategory.active = !ui.creatingCategory.active;
      renderCategories();
    } else if (mode === 'edit' && ui.editingCategory) {
      ui.editingCategory.draft.active = !ui.editingCategory.draft.active;
      renderCategories();
    }
  }

  function updateDraftValue(mode, field, value) {
    if (mode === 'create' && ui.creatingCategory) {
      ui.creatingCategory[field] = value;
    } else if (mode === 'edit' && ui.editingCategory) {
      ui.editingCategory.draft[field] = value;
    }
  }

  function normalizeReportsData(source) {
    if (!source) {
      return cloneReports(DEFAULT_DATA.reports);
    }
    if (Array.isArray(source)) {
      return {
        users: source.slice(),
        products: []
      };
    }
    return {
      users: Array.isArray(source.users) ? source.users.slice() : [],
      products: Array.isArray(source.products) ? source.products.slice() : []
    };
  }

  function hydrateFromWindow() {
    if (!window.__ADMIN_DASHBOARD__) {
      return;
    }
    try {
      const external = window.__ADMIN_DASHBOARD__;
      state.data = {
        users: Array.isArray(external.users) ? external.users
            : DEFAULT_DATA.users,
        categories: Array.isArray(external.categories) ? external.categories
            : DEFAULT_DATA.categories,
        reports: normalizeReportsData(external.reports)
      };
    } catch (error) {
      console.warn(
          '[admin-dashboard] failed to load external data, fallback to defaults',
          error);
      state.data = DEFAULT_DATA;
    }
  }

  $(function () {
    hydrateFromWindow();

    renderUsers();
    renderCategories();
    renderUserReports();
    renderProductReports();
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
    $('#reportStatus, #reportPeriod').on('change', () => renderUserReports());
    $('#productReportStatus, #productReportPeriod').on('change',
        () => renderProductReports());

    $('#reportTableBody').on('click', '[data-action="view-report"]',
        function () {
          const $row = $(this).closest('tr');
          openReportPopup('user',
              findReportById('user', $row.data('reportId')));
        });

    $('#productReportTableBody').on('click', '[data-action="view-report"]',
        function () {
          const $row = $(this).closest('tr');
          openReportPopup('product',
              findReportById('product', $row.data('reportId')));
        });

    $('#dashboardRefreshBtn').on('click', () => {
      window.location.reload();
    });

    $('[data-action="create-category"]').on('click',
        () => startCreateCategory());

    $('#categoryTableBody')
    .on('click', '[data-action="edit-category"]', function () {
      const id = $(this).closest('tr').data('id');
      startEditCategory(id);
    })
    .on('click', '[data-action="save-new-category"]', () => saveNewCategory())
    .on('click', '[data-action="cancel-new-category"]',
        () => cancelCreateCategory())
    .on('click', '[data-action="save-category"]', () => saveExistingCategory())
    .on('click', '[data-action="cancel-edit-category"]',
        () => cancelEditCategory())
    .on('click', '[data-action="toggle-form-status"]', function () {
      const mode = $(this).closest('tr').data('mode');
      toggleDraftStatus(mode);
    })
    .on('input change', '.category-input', function () {
      const field = $(this).data('field');
      if (!field) {
        return;
      }
      const mode = $(this).closest('tr').data('mode');
      if (mode !== 'create' && mode !== 'edit') {
        return;
      }
      updateDraftValue(mode, field, $(this).val());
    });
  });
})();

function findReportById(type, id) {
  const collection = type === 'product'
      ? (state.data.reports.products || [])
      : (state.data.reports.users || []);
  return collection.find((item) => String(item.id) === String(id));
}
