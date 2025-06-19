<!--시험하는 코드 여기잘까랑 상관없음-->

document.addEventListener("DOMContentLoaded", function () {
    let kakaoRestApiKey = "";
    let RedirectUri = "http://localhost:9090/main";
    const NaverRedirectUri = "http://localhost:9090/naver-callback";
    let kakaoLoginJavaScriptKey = window.kakaoLoginJavaScriptKey;    // 카카오 SDK 초기화
    Kakao.init(kakaoLoginJavaScriptKey); // 실제 JavaScript 키로 교체
    console.log("Kakao SDK 초기화:", Kakao.isInitialized()); // true 출력
    const url = "http://localhost:9090";
    let accessToken = null;

    let joJoha = document.querySelector(".kakao-login-btn"); // 클래스 확인
    joJoha.onclick = function () {

        Kakao.Auth.authorize({
            // 인증 완료 리디렉션 URI 설정
            redirectUri: RedirectUri // 카카오 개발자 콘솔에 등록된 리디렉션 URI


        })


    }

    let naverClientId = window.naverClientId; // 환경 변수로 관리


    let naver_id_login = new naver.LoginWithNaverId({
        clientId: naverClientId, // 환경 변수로 관리
        callbackUrl: RedirectUri, // 환경 변수로 관리
        isPopup: true, // 팝업 방식 사용 여부
        loginButton: {
            class: "naver-login-btn"
            , color: "green", type: 3, height: 48
        }, // 로그인 버튼 스타일
    });

    // 스크립트 로드 완료 이후 init() 호출
    window.onload = function () {
        naver_id_login.init();
    };

    // 네이버 로그인 버튼 클릭 이벤트 추가
    let naverLoginBtn = document.querySelector(".naver-login-btn");
    if (naverLoginBtn) {
        naverLoginBtn.addEventListener("click", function () {
            console.log("네이버 로그인 버튼 클릭됨.");
            try {
                naver_id_login.authorize(); // 네이버 로그인 실행
            } catch (err) {
                console.error("네이버 로그인 중 오류 발생:", err);
                alert("네이버 로그인에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        });
    } else {
        console.error('네이버 로그인 버튼을 찾을 수 없습니다. "naver-login-btn" 클래스를 확인하세요.');
        alert("로그인 버튼 UI를 찾을 수 없습니다. 관리자에게 문의하세요.");
    }

    let gsi_material_button = document.querySelector(".gsi-material-button");
    gsi_material_button.addEventListener("click", function () {
        window.location.href = "/api/kakao/google/auth";


    });

//JO-JOHA회원가입 모달창
    let joJohaJoin = document.getElementById("jo-joha-join");
    let joJohaJoinModalFragment = document.getElementById("jo-joha-join-modal_fragment");
    joJohaJoin.addEventListener("click", function () {
        joJohaJoinModalFragment.style.display = "flex";



    });
    joJohaJoinModalFragment.addEventListener("click", function (e) {
        if(e.target === joJohaJoinModalFragment) {
            joJohaJoinModalFragment.style.display = "none";
        }
    })

    let joJohaLoginBox=document.querySelector(".jo-joha-login-box");
    let realJoJohaLoginModal=document.querySelector(".real-jo-joha-login-modal");
    joJohaLoginBox.addEventListener("click",function(){
        realJoJohaLoginModal.style.display="flex";
    });
    realJoJohaLoginModal.addEventListener("click",function(e){
        if(e.target===realJoJohaLoginModal){
            realJoJohaLoginModal.style.display="none";
        }
    })
    let jo_joha_join_button=document.querySelector(".jo-joha-join-button");






});

