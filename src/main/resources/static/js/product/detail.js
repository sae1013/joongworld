// 상품 상세 페이지 전용 스크립트

$(function () {
    const FILE_PREFIX = '/files/';
    const $page = $('main.page');
    const productId = $page.data('productId');
    const $mainImage = $('#mainImage');

    function resolveImageUrl(path) {
        if (!path) return '';
        if (/^https?:\/\//i.test(path)) {
            return path;
        }
        if (path.startsWith(FILE_PREFIX) || path.startsWith('/')) {
            return path;
        }
        return FILE_PREFIX + path.replace(/^\/+/, '');
    }

    function bindThumbnailClick() {
        if (!$mainImage.length) return;
        const $thumbnails = $('.thumbs .thumb');
        if (!$thumbnails.length) return;

        $thumbnails.on('click', function () {
            const $thumbnail = $(this);
            $thumbnails.removeClass('active');
            $thumbnail.addClass('active');

            const directPath = $thumbnail.data('src');
            const backgroundStyle = $thumbnail.css('background-image');
            const resolvedUrl = resolveImageUrl(directPath);

            if (resolvedUrl) {
                $mainImage.css('background-image', `url('${resolvedUrl}')`);
            } else if (backgroundStyle && backgroundStyle !== 'none') {
                $mainImage.css('background-image', backgroundStyle);
            }
        });
    }

    async function requestProductDelete(productId) {
        try{
            await window.apiService.delete(`/api/products/${productId}`);

            window.Popup.show({
                title: '상품이 삭제되었습니다.',
                message:'상품을 정상적으로 삭제했어요.',
                actions: [
                    { label: '확인', variant: 'primary', handler: function(){
                        // category로 이동하기
                            const categoryId = $('#productDeleteBtn').data('category');
                            window.location.href = `/search?category=${categoryId}`
                        } }
                ]
            });

        }catch(error){
            window.Popup.show({
                title: '상품이 삭제되지 않았어요',
                message:error.message,
                actions: [
                    { label: '확인', variant: 'primary' }
                ]
            });
        }

    }

    function bindDeleteButtons() {
        if (!productId) return;

        $('#productDeleteBtn').on('click', async function () {
            window.Popup.show({
                title: '상품을 삭제하면 되돌릴 수 없어요.',
                message:'상품을 삭제하시겠습니까?',
                actions: [
                    { label: '확인', variant: 'primary', handler: function() {
                        requestProductDelete(productId)
                        } }
                ]
            });

        });
    }

    function bindEditButton() {
        const $editBtn = $('#productEditBtn');
        if (!productId || !$editBtn.length) {
            return;
        }

        $editBtn.on('click', function () {
            window.Popup.show({
                title: '상품을 수정하시겠습니까?',
                message: '확인을 누르면 수정페이지로 이동합니다.',
                actions: [{
                    label: '수정하기',
                    variant: 'primary',
                    handler: function () {
                        window.location.href = `/product/${productId}/edit`;
                    }
                }]
            });
        });
    }

    bindThumbnailClick();
    bindDeleteButtons();
    bindEditButton();
});
