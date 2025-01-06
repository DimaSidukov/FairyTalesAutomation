document.addEventListener('DOMContentLoaded', function() {
    const userInfo = document.getElementById('user-info');
    const urlParams = new URLSearchParams(window.location.search);
    const roundId = urlParams.get('id');
    const roundNameElement = document.getElementById('round-name');
    const wondersListElement = document.getElementById('wonders-list');
    const newWonderNameInput = document.getElementById('new-wonder-name');
    const submitWonderBtn = document.getElementById('submit-wonder-btn');
    const errorMsg = document.getElementById('error-msg');
    const eventContainer = document.getElementById('event-container');
    const notifyElement = document.querySelector('.notify');
    const currentUserId = localStorage.getItem('currentUserId');

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

    // Получение роли пользователя
    const getUserRole = async (userId) => {
        try {
            const response = await fetch(`${BASE_URL}/assignments?participantId=${userId}`);
            const assignments = await response.json();

            // Возвращаем первую роль, если она существует
            return assignments[0]?.roleId || null;
        } catch (error) {
            console.error(error);
            return null;
        }
    };

    // Получение списка раундов, в которых участвует пользователь
    const getUserRounds = async (userId) => {
        try {
            const response = await fetch(`${BASE_URL}/assignments?participantId=${userId}`);
            const assignments = await response.json();

            // Получаем all roundId, с которыми пользователь участвует
            const roundIds = assignments.map(a => a.roundId);

            const roundsResponse = await fetch(`${BASE_URL}/rounds`);
            const rounds = await roundsResponse.json();

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

    // Настройка функционала личного кабинета
    const roundDashboard = async () => {
        const assignments = await getAssignments();
        const userAssignment = assignments.find(assignment => assignment.participantId === currentUserId);

        // Скрываем все кнопки по умолчанию
        document.getElementById('create-wonder-form').style.display = 'none';
        document.querySelectorAll('.status-btn').forEach(button => {
            button.style.display = 'none';
        });

        if (userAssignment) {
            const userRole = userAssignment.roleId;

            // Настройка кнопок в зависимости от роли пользователя
            if (userRole === 'gvidon') {
                return;

            } else if (userRole === 'babarikha') {
                document.getElementById('create-wonder-form').style.display = 'block';
                document.querySelectorAll('.status-btn').forEach(button => {
                    if (button.textContent === 'Верифицировать') {
                        button.style.display = 'inline-block';
                    }
                    else if (button.textContent === 'Отказать') {
                        button.style.display = 'inline-block';
                    }
                });
            } else if (userRole === 'lebed') {
                document.querySelectorAll('.status-btn').forEach(button => {
                    if (button.textContent === 'Создать') {
                        button.style.display = 'inline-block';
                    }
                });
            } else if (userRole === 'saltan') {
                document.querySelectorAll('.status-btn').forEach(button => {
                    if (button.textContent === 'Утвердить') {
                        button.style.display = 'inline-block';
                    }
                });
            }
        }
    };
    roundDashboard();

    // Получаем текущие назначения
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

    let approvedCount = 0;

    if (!roundId) {
        errorMsg.textContent = 'Ошибка: не указан ID раунда.';
        return;
    }

    const fetchRoundDetails = async () => {
        try {
            const response = await fetch(`${BASE_URL}/rounds/${roundId}`);
            if (!response.ok) throw new Error('Не удалось получить данные о раунде');
            const round = await response.json();

            const wondersResponse = await fetch(`${BASE_URL}/wonders?roundId=${roundId}`);
            if (!wondersResponse.ok) throw new Error('Не удалось получить данные о чудесах');
            const wonders = await wondersResponse.json();

            roundNameElement.textContent = round.name;

            wondersListElement.innerHTML = '';
            approvedCount = 0;

            if (wonders.length === 0) {
                wondersListElement.textContent = 'Чудес пока нет.';
            } else {
                wonders.forEach(wonder => {
                    const listItem = document.createElement('li');
                    const wonderStatus = wonderStatusMapping[wonder.status]
                    listItem.innerHTML = `Чудо - ${wonder.name} / Статус - <span>${wonderStatus}</span>`;

                    const createBtn = document.createElement('button');
                    createBtn.textContent = 'Создать';
                    createBtn.classList.add('status-btn', 'status-btn--verify');
                    createBtn.setAttribute('data-id', wonder.id);
                    createBtn.setAttribute('data-status', 'is_created');
                    if (wonder.status !== 'in_progress' || approvedCount >= 4) {
                        createBtn.classList.add('disabled');
                        createBtn.disabled = true;
                    }

                    const verifyBtn = document.createElement('button');
                    verifyBtn.textContent = 'Верифицировать';
                    verifyBtn.classList.add('status-btn', 'status-btn--verify');
                    verifyBtn.setAttribute('data-id', wonder.id);
                    verifyBtn.setAttribute('data-status', 'is_verified');
                    if (wonder.status !== 'is_created') {
                        verifyBtn.classList.add('disabled');
                        verifyBtn.disabled = true;
                    }

                    const rejectBtn = document.createElement('button');
                    rejectBtn.textContent = 'Отказать';
                    rejectBtn.classList.add('status-btn', 'status-btn--reject');
                    rejectBtn.setAttribute('data-id', wonder.id);
                    rejectBtn.setAttribute('data-status', 'is_rejected');
                    if (wonder.status !== 'is_created') {
                        rejectBtn.classList.add('disabled');
                        rejectBtn.disabled = true;
                    }

                    const approveBtn = document.createElement('button');
                    approveBtn.textContent = 'Утвердить';
                    approveBtn.classList.add('status-btn', 'status-btn--verify');
                    approveBtn.setAttribute('data-id', wonder.id);
                    approveBtn.setAttribute('data-status', 'is_approved');
                    if (wonder.status !== 'is_verified') {
                        approveBtn.classList.add('disabled');
                        approveBtn.disabled = true;
                    }

                    listItem.appendChild(createBtn);
                    listItem.appendChild(verifyBtn);
                    listItem.appendChild(approveBtn);
                    listItem.appendChild(rejectBtn);
                    wondersListElement.appendChild(listItem);

                    roundDashboard();

                    if (wonder.status === 'is_approved') {
                        approvedCount++;
                    }
                    // Добавляем обработчики для кнопок
                    createBtn.addEventListener('click', () => updateWonderStatus(wonder.id, 'is_created', wonder.name));
                    verifyBtn.addEventListener('click', () => updateWonderStatus(wonder.id, 'is_verified', wonder.name));
                    rejectBtn.addEventListener('click', () => updateWonderStatus(wonder.id, 'is_rejected', wonder.name));
                    approveBtn.addEventListener('click', () => updateWonderStatus(wonder.id, 'is_approved', wonder.name));
                });
            }

            if (approvedCount >= 4) {
                submitWonderBtn.disabled = true;
                loadEventImage();
                completeRound();

            } else {
                submitWonderBtn.disabled = false;
                eventContainer.innerHTML = '';
            }

        } catch (error) {
            errorMsg.textContent = 'Ошибка при получении данных раунда: ' + error.message;
        }
    };

    const updateWonderStatus = async (wonderId, newStatus, wonderName) => {
        try {
            const response = await fetch(`${BASE_URL}/wonders/${wonderId}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) throw new Error('Не удалось обновить статус чуда');
            fetchRoundDetails();
            displayStatusMessage(wonderName, newStatus);

        } catch (error) {
            errorMsg.textContent = 'Ошибка при обновлении статуса чуда: ' + error.message;
        }
    };

    const displayStatusMessage = (wonderName, status) => {

        let message = '';

        switch (status) {
            case 'is_created':
                message = `Чудо ${wonderName} успешно создано!`;
                break;
            case 'is_verified':
                message = `Чудо ${wonderName} успешно верифицировано!`;
                break;
            case 'is_rejected':
                message = `В верификации Чуда ${wonderName} отказано!`;
                break;
            case 'is_approved':
                message = `Чудо ${wonderName} успешно утверждено!`;
                break;
        }

        // Очистка предыдущих сообщений, если требуется
        errorMsg.innerHTML = '';
        // Сохраняем сообщение в localStorage
        localStorage.setItem(`wonder_${wonderName}`, message);
        // Обновляем отображение всех сообщений
        displayAllMessages();
    };

    const completeRound = async () => {
        try {
            const response = await fetch(`${BASE_URL}/rounds/${roundId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ status: 'completed' })
            });

            if (!response.ok) throw new Error('Не удалось завершить раунд');

            errorMsg.textContent = '';

            // Отображаем сообщение о завершении раунда
            const roundCompleteMessage = document.createElement('div');
            roundCompleteMessage.textContent = 'Раунд завершен!';
            errorMsg.appendChild(roundCompleteMessage);
        } catch (error) {
            errorMsg.textContent = 'Ошибка при завершении раунда: ' + error.message;
        }
    };

    const loadEventImage = () => {
        eventContainer.innerHTML = `
            <img src="wedding.jpg" width="500" height="400" alt="Свадьба" />
            <div class="event-text">Свадьба!!!</div>
        `;
    };

    wondersListElement.addEventListener('click', async (event) => {
        if (event.target.classList.contains('status-btn')) {
            const wonderId = event.target.getAttribute('data-id');
            const newStatus = event.target.getAttribute('data-status');
            await updateWonderStatus(wonderId, newStatus);
        }
    });

    function cleanUpUndefinedWonders() {
        // Проходим по ключам в localStorage
        for (let key in localStorage) {
            if (localStorage.hasOwnProperty(key)) {
                // Если ключ содержит "undefined", удаляем его
                if (key === 'wonder_undefined') {
                    localStorage.removeItem(key);
                    console.log('Удалено wonder_undefined из localStorage');
                }
            }
        }
    }

    // Функция для отображения всех сообщений из localStorage
    function displayAllMessages() {
        cleanUpUndefinedWonders();
        notifyElement.innerHTML = '';

        for (const key in localStorage) {
            if (localStorage.hasOwnProperty(key) && key.startsWith('wonder_') && key != 'wonder_undefined') {
                const message = localStorage.getItem(key);
                const messageElement = document.createElement('div');
                messageElement.textContent = message;
                notifyElement.appendChild(messageElement);
            }
        }
    }

    submitWonderBtn.addEventListener('click', async () => {
        const newWonderName = newWonderNameInput.value.trim();

        // Проверяем, что имя чуда введено, иначе выдаем ошибку
        if (!newWonderName || newWonderName === '') {
            errorMsg.style.color = 'red';
            errorMsg.textContent = 'Пожалуйста, введите название чуда!';
            return;
        }

        if (newWonderName) {
            try {
                const response = await fetch(`${BASE_URL}/wonders`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newWonderName, roundId, status: 'in_progress' })
                });

                if (response.ok) {
                    errorMsg.style.color = 'green';
                    errorMsg.textContent = 'Заявка на чудо успешно создана!';
                    newWonderNameInput.value = '';

                    if (notifyElement) {
                      const message = `Заявка на Чудо - ${newWonderName} создана. Требуется его создание!`;
                      notifyElement.textContent = message;
                      // Сохраняем сообщение в localStorage
                      localStorage.setItem(`wonder_${newWonderName}`, message);
                      // Обновляем отображение всех сообщений
                      displayAllMessages();
                    } else {
                        console.error('Элемент notifyElement не найден в DOM.');
                    }

                    await fetchRoundDetails();

                } else {
                    const result = await response.json();
                    errorMsg.style.color = 'red';
                    errorMsg.textContent = result.message || 'Ошибка при создании заявки на чудо';
                }
            } catch (error) {
                errorMsg.style.color = 'red';
                errorMsg.textContent = 'Ошибка при создании заявки на чудо: ' + error.message;
            }
        } else {
            errorMsg.style.color = 'red';
            errorMsg.textContent = 'Имя чуда не может быть пустым.';
        }
    });

    displayUserInfo();
    fetchRoundDetails();
    cleanUpUndefinedWonders();
    displayAllMessages();
});
