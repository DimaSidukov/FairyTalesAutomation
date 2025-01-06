document.addEventListener('DOMContentLoaded', () => {
    const loginButton = document.getElementById('login-button');
    const errorMessage = document.getElementById('error-message');
    const dashErrorMessage = document.getElementById('dash-error-message');
    const dashboard = document.getElementById('dashboard');
    const userInfo = document.getElementById('user-info');
    const loginContainer = document.getElementById('login-container');
    const loginField = document.getElementById('login');
    const passwordField = document.getElementById('password');
    const logoutButton = document.getElementById('logout');
    const logoutShortButton = document.getElementById('logout-short');
    const listRoundsButton = document.getElementById('list-rounds-btn');
    const usersListContainer = document.getElementById('users-list');
    const usersButton = document.getElementById('users-btn');
    const roleChosedButton = document.getElementById('role-chosed-btn');
    const allUsersButton = document.getElementById('all-users-btn');
    const allRoundsButton = document.getElementById('all-rounds-btn');
    const allRoundsListContainer = document.getElementById('all-rounds-list');
    const assignParticipantsButton = document.getElementById('assign-participants-btn');
    const roundsListContainer = document.getElementById('rounds-list');
    const registerButton = document.getElementById('register-button');
    const roleButton = document.querySelector('.role-button');
    const rolesList = document.getElementById('roles-list');
    const assignRoleBtn = document.getElementById('assign-role-btn');
    const rolesCheckboxes = document.querySelectorAll('#roles-list input[type="checkbox"]');
    const blockParticipants = document.getElementById('block-participants-btn');
    const dashboardButtons = document.querySelectorAll('.dashboard .buttons-row button, .dashboard .assign-button');
    const shortDashboard = document.getElementById('short-dashboard');
    const deleteAccount = document.getElementById('delete-account-btn');
    const deleteAccountShort = document.getElementById('delete-account-btn-short');
    const statisticsButton = document.getElementById('statistics-button');
    const statRoundsContainer = document.getElementById('stat-rounds-container');
    const statRoundsList = document.getElementById('stat-rounds-list');
    const statWondersContainer = document.getElementById('stat-wonders-container');
    const statWondersList = document.getElementById('stat-wonders-list');

    // Базовый URL для API сервера
    const BASE_URL = 'http://localhost:8080';

    const rolesMapping = {
        'gvidon': 'Князь Гвидон',
        'babarikha': 'Баба Бабариха',
        'lebed': 'Царевна Лебедь',
        'saltan': 'Царь Салтан'
    };

    const wonderStatusMapping = {
        'in_progress': 'В работе',
        'is_created': 'Создано',
        'is_verified': 'Верифицировано',
        'is_rejected': 'Отказано',
        'is_approved': 'Утверждено'
    };

    roleButton.addEventListener('click', function() {

      rolesList.style.display = (rolesList.style.display === 'none' || rolesList.style.display === '') ? 'block' : 'none';
    });

    const currentUserId = localStorage.getItem('currentUserId');

    // Настройка функционала личного кабинета
    const setupDashboard = async () => {
        const assignments = await getAssignments();
        const userAssignments = assignments.filter(assignment => assignment.participantId === currentUserId);
        const userRole = userAssignments.find(assignment => assignment.roleId === 'gvidon');

        if (!userRole) {
            usersButton.style.display = 'none';
            allRoundsButton.style.display = 'none';
            assignParticipantsButton.style.display = 'none';
            blockParticipants.style.display = 'none';
            allRoundsListContainer.style.display = 'none';
        };
    }

    // Создаем форму регистрации
    const registrationForm = document.createElement('div');
    registrationForm.classList.add('login-container');
    registrationForm.innerHTML = `
        <h2>Регистрация</h2>
        <input type="text" id="reg-login" placeholder="Логин">
        <input type="password" id="reg-password" placeholder="Пароль">
        <input type="email" id="reg-email" placeholder="Электронная почта">
        <label class="checkbox-container">
            <input type="checkbox" id="agree-policy"> Согласен с политикой обработки персональных данных
            <span class="checkmark"></span>
        </label>
        <button id="submit-registration">Зарегистрироваться</button>
        <b>
          Если у Вас уже есть аккаунт, тогда <button id="logPage-button">Авторизуйтесь</button>
        </b>
        <p class="error-message" id="reg-error-message"></p>
    `;

    // Вставляем форму регистрации в контейнер
    document.body.appendChild(registrationForm);
    registrationForm.style.display = 'none';

    registerButton.addEventListener('click', () => {
        registrationForm.style.display = 'block';
        loginContainer.style.display = 'none';

    });

    const logPageButton = document.getElementById('logPage-button');
    logPageButton.addEventListener('click', function() {
            // Перенаправление пользователя на заглавную страницу
            window.location.href = 'index.html';
        });

    const submitRegistrationButton = document.getElementById('submit-registration');
    const regErrorMessage = document.getElementById('reg-error-message');

    submitRegistrationButton.addEventListener('click', async () => {
        const regLogin = document.getElementById('reg-login').value;
        const regPassword = document.getElementById('reg-password').value;
        const regEmail = document.getElementById('reg-email').value;
        const agreePolicy = document.getElementById('agree-policy').checked;

        // Проверка на заполнение всех полей
        if (!regLogin || !regPassword || !regEmail) {
            regErrorMessage.textContent = 'Требуется заполнить все поля.';
            return;
        }

        // Проверка email на корректность
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(regEmail)) {
            regErrorMessage.textContent = 'Некорректный формат электронной почты.';
            return;
        }

        // Проверка на согласие
        if (!agreePolicy) {
            regErrorMessage.textContent = 'Вы должны согласиться на обработку персональных данных.';
            return;
        }

        // Проверка существующих пользователей
        const response = await fetch(`${BASE_URL}/users/all`);
        const users = await response.json();
        const userExists = users.some(user => user.login === regLogin || user.email === regEmail);

        if (userExists) {
            regErrorMessage.textContent = 'Такой логин или электронная почта уже существуют. Пожалуйста, укажите другие.';
            return;
        }

        function generateUniqueId() {
            const letters = 'abcdefghijklmnopqrstuvwxyz';
            let id;
            do {
                id = '';
                for (let i = 0; i < 4; i++) {
                    id += letters[Math.floor(Math.random() * letters.length)];
                }
            } while (users.some(user => user.id === id));
            return id;
        }

        const newUser = {
            id: generateUniqueId(),
            login: regLogin,
            password: regPassword,
            email: regEmail,
            isBanned: false
        };

        await fetch(`${BASE_URL}/users/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newUser)
        });

        localStorage.setItem('authenticated', 'true');
        localStorage.setItem('currentUserId', newUser.id);
        localStorage.setItem('currentUserName', newUser.login);

        loginContainer.style.display = 'none';
        dashboard.style.display = 'flex';
        location.reload();
    });

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
            const response = await fetch(`${BASE_URL}/users/login`, {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify({ login, password }),
            });

            if (response.ok) {
                const user = response.json()
                if (user.isBanned) {
                    loginContainer.style.display = 'none';
                    dashboard.style.display = 'none';
                    shortDashboard.style.display = 'block';
                    return;
                }
                localStorage.setItem('authenticated', 'true');
                localStorage.setItem('currentUserId', user.id);
                localStorage.setItem('currentUserName', user.login);
                loginContainer.style.display = 'none';
                dashboard.style.display = 'flex';
                setupDashboard();
                location.reload();
            } else {
                errorMessage.textContent = 'В доступе отказано! Неправильное имя пользователя или пароль.';
            }
        } catch (error) {
            errorMessage.textContent = 'Ошибка при подключении к серверу.';
        }
    };

    // Получение роли пользователя
    const getUserRole = async (userId) => {
        try {
            const response = await fetch(`${BASE_URL}/users/${userId}`);
            const assignments = await response.json();

            // Возвращаем первую роль, если она существует
            return assignments?.preferredRole || null;
        } catch (error) {
            console.error(error);
            return null;
        }
    };

    // Получение списка раундов, в которых участвует пользователь
    const getUserRounds = async (userId) => {
        try {
            const response = await fetch(`${BASE_URL}/rounds/user/${userId}`);
            const rounds = await response.json();

            // Сопоставляем roundId из assignments с реальными данными round
            return rounds.filter(round => roundIds.includes(round.id)).map(round => ({
                    name: round.name,
                    status: round.status === "active" ? "Активный" : "Завершен"
                }));
        } catch (error) {
            console.error('Ошибка при загрузке раундов:', error);
            return [];
        }
    };

    // Отображение информации пользователя
    const displayUserInfo = async (user) => {
        try {
            const currentUserId = localStorage.getItem('currentUserId');
            const currentUserName = localStorage.getItem('currentUserName');
            // Проверяем, есть ли текущий пользователь
            if (!currentUserId || !currentUserName) {
                userInfo.innerHTML = `<p class="error">Ошибка: пользователь не авторизован.</p>`;
                return;
            }
            // Получаем данные о роли и участии в раундах
            const roleId = await getUserRole(currentUserId);
            const userRounds = await getUserRounds(currentUserId);
            // Устанавливаем данные в личный кабинет
            const roleName = rolesMapping[roleId] || 'Роль не назначена';
            const roundsText = userRounds.length ? userRounds.map((round) => `${round.name} (${round.status})`).join('<br>') : 'Не участвует ни в одном раунде';
            // Обновляем HTML
            userInfo.innerHTML = `
                <p><strong>Имя пользователя:</strong> ${localStorage.getItem('currentUserName')}</p>
                <p><strong>Роль пользователя:</strong> ${roleName}</p>
                <p><strong>Участие в раундах:</strong><br>${roundsText}</p>
            `;
        } catch (error) {
            console.error('Ошибка при отображении информации пользователя:', error);
            userInfo.innerHTML = `<p class="error">Ошибка при загрузке данных пользователя.</p>`;
        }
    };

    // TODO: fix this
    roleChosedButton.addEventListener('click', async () => {
      try {
          const response = await fetch(`${BASE_URL}/assignments`);
          const assignments = await response.json();
          // Собираем список пользователи с назначенными ролями
          const usersWithRoles = assignments.filter(assignment => assignment.roleId);

          const usersResponse = await fetch(`${BASE_URL}/users/all`);
          const users = await usersResponse.json();
          const filterUsers = users.filter(user => user.isBanned === false);

          // Получаем список всех раундов
          const roundsResponse = await fetch(`${BASE_URL}/rounds`);
          const rounds = await roundsResponse.json();

          const userItems = filterUsers.filter(user => usersWithRoles.some(assignment => assignment.participantId === user.id)).map(user => {
              const userAssignment = usersWithRoles.find(assignment => assignment.participantId === user.id); // Ищем соответствующее назначение
              const roleName = rolesMapping[userAssignment.roleId] || 'Роль не назначена'; // Находим русское название роли
              const userRound = rounds.find(round => round.id === userAssignment.roundId); // Находим раунд по ID из assignment
              const roundInfo = userRound ? `${userRound.name} (${userRound.status === 'active' ? 'Активен' : 'Завершен'})`: 'Не участвует в раундах';
              return `
              <li>
                  <input type="checkbox" class="participant-checkbox" data-id="${user.id}">
                  ${user.login} (Роль: ${roleName}), Раунд: ${roundInfo}
              </li>
              `;
          })
          .join('');

          document.querySelector('#users-list ul').innerHTML = userItems;
      } catch (error) {
          console.error('Ошибка при получении списка участников:', error);
      }
    });

    assignRoleBtn.addEventListener('click', async () => {
        const selectedRoles = Array.from(rolesCheckboxes).filter(checkbox => checkbox.checked);

        // Проверка, выбрана ли хотя бы одна роль
        if (selectedRoles.length === 0) {
            dashErrorMessage.textContent = 'Необходимо выбрать роль';
            return;
        }

        // Проверка, чтобы была выбрана только одна роль
        if (selectedRoles.length > 1) {
            dashErrorMessage.textContent = 'Необходимо выбрать только одну роль';
            return;
        }

        const selectedRoleId = selectedRoles[0].id;
        const currentUserId = localStorage.getItem('currentUserId');
        const userId = currentUserId;

        // Проверка, выбрана ли уже роль "gvidon"
        if (selectedRoleId === 'gvidon') {
            const assignments = await getAssignments();

            const gvidonAssignment = assignments.find(assignment => assignment.roleId === 'gvidon');

        if (gvidonAssignment && gvidonAssignment.participantId !== userId) {
            dashErrorMessage.textContent = 'Роль "Князь Гвидон" уже выбрана другим пользователем! Пожалуйста, выберите другую роль';
            return;
            }
        }

        // Проверка активных раундов
        const curUserId = localStorage.getItem('currentUserId');
        const isParticipatingInActiveRound = await checkActiveRound(curUserId);
        if (isParticipatingInActiveRound) {
            dashErrorMessage.textContent = 'Пользователь участвует в активном раунде. Смена роли невозможна до завершения раунда!';
            return;
        }

        const existingAssignment = await findAssignmentForUser(userId);
        let success = false;

        if (existingAssignment) {
            success = await updateAssignment(existingAssignment.id, selectedRoleId);
            success ? alert('Роль успешно обновлена!') : alert('Ошибка при обновлении роли');
        } else {
            const newUserId = localStorage.getItem('currentUserId');
            success = await createAssignment(newUserId, selectedRoleId);
            success ? alert('Роль успешно назначена!') : alert('Ошибка при назначении роли');
        }

        if (success) {
            setupDashboard();
            location.reload();
        }
    });

    // TODO: fix this
    // Функция для получения текущих назначений (fetch запрос к db.json)
    async function getAssignments() {
        try {
            const response = await fetch(`${BASE_URL}/assignments`);
            const assignments = await response.json();
            return assignments;
        } catch (error) {
            console.error('Ошибка при получении назначений:', error);
            return [];
        }
    }

    // Функция для вывода успеха
    function showSuccess(message) {
        dashErrorMessage.textContent = message;
        dashErrorMessage.style.color = 'green';
    }

    // TODO: will need logic updates in invocations
    // Проверка, существует ли назначение для пользователя
    async function findAssignmentForUser(userId) {
        // Получаем userId из локального хранилища
        const currUserId = localStorage.getItem('currentUserId');
        console.log(`userId равен ${currUserId}`);

        try {
            // Шаг 1: Попытаться получить assignments с participantId, равным currUserId
            const response = await fetch(`${BASE_URL}/rounds/user/${currUserId}`);
            if (!response.ok) {
                console.error('Ошибка при получении assignments. Статус ответа:', response.status);
                return null;
            }

            const rounds = await response.json();
            console.log('Полученные assignments с participantId равным currUserId:', rounds);

            // Шаг 2: Если найдено, вернуть первый assignment
            if (rounds.length > 0) {
                return rounds[0];
            }

        } catch (error) {
              console.error('Ошибка при поиске назначения для пользователя:', error);
              return null;
          }
    }

    // TODO: will need logic updates in invocations
    // Обновление существующего назначения
    async function updateAssignment(userId, preferredRole) {
        try {
            const response = await fetch(`${BASE_URL}/${userId}/set_role?preferred_role=${encodeURIComponent(preferredRole)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            return response.ok;
        } catch (error) {
            console.error('Ошибка при обновлении назначения:', error);
            return false;
        }
    }

    // Создание нового назначения
    // TODO: will need logic updates in invocations
    // returned data - User
    async function createAssignment(userId, preferredRole) {
        try {
            const response = await fetch(`${BASE_URL}/${userId}/set_role?preferred_role=${encodeURIComponent(preferredRole)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('Ошибка при создании назначения');
            }

            // Получаем созданное назначение от сервера
            const updatedUser = await response.json();
            console.log('Назначение успешно создано:', updatedUser);

            return updatedUser; // Возвращаем созданное назначение

        } catch (error) {
            console.error('Ошибка при создании назначения:', error);
            return null;
        }
    }

    // Проверка, есть ли у пользователя активные раунды
    async function checkActiveRound(userId) {
       const response = await fetch(`${BASE_URL}/rounds/user/${userId}`);
       const roundList = response.json()

        if (!response.ok) {
            return false;
        }

        const roundList = await response.json();
        // Check if any round has status "active"
        return roundList.some(round => round.status === "active");
    }

    const fetchParticipants = async () => {
        try {
            const response = await fetch(`${BASE_URL}/users/all`);
            const users = await response.json();
            const filterUsers = users.filter(user => user.isBanned === false);

            const userItems = filterUsers.map(user => `
                <li>
                    <input type="checkbox" class="participant-checkbox" data-id="${user.id}">
                    ${user.login}
                </li>
            `).join('');

            usersListContainer.querySelector('ul').innerHTML = userItems;
        } catch (error) {
            console.error('Ошибка при получении списка участников:', error);
        }
    };

    const fetchRounds = async () => {
        try {
            const response = await fetch(`${BASE_URL}/rounds/all`);
            const rounds = await response.json();

            const roundItems = rounds.map(round => `
                <li>${round.name}</li>
            `).join('');

            roundsListContainer.querySelector('ul').innerHTML = roundItems;
        } catch (error) {
            console.error('Ошибка при получении списка раундов:', error);
        }
    };

    const fetchAllRounds = async () => {
        try {
            const response = await fetch(`${BASE_URL}/rounds/active`);
            const filterRounds = await response.json();

            const roundItems = filterRounds.map(round => `
                <li>
                    <input type="checkbox" class="round-checkbox" data-id="${round.id}">
                    ${round.name}
                </li>
            `).join('');

            allRoundsListContainer.querySelector('ul').innerHTML = roundItems;
        } catch (error) {
            console.error('Ошибка при получении списка раундов:', error);
        }
    };


    const assignParticipantsToRounds = async () => {
        const selectedParticipants = Array.from(document.querySelectorAll('.participant-checkbox:checked')).map(checkbox => checkbox.dataset.id);
        const selectedRounds = Array.from(document.querySelectorAll('.round-checkbox:checked')).map(checkbox => checkbox.dataset.id);

        // Проверяем, чтобы был выбран только один раунд
        if (selectedRounds.length !== 1) {
            dashErrorMessage.textContent = 'Пожалуйста, выберите один и только один раунд';
            return;
        }

        if (selectedParticipants.length === 0 || selectedRounds.length === 0) {
            dashErrorMessage.textContent = 'Пожалуйста, выберите хотя бы одного участника и один раунд';
            //alert('Пожалуйста, выберите хотя бы одного участника и один раунд.');
            return;
        }

        // Сброс контейнера сообщений
        dashErrorMessage.textContent = '';
        const errors = [];
        const successMessages = [];
        const roundId = selectedRounds[0];

        try {
            // Проверяем статус раунда
            const roundResponse = await fetch(`${BASE_URL}/rounds/${roundId}`);
            const round = await roundResponse.json();

            if (round.status !== 'active') {
            dashErrorMessage.textContent = `Нельзя назначить участников в этот раунд, так как его статус: ${round.status}`;
            return;
            }

            // Создаем объект для хранения ролей назначаемых участников
            const rolesMap = {};

            for (const participantId of selectedParticipants) {
                // Проверка наличия ролей у участников
                const roleResponse = await fetch(`${BASE_URL}/users/${participantId}`);
                const assignments = await roleResponse.json();

                // Проверяем, есть ли roleId в assignments
                const participantRole = assignments[0]?.preferredRole;

                if (!participantRole) {
                    // Получаем информацию о пользователе
                    const userResponse = await fetch(`${BASE_URL}/users/${participantId}`);
                    const user = await userResponse.json();

                    // Добавляем сообщение с login пользователя
                    errors.push(`Нельзя назначить пользователя ${user.name}, так как у него нет выбранной роли!`);
                    continue;
                }

                // Проверка на роль "gvidon"
                if (participantRole === 'gvidon') {
                    const userResponse = await fetch(`${BASE_URL}/users/${participantId}`);
                    const user = await userResponse.json();
                    errors.push(`Пользователь ${user.name} с ролью "gvidon" не может назначаться в раунды!`);
                    continue;
                }

                // Считаем количество участников с каждой ролью из выбранных
                if (rolesMap[participantRole]) {
                    rolesMap[participantRole]++;
                } else {
                    rolesMap[participantRole] = 1;
                }

                // Проверяем роли в текущем раунде
                const roundAssignmentsResponse = await fetch(`${BASE_URL}/assignments?roundId=${roundId}`);
                const roundAssignments = await roundAssignmentsResponse.json();
                const filteredAssignments = roundAssignments.filter(assignment => assignment.roundId === roundId);

                if (filteredAssignments.length >= 3) {
                    errors.push(`Раунд ${roundId} уже содержит максимальное количество участников (3)!`);
                    continue;
                }

                // Проверяем существующие назначения: пропускаем, если нет ни одного назначения для текущего раунда
                const conflictAssignment = roundAssignments.find(assignment => assignment.roleId === participantRole && assignment.roundId === roundId);

                if (conflictAssignment) {
                    const conflictingParticipantResponse = await fetch(`${BASE_URL}/users/${conflictAssignment.participantId}`);
                    const conflictingParticipant = await conflictingParticipantResponse.json();
                    errors.push(`Участник с ролью ${participantRole} уже есть в раунде ${roundId}. Назначение невозможно! (конфликт с пользователем ${conflictingParticipant.login})`);
                    continue;
                }
            }

            // Проверяем, есть ли дублирующиеся роли среди выбранных участников
            const duplicateRoles = Object.keys(rolesMap).filter(role => rolesMap[role] > 1);
            if (duplicateRoles.length > 0) {
                errors.push(`Нельзя выбирать нескольких участников с одинаковыми ролями (${duplicateRoles.join(', ')}). Назначение отменено!`);
                return;
            }

            // Если есть ошибки, выводим все ошибки и прерываем выполнение
            if (errors.length > 0) {
                dashErrorMessage.innerHTML = errors.join('<br>');
                return;
            }

            // Если дублирующихся ролей нет, выполняем назначение
            for (const participantId of selectedParticipants) {
                const roleResponse = await fetch(`${BASE_URL}/assignments?participantId=${participantId}`);
                const assignments = await roleResponse.json();
                const participantRole = assignments[0]?.roleId;

                const roundAssignmentsResponse = await fetch(`${BASE_URL}/assignments?roundId=${roundId}`);
                const roundAssignments = await roundAssignmentsResponse.json();
                const rolesInRound = roundAssignments.map(assignment => assignment.roleId);

                if (assignments.length > 0) {
                    // Обновление существующего assignment
                    const assignmentId = assignments[0].id;
                    await fetch(`${BASE_URL}/assignments/${assignmentId}`, {
                        method: 'PATCH',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            roundId
                        })
                    });
                } else {
                    // Создание нового assignment
                    await fetch('${BASE_URL}/assignments', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            participantId,
                            roundId
                        })
                    });
                }

              // Получаем информацию об успешно назначенном пользователе
              const successUserResponse = await fetch(`${BASE_URL}/users/${participantId}`);
              const successUser = await successUserResponse.json();
              successMessages.push(`Участник ${successUser.login} успешно назначен на раунд!`);
              // Выводим success-сообщения
              if (successMessages.length > 0) {
                  dashErrorMessage.style.color = 'green';
                  dashErrorMessage.innerHTML = successMessages.join('<br>');
              }
          }

      } catch (error) {
          console.error('Ошибка в процессе выполнения:', error);
          errors.push(`Что-то пошло не так: ${error.message}`);
          dashErrorMessage.innerHTML = errors.join('<br>');
      }
    };

    loginButton.addEventListener('click', authenticateUser);

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            authenticateUser();
        }
    });

    const handleLogout = () => {
        localStorage.removeItem('authenticated');
        dashboard.style.display = 'none';
        shortDashboard.style.display = 'none';
        registrationForm.style.display = 'none';
        loginContainer.style.display = 'block';
        loginField.value = '';
        passwordField.value = '';
        errorMessage.textContent = '';
    };

    logoutButton.addEventListener('click', handleLogout);
    logoutShortButton.addEventListener('click', handleLogout);

    listRoundsButton.addEventListener('click', () => {
        if (localStorage.getItem('authenticated') !== 'true') {
            window.location.href = 'index.html';
        } else {
            window.location.href = 'rounds.html';
        }
    });

    usersButton.addEventListener('click', () => {
        usersListContainer.classList.toggle('visible')
        if (usersListContainer.classList.contains('visible')) {
            fetchParticipants();
        }
    });

    allUsersButton.addEventListener('click', () => {
        //usersListContainer.classList.toggle('visible')
        if (usersListContainer.classList.contains('visible')) {
        fetchParticipants();
        }
    });

    allRoundsButton.addEventListener('click', () => {
        allRoundsListContainer.classList.toggle('visible');
        if (allRoundsListContainer.classList.contains('visible')) {
            fetchAllRounds();
        }
    });

    assignParticipantsButton.addEventListener('click', assignParticipantsToRounds);

    blockParticipants.addEventListener('click', async () => {
        // Сброс контейнера сообщений
        dashErrorMessage.textContent = '';

        const selectedParticipants = Array.from(document.querySelectorAll('.participant-checkbox:checked')).map(checkbox => checkbox.dataset.id);

        if (selectedParticipants.length === 0) {
            dashErrorMessage.textContent = 'Пожалуйста, выберите хотя бы одного участника для блокировки';
            return;
            }

        for (const participantId of selectedParticipants) {
            try {
            const response = await fetch(`${BASE_URL}/users/${participantId}`);
            const user = await response.json();

            const assignmentResponse = await fetch(`${BASE_URL}/assignments?participantId=${participantId}`);
            const assignments = await assignmentResponse.json();

            if (!assignments.length) {
                dashErrorMessage.innerHTML += `Ошибка: Не найдена запись assignment для participantId ${participantId}<br>`;
                continue;
            }

            const assignmentId = assignments[0].id;

            // Проверка на существование пользователя, если не валидный ID.
            if (!user) {
                dashErrorMessage.innerHTML += `Ошибка: Пользователь с ID ${participantId} не найден!<br>`;
                continue;
            }

            if (user.id === currentUserId) {
                dashErrorMessage.textContent = 'Вы не можете заблокировать самого себя!';
                return;
                }

            // Обновляем пользователя, добавляя isBanned: true
            const updateResponse = await fetch(`${BASE_URL}/users/${participantId}`, {
                method: 'PATCH',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify({ isBanned: true }),
            });

            // Обновляем RoundId в assignments
            const updateAssignment = await fetch(`${BASE_URL}/assignments/${assignmentId}`, {
                method: 'PATCH',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify({ roundId: 'noround' }),
            });

            if (!updateResponse.ok || !updateAssignment.ok) {
                console.error(`Не удалось обновить пользователя с ID: ${participantId}`);
            }
            else {
                // Добавляем сообщение с login пользователя
                dashErrorMessage.innerHTML += `Пользователь ${user.login} был заблокирован!<br>`;
                dashboardButtons.forEach(button => button.disabled = true);
            }
          } catch (error) {
                console.error(`Ошибка при подключении к серверу для пользователя ${participantId}`);
            }
          }
    });

    const deleteUserAccount = async () => {
        const userId = localStorage.getItem('currentUserId');

        if (!userId) {
            alert('Не удалось определить идентификатор пользователя. Пожалуйста, войдите в систему снова.');
            return;
        }

        try {
          const response = await fetch(`http://localhost:3001/users/${userId}`, {
              method: 'DELETE',
              headers: {
                  'Content-Type': 'application/json'
              }
          });

            if (response.ok) {
                // Успешное удаление аккаунта
                alert('Ваш аккаунт был успешно удален.');
                localStorage.removeItem('currentUserId');
                localStorage.removeItem('authenticated');
                dashboard.style.display = 'none';
                // Переключение на форму входа или другая логика
                loginContainer.style.display = 'block';
            } else {
                alert('Ошибка при удалении аккаунта. Пожалуйста, попробуйте снова.');
            }
        } catch (error) {
              console.error('Ошибка при удалении аккаунта:', error);
              alert('Произошла ошибка при подключении к серверу. Пожалуйста, попробуйте снова позже.');
          }
    }

    // Статус отображения
    let roundsVisible = false;
    const wondersVisible = {};

    // Универсальная функция для загрузки данных по URL
    async function fetchData(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Ошибка загрузки данных: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Ошибка:', error);
            return null;
        }
    }
    // Обработчик для отображения списка раундов
    statisticsButton.addEventListener('click', async () => {
        if (roundsVisible) {
        // Если список раундов видим, скрываем его
            statRoundsContainer.style.display = 'none';
            roundsVisible = false;
            return;
        }

        // Загрузка раундов из API
        const rounds = await fetchData(`${BASE_URL}/rounds`);
        if (!rounds || rounds.length === 0) {
            statRoundsList.innerHTML = '<li>Ошибка загрузки данных или раундов нет</li>';
            return;
        }

        // Очистка списка и добавление элементов
        statRoundsList.innerHTML = '';
        rounds.forEach(round => {
            const roundItem = document.createElement('li');
            const statusText = round.status === 'active' ? 'Активный' : 'Завершен';
            roundItem.innerHTML = `<button class="round-button" data-round-id="${round.id}" data-round-status="${round.status}">
                ${round.name} (Статус: ${statusText})
            </button>`;
            statRoundsList.appendChild(roundItem);
            wondersVisible[round.id] = false; // Изначально чудеса для всех раундов скрыты
        });

        statRoundsContainer.style.display = 'block'; // Показать контейнер с раундами
        roundsVisible = true;
    });

    // Обработчик для отображения списка чудес выбранного раунда
    statRoundsList.addEventListener('click', async (event) => {
        const button = event.target.closest('button.round-button');
        if (!button) return; // Если кликнули не на кнопку
        const roundId = button.dataset.roundId;
        // Проверяем, открыт ли список чудес для текущего раунда
        if (wondersVisible[roundId]) {
            const existingWonderList = document.getElementById(`stat-wonders-list-${roundId}`);
            if (existingWonderList) existingWonderList.remove(); // Удаляем существующие чудеса
            wondersVisible[roundId] = false;
            return;
        }

        const wonders = await fetchData(`${BASE_URL}/wonders`);
        if (!wonders) {
            const errorMessage = document.createElement('li');
            errorMessage.textContent = 'Ошибка загрузки данных';
            statRoundsList.appendChild(errorMessage);
            return;
        }

        // Фильтрация чудес по текущему roundId
        const roundWonders = wonders.filter(wonder => wonder.roundId);
        const wonderList = document.createElement('ul');
        wonderList.id = `stat-wonders-list-${roundId}`;

        if (roundWonders.length > 0) {
            roundWonders.forEach(wonder => {
                const wonderItem = document.createElement('li');
                const wonderStatus = wonderStatusMapping[wonder.status]
                wonderItem.textContent = `Чудо - ${wonder.name} (Статус: ${wonderStatus})`;
                wonderList.appendChild(wonderItem);
            });
        } else {
            const noWondersMessage = document.createElement('li');
            noWondersMessage.textContent = 'Нет чудес для этого раунда';
            wonderList.appendChild(noWondersMessage);
        }

        // Добавляем чудеса ПОСЛЕ раунда
        button.parentElement.appendChild(wonderList);
        wondersVisible[roundId] = true;
    });


    deleteAccount.addEventListener('click', () => deleteUserAccount(currentUserId));
    deleteAccountShort.addEventListener('click', () => deleteUserAccount(currentUserId));

    setupDashboard();
    displayUserInfo();
});
