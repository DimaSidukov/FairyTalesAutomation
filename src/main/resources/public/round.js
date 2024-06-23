document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const roundId = urlParams.get('id');
    const roundNameElement = document.getElementById('round-name');
    const wondersListElement = document.getElementById('wonders-list');
    const newWonderNameInput = document.getElementById('new-wonder-name');
    const submitWonderBtn = document.getElementById('submit-wonder-btn');
    const errorMsg = document.getElementById('error-msg');
    const eventContainer = document.getElementById('event-container');

    if (!roundId) {
        errorMsg.textContent = 'Ошибка: не указан ID раунда.';
        return;
    }

    const fetchRoundDetails = async () => {
        try {
            const response = await fetch(`http://localhost:8000/rounds/${roundId}`);
            if (!response.ok) throw new Error('Не удалось получить данные о раунде');
            const round = await response.json();

            const wonders = round.wonders

            console.log(wonders)

            roundNameElement.textContent = round.name;

            wondersListElement.innerHTML = '';

            if (wonders.length === 0) {
                wondersListElement.textContent = 'Чудес пока нет.';
            } else {
                wonders.forEach(wonder => {
                    const listItem = document.createElement('li');
                    listItem.innerHTML = `${wonder.name} - <span>${wonder.status}</span>`;

                    const createBtn = document.createElement('button');
                    createBtn.textContent = 'Создать';
                    createBtn.classList.add('status-btn');
                    createBtn.setAttribute('data-id', wonder.id);
                    createBtn.setAttribute('data-status', 'Создано');
                    createBtn.disabled = wonder.status !== 1;

                    const verifyBtn = document.createElement('button');
                    verifyBtn.textContent = 'Верифицировать';
                    verifyBtn.classList.add('status-btn');
                    verifyBtn.setAttribute('data-id', wonder.id);
                    verifyBtn.setAttribute('data-status', 'Верифицировано');
                    verifyBtn.disabled = wonder.status !== 'Создано';

                    const approveBtn = document.createElement('button');
                    approveBtn.textContent = 'Утвердить';
                    approveBtn.classList.add('status-btn');
                    approveBtn.setAttribute('data-id', wonder.id);
                    approveBtn.setAttribute('data-status', 'Утверждено');
                    approveBtn.disabled = wonder.status !== 'Верифицировано';

                    listItem.appendChild(createBtn);
                    listItem.appendChild(verifyBtn);
                    listItem.appendChild(approveBtn);
                    wondersListElement.appendChild(listItem);

                    if (wonder.status === 'Утверждено') {
                        approvedCount++;
                    }
                });
            }

            if (approvedCount >= 4) {
                submitWonderBtn.disabled = true;
                loadEventImage();
            } else {
                submitWonderBtn.disabled = false;
                eventContainer.innerHTML = '';
            }

        } catch (error) {
            errorMsg.textContent = 'Ошибка при получении данных раунда: ' + error.message;
        }
    };

    const updateWonderStatus = async (id, status) => {
        try {
            const response = await fetch(`http://localhost:8000/wonders/${id}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status })
            });

            if (!response.ok) throw new Error('Не удалось обновить статус чуда');
            await fetchRoundDetails();
        } catch (error) {
            errorMsg.textContent = 'Ошибка при обновлении статуса чуда: ' + error.message;
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

    submitWonderBtn.addEventListener('click', async () => {
        const newWonderName = newWonderNameInput.value.trim();

        console.log(newWonderName)

        if (newWonderName) {
            try {
                const response = await fetch(`http://localhost:8000/rounds/${roundId}/make_wonder?wonderName=${newWonderName}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                });

                if (response.ok) {
                    errorMsg.style.color = 'green';
                    errorMsg.textContent = 'Заявка на чудо успешно создана!';
                    newWonderNameInput.value = '';
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

    fetchRoundDetails();
});
