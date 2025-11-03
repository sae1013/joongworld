(() => {
    const STORAGE_KEY = 'jw-theme';
    const root = document.documentElement;
    const toggleSelector = '#themeToggle';

    function applyTheme(theme) {
        root.setAttribute('data-theme', theme);
        const toggle = document.querySelector(toggleSelector);
        if (toggle) {
            toggle.dataset.theme = theme;
            toggle.setAttribute('aria-label', theme === 'dark' ? '라이트 모드로 전환' : '다크 모드로 전환');
        }
    }

    function resolveInitialTheme() {
        try {
            const stored = localStorage.getItem(STORAGE_KEY);
            if (stored === 'light' || stored === 'dark') {
                return stored;
            }
            if (window.matchMedia) {
                return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
            }
        } catch (error) {
        }
        return 'light';
    }

    let currentTheme = root.getAttribute('data-theme') || resolveInitialTheme();
    applyTheme(currentTheme);

    document.addEventListener('DOMContentLoaded', () => {
        const toggle = document.querySelector(toggleSelector);
        if (toggle) {
            toggle.addEventListener('click', () => {
                currentTheme = root.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
                try {
                    localStorage.setItem(STORAGE_KEY, currentTheme);
                } catch (error) {
                    /* ignore */
                }
                applyTheme(currentTheme);
            });
        }

        const myPage = document.querySelector('.my-page');
        if (myPage) {
            myPage.addEventListener('click', () => {
                window.location.href = '/my';
            });
        }
    });

    if (!localStorage.getItem(STORAGE_KEY) && window.matchMedia) {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (event) => {
            const preferred = event.matches ? 'dark' : 'light';
            if (!localStorage.getItem(STORAGE_KEY)) {
                applyTheme(preferred);
            }
        });
    }
})();
