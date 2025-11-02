// 썸네일 이미지 클릭시 변경 & 게시판에 툴바 노출 삭제

$(function () {
    const $main = $('#mainImage');

    if ($main.length) {
        const $thumbs = $('.thumbs .thumb');

        $thumbs.on('click', function () {
            const $thumb = $(this);
            $thumbs.removeClass('active');
            $thumb.addClass('active');

            const direct = $thumb.data('src');
            const bg = $thumb.css('background-image');
            let url = direct;

            if (url) {
                $main.css('background-image', `url('${url}')`);
            }
        });
    }

    $('.ck-sticky-panel__content').css('display', 'none');
});