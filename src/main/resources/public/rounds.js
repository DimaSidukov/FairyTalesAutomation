document.addEventListener('DOMContentLoaded', async function() {
    if (localStorage.getItem('authenticated') !== 'true') {
        window.location.href = 'index.html';
        return;
    }

    const roundsContainer = document.getElementById('rounds-container');
    const createRoundBtn = document.getElementById('create-round-btn');
    const createRoundForm = document.getElementById('create-round-form');
    const submitRoundBtn = document.getElementById('submit-round-btn');
    const newRoundNameInput = document.getElementById('new-round-name');
    const errorMsg = document.getElementById('error-msg');

    let rounds = [];

    createRoundBtn.addEventListener('click', () => {
        createRoundForm.style.display = 'block';
    });

    const fetchRounds = async () => {
        try {
            const response = await fetch(
                'http://localhost:8000/rounds'
            );
            console.log(response);
            rounds = await response.json();

            console.log(rounds);

            if (rounds.length === 0) {
                roundsContainer.textContent = 'Список раундов пуст, необходимо создать раунд.';
            } else {
                const list = document.createElement('ul');
                rounds.forEach(round => {
                    const listItem = document.createElement('li');
                    const link = document.createElement('a');
                    link.href = `round_template.html?id=${round.id}`;
                    link.textContent = round.name;
                    listItem.appendChild(link);
                    list.appendChild(listItem);
                });
                roundsContainer.appendChild(list);
            }
        } catch (error) {
            console.log(error);
            roundsContainer.textContent = 'Ошибка при получении списка раундов.';
        }
    };

    await fetchRounds();

    submitRoundBtn.addEventListener('click', async () => {
        const newRoundName = newRoundNameInput.value.trim();

        if (newRoundName) {
            const isDuplicate = rounds.some(round => round.name.toLowerCase() === newRoundName.toLowerCase());

            if (isDuplicate) {
                errorMsg.style.color = 'red';
                errorMsg.textContent = 'Раунд с таким названием уже существует.';
                return;
            }

            try {
                const response = await fetch(`http://localhost:8000/rounds/create?name=${newRoundName}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                });

                if (response.ok) {
                    errorMsg.style.color = 'green';
                    errorMsg.textContent = 'Раунд успешно создан!';
                    createRoundForm.style.display = 'none';
                    newRoundNameInput.value = '';
                    roundsContainer.innerHTML = '';
                    await fetchRounds();
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
});
