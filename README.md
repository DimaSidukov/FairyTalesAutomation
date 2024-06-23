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

Поле role соответствует следующим состояниям:

| Состояние        | Значение |
|------------------|----------|
| Царь             | 0        |
| Аудитор          | 1        |
| Принцесса Лебедь | 2        |

Поле status соответствует следующим состояниям:

| Состояние                              | Значение |
|----------------------------------------|----------|
| Не запущен (не начат)                  | 0        |
| Запущен (начат)                        | 1        |
| Ожидается удтверждение первого чуда    | 2        |
| Первое чудо утверждено                 | 3        |
| Первое чудо отклонено                  | 4        |
| Ожидается удтверждение второго чуда    | 5        |
| Второе чудо утверждено                 | 6        |
| Второе чудо отклонено                  | 7        |
| Ожидается удтверждение третьего чуда   | 8        |
| Третье чудо утверждено                 | 9        |
| Третье чудо отклонено                  | 10       |
| Ожидается удтверждение четвёртого чуда | 11       |
| Четвёртое чудо утверждено              | 12       |
| Четвёртое чудо отклонено               | 13       |
| Завершён                               | 14       |

## Примечания

- Поле status есть только у раунда. С помощью этого регулируется состояние раунда, чтобы можно было определить, на каком этапе оно сейчас находится. Например, два чуда прошли проверку и третье на ожидании, или третье чудо было отклонено. Это нужно для того, чтобы хранить общее состояние о раунде, то есть как далёк раунд от завершения
- У каждого чуда есть поле `created_for_stage`, которое характеризует, для какого порядкого номера чуда данное чудо создано. Также у чудес есть поля `is_verified` и `is_approved`. Таким образом, чудес может быть сколько угодно, и они могут создаваться для одних и тех же этапов (порядовое число). Но утверждено для каждого этапа может быть только одно (`isApproved = true`)


