// 썸네일 이미지 클릭시 변경 & 게시판에 툴바 노출 삭제

$(function () {
    const $main = $('#mainImage');
    const FILE_PREFIX = '/files/';

    function resolveImageUrl(path) {
        if (!path) return '';
        // https 주소는 그대로 사용
        if (/^https?:\/\//i.test(path)) {
            return path;
        }
        // 그외 로컬 이미지 경로
        return FILE_PREFIX + path.replace(/^\/+/, '');
    }

    if ($main.length) {
        const $thumbs = $('.thumbs .thumb');

        $thumbs.on('click', function () {
            const $thumb = $(this);
            $thumbs.removeClass('active');
            $thumb.addClass('active');

            const direct = $thumb.data('src');
            const bg = $thumb.css('background-image');
            let url = resolveImageUrl(direct);

            if (url) {
                $main.css('background-image', `url('${url}')`);
            } else if (bg && bg !== 'none') {
                $main.css('background-image', bg);
            }
        });
    }
});
