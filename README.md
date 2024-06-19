EN:

1. Project Name:

Noteally - an application for storing and sharing notes.



2. List of main functionalities:

- User registration and login, authentication and authorization

- Add/remove/edit directories

- Add/remove/edit notes inside the directory

- Added an optional link in the note

- Share a note with another user

- Email notification about a note shared by another user

- Validation of data and forms

- Sorting/filtering/searching directories and notes (date, name)

- User roles:
  - USER - base user.
  - ADMIN - can manage (delete/edit/change roles) other users
  - SUSPENDED - a suspended user can view existing content, but cannot create new ones
  - Unlogged user - has access only to the home page and registration and login forms

- Session and cookies - remembering the user ("Remember Me")

- Relational database (MySQL) used to run the application

- File database (H2) used by unit tests



4. Start-up instructions:

- Java version 21 is required to run.

- You also need a locally running MySQL database called noteally. (jdbc:mysql://localhost:3306/noteally).

- The project can be run in an IDE, e.g. IntelliJ IDEA.

- After starting the application, tables in the database will be created automatically.


5. Technology stack: Java, Spring Boot, Thymeleaf, Hibernate, MySQL, H2



PL:

1. Nazwa Projektu:

Noteally - aplikacja do przechowywania i udostępniania notatek.



2. Lista głównych funkcjonalności:

- Rejestracja oraz logowanie użytkownika, autentykacja i autoryzacja

- Dodawanie/usuwanie/edytowanie katalogów

- Dodawanie/usuwanie/edycja notatek wewnątrz katalogu

- Dodanie opcjonalnego linku w notatce

- Udostępnienie notatki innemu użytkownikowi

- Powiadomienie email o udostępnionej przez innego użytkownika notatce

- Walidacja danych i formularzy

- Sortowanie/filtrowanie/wyszukiwanie katalogów oraz notatek (data, nazwa)

- Role użytkowników:
	- USER - bazowy użytkownik.
	- ADMIN - może zarządzać (usuwać/edytować/zmieniać role) innych użytkowników
	- SUSPENDED - użytkownik zawieszony, może przeglądać istniejące już treści, ale nie może tworzyć nowych
	- Użytkownik niezalogowany - ma dostęp tylko do strony startowej oraz formularzy rejestracji i logowania

- Sesja oraz ciasteczka - zapamiętywanie użytkownika ("Remember Me")

- Relacyjna baza danych (MySQL) używana przy działaniu aplikacji

- Baza danych w pliku (H2) używana przez testy jednostkowe



4. Instrukcja uruchomienia:

- Do uruchomienia potrzebna jest zainstalowana Java w wersji 21.

- Potrzebna jest również uruchomiona lokalnie baza danych MySQL o nazwie noteally. (jdbc:mysql://localhost:3306/noteally).

- Projekt uruchomić można w IDE, np. IntelliJ IDEA.

- Po uruchomieniu aplikacji tabele w bazie danych zostaną utworzone automatycznie.


5. Stack technologiczny: Java, Spring Boot, Thymeleaf, Hibernate, MySQL, H2
