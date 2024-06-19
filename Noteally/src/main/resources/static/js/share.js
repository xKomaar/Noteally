function searchUser() {
    // Pobierz wartość wprowadzoną w polu tekstowym
    var input = document.getElementById('username').value.toLowerCase();

    // Pobierz wszystkie paragrafy
    var paragraphs = document.getElementsByTagName('p');

    // Iteruj przez paragrafy i sprawdź, czy ich tekst zawiera kolejne litery z wprowadzonej nazwy
    for (var i = 0; i < paragraphs.length; i++) {
        var paragraph = paragraphs[i];
        var name = paragraph.textContent.toLowerCase();

        if (name.startsWith(input)) {
            paragraph.style.display = 'block';  // Pokaż paragraf, jeśli pasuje do wprowadzonej nazwy
        } else {
            paragraph.style.display = 'none';   // Ukryj paragraf, jeśli nie pasuje
        }
    }
}