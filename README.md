# 📌 Reddit Clone API

🚀 **Reddit Clone API** is a backend service for managing posts, comments, voting, and authentication. Built using **Spring Boot**, it features **Spring Security for authentication** and **OpenAPI documentation** for easy API exploration.

## ✨ Features

- 🏗️ **Spring Boot 3 & Java 17**
- 🔐 **Spring Security with JWT authentication**
- 📄 **OpenAPI 3 documentation with Swagger UI**
- 🗂️ **RESTful API endpoints for posts, comments, voting, and authentication**
- 🛠️ **Spring Data JPA with MySQL/PostgreSQL support**

## 🏗️ Tech Stack

- ☕ **Java 17**
- 🌱 **Spring Boot 3**
- 🔐 **Spring Security & JWT**
- 🗄️ **Spring Data JPA & Hibernate**
- 🐘 **PostgreSQL (or MySQL)**
- 📄 **Swagger UI & OpenAPI**

## 🚀 Installation & Setup

### 1️⃣ Clone the repository
```bash
  git clone https://github.com/yourusername/reddit-clone-api.git
  cd reddit-clone-api
```

### 2️⃣ Configure the database
Update **`application.properties`**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reddit
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```

### 3️⃣ Run the application
```bash
mvn spring-boot:run
```

### 4️⃣ API Documentation (Swagger UI)
Once the server is running, open:
📄 **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🛠️ API Endpoints

### 🔐 Authentication
- `POST /api/auth/signup` → Register a user
- `POST /api/auth/login` → Authenticate user
- `POST /api/auth/logout` → Logout user
- `POST /api/auth/refresh/token` → Refresh JWT token

### 📬 Posts
- `GET /api/posts` → Get all posts
- `POST /api/posts` → Create a new post
- `GET /api/posts/{id}` → Get post by ID

### 💬 Comments
- `GET /api/comments?postId={id}` → Get comments for a post
- `POST /api/comments` → Add a new comment

### 📊 Voting
- `POST /api/votes` → Upvote/downvote a post

## 🔐 Security
- Uses **Spring Security** with **JWT Authentication**
- Protects API routes from unauthorized access

## 📜 License
Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## 🤝 Contributing
PRs are welcome! Feel free to **fork** and **contribute**.

---

🚀 **Enjoy building with Reddit Clone API!** 🚀
