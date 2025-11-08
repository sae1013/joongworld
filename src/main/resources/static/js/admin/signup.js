(function () {
    function fallbackPopup(options) {
        const message = typeof options === 'string'
            ? options
            : options?.message || options?.description || '알림';
        alert(message);
    }

    function showPopup(options) {
        if (window.Popup && typeof window.Popup.show === 'function') {
            window.Popup.show(options);
        } else {
            fallbackPopup(options);
        }
    }

    function formatResident(value) {
        const digits = (value || '').replace(/\D/g, '').slice(0, 13);
        if (digits.length <= 6) {
            return digits;
        }
        return digits.slice(0, 6) + '-' + digits.slice(6);
    }

    function formatPhone(value) {
        const digits = (value || '').replace(/\D/g, '').slice(0, 11);
        if (digits.length < 4) {
            return digits;
        }
        if (digits.length < 7) {
            return digits.slice(0, 3) + '-' + digits.slice(3);
        }
        if (digits.length === 10) {
            return digits.slice(0, 3) + '-' + digits.slice(3, 6) + '-' + digits.slice(
                6);
        }
        return digits.slice(0, 3) + '-' + digits.slice(3, 7) + '-' + digits.slice(
            7);
    }

    $(function () {
        const $form = $('#adminSignupForm');
        const $submit = $('#submitBtn');
        const $cancel = $('#cancelBtn');

        const fields = {
            email: $('#email'),
            password: $('#password'),
            passwordConfirm: $('#passwordConfirm'),
            name: $('#name'),
            nickname: $('#nickname'),
            // resident: $('#residentNumber'),
            phone: $('#phoneNumber'),
            department: $('#department'),
            position: $('#position')
        };

        const errors = {
            email: $('#emailErr'),
            password: $('#passwordErr'),
            passwordConfirm: $('#passwordConfirmErr'),
            name: $('#nameErr'),
            nickname: $('#nicknameErr'),
            // resident: $('#residentErr'),
            phone: $('#phoneErr'),
            position: $('#positionErr')
        };

        const emailRx = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const nickRx = /^[가-힣a-zA-Z0-9]{2,15}$/;
        let isSubmitting = false;

        function setError(key, on) {
            const $el = errors[key];
            if (!$el || !$el.length) {
                return;
            }
            $el.toggleClass('is-visible', !!on);
        }

        function sanitizeDigits(value) {
            return (value || '').replace(/\D/g, '');
        }

        function pwScore(v) {
            if (!v) {
                return 0;
            }
            let score = 0;
            if (v.length >= 8) {
                score++;
            }
            if (/[A-Z]/.test(v) || /[a-z]/.test(v)) {
                score++;
            }
            if (/\d/.test(v)) {
                score++;
            }
            if (/[^\w\s]/.test(v)) {
                score++;
            }
            if (v.length >= 12 && score >= 3) {
                score++;
            }
            return Math.min(4, Math.max(1, score));
        }

        function applySubmitState(valid) {
            if (isSubmitting) {
                $submit.prop('disabled', true);
            } else {
                $submit.prop('disabled', !valid);
            }
        }

        // 검증함수.
        function validate(showErrors = false) {
            const rawEmail = fields.email.val() || '';
            const email = rawEmail.trim();
            const okEmail = emailRx.test(email);
            setError('email', (showErrors || rawEmail !== '') && !okEmail);

            const pw = fields.password.val() || '';
            const okPw = pw.length >= 8 && pwScore(pw) >= 2;
            setError('password', (showErrors || pw !== '') && !okPw);

            const pw2 = fields.passwordConfirm.val() || '';
            const okPw2 = pw2 !== '' && pw2 === pw;
            setError('passwordConfirm', (showErrors || pw2 !== '') && !okPw2);

            const rawName = fields.name.val() || '';
            const okName = rawName.trim().length > 0;
            setError('name', (showErrors || rawName !== '') && !okName);

            const rawNick = fields.nickname.val() || '';
            const okNick = nickRx.test(rawNick.trim());
            setError('nickname', (showErrors || rawNick !== '') && !okNick);

            // const rawResident = fields.resident.val() || '';
            // const digitsResident = sanitizeDigits(rawResident);
            // const okResident = digitsResident.length === 13;
            // setError('resident', (showErrors || rawResident !== '') && !okResident);

            const rawPhone = fields.phone.val() || '';
            const digitsPhone = sanitizeDigits(rawPhone);
            const okPhone = digitsPhone.length >= 10 && digitsPhone.length <= 11
                && digitsPhone.startsWith('01');
            setError('phone', (showErrors || rawPhone !== '') && !okPhone);

            const positionVal = fields.position.val() || '';
            const okPosition = positionVal === 'SUPER_ADMIN' || positionVal
                === 'MANAGER';
            setError('position', (showErrors || positionVal !== '') && !okPosition);

            const allOk = okEmail && okPw && okPw2 && okName && okNick
                && okPhone && okPosition;
            applySubmitState(allOk);
            return allOk;
        }

        // fields.resident.on('input', function () {
        //     const prevValue = this.value;
        //     const caret = typeof this.selectionStart === 'number'
        //         ? this.selectionStart : prevValue.length;
        //     const formatted = formatResident(prevValue);
        //     this.value = formatted;
        //
        //     if (typeof this.setSelectionRange === 'function') {
        //         const digitsBefore = prevValue.slice(0, caret).replace(/\D/g,
        //             '').length;
        //         let nextPos = formatted.length;
        //         let seen = 0;
        //         for (let i = 0; i < formatted.length; i++) {
        //             if (/\d/.test(formatted[i])) {
        //                 seen++;
        //             }
        //             if (seen >= digitsBefore) {
        //                 nextPos = i + 1;
        //                 break;
        //             }
        //         }
        //         this.setSelectionRange(nextPos, nextPos);
        //     }
        //
        //     validate(false);
        // });

        fields.phone.on('input', function () {
            const prevValue = this.value;
            const caret = typeof this.selectionStart === 'number'
                ? this.selectionStart : prevValue.length;
            const formatted = formatPhone(prevValue);
            this.value = formatted;

            if (typeof this.setSelectionRange === 'function') {
                const digitsBefore = prevValue.slice(0, caret).replace(/\D/g,
                    '').length;
                let nextPos = formatted.length;
                let seen = 0;
                for (let i = 0; i < formatted.length; i++) {
                    if (/\d/.test(formatted[i])) {
                        seen++;
                    }
                    if (seen >= digitsBefore) {
                        nextPos = i + 1;
                        break;
                    }
                }
                this.setSelectionRange(nextPos, nextPos);
            }

            validate(false);
        });

        $form.on('input change blur', () => validate(false));

        $cancel.on('click', function () {
            if (document.referrer) {
                history.back();
            } else {
                window.location.href = '/';
            }
        });

        function setLoading(loading) {
            isSubmitting = loading;
            if (loading) {
                $submit.text('등록 중...');
                $submit.prop('disabled', true);
            } else {
                $submit.text('관리자로 등록');
                validate(false);
            }
        }

        $form.on('submit', async function (event) {
            event.preventDefault();
            const valid = validate(true);
            if (!valid) {
                showPopup('입력한 정보를 다시 확인해 주세요.');
                return;
            }

            if (!window.apiService) {
                showPopup('요청 클라이언트를 초기화하지 못했습니다.');
                return;
            }

            const payload = {
                email: (fields.email.val() || '').trim(),
                password: fields.password.val() || '',
                name: (fields.name.val() || '').trim(),
                nickname: (fields.nickname.val() || '').trim(),
                agree: true,
                isAdmin: true,
                phoneNum: sanitizeDigits(fields.phone.val()),
                position: fields.position.val() || null,
                status: 'ACTIVE'
            };

            try {
                setLoading(true);
                console.log('[admin signup payload]', payload);
                await window.apiService.post('/api/admin/signup', payload);
                showPopup({
                    title: '등록 완료',
                    message: '새 관리자 계정이 생성되었습니다.',
                    confirmText: '관리자 로그인으로 이동',
                    onConfirm: () => {
                        window.location.href = '/auth/login';
                    }
                });
            } catch (error) {
                const message = error?.message || '관리자 등록 중 오류가 발생했습니다.';
                showPopup({title: '오류', message});
            } finally {
                setLoading(false);
            }
        });

        validate(false);
    });
})();
