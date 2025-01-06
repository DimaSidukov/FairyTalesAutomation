document.addEventListener('DOMContentLoaded', async function() {
    if (localStorage.getItem('authenticated') !== 'true') {
        window.location.href = 'index.html';
        return;
    }

    const userInfo = document.getElementById('user-info');
    const roundsContainer = document.getElementById('rounds-container');
    const createRoundBtn = document.getElementById('create-round-btn');
    const createRoundForm = document.getElementById('create-round-form');
    const submitRoundBtn = document.getElementById('submit-round-btn');
    const newRoundNameInput = document.getElementById('new-round-name');
    const errorMsg = document.getElementById('error-msg');
    const currentUserId = localStorage.getItem('currentUserId');

    // Базовый URL для API сервера
    const BASE_URL = 'http://localhost:8080';

    const rolesMapping = {
        'gvidon': 'Князь Гвидон',
        'babarikha': 'Баба Бабариха',
        'lebed': 'Царевна Лебедь',
        'saltan': 'Царь Салтан'
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

    // Функция для получения текущих назначений (fetch запрос к db.json)
    async function getAssignments() {
        try {
            const response = await fetch(`${BASE_URL}/assignments`);
            return await response.json();
        } catch (error) {
            console.error('Ошибка при получении назначений:', error);
            return [];
        }
    }

    const fetchRounds = async () => {
        try {
            const response = await fetch(`${BASE_URL}/rounds`);
            return await response.json();

        } catch (error) {
            console.error('Ошибка при получении списка раундов:', error);
            roundsContainer.textContent = 'Ошибка при получении списка раундов.';
            return [];
        }
    };

    // Проверка роли текущего пользователя (доступна ли кнопка создания раунда)
    const checkUserRole = async () => {
        try {
            const assignments = await getAssignments();
            const isBabarikha = assignments.some(
                assignment => assignment.participantId === currentUserId && assignment.roleId === 'babarikha'
            );
            // Логика отображения кнопки "Создать раунд"
            if (isBabarikha) {
                // Если роль gvidon или babarikha есть, показать кнопку
                createRoundBtn.style.display = 'block';
                createRoundBtn.disabled = false;
            } else {
                // Если роль отсутствует, скрыть кнопку
                createRoundBtn.style.display = 'none';
            }
        } catch (error) {
            console.error('Ошибка при проверке роли пользователя:', error);
        }
    }

    // Отобразить раунды текущего пользователя
    const displayUserRounds = async () => {
        try {
            const assignments = await getAssignments();
            const rounds = await fetchRounds();

            // Если раундов на сервере нет вообще
            if (rounds.length === 0) {
                roundsContainer.textContent = 'Список раундов пуст, необходимо создать раунды.';
                return;
            }
            // Проверяем, является ли пользователь "gvidon"
            const isGvidon = assignments.some(assignment => assignment.participantId === currentUserId && assignment.roleId === 'gvidon');
            let userRounds = [];

            if (isGvidon) {
            // Если пользователь "gvidon", он видит все раунды
                userRounds = rounds;
            } else {
                // Для остальных пользователей – фильтруем назначения по userId
                const userAssignments = assignments.filter(assignment => String(assignment.participantId) === String(currentUserId));
                // Получаем только те roundId, которые связаны с пользователем
                const userRoundIds = userAssignments.map(assignment => assignment.roundId).filter(Boolean);
                // Фильтруем раунды для текущего пользователя
                userRounds = rounds.filter(round => userRoundIds.includes(round.id));
            }
            // Если пользователь не участвует ни в одном раунде и не "gvidon"
            if (userRounds.length === 0) {
                roundsContainer.textContent = 'Вы не участвуете ни в одном раунде.';
                return;
            }
            // Иначе отображаем список раундов
            const list = document.createElement('ul');
            userRounds.forEach(round => {
                const listItem = document.createElement('li');
                const link = document.createElement('a');
                link.href = `round_template.html?id=${round.id}`; // Ссылка на страницу раунда
                link.textContent = round.name;
                listItem.appendChild(link);
                list.appendChild(listItem);
            });
            roundsContainer.appendChild(list);

        } catch (error) {
            roundsContainer.textContent = 'Ошибка при загрузке данных.';
            console.error('Ошибка:', error);
        }
    }

    createRoundBtn.addEventListener('click', () => {
        createRoundForm.style.display = 'block';
    });

    // Запускаем проверку роли и отображение раундов
    await checkUserRole();
    await displayUserRounds();

    submitRoundBtn.addEventListener('click', async () => {
        const newRoundName = newRoundNameInput.value.trim();
        const rounds = await fetchRounds();

        if (newRoundName) {
            const isDuplicate = rounds.some(round => round.name.toLowerCase() === newRoundName.toLowerCase());

            if (isDuplicate) {
                errorMsg.style.color = 'red';
                errorMsg.textContent = 'Раунд с таким названием уже существует.';
                return;
            }

            try {
                const response = await fetch(`${BASE_URL}/rounds`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: newRoundName, status: 'active' })
                });

                if (response.ok) {
                    errorMsg.style.color = 'green';
                    errorMsg.textContent = 'Раунд успешно создан!';
                    createRoundForm.style.display = 'none';
                    newRoundNameInput.value = '';
                    roundsContainer.innerHTML = '';
                } else {
                    const result = await response.json();
                    errorMsg.style.color = 'red';
                    errorMsg.textContent = result.message;
                }
            } catch (error) {
                errorMsg.style.color = 'red';
                errorMsg.textContent = 'Ошибка при создании раунда.';
            }
        } else {
            errorMsg.style.color = 'red';
            errorMsg.textContent = 'Имя раунда не может быть пустым.';
        }
    });
    displayUserInfo();
});
