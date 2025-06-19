
<!--시험하는 코드 여기잘까랑 상관없음-->

document.addEventListener("DOMContentLoaded",function(){
  let menuBackground=document.querySelector(".menu-background");
    let hamburger=document.querySelector(".hamburger");
    hamburger.onclick=function(){
        menuBackground.style.display="flex";
        console.log("hamburger");
    }
    menuBackground.addEventListener("click",function(event){
        if(event.target ===menuBackground){
            menuBackground.style.display="none";
            console.log("menuBackground");
        }
    })

})


