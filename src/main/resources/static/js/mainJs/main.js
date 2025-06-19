<!--시험하는 코드 여기잘까랑 상관없음-->


document.addEventListener("DOMContentLoaded", function () {

    // 위의 세개의 변수는 네이버 구글 카카오 다 적용
    const urlParams = new URLSearchParams(window.location.search);
    const authorizationCode = urlParams.get("code");
    const stateToken = urlParams.get("state");

    console.log("authorizationCode" + authorizationCode);
    console.log(urlParams);
    let accessToken = "";
    if (authorizationCode) {
        console.log("리디렉션에서 받은 인증 코드:", authorizationCode);
    //카카오톡 로그인
        $.ajax({
            type: "POST", // 'Get'을 'POST'로 변경
            url: "/api/kakao/token",
            headers: {
                "Content-Type": "application/json" // JSON 요청
            },
            data: JSON.stringify({
                authorizationCodes: authorizationCode // Body에 데이터 전달
            }),
            success: function (response) {
                const accessToken = response.access_token;
                const refreshToken = response.refresh_token;
                const idToken = response.id_token;
                console.log("카카오");
                console.log("Access Token:", accessToken);
                console.log("Refresh Token:", refreshToken);
                console.log("ID Token:", idToken);
                fetch("/api/kakao/people", {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${accessToken}`, // Access Token 삽입
                        "Content-Type": "application/x-www-form-urlencoded;charset=utf-8"


                    },
                }).then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText);
                    }
                    return response.json();

                }).then(data => {
                    console.log("카카오 사용자 데이터:", data);
                })
                    .catch(error => {
                        console.error("API 요청 중 오류 발생:", error);
                    });


            },
            error: function (xhr, status, error) {
                console.error("Error:", error);
                console.error("Status:", status);
                console.error("Response:", xhr.responseText);
                con
            }
        });
        //구글 로그인
        $.ajax({
            type:"POST",
            url:"/api/kakao/google/token",
            contentType:"application/json",
            headers:{
                "Authorization": authorizationCode // 헤더에 실제 인증 토큰 추가

            },       data: JSON.stringify({ code: "AUTHORIZATION_CODE_FROM_GOOGLE"
            }),
            success:function (response) {
                let accessToken = response.access_token;
                let refreshToken = response.refresh_token;
                let idToken = response.id_token;
                console.log("Access Token:", accessToken);
                console.log("Refresh Token:", refreshToken);
                console.log("ID Token:", idToken);
                console.log("구글:", response);
                $.ajax({
                    type:"GET",
                    url:"/api/kakao/google/userinfo",
                    contentType:"application/json",

                })
            }
        })
    }


    const hash = window.location.hash; // # 뒤의 값
    console.log("해시 값(raw):", hash); // 예: #access_token=...&state=...&token_type=bearer&expires_in=3600

//네이버 로그인
    if (hash) {

        const params = new URLSearchParams(hash.slice(1));
        const accessToken = params.get("access_token");
        const stateToken = params.get("state");
        const tokenType = params.get("token_type");
        const expiresIn = params.get("expires_in");

        // 값 출력 (디버깅용)
        console.log("Access Token:", accessToken);
        console.log("State Token:", stateToken);
        if (accessToken && stateToken) {

            fetch("/api/kakao/naver/token", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${accessToken}`

                },

            }).then(response => response.json()).then(data => {
                console.log("Access Token" + data.access_token);
            }).catch((error) => console.log(error));
        } else {
            console.log("authorizationCode가 없습니다.");
        }
    }

})

