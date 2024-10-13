# Project information

## Пользователь
###  POST `/user/login`
Метод для входа в систему

Query params:
  * `login` - логин пользователя
  * `password` - пароль пользователя

Return type:
  * `User` - пользователь найден 
  * Если пользователь не найден:
  ```
    {
        "code": 404,
        "message": "There is no user with given credentials."
    }
  ```

Пример запроса:
`/user/login?login=admin&password=admin`

### POST `/user/signup`
Метод для регистрации

Query params:
  * `login` - логин пользователя
  * `password` - пароль пользователя
  * `name` - имя пользователя
  * `email` - почтовый ящик пользователя

Return type:
  * `User` - пользователь успешно зарегистрирован
  * Если пользователь с таким логином уже существует
  ```
  {
    "code": 400,
    "message": "User with given login already exists!"
  }
  ```

### POST `/user/{userId}/set_role`
Метод для установки роля пользователем

Path variables:
* `userId` - идентификатор пользователя

Query params:
* `preferred_role` - новая роль пользователя

Return type:
* `User`

Пример запроса: `user/:userId/set_role`

### GET `/user/all`
Метод для получения списка всех пользователей с информацией о них

Return type:
* `User`

### POST `/user/{userId}/delete`
Метод для удаления пользователя

Return type:
* true - Если удалось удалить пользователя
* Если пользователь участвует в активных раундах
  ```
  {
    "code": 405,
    "message": "Not possible to delete account at the moment. You should finish all active rounds first!"
  }
  ```

Пример запроса: `user/:userId/delete`

### POST `/user/{userId}/block`
Метод для блокировки пользователя

Return type:
* `User`

Пример запроса: `/user/:userId/block`

### POST `/user/{userId}/unblock`
Метод для разблокировки пользователя

Return type:
* `User`

Пример запроса: `/user/:userId/unblock`

## Раунды
### POST `/rounds/create`
Метод для создания раунда

Query params:
* `name` - название раунда

Return type: 
* `Round`

Пример запроса:
`rounds/create?name=first round eva`

### POST `/{roundId}/select_users`
Метод для выбора участников, принимающих участие в раунде

Path variables:
* `roundId` - идентификатор раунда

Request Body:
* `players` - список участников (`[Player]`), которые участвуют в раунде
  ```json
    [
        {
            "user_id": "64426298-e04b-4d35-8d2e-a88a006f3c2f",
            "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
            "role": "king"
        }
    ]

Return type: 
* `Round`

Пример запроса:
`rounds/:roundId/select_users`

### GET `/rounds/{roundId}`
Метод для получения информации о раунде

Path variables:
* `roundId` - идентификатор раунда

Return type:
* `Round`

Пример запроса:
`rounds/:roundId`

### GET `/rounds/all`
Метод для получения списка ВСЕХ раундов (включая завершённые)

Return type:
* `[Round]`

### GET `/rounds/active`
Метод для получения списка АКТИВНЫХ раундов
Return type:
* `[Round]`

### GET `/rounds/available_players`
Метод для получения списка игроков, которые участвуют в раундах

*P. S. - возможно здесь лучше возвращать список всех пользователей, чтобы из них формировать игроков?*

Return type:
* `[Player]`
  
### GET `/rounds/wonders`
Метод для получения списка всех созданных чудес (независимо от раунда, в котором они используются)

Return type:
* `[Wonder]`

### GET `/rounds/wonders_awaiting_approval`
Метод для получения списка чудес, ожидающих утверждения

Return type:
* `[Wonder]`
  
### GET `/rounds/need_wonder`
Методя для получения списка раундов, которым требуется создать чудо

Return type:
* `[Round]`

### POST  `/rounds/{roundId}/make_wonder`
Метод для создания чуда для конкретного раунда

Path variables:
* `roundId` - идентификатор раунда

Query params:
* `wonderName` - название чуда

Return type:
* `Round`

Пример запроса:
` /rounds/:roundId/make_wonder?wonderName=shining chain`

### POST `/rounds/verify_wonder`
Метод для верификации чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/verify_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`

### POST `/rounds/approve_wonder`
Метод для одобрения чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/approve_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`

### POST `/rounds/reject_wonder`
Метод для отклонения чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/reject_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`


## Типы данных
### User
```json
  {
      "id": "69a89a57-65d7-4e8a-806b-9b67ca302a24",
      "name": "SarahConnor",
      "email": "sarah@resistance.com",
      "createdAt": "2024-10-05 21:06:44",
      "preferredRole": "princess_swan",
      "isBanned": false
  }
```

### Round
```json
 {
      "id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
      "name": "first round eva",
      "participants": [
          {
              "user_id": "e0471522-9e53-44d7-ba4d-a9271ebb9c1b",
              "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
              "role": "princess_swan"
          }
      ],
      "status": "second_wonder_rejected",
      "wonders": [
          {
              "id": "6c961f7c-0941-45fd-af5f-7fc669f620f6",
              "name": "singing cat",
              "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
              "is_verified": false,
              "is_approved": true,
              "created_for_stage": 1
          },
          {
              "id": "b9e4607f-103d-494f-9143-5145deb58bf2",
              "name": "shining chain",
              "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
              "is_verified": true,
              "is_approved": false,
              "created_for_stage": 2
          }
      ]
  }
```

### Player
```json
{
  "user_id": "f98585af-0ed4-441e-9d4a-ce4745d2db12",
  "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
  "role": "auditor"
}
```

### Wonder
```json
{
  "id": "6c961f7c-0941-45fd-af5f-7fc669f620f6",
  "name": "singing cat",
  "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
  "is_verified": false,
  "is_approved": true,
  "created_for_stage": 1
}
```

Поле role соответствует следующим состояниям:

| Состояние        | Значение      |
|------------------|---------------|
| Царь             | king          |
| Аудитор          | auditor       |
| Принцесса Лебедь | princess_swan |
| Князь Гвидон     | prince_guidon | 

Поле status соответствует следующим состояниям:

| Состояние                              | Значение                     |
|----------------------------------------|------------------------------|
| Не запущен (не начат)                  | not_started                  |
| Запущен (начат)                        | started                      |
| Ожидается удтверждение первого чуда    | await first wonder approval  |
| Первое чудо утверждено                 | first wonder approved        |
| Первое чудо отклонено                  | first wonder rejected        |
| Ожидается удтверждение второго чуда    | await second wonder approval |
| Второе чудо утверждено                 | second wonder approved       |
| Второе чудо отклонено                  | second wonder rejected       |
| Ожидается удтверждение третьего чуда   | await third wonder approval  |
| Третье чудо утверждено                 | third wonder approved        |
| Третье чудо отклонено                  | third wonder rejected        |
| Ожидается удтверждение четвёртого чуда | await last wonder approval   |
| Четвёртое чудо утверждено              | last wonder approved         |
| Четвёртое чудо отклонено               | last wonder rejected         |
| Завершён                               | finished                     |

## Примечания

- Поле status есть только у раунда. С помощью этого регулируется состояние раунда, чтобы можно было определить, на каком этапе оно сейчас находится. Например, два чуда прошли проверку и третье на ожидании, или третье чудо было отклонено. Это нужно для того, чтобы хранить общее состояние о раунде, то есть как далёк раунд от завершения
- У каждого чуда есть поле `created_for_stage`, которое характеризует, для какого порядкового номера чуда данное чудо создано. Также у чудес есть поля `is_verified` и `is_approved`. Таким образом, чудес может быть сколько угодно, и они могут создаваться для одних и тех же этапов (порядовое число). Но утверждено для каждого этапа может быть только одно (`isApproved = true`)


