const rangeInputQ = document.querySelectorAll(".range-input-q input"),
    quantityInput = document.querySelectorAll(".quantity-input input"),
    rangeQ = document.querySelector(".sliderq .progressq");
let quantityGap = 100;
quantityInput.forEach(input => {
    input.addEventListener("input", e => {
        let minPrice = parseInt(quantityInput[0].value),
            maxPrice = parseInt(quantityInput[1].value);

        if ((maxPrice - minPrice >= quantityGap) && maxPrice <= rangeInputQ[1].max) {
            if (e.target.className === "input-min-q") {
                rangeInputQ[0].value = minPrice;
                rangeQ.style.left = ((minPrice / rangeInputQ[0].max) * 100) + "%";
            } else {
                rangeInputQ[1].value = maxPrice;
                rangeQ.style.right = 100 - (maxPrice / rangeInputQ[1].max) * 100 + "%";
            }
        }
    });
});
rangeInputQ.forEach(input => {
    input.addEventListener("input", e => {
        let minVal = parseInt(rangeInputQ[0].value),
            maxVal = parseInt(rangeInputQ[1].value);
        if ((maxVal - minVal) < quantityGap) {
            if (e.target.className === "range-min-q") {
                rangeInputQ[0].value = maxVal - quantityGap
            } else {
                rangeInputQ[1].value = minVal + quantityGap;
            }
        } else {
            quantityInput[0].value = minVal;
            quantityInput[1].value = maxVal;
            rangeQ.style.left = ((minVal / rangeInputQ[0].max) * 100) + "%";
            rangeQ.style.right = 100 - (maxVal / rangeInputQ[1].max) * 100 + "%";
        }
    });
});