# java-shareit

## Sprint-13

### 1. Реализован UserController с эндпоинтами:

- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PATCH /users/{id}`
- `DELETE /users/{id}`

### 2. Реализован ItemController с эндпоинтами:

- `POST /items`
- `PATCH /items/{itemId}`
- `GET /items/{itemId}`
- `GET /items`
- `GET /items/search`

## Sprint-14

### 1. Переход на БД с использование JPA

### 2. Реализован BookingController с эндпоинтами:

- `POST /bookings`
- `PATCH /bookings/{bookingId}?approved={approved}`
- `GET /bookings/{bookingId}`
- `GET /bookings?state={state}`
- `GET /bookings/owner?state={state}`

### 3. Доработан ItemController с эндпоинтами:

- `POST /items/{itemId}/comment`
- `GET /items/{itemId}`
- `GET /items`

## Sprint-15

### 1. Рерализован ItemRequestController с эндпоинтами:
- `POST /requests`
- `GET /requests`
- `GET /requests/all?from={from}&size={size}`
- `GET /requests/{requestId}`

### 2. Добавил опцию ответа на запрос:
- `POST /items`

### 3. Добавлил пагинацию к существующим эндпоинтам:
- `GET /items`
- `GET /items/search`
- `GET /bookings`
- `GET /bookings/owner`

### 4. Тестовое покрытие сервиса

## Sprint-16

### 1. Рефакторинг на многомодульный проект: gateway & server
### 2. Добавлена контеризация в Docker
### 3. Реализован модуль gateway с controller & client
