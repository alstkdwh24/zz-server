document.addEventListener( "DOMContentLoaded", function() {

    let menuButton = document.querySelector( ".menu-button" );
    let rightSideBar=document.querySelector(".right-side-bar");

    menuButton.onclick = function(event) {
        if(rightSideBar.style.display==='none'){

            event.stopPropagation(); // 중요!

            rightSideBar.style.display = 'flex';

        }

        console.log(1);
    }

    document.onclick = function(event) {

        // 클릭한 곳이 사이드바 내부인지 확인
        if (!rightSideBar.contains(event.target)) {
            // 사이드바 밖을 클릭한 경우
            rightSideBar.style.display = 'none';

        }
    }

});