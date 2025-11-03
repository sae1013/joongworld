$(function () {
    $('#logoutBtn').on('click', function () {
        window.Popup.show({
            title: '로그아웃',
            message:'로그아웃 하시겠어요?',
            actions: [
                { label: '확인', variant: 'primary', handler: function(){
                    requestLogout()
                    window.location.href = `/`
                    } }
            ]
        });

    });

    /**
     * 로그아웃 요청
     * @returns {Promise<void>}
     */
    async function requestLogout(){
        window.apiService.post('/api/auth/logout')
    }
    // 향후 내 글 목록 데이터 연동 시 이 영역에서 처리 예정
});
