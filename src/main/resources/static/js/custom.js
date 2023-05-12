// custom.js
function changeBackgroundColor(element) {
    element.style.backgroundColor = '#cfe2ff';
}

function restoreBackgroundColor(element) {
    element.style.backgroundColor = 'transparent';
}

function redirectToDetail(id) {
    window.location.href = '/detail/' + id;
}

document.addEventListener('DOMContentLoaded', function() {
    const rows = document.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const id = row.querySelector('td:first-child').innerText;
        row.addEventListener('click', () => redirectToDetail(id));
        row.addEventListener('mouseover', () => changeBackgroundColor(row));
        row.addEventListener('mouseout', () => restoreBackgroundColor(row));
    });
});