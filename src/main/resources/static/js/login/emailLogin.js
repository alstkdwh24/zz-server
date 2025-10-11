// 폼 요소 가져오기
const form = document.getElementById('registerForm');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const confirmInput = document.getElementById('confirm');
const errorMessage = document.getElementById('errorMessage');
const loginLink = document.getElementById('loginLink');

// 폼 제출 이벤트
form.addEventListener('submit', function(e) {
    e.preventDefault();

    const email = emailInput.value;
    const password = passwordInput.value;
    const confirm = confirmInput.value;

    // 비밀번호 확인
    if (password !== confirm) {
        errorMessage.classList.add('show');
        confirmInput.focus();
        return;
    }

    // 에러 메시지 숨기기
    errorMessage.classList.remove('show');

    // 회원가입 요청 (콘솔에 출력)
    console.log('회원가입 요청:', { email, password });

    // 실제로는 서버로 데이터 전송
    alert('회원가입이 완료되었습니다!');

    // 폼 초기화
    form.reset();
});

// 입력 시 에러 메시지 숨기기
confirmInput.addEventListener('input', function() {
    errorMessage.classList.remove('show');
});

// 로그인 링크 클릭
loginLink.addEventListener('click', function() {
    // 실제로는 로그인 페이지로 이동
    window.location.href = '/login/emailLogin';
});