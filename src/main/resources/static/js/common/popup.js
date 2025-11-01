(() => {
    const state = {
        modals: new Map(),
        active: null
    };

    function register(id, element) {
        if (!id || !element) return;
        state.modals.set(id, element);
    }

    function lockScroll() {
        if (state.active) {
            document.documentElement.classList.add('popup-open');
            document.body.classList.add('popup-open');
        }
    }

    function unlockScroll() {
        document.documentElement.classList.remove('popup-open');
        document.body.classList.remove('popup-open');
    }

    function open(id) {
        const modal = state.modals.get(id);
        if (!modal) return;

        close(state.active);

        modal.classList.add('is-open');
        modal.setAttribute('aria-hidden', 'false');
        state.active = id;
        lockScroll();
    }

    function close(id) {
        const target = id ? state.modals.get(id) : (state.active ? state.modals.get(state.active) : null);
        if (!target) return;

        target.classList.remove('is-open');
        target.setAttribute('aria-hidden', 'true');
        if (!id || state.active === id) {
            state.active = null;
        }
        if (!state.active) {
            unlockScroll();
        }
    }

    function handleBackdropClick(event) {
        if (event.target.dataset.popupClose !== undefined) {
            const modal = event.target.closest('[data-popup]');
            if (!modal) return;
            const id = modal.dataset.popup;
            close(id);
        }
    }

    function bindTriggers(root = document) {
        root.querySelectorAll('[data-popup-trigger]').forEach(btn => {
            btn.addEventListener('click', () => {
                const targetId = btn.dataset.popupTrigger;
                open(targetId);
            });
        });
    }

    function init(root = document) {
        root.querySelectorAll('[data-popup]').forEach(modal => {
            const id = modal.dataset.popup;
            register(id, modal);
            modal.setAttribute('aria-hidden', 'true');
            modal.addEventListener('click', handleBackdropClick);
            modal.querySelectorAll('[data-popup-close]').forEach(btn => {
                btn.addEventListener('click', () => close(id));
            });
        });
        bindTriggers(root);
    }

    const Popup = {
        init,
        open,
        close,
        register
    };

    window.Popup = window.Popup || Popup;
    document.addEventListener('DOMContentLoaded', () => Popup.init());
})();
