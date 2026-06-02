````md
# AITasker

## AI Service Marketplace Platform

AITasker là nền tảng Marketplace chuyên biệt kết nối các doanh nghiệp, startup và nhà sáng lập với các chuyên gia Trí tuệ nhân tạo (AI) nhằm hỗ trợ triển khai các giải pháp AI trong thực tế.

Hệ thống được xây dựng như một cầu nối giữa khách hàng có nhu cầu ứng dụng AI và các chuyên gia AI có khả năng cung cấp dịch vụ. Ngoài chức năng marketplace truyền thống, AITasker còn tích hợp các công cụ AI hỗ trợ tự động hóa quá trình tạo yêu cầu, đề xuất chuyên gia phù hợp và nâng cao hiệu quả ghép nối giữa hai bên.

---

# 1. Project Overview

## Problem Statement

Hiện nay việc ứng dụng AI vào doanh nghiệp đang phát triển nhanh chóng. Tuy nhiên nhiều doanh nghiệp gặp các khó khăn như:

- Thiếu chuyên môn kỹ thuật về AI
- Khó xác định yêu cầu dự án
- Khó tìm được chuyên gia phù hợp
- Thiếu cơ chế quản lý dự án và thanh toán đáng tin cậy

Trong khi đó các chuyên gia AI cũng gặp các vấn đề:

- Khó tiếp cận khách hàng tiềm năng
- Thiếu nền tảng chuyên biệt cho dịch vụ AI
- Khó xây dựng uy tín cá nhân

AITasker được xây dựng nhằm giải quyết các vấn đề trên thông qua một nền tảng marketplace chuyên biệt dành riêng cho dịch vụ AI.

---

# 2. Project Objectives

Mục tiêu của hệ thống:

- Kết nối khách hàng với chuyên gia AI phù hợp
- Hỗ trợ tạo yêu cầu công việc bằng AI
- Hỗ trợ đề xuất chuyên gia thông minh
- Quản lý toàn bộ vòng đời dự án AI
- Tích hợp hệ thống thanh toán ký quỹ (Escrow)
- Hỗ trợ giao tiếp thời gian thực giữa các bên

---

# 3. Primary Actors

## Client

Bao gồm:

- Doanh nghiệp
- Startup
- Nhà sáng lập

Chức năng:

- Đăng công việc
- Thuê chuyên gia
- Quản lý dự án
- Thanh toán
- Đánh giá chuyên gia

## AI Expert

Bao gồm:

- Freelancer AI
- AI Engineer
- AI Consultant

Chức năng:

- Tạo hồ sơ chuyên gia
- Đăng dịch vụ AI
- Gửi proposal
- Thực hiện dự án
- Nhận thanh toán

## Administrator

Chức năng:

- Quản lý hệ thống
- Quản lý người dùng
- Quản lý giao dịch
- Giải quyết tranh chấp
- Theo dõi hoạt động nền tảng

---

# 4. Main Features

## Authentication & Authorization

- User Registration
- User Login
- JWT Authentication
- Role-based Authorization

Roles:

- CLIENT
- EXPERT
- ADMIN

### User Management

- Quản lý hồ sơ cá nhân
- Hồ sơ chuyên gia
- Portfolio
- Kỹ năng chuyên môn

### Job Marketplace

- Tạo Job Post
- Cập nhật Job Post
- Xóa Job Post
- Tìm kiếm công việc
- Quản lý công việc

### Proposal Management

- Gửi Proposal
- Chấp nhận Proposal
- Từ chối Proposal
- Quản lý ứng tuyển

### AI Service Marketplace

- Đăng dịch vụ AI
- Tìm kiếm dịch vụ
- Thuê dịch vụ

### Project Management

- Tạo dự án
- Quản lý tiến độ
- Theo dõi trạng thái
- Quản lý Deliverables

### Milestone Management

- Tạo Milestone
- Nộp sản phẩm
- Phê duyệt Milestone
- Từ chối Milestone

### Payment & Escrow

- Deposit
- Escrow
- Release Payment
- Refund
- Transaction History

### Review System

- Đánh giá chuyên gia
- Đánh giá khách hàng
- Rating System

### Realtime Communication

- Realtime Chat
- Project Discussion
- Notification

---

# 5. AI Modules

## AI Job Assistant

Hỗ trợ:

- Sinh Job Title
- Sinh Job Description
- Gợi ý Required Skills
- Gợi ý Budget

## AI Expert Recommendation

Đề xuất chuyên gia dựa trên:

- Kỹ năng
- Kinh nghiệm
- Đánh giá
- Tỷ lệ hoàn thành dự án

## AI Service Generator

Tự động sinh mô tả dịch vụ AI.

---

# 6. System Architecture

Hệ thống được xây dựng theo mô hình Layered Architecture.

```text
Client
    ↓
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
SQL Server Database
````

---

# 7. Technology Stack

## Backend

* Java 24
* Spring Boot 3
* Maven

## Database

* SQL Server 2022
* SQL Server Management Studio (SSMS)

## ORM

* Spring Data JPA
* Hibernate

## Security

* Spring Security
* JWT Authentication

## Realtime Communication

* WebSocket
* STOMP

## AI Integration

* OpenAI API

## Version Control

* Git
* GitHub

## API Testing

* Postman

---

# 8. Project Structure

```text
com.aitasker

├── config
├── security
├── common
├── exception
├── auth
├── user
├── job
├── proposal
├── service_marketplace
├── project
├── milestone
├── payment
├── review
├── message
├── websocket
├── notification
├── ai
└── admin
```

---

# 9. Database Core Entities

* User
* ExpertProfile
* JobPost
* Proposal
* AiService
* Project
* Milestone
* Payment
* Transaction
* Review
* Message
* Notification

---

# 10. Development Workflow

## Branch Strategy

```text
main
│
develop
│
├── feature-auth
├── feature-user
├── feature-job
├── feature-project
├── feature-payment
└── feature-chat
```

## Commit Convention

Feature:

```bash
feat: create login api
```

Fix:

```bash
fix: resolve jwt validation bug
```

Refactor:

```bash
refactor: optimize project service
```

Docs:

```bash
docs: update README
```

---

# 11. Future Enhancements

* AI-powered dispute resolution
* AI project risk prediction
* Recommendation engine optimization
* Dashboard analytics
* Cloud deployment (AWS/GCP)
* Mobile application

---

# 12. Team

Project: AITasker

Course: Java Programming Project

Development Process:

1. Requirement Analysis
2. System Design
3. Technical Foundation Setup
4. Backend Development
5. Testing & Integration

---

# 13. License

This project is developed for educational and research purposes.

```
```
