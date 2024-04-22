function redirectToPath(path) {
  window.location.href = path;
}

function getData(selectChange) {
  selectChange.addEventListener("change", function (event) {
    const option = selectChange.options[selectChange.selectedIndex];
    window.location.href = option.getAttribute("data");
  });
}
