(() => {
    const state = {
        modals: new Map(),
        active: null,
        autoIncrement: 0,
        scrollLock: null
    };

    const VARIANT_CLASS = {
        primary: 'btn btn-primary',
        secondary: 'btn btn-outline-secondary',
        danger: 'btn btn-danger',
        ghost: 'btn btn-ghost'
    };

    function getScrollbarWidth() {
        return Math.max(0, window.innerWidth - document.documentElement.clientWidth);
    }

    function lockScroll() {}

    function unlockScroll() {}

    function attachListeners(modal, id) {
        if (!modal || modal.dataset.popupBound === 'true') {
            return;
        }
        modal.dataset.popupBound = 'true';
        modal.addEventListener('click', handleBackdropClick);
        bindCloseButtons(modal, id);
    }

    function bindCloseButtons(modal, id) {
        modal.querySelectorAll('[data-popup-close]').forEach(btn => {
            if (btn.dataset.popupBound === 'true') return;
            btn.dataset.popupBound = 'true';
            btn.addEventListener('click', () => close(id));
        });
    }

    function register(id, element, meta = {}) {
        if (!id || !element) return;

        element.dataset.popup = id;
        element.setAttribute('aria-hidden', element.classList.contains('is-open') ? 'false' : 'true');
        attachListeners(element, id);

        const existing = state.modals.get(id);
        state.modals.set(id, {
            element,
            dynamic: meta.dynamic ?? existing?.dynamic ?? false,
            onClose: meta.onClose ?? existing?.onClose ?? null
        });
    }

    function unregister(id) {
        const entry = state.modals.get(id);
        if (!entry) return;
        state.modals.delete(id);
    }

    function open(id) {
        const entry = state.modals.get(id);
        if (!entry) return;

        if (state.active && state.active !== id) {
            close(state.active);
        }

        entry.element.classList.add('is-open');
        entry.element.setAttribute('aria-hidden', 'false');
        state.active = id;
        lockScroll();
    }

    function close(id) {
        const targetId = id || state.active;
        if (!targetId) return;

        const entry = state.modals.get(targetId);
        if (!entry) return;

        const { element, dynamic, onClose } = entry;

        element.classList.remove('is-open');
        element.setAttribute('aria-hidden', 'true');

        if (typeof onClose === 'function') {
            try {
                onClose();
            } catch (err) {
                console.error('[popup] onClose handler error', err);
            }
        }

        if (state.active === targetId) {
            state.active = null;
        }

        if (dynamic) {
            unregister(targetId);
            window.requestAnimationFrame(() => {
                element.remove();
            });
        }

        if (!state.active) {
            unlockScroll();
        } else {
            lockScroll();
        }
    }

    function handleBackdropClick(event) {
        if (event.target.dataset.popupClose !== undefined) {
            const modal = event.currentTarget;
            if (!modal) return;
            const id = modal.dataset.popup;
            close(id);
        }
    }

    function bindTriggers(root = document) {
        root.querySelectorAll('[data-popup-trigger]').forEach(btn => {
            if (btn.dataset.popupTriggerBound === 'true') return;
            btn.dataset.popupTriggerBound = 'true';
            btn.addEventListener('click', () => {
                const targetId = btn.dataset.popupTrigger;
                open(targetId);
            });
        });
    }

    function buildModal(id, options) {
        const modal = document.createElement('div');
        modal.className = 'popup';
        modal.dataset.popup = id;
        modal.dataset.popupDynamic = 'true';
        modal.setAttribute('role', 'dialog');
        modal.setAttribute('aria-modal', 'true');

        const backdrop = document.createElement('div');
        backdrop.className = 'popup-backdrop';
        backdrop.dataset.popupClose = '';

        const dialog = document.createElement('div');
        dialog.className = 'popup-dialog';
        if (options.className) {
            dialog.classList.add(options.className);
        }
        dialog.setAttribute('role', 'document');

        const header = document.createElement('div');
        header.className = 'popup-header';

        const titleEl = document.createElement('h2');
        titleEl.className = 'popup-title';
        titleEl.id = `${id}-title`;

        const closeBtn = document.createElement('button');
        closeBtn.className = 'popup-close';
        closeBtn.type = 'button';
        closeBtn.setAttribute('aria-label', '닫기');
        closeBtn.dataset.popupClose = '';
        closeBtn.innerHTML = '&times;';

        header.append(titleEl, closeBtn);

        const body = document.createElement('div');
        body.className = 'popup-body';

        const actions = document.createElement('div');
        actions.className = 'popup-actions';

        dialog.append(header, body, actions);
        modal.append(backdrop, dialog);
        modal.dataset.popupTitleId = titleEl.id;

        updateModal(modal, options);

        return modal;
    }

    function updateModal(modal, options = {}) {
        const dialog = modal.querySelector('.popup-dialog');
        const header = modal.querySelector('.popup-header');
        const titleEl = modal.querySelector('.popup-title');
        const body = modal.querySelector('.popup-body');
        const actions = modal.querySelector('.popup-actions');

        if (options.className) {
            dialog.classList.add(options.className);
        }

        if (titleEl) {
            const title = options.title || '';
            titleEl.textContent = title;
            if (header) {
                header.style.display = title ? 'flex' : 'none';
            }
            const titleId = titleEl.id || `${modal.dataset.popup}-title`;
            titleEl.id = titleId;
            modal.setAttribute('aria-labelledby', title ? titleId : '');
        }

        if (body) {
            body.innerHTML = '';
            if (options.html) {
                body.innerHTML = options.html;
            } else {
                if (options.message) {
                    const p = document.createElement('p');
                    p.textContent = options.message;
                    body.appendChild(p);
                }
                if (options.description) {
                    const p2 = document.createElement('p');
                    p2.textContent = options.description;
                    body.appendChild(p2);
                }
            }
        }

        if (actions) {
            actions.innerHTML = '';
            const actionDefs = normalizeActions(options);
            actionDefs.forEach(action => {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.textContent = action.label || '확인';
                btn.className = action.className || VARIANT_CLASS[action.variant || 'primary'] || 'btn btn-primary';

                btn.addEventListener('click', (event) => {
                    if (typeof action.handler === 'function') {
                        action.handler({
                            close: () => close(modal.dataset.popup),
                            event
                        });
                    }
                    if (action.close !== false) {
                        close(modal.dataset.popup);
                    }
                });

                if (action.close) {
                    btn.dataset.popupClose = '';
                }

                actions.appendChild(btn);
            });

            actions.style.display = actionDefs.length ? 'flex' : 'none';
        }

        // refresh close buttons in case new ones were rendered
        bindCloseButtons(modal, modal.dataset.popup);
    }

    function normalizeActions(options = {}) {
        if (Array.isArray(options.actions) && options.actions.length) {
            return options.actions.map(action => ({
                close: action.close !== false,
                variant: action.variant || 'primary',
                label: action.label,
                handler: action.handler,
                className: action.className
            }));
        }

        const actions = [];

        if (options.cancelText) {
            actions.push({
                label: options.cancelText,
                variant: 'secondary',
                handler: options.onCancel,
                close: options.closeOnCancel !== false
            });
        }

        actions.push({
            label: options.confirmText || '확인',
            variant: 'primary',
            handler: options.onConfirm,
            close: options.closeOnConfirm !== false
        });

        return actions;
    }

    function show(options = {}) {
        if (typeof options === 'string') {
            options = { message: options };
        }

        const id = options.id || `popup-auto-${++state.autoIncrement}`;
        let entry = state.modals.get(id);

        if (!entry || !entry.dynamic) {
            const modal = buildModal(id, options);
            document.body.appendChild(modal);
            register(id, modal, {
                dynamic: options.keepAlive ? false : true,
                onClose: options.onClose
            });
            entry = state.modals.get(id);
        } else {
            updateModal(entry.element, options);
            entry.onClose = options.onClose || entry.onClose;
        }

        open(id);
        return { id, close: () => close(id) };
    }

    function init(root = document) {
        root.querySelectorAll('[data-popup]').forEach(modal => {
            const id = modal.dataset.popup;
            register(id, modal, {
                dynamic: modal.dataset.popupDynamic === 'true'
            });
        });
        bindTriggers(root);
    }

    const Popup = {
        init,
        open,
        close,
        register,
        show
    };

    window.Popup = Object.assign(window.Popup || {}, Popup);
    document.addEventListener('DOMContentLoaded', () => Popup.init());
})();
