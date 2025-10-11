// 카카오 로그인 처리 함수
function handleKakaoLogin() {
    console.log('카카오 로그인 클릭');
    // 실제로는 카카오 SDK를 이용한 로그인 로직이 들어갑니다
    alert('카카오 로그인 페이지로 이동합니다');
    location.href = '/oauth2/authorization/kakao';

}

// 네이버 로그인 처리 함수
function handleNaverLogin() {
    console.log('네이버 로그인 클릭');
    // 실제로는 네이버 SDK를 이용한 로그인 로직이 들어갑니다
    alert('네이버 로그인 페이지로 이동합니다');
    location.href = '/oauth2/authorization/naver';

}

// 이메일 로그인 처리 함수 (회원가입 페이지로 이동)
function handleEmailLogin() {
    console.log('이메일 로그인 클릭');
    // React의 navigate('/sign-up')와 동일한 기능
    window.location.href = '/sign-up';
    // 또는 테스트용으로: alert('회원가입 페이지로 이동합니다');
}

// 비즈니스 로그인 처리 함수
function handleBusinessLogin() {
    console.log('비즈니스 로그인 클릭');
    alert('비즈니스 로그인 페이지로 이동합니다');
}