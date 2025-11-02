(() => {
    'use strict';

    const state = {
        files: [],
        maxFiles: 10,
        isSubmitting: false,
        editor: null
    };

    const dom = {};

    function ready(handler) {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', handler);
        } else {
            handler();
        }
    }

    function showPopup(options) {
        const popup = window.Popup;
        if (popup && typeof popup.show === 'function') {
            if (typeof options === 'string') {
                popup.show({
                    title: '알림',
                    message: options,
                    actions: [{label: '확인', variant: 'primary'}]
                });
            } else {
                popup.show(options);
            }
        } else {
            const message = typeof options === 'string'
                ? options
                : options?.message || '알림';
            window.alert(message);
        }
    }

    function formatPrice(value) {
        if (!value) return '';
        const digits = String(value).replace(/[^\d]/g, '');
        if (!digits) return '';
        return '₩ ' + digits.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    function parsePrice(value) {
        if (!value) return 0;
        const digits = String(value).replace(/[^\d]/g, '');
        if (!digits) return 0;
        const parsed = parseInt(digits, 10);
        return Number.isNaN(parsed) ? 0 : parsed;
    }

    function syncPriceInput() {
        if (!dom.price) return;
        const raw = dom.price.value;
        const digits = String(raw).replace(/[^\d]/g, '');
        const formatted = formatPrice(digits);
        dom.price.value = formatted;
    }

    function renderThumbnails() {
        if (!dom.uploader || !dom.pickLabel) return;

        dom.uploader.querySelectorAll('.thumb').forEach((node) => {
            node.remove();
        });

        state.files.forEach((item, index) => {
            const thumb = document.createElement('div');
            thumb.className = 'thumb';
            const img = document.createElement('img');
            img.src = item.previewUrl;
            img.alt = `상품 이미지 ${index + 1}`;
            const button = document.createElement('button');
            button.type = 'button';
            button.dataset.action = 'remove';
            button.dataset.idx = String(index);
            button.textContent = '삭제';

            thumb.appendChild(img);
            thumb.appendChild(button);
            dom.uploader.insertBefore(thumb, dom.pickLabel);
        });

        dom.uploader.classList.remove('dragover');

        if (dom.imgCount) {
            dom.imgCount.textContent = String(state.files.length);
        }

        dom.pickLabel.style.display = state.files.length >= state.maxFiles ? 'none' : 'flex';
    }

    function addFiles(fileList) {
        if (!fileList || !fileList.length) return;
        const remain = state.maxFiles - state.files.length;
        if (remain <= 0) {
            showPopup('이미지는 최대 10장까지 등록할 수 있습니다.');
            return;
        }

        const candidates = Array.from(fileList)
            .filter((file) => file && file.type && file.type.startsWith('image/'));

        if (!candidates.length) return;

        candidates.slice(0, remain).forEach((file) => {
            const previewUrl = URL.createObjectURL(file);
            state.files.push({file, previewUrl});
        });

        renderThumbnails();
    }

    function removeFile(index) {
        if (index < 0 || index >= state.files.length) return;
        const [removed] = state.files.splice(index, 1);
        if (removed && removed.previewUrl) {
            URL.revokeObjectURL(removed.previewUrl);
        }
        renderThumbnails();
    }

    function handleThumbClick(event) {
        const target = event.target;
        if (!(target instanceof HTMLElement)) return;
        if (target.dataset.action === 'remove') {
            const idx = Number(target.dataset.idx);
            if (!Number.isNaN(idx)) {
                removeFile(idx);
            }
        }
    }

    // function handleDragOver(event) {
    //     event.preventDefault();
    //     if (dom.uploader) {
    //         dom.uploader.classList.add('dragover');
    //     }
    // }
    //
    // function handleDragLeave(event) {
    //     if (!dom.uploader) return;
    //     const related = event.relatedTarget;
    //     if (!dom.uploader.contains(related)) {
    //         dom.uploader.classList.remove('dragover');
    //     }
    // }
    //
    // function handleDrop(event) {
    //     event.preventDefault();
    //     if (dom.uploader) {
    //         dom.uploader.classList.remove('dragover');
    //     }
    //     addFiles(event.dataTransfer?.files);
    // }

    function syncShippingUI() {
        if (!dom.shipEnabled || !dom.shipBox || !dom.shipCostRow) return;
        const enabled = dom.shipEnabled.checked;
        dom.shipBox.style.display = enabled ? 'block' : 'none';

        const selectedCost = document.querySelector('input[name="shipcost"]:checked');
        const requiresCost = enabled && selectedCost && selectedCost.value === 'excluded';
        dom.shipCostRow.style.display = requiresCost ? 'grid' : 'none';
        if (!requiresCost && dom.shipCost) {
            dom.shipCost.value = '';
        }
    }

    function resolveCategoryId() {
        return dom.category1 ? dom.category1.value : '';
    }

    function getDescription() {
        return $('.ck-content')[0].innerHTML
    }

    function buildSubmission() {
        const title = dom.title ? dom.title.value.trim() : '';
        if (!title) {
            return {ok: false, message: '상품명을 입력해 주세요.'};
        }

        const category1Id = resolveCategoryId();
        if (!category1Id) {
            return {ok: false, message: '카테고리를 선택해 주세요.'};
        }

        const priceValue = parsePrice(dom.price ? dom.price.value : '');
        if (priceValue <= 0) {
            return {ok: false, message: '판매 가격을 입력해 주세요.'};
        }

        const region = dom.region ? dom.region.value.trim() : '';
        if (!region) {
            return {ok: false, message: '거래 지역을 입력해 주세요.'};
        }

        const description = getDescription();
        if (!description) {
            return {ok: false, message: '상품 설명을 입력해 주세요.'};
        }

        const condition = document.querySelector('input[name="cond"]:checked')?.value || '중고';
        const shippingEnabled = dom.shipEnabled ? dom.shipEnabled.checked : false;
        const shipCostMode = document.querySelector('input[name="shipcost"]:checked')?.value || 'included';
        const safePay = dom.safePay ? dom.safePay.checked : false;

        let shippingCostValue = 0;
        if (shippingEnabled && shipCostMode === 'excluded') {
            const costInput = dom.shipCost ? parsePrice(dom.shipCost.value) : 0;
            if (costInput <= 0) {
                return {ok: false, message: '배송비를 숫자로 입력해 주세요.'};
            }
            shippingCostValue = costInput;
        }

        const meetup = dom.meetup ? dom.meetup.checked : false;

        const formData = new FormData();
        formData.append('title', title);
        formData.append('price', parseInt(priceValue, 10));
        formData.append('region', region);
        formData.append('condition_status', condition);
        formData.append('description', description);
        formData.append('safe_pay', safePay);
        formData.append('shipping_available', shippingEnabled);
        formData.append('meetup_available', meetup);
        formData.append('shipping_cost', parseInt(shippingCostValue, 10));

        formData.append('categoryId', category1Id);

        state.files.forEach((item, index) => {
            const filename = item.file.name || `image-${index + 1}.jpg`;
            formData.append('images', item.file, filename);
        });

        formData.append('image_count', String(state.files.length));
        formData.append('thumbnail_index', state.files.length > 0 ? '0' : '-1');

        return {ok: true, formData};
    }

    function setSubmitting(isSubmitting) {
        state.isSubmitting = isSubmitting;
        if (!dom.submitBtn) return;
        if (isSubmitting) {
            if (!dom.submitBtn.dataset.originalText) {
                dom.submitBtn.dataset.originalText = dom.submitBtn.textContent || '등록하기';
            }
            dom.submitBtn.disabled = true;
            dom.submitBtn.textContent = '등록 중...';
        } else {
            dom.submitBtn.disabled = false;
            const original = dom.submitBtn.dataset.originalText || '등록하기';
            dom.submitBtn.textContent = original;
        }
    }

    async function submitForm(formData) {
        const client = window.apiService || window.axios;
        if (!client) {
            showPopup('요청 클라이언트를 초기화할 수 없습니다.');
            return;
        }

        try {
            setSubmitting(true);
            const response = await client.post('/api/products', formData, {
                headers: {'Content-Type': 'multipart/form-data'}
            });
            const result = response && Object.prototype.hasOwnProperty.call(response, 'data')
                ? response.data
                : response;
            const productId = result?.id || result?.productId;
            if (productId) {
                window.location.href = `/product/${productId}`;
                return;
            }
            showPopup({
                title: '등록 완료',
                message: '상품이 등록되었습니다.',
                actions: [
                    {
                        label: '상품 목록으로',
                        variant: 'primary',
                        handler: () => window.location.replace('/search')
                    }
                ]
            });
        } catch (error) {
            const message = error?.message || '상품 등록 중 오류가 발생했습니다. 다시 시도해 주세요.';
            showPopup({
                title: '등록 실패',
                message,
                actions: [{label: '확인', variant: 'primary'}]
            });
        } finally {
            setSubmitting(false);
        }
    }

    function initCategory() {
        if (!dom.cat1 || !dom.cat2 || !dom.cat3) return;

        const dynamicMap = window.__PRODUCT_CATEGORY_MAP__;
        if (dynamicMap && typeof dynamicMap === 'object') {
            dom.cat1.addEventListener('change', () => {
                const first = dom.cat1.value;
                // dom.cat2.innerHTML = '<option value="">중분류</option>';
                // dom.cat3.innerHTML = '<option value="">소분류</option>';
                const middle = Array.isArray(dynamicMap[first]) ? dynamicMap[first] : [];
                middle.forEach((entry) => {
                    const option = document.createElement('option');
                    const value = entry?.value ?? entry?.name ?? '';
                    option.value = value;
                    if (entry?.id) {
                        option.dataset.id = entry.id;
                    }
                    option.textContent = entry?.label ?? entry?.name ?? value;
                    dom.cat2.appendChild(option);
                });
            });

            dom.cat2.addEventListener('change', () => {
                const first = dom.cat1.value;
                const second = dom.cat2.value;
                dom.cat3.innerHTML = '<option value="">소분류</option>';
                const middle = Array.isArray(dynamicMap[first]) ? dynamicMap[first] : [];
                const leafSource = middle.find((entry) => {
                    const entryValue = entry?.value ?? entry?.name ?? '';
                    return entryValue === second;
                });
                const leafList = leafSource && Array.isArray(leafSource.children) ? leafSource.children : [];
                leafList.forEach((entry) => {
                    const option = document.createElement('option');
                    const value = entry?.value ?? entry?.name ?? '';
                    option.value = value;
                    if (entry?.id) {
                        option.dataset.id = entry.id;
                    }
                    option.textContent = entry?.label ?? entry?.name ?? value;
                    dom.cat3.appendChild(option);
                });
            });
            return;
        }

        const fallbackMap = {
            '디지털/가전': ['모바일/태블릿', '노트북', '카메라/드론', '게임/콘솔'],
            '의류/잡화': ['남성의류', '여성의류', '패션잡화'],
            '도서': ['소설', '에세이', '전공/참고서'],
            '취미': ['피규어', '프라모델', '음반/악기'],
            '육아/유아동': ['유모차/카시트', '장난감', '의류'],
            '모바일/태블릿': ['스마트폰', '태블릿', '웨어러블']
        };

        dom.cat1.addEventListener('change', () => {
            dom.cat2.innerHTML = '<option value="">중분류</option>';
            dom.cat3.innerHTML = '<option value="">소분류</option>';
            const arr = fallbackMap[dom.cat1.value] || [];
            arr.forEach((value) => {
                const option = document.createElement('option');
                option.value = value;
                option.textContent = value;
                dom.cat2.appendChild(option);
            });
        });

        dom.cat2.addEventListener('change', () => {
            dom.cat3.innerHTML = '<option value="">소분류</option>';
            if (!dom.cat2.value) return;
            ['A', 'B', 'C'].forEach((suffix) => {
                const option = document.createElement('option');
                option.value = `${dom.cat2.value}-${suffix}`;
                option.textContent = `${dom.cat2.value}-${suffix}`;
                dom.cat3.appendChild(option);
            });
        });
    }

    function handleSubmit(event) {
        event.preventDefault();
        if (state.isSubmitting) return;
        const {ok, message, formData} = buildSubmission();
        if (!ok) {
            if (message) {
                showPopup({
                    title: '확인 필요',
                    message,
                    actions: [{label: '확인', variant: 'primary'}]
                });
            }
            return;
        }
        console.log(Object.fromEntries(formData.entries()));
        return
        submitForm(formData);
    }

    function cacheDom() {
        // dom.wrap = document.querySelector('.wrap');
        // if (!dom.wrap) return false;
        dom.picker = document.getElementById('pick');
        dom.pickLabel = document.getElementById('pickLabel');
        dom.uploader = document.getElementById('uploader');
        dom.imgCount = document.getElementById('imgCount');
        dom.price = document.getElementById('price');
        dom.shipEnabled = document.getElementById('shipEnabled');
        dom.shipBox = document.getElementById('shipBox');
        dom.shipCostRow = document.getElementById('shipCostRow');
        dom.shipCost = document.getElementById('shipCost');
        dom.meetup = document.getElementById('meetup');
        dom.submitBtn = document.getElementById('submitBtn');
        dom.category1 = document.getElementById('category1');
        // dom.cat2 = document.getElementById('cat2');
        // dom.cat3 = document.getElementById('cat3');
        dom.title = document.getElementById('title');
        dom.editorTarget = document.getElementById('editor');
        dom.region = document.getElementById('region');
        dom.safePay = document.getElementById('safePay');
        return true;
    }

    function attachEditorWatcher() {
        if (!dom.editorTarget) return;
        dom.editorTarget.addEventListener('editor:ready', (event) => {
            state.editor = event.detail?.editor || null;
        }, {once: true});
    }

    function bindEvents() {
        if (dom.picker) {
            dom.picker.addEventListener('change', (event) => {
                addFiles(event.target.files);
                event.target.value = '';
            });
        }

        if (dom.uploader) {
            dom.uploader.addEventListener('click', handleThumbClick);
            // TODO: 드래그기능 비활성화 (드래그로 인한 중복호출 버그)
            // dom.uploader.addEventListener('dragover', handleDragOver);
            // dom.uploader.addEventListener('dragleave', handleDragLeave);
            // dom.uploader.addEventListener('drop', handleDrop);
        }
        if (dom.price) {
            dom.price.addEventListener('input', syncPriceInput);
            syncPriceInput();
        }
        if (dom.shipEnabled) {
            dom.shipEnabled.addEventListener('change', syncShippingUI);
        }
        document.querySelectorAll('input[name="shipcost"]').forEach((radio) => {
            radio.addEventListener('change', syncShippingUI);
        });
        syncShippingUI();
        if (dom.submitBtn) {
            dom.submitBtn.addEventListener('click', handleSubmit);
        }
    }

    ready(() => {
        if (!cacheDom()) return;
        attachEditorWatcher();
        bindEvents();
        renderThumbnails();
        // initCategory();
    });
})();
