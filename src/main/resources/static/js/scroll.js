let navbar = document.querySelector('nav');
let scrollPrev = window.pageYOffset;
window.onscroll = function () {
    const scrollCur = window.pageYOffset;
    if (scrollPrev > scrollCur) {
        navbar.style.top = "0";
    } else {
        navbar.style.top = "-90px";

    }
    scrollPrev = scrollCur;
}
