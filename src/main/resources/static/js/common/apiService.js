(() => {
    if (!window.axios) {
        console.error('[http] axios is not loaded. Include axios before this script.');
        return;
    }

    const api = axios.create({
        baseURL: '/',
        timeout: 10000,
        headers: {
            'Content-Type': 'application/json'
        }
    });

    api.interceptors.response.use(
        response => response.data,
        error => {
            const status = error?.response?.status;
            const message = error?.response?.data?.message || error?.message || '요청 처리 중 오류가 발생했습니다.';
            return Promise.reject({ status, message });
        }
    );

    window.apiService = window.apiService || api;
})();
