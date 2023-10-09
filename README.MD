<h1>Тестовое задание CRM+Уголок (выполнял: Садик Назар)</h1>

<h2>Инструкция</h2>
<ol>
  <li>Откройте Docker Desktop</li>
  <li>Откройте консоль и перейдите в папку с проектом, где находиться файл docker-compose.yml</li>
  <li>Пропишите эту команду: <b>docker-compose up --build</b></li>
</ol>

После у вас должно запуститься 3 контейнера (база данных, интерфейс для бд, приложение)

Что бы перейти в swagger: <b>http://localhost:8080/swagger-ui/index.html#/</b>

Что бы перейти в pgAdmin(панель управления БД), перейдите по этой ссылке: <b>http://localhost:5050/browser/</b>

Данные для входа в аккаунт админа:<br>
Login: <b>test@gmail.com</b><br>
Password: <b>1234</b>

Данные для регистрации сервера
<ul>
  <li>Host name/address: <b>db</b></li>
  <li>Port: <b>5432</b></li>
  <li>Username: <b>postgres</b></li>
  <li>Password <b>1234</b></li>
</ul>
