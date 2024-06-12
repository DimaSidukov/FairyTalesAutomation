# Project information

## Авторизация
###  POST `/auth/login`
Метод для входа в систему

Query params:
  * `login` - логин пользователя
  * `password` - пароль пользователя

Return type:
  * `boolean` - удалось войти или нет

Пример запроса:
`/auth/login?login=admin&password=admin`

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
            "user_id": 2,
            "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
            "role": 1
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

### GET `/rounds`
Метод для получения списка раундов

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
  
### GET `rounds/need_wonder`
Методя для получения списка раундов, которым требуется создать чудо

Return type:
* `[Round]`

### POST  `rounds/{roundId}/make_wonder`
Метод для создания чуда для конкретного раунда

Path variables:
* `roundId` - идентификатор раунда

Query params:
* `wonderName` - название чуда

Return type:
* `Round`

Пример запроса:
` /rounds/:roundId/make_wonder?wonderName=shining chain`

### POST `rounds/verify_wonder`
Метод для верификации чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/verify_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`

### POST `rounds/approve_wonder`
Метод для одобрения чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/approve_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`

### POST `rounds/reject_wonder`
Метод для отклонения чуда

Query params:
* wonderId

Return type:
* `Wonder`

Пример запроса: `rounds/reject_wonder?wonderId=b9e4607f-103d-494f-9143-5145deb58bf2`


## Типы данных
### Round
```json
 {
      "id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
      "name": "first round eva",
      "participants": [
          {
              "user_id": 2,
              "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
              "role": 0
          }
      ],
      "status": 7,
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
  "user_id": 2,
  "round_id": "8d601064-cac4-4ead-a8ec-d76f4cc0ff70",
  "role": 1
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


