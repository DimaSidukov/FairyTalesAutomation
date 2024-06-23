document.addEventListener('DOMContentLoaded', () => {
    const loginButton = document.getElementById('login-button');
    const errorMessage = document.getElementById('error-message');
    const dashboard = document.getElementById('dashboard');
    const loginContainer = document.getElementById('login-container');
    const loginField = document.getElementById('login');
    const passwordField = document.getElementById('password');
    const logoutButton = document.getElementById('logout');
    const listRoundsButton = document.getElementById('list-rounds-btn');
    const usersListContainer = document.getElementById('users-list');
    const createRoundButton = document.getElementById('create-round-btn');

    if (localStorage.getItem('authenticated') === 'true') {
        loginContainer.style.display = 'none';
        dashboard.style.display = 'flex';
    }

    const authenticateUser = async () => {
        const login = loginField.value.trim();
        const password = passwordField.value.trim();

        if (!login || !password) {
            errorMessage.textContent = 'Необходимо ввести и Логин, и Пароль.';
            return;
        }

        try {
            const response = await fetch('http://localhost:8000/auth/login?' + new URLSearchParams({
                  login: login,
                  password: password,
              }), {
                method: 'POST'
              });
            const user = await response;

            if (user) {
                localStorage.setItem('authenticated', 'true');
                loginContainer.style.display = 'none';
                dashboard.style.display = 'flex';
            } else {
                errorMessage.textContent = 'Отказано в доступе.';
            }
        } catch (error) {
            console.log(error)
            errorMessage.textContent = 'Ошибка при подключении к серверу.';
        }
    };

    loginButton.addEventListener('click', authenticateUser);

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            authenticateUser();
        }
    });

    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('authenticated');
        dashboard.style.display = 'none';
        loginContainer.style.display = 'block';
        loginField.value = '';
        passwordField.value = '';
        errorMessage.textContent = '';
    });

    listRoundsButton.addEventListener('click', () => {
        if (localStorage.getItem('authenticated') !== 'true') {
            window.location.href = 'index.html';
        } else {
            window.location.href = 'rounds.html';
        }
    });
    createRoundButton.addEventListener('click', async () => {
            if (usersListContainer.classList.contains('visible')) {
                usersListContainer.classList.remove('visible');
            } else {
                usersListContainer.classList.add('visible');
                try {
                    const response = await fetch('http://localhost:8000/rounds/available_players');
                    const users = await response.json();
                    console.log(users);
                    renderUsersList(users);
                    usersListContainer.classList.add('visible');
                } catch (error) {
                    usersListContainer.innerHTML = '<p>Ошибка при загрузке пользователей.</p>';
                }
            }
        });

        const renderUsersList = (users) => {
            const listItems = users.map(user => `<li>${user.name}</li>`).join('');
            usersListContainer.innerHTML = `<ul>${listItems}</ul>`;
        }
});
