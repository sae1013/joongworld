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

    function initCommentsModule() {
        const $commentSection = $('[data-comment-section]');
        if (!$commentSection.length || !productId || !window.apiService) {
            return;
        }

        const $commentList = $('#commentList');
        const $commentCount = $('#commentCount');
        const $commentContent = $('#commentContent');
        const $submitBtn = $('#commentSubmitBtn');
        let inlineReplyForm = null;
        let commentsCache = [];
        const commentMap = new Map();
        const INLINE_REPLY_TEMPLATE = `
            <div class="comment-inline-reply" data-inline-reply>
                <textarea class="form-control form-control-sm mb-2 mt-4" rows="2" placeholder="대댓글을 입력해 주세요."></textarea>
                <div class="d-flex gap-2 justify-content-end">
                    <button type="button" class="btn btn-sm btn-secondary" data-action="cancel-inline-reply">취소</button>
                    <button type="button" class="btn btn-sm btn-brand" data-action="submit-inline-reply">등록</button>
                </div>
            </div>
        `;

        function closeInlineReply() {
            if (inlineReplyForm) {
                inlineReplyForm.remove();
                inlineReplyForm = null;
            }
        }

        function openInlineReply(comment, $wrapper) {
            closeInlineReply();
            const $form = $(INLINE_REPLY_TEMPLATE);
            $form.attr('data-parent-id', comment.id);
            inlineReplyForm = $form;
            const $targetContainer = $wrapper.find('> .d-flex');
            if ($targetContainer.length) {
                $form.insertAfter($targetContainer);
            } else {
                $wrapper.append($form);
            }
            const $textarea = $form.find('textarea');
            $textarea.focus();
        }

        function updateCommentMap(list) {
            commentMap.clear();
            const traverse = (items) => {
                items.forEach((item) => {
                    commentMap.set(item.id, item);
                    if (item.replies && item.replies.length) {
                        traverse(item.replies);
                    }
                });
            };
            traverse(list);
        }

        function renderComments() {
            $commentList.empty();
            const totalCount = commentsCache.length
                ? commentsCache.reduce((acc, item) => acc + 1 + countChildren(item), 0)
                : 0;
            $commentCount.text(totalCount);
            if (!commentsCache.length) {
                $commentList.append('<div class="text-center text-secondary small py-4">등록된 댓글이 없습니다.</div>');
                return;
            }
            const fragment = $(document.createDocumentFragment());
            commentsCache.forEach((comment) => {
                fragment.append(buildCommentElement(comment, 0));
            });
            $commentList.append(fragment);
        }

        function countChildren(node) {
            if (!node.replies || !node.replies.length) {
                return 0;
            }
            return node.replies.reduce((acc, child) => acc + 1 + countChildren(child), 0);
        }

        function buildCommentElement(comment, depth) {
            const isDeleted = comment.deleted;
            const isOwner = Boolean(comment.owner);
            const content = isDeleted ? '삭제된 댓글입니다.' : comment.content;
            const actions = [];
            if (!isDeleted) {
                actions.push('<button type="button" class="btn-link" data-action="reply">답글</button>');
                actions.push(`<button type="button" class="btn-link" data-action="like">${comment.likedByMe ? '♥' : '♡'} ${comment.likeCount || 0}</button>`);
                if (isOwner) {
                    actions.push('<button type="button" class="btn-link" data-action="edit">수정</button>');
                    actions.push('<button type="button" class="btn-link" data-action="delete">삭제</button>');
                }
            }

            const $wrapper = $(`
                <div class="comment-item" data-comment-id="${comment.id || ''}" style="margin-left:${depth * 24}px">
                    <div class="d-flex gap-3">
                        <div class="avatar bg-secondary-subtle rounded-circle"></div>
                        <div class="flex-grow-1">
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <div>
                                    <strong class="me-2">${comment.authorNickname || '익명'}</strong>
                                    <span class="text-secondary small">${formatTimestamp(comment.createdAt)}</span>
                                </div>
                                <div class="comment-actions">
                                    ${actions.join('')}
                                </div>
                            </div>
                            ${depth > 0 ? buildParentIndicator(comment.parentId) : ''}
                            <div class="comment-content" data-comment-content>
                                <p class="mb-0 small">${escapeHtml(content)}</p>
                            </div>
                        </div>
                    </div>
                </div>
            `);

            if (comment.replies && comment.replies.length) {
                comment.replies.forEach((child) => {
                    $wrapper.append(buildCommentElement(child, depth + 1));
                });
            }
            return $wrapper;
        }

        function buildParentIndicator(parentId) {
            if (!parentId || !commentMap.has(parentId)) {
                return '';
            }
            const parent = commentMap.get(parentId);
            const nickname = parent?.authorNickname || '익명';
            return `
                <div class="comment-parent text-secondary small mb-1">
                    ↳ <span class="fw-semibold">${escapeHtml(nickname)}</span>님에게 답글
                </div>
            `;
        }

        function formatTimestamp(ts) {
            if (!ts) return '';
            const date = new Date(ts);
            if (Number.isNaN(date.getTime())) return '';
            return date.toLocaleString('ko-KR', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
        }

        function escapeHtml(text) {
            if (text == null) return '';
            return String(text)
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;');
        }

        async function fetchComments() {
            try {
                const data = await window.apiService.get(`/api/products/${productId}/comments`);
                commentsCache = Array.isArray(data) ? data : [];
                updateCommentMap(commentsCache);
                closeInlineReply();
                renderComments();
            } catch (error) {
                console.error('[comments] failed to fetch', error);
                $commentList.html('<div class="text-center text-danger small py-4">댓글을 불러오지 못했습니다.</div>');
            }
        }

        async function submitComment() {
            const content = ($commentContent.val() || '').trim();
            if (!content) {
                window.Popup?.show({
                    title: '안내',
                    message: '댓글 내용을 입력해 주세요.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
                return;
            }
            const payload = { content };
            try {
                $submitBtn.prop('disabled', true);
                await window.apiService.post(`/api/products/${productId}/comments`, payload);
                $commentContent.val('');
                await fetchComments();
            } catch (error) {
                console.error('[comments] failed to post', error);
                window.Popup?.show({
                    title: '댓글 등록 실패',
                    message: error?.message || '댓글을 등록할 수 없습니다.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
            } finally {
                $submitBtn.prop('disabled', false);
            }
        }

        async function submitInlineReply($form) {
            if (!$form || !$form.length) return;
            const parentId = Number($form.data('parentId'));
            const $textarea = $form.find('textarea');
            const content = ($textarea.val() || '').trim();
            if (!content) {
                window.Popup?.show({
                    title: '안내',
                    message: '대댓글 내용을 입력해 주세요.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
                return;
            }
            const $submit = $form.find('[data-action="submit-inline-reply"]');
            try {
                $submit.prop('disabled', true);
                await window.apiService.post(`/api/products/${productId}/comments`, {
                    content,
                    parentId
                });
                closeInlineReply();
                await fetchComments();
            } catch (error) {
                console.error('[comments] inline reply failed', error);
                window.Popup?.show({
                    title: '대댓글 등록 실패',
                    message: error?.message || '대댓글을 등록할 수 없습니다.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
            } finally {
                $submit.prop('disabled', false);
            }
        }

        async function toggleLike(commentId) {
            if (!commentId) return;
            try {
                await window.apiService.post(`/api/comments/${commentId}/likes`);
                await fetchComments();
            } catch (error) {
                console.error('[comments] like toggle failed', error);
            }
        }

        function findCommentById(id) {
            return commentMap.get(id) || null;
        }

        function switchToEditMode(commentId, $wrapper, comment) {
            const $contentArea = $wrapper.find('[data-comment-content]');
            if (!$contentArea.length || $contentArea.data('editing')) {
                return;
            }
            const originalHtml = $contentArea.html();
            $contentArea
                .data('editing', true)
                .data('original', originalHtml);

            const safeValue = escapeHtml(comment.content || '');
            const editorTemplate = `
                <div class="comment-edit-form">
                    <textarea class="form-control form-control-sm mb-2" rows="3">${safeValue}</textarea>
                    <div class="d-flex gap-2 justify-content-end">
                        <button type="button" class="btn btn-sm btn-secondary" data-action="cancel-edit">취소</button>
                        <button type="button" class="btn btn-sm btn-brand" data-action="confirm-edit">저장</button>
                    </div>
                </div>
            `;
            $contentArea.html(editorTemplate);
        }

        function cancelEditMode($wrapper) {
            const $contentArea = $wrapper.find('[data-comment-content]');
            if (!$contentArea.length || !$contentArea.data('editing')) {
                return;
            }
            const original = $contentArea.data('original');
            if (original !== undefined) {
                $contentArea.html(original);
            }
            $contentArea.removeData('editing').removeData('original');
        }

        async function confirmEdit(commentId, $wrapper) {
            const $contentArea = $wrapper.find('[data-comment-content]');
            const $textarea = $wrapper.find('textarea');
            if (!$textarea.length) {
                return;
            }
            const text = ($textarea.val() || '').trim();
            if (!text) {
                window.Popup?.show({
                    title: '안내',
                    message: '내용을 입력해 주세요.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
                return;
            }
            try {
                await window.apiService.put(`/api/comments/${commentId}`, { content: text });
                await fetchComments();
            } catch (error) {
                console.error('[comments] update failed', error);
                window.Popup?.show({
                    title: '댓글 수정 실패',
                    message: error?.message || '댓글을 수정할 수 없습니다.',
                    actions: [{ label: '확인', variant: 'primary' }]
                });
                cancelEditMode($wrapper);
            }
        }

        async function deleteComment(commentId) {
            const confirmHandler = async () => {
                try {
                    await window.apiService.delete(`/api/comments/${commentId}`);
                    await fetchComments();
                } catch (error) {
                    console.error('[comments] delete failed', error);
                    window.Popup?.show({
                        title: '댓글 삭제 실패',
                        message: error?.message || '댓글을 삭제할 수 없습니다.',
                        actions: [{ label: '확인', variant: 'primary' }]
                    });
                }
            };

            if (window.Popup?.show) {
                window.Popup.show({
                    title: '댓글 삭제',
                    message: '댓글을 삭제하시겠습니까?',
                    actions: [
                        { label: '삭제', variant: 'primary', handler: confirmHandler },
                        { label: '취소', variant: 'secondary' }
                    ]
                });
            } else if (window.confirm('댓글을 삭제하시겠습니까?')) {
                await confirmHandler();
            }
        }

        function handleListClick(event) {
            const $target = $(event.target);
            const action = $target.data('action');
            if (!action) return;
            const $item = $target.closest('.comment-item');
            const commentId = Number($item.data('commentId'));
            const comment = findCommentById(commentId);
            if (!comment) return;

            switch (action) {
                case 'reply':
                    openInlineReply(comment, $item);
                    break;
                case 'like':
                    toggleLike(commentId);
                    break;
                case 'edit':
                    switchToEditMode(commentId, $item, comment);
                    break;
                case 'delete':
                    deleteComment(commentId);
                    break;
                case 'cancel-edit':
                    cancelEditMode($item);
                    break;
                case 'confirm-edit':
                    confirmEdit(commentId, $item);
                    break;
                default:
                    break;
            }
        }

        $submitBtn.on('click', submitComment);
        $commentList.on('click', '[data-action]', function (event) {
            const $target = $(event.target);
            const action = $target.data('action');
            if (action === 'cancel-inline-reply') {
                closeInlineReply();
                return;
            }
            if (action === 'submit-inline-reply') {
                const $form = $target.closest('[data-inline-reply]');
                submitInlineReply($form);
                return;
            }
            handleListClick(event);
        });

        fetchComments();
    }

    bindThumbnailClick();
    bindDeleteButtons();
    bindEditButton();
    initCommentsModule();
});
