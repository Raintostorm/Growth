# 🚀 Real-time Chat Application - Hướng dẫn sử dụng

## 📋 Tổng quan

Ứng dụng chat thời gian thực được xây dựng với:
- **Backend**: Java Spring Boot với WebSocket, JWT Authentication
- **Frontend**: React TypeScript với Tailwind CSS
- **Database**: H2 (development) / PostgreSQL (production)
- **Real-time**: WebSocket với STOMP protocol

## 🏃‍♂️ Chạy ứng dụng

### 1. Backend (Spring Boot)

```bash
# Di chuyển vào thư mục backend
cd backend

# Chạy backend
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080/api`

### 2. Frontend (React)

```bash
# Mở terminal mới và di chuyển vào frontend
cd frontend

# Chạy frontend
npm start
```

Frontend sẽ chạy tại: `http://localhost:3000`

## 🎯 Sử dụng ứng dụng

### 1. Đăng ký tài khoản

1. Truy cập `http://localhost:3000`
2. Click "Register" 
3. Điền thông tin:
   - Username: tên đăng nhập
   - Email: email hợp lệ
   - Password: mật khẩu (tối thiểu 6 ký tự)
   - Display Name: tên hiển thị

### 2. Đăng nhập

1. Click "Login"
2. Nhập username và password
3. Click "Sign In"

### 3. Sử dụng chat

1. **Tạo phòng chat mới**:
   - Click "Create Room" trong sidebar
   - Nhập tên phòng và mô tả
   - Click "Create"

2. **Tham gia phòng chat**:
   - Click vào phòng trong danh sách
   - Bắt đầu gửi tin nhắn

3. **Gửi tin nhắn**:
   - Nhập tin nhắn vào ô input
   - Nhấn Enter hoặc click "Send"

4. **Xem người dùng online**:
   - Danh sách hiển thị trong sidebar
   - Có indicator xanh cho người online

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `GET /api/auth/me` - Lấy thông tin user hiện tại
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Đăng xuất

### Chat
- `GET /api/chat/rooms` - Lấy danh sách phòng chat
- `POST /api/chat/rooms` - Tạo phòng chat mới
- `GET /api/chat/rooms/{roomId}` - Lấy thông tin phòng
- `POST /api/chat/rooms/{roomId}/join` - Tham gia phòng
- `POST /api/chat/rooms/{roomId}/leave` - Rời phòng
- `GET /api/chat/rooms/{roomId}/messages` - Lấy tin nhắn
- `POST /api/chat/rooms/{roomId}/messages` - Gửi tin nhắn
- `GET /api/chat/users/online` - Lấy danh sách user online

### WebSocket
- **Endpoint**: `/ws`
- **Public messages**: `/topic/public`
- **Room messages**: `/topic/room/{roomId}`
- **Private messages**: `/user/queue/private`

## 🛠️ Development

### Backend Development

```bash
cd backend

# Compile
mvn clean compile

# Run tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# Check code quality
mvn pmd:check
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build

# Check code quality
npm run lint
```

## 📚 Documentation

Chi tiết về các technical concepts được giải thích trong thư mục `backend/docs/`:

- [01-PROJECT-OVERVIEW.md](backend/docs/01-PROJECT-OVERVIEW.md) - Tổng quan dự án
- [02-SPRING-BOOT-BASICS.md](backend/docs/02-SPRING-BOOT-BASICS.md) - Spring Boot cơ bản
- [03-JWT-AUTHENTICATION.md](backend/docs/03-JWT-AUTHENTICATION.md) - JWT Authentication
- [04-WEBSOCKET-REALTIME.md](backend/docs/04-WEBSOCKET-REALTIME.md) - WebSocket Real-time
- [05-REDIS-CACHING.md](backend/docs/05-REDIS-CACHING.md) - Redis Caching
- [06-TESTING-STRATEGY.md](backend/docs/06-TESTING-STRATEGY.md) - Testing Strategy

## 🎓 Learning Objectives

Sau khi hoàn thành dự án này, bạn sẽ có thể:

1. **Hiểu và áp dụng** các design patterns trong Spring Boot
2. **Thực hiện** JWT authentication và authorization
3. **Xây dựng** real-time applications với WebSocket
4. **Sử dụng** Spring Data JPA cho database operations
5. **Viết** unit tests và integration tests
6. **Thiết lập** Spring Security configuration
7. **Áp dụng** best practices trong Spring Boot development

## 🔗 Liên kết

- [Backend README](backend/README.md) - Hướng dẫn backend
- [Frontend README](frontend/README.md) - Hướng dẫn frontend
- [Main README](README.md) - Tổng quan dự án

## 🚨 Troubleshooting

### Lỗi thường gặp

1. **Backend không start được**:
   - Kiểm tra Java 17 đã cài đặt
   - Kiểm tra port 8080 có bị chiếm không
   - Chạy `mvn clean compile` trước

2. **Frontend không connect được backend**:
   - Kiểm tra backend đã chạy chưa
   - Kiểm tra CORS configuration
   - Kiểm tra API URL trong frontend

3. **WebSocket không hoạt động**:
   - Kiểm tra WebSocket endpoint `/ws`
   - Kiểm tra STOMP configuration
   - Kiểm tra browser console để xem lỗi

### Logs và Debug

- **Backend logs**: Xem trong terminal chạy `mvn spring-boot:run`
- **Frontend logs**: Xem trong browser console (F12)
- **Database**: Truy cập H2 console tại `http://localhost:8080/api/h2-console`

---

**Lưu ý**: Dự án này được thiết kế để học tập, có thể mở rộng và cải thiện theo nhu cầu thực tế.
