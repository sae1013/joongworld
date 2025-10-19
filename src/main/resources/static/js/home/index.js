class HomePage {
    constructor(params = {}) {
        this.url = params.url ?? '/search';
        // this.scrollControlSelector = options.scrollControlSelector ?? '[data-controls]';
    }

    init() {
        this.bindCategoryNavigation();
        this.bindHorizontalScroll();
    }

    bindCategoryNavigation() {
        const targetUrl = this.url ?? '/search';
        $(document).on('click', "[data-category]", function () {
            const $btn = $(this);
            const categoryId = $btn.attr('data-category');
            if (!categoryId) {
                window.location.href = targetUrl;
                return;
            }

            window.location.href = `${targetUrl}?category=${categoryId}`;
        });
    }

    bindHorizontalScroll() {
        document.querySelectorAll(this.scrollControlSelector).forEach(ctrl => {
            const targetId = ctrl.getAttribute('data-controls');
            const row = document.getElementById(targetId);
            if (!row) {
                return;
            }

            const buttons = ctrl.querySelectorAll('button');
            if (buttons.length < 2) {
                return;
            }

            const step = () => Math.min(480, row.clientWidth * 0.9);
            buttons[0].addEventListener('click', () => {
                row.scrollBy({left: -step(), behavior: 'smooth'});
            });
            buttons[1].addEventListener('click', () => {
                row.scrollBy({left: step(), behavior: 'smooth'});
            });
        });
    }
}

window.HomePage = HomePage;
