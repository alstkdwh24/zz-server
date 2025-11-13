# 🏨 여기어때 숙박 예약 시스템

## 📌 프로젝트 개요

- **프로젝트명**: 여기 어때

- **개발기간**: 2025.05 ~ 2025.10

- **프로젝트 형태**: 팀 프로젝트 (2인)

- **목표**: 숙소 등록부터 예약/결제까지 통합 관리 가능한 예약 플랫폼 구현

---

## 👥 팀 구성 및 역할

| 이름  | 역할 | 담당 기능                                                                        |
|-----|------|------------------------------------------------------------------------------|
| 조민상 | **프론트엔드 & 회원 관리 담당** | 회원 인증/인가(Spring Security, JWT,Oauth2) 구현,Theymleaf 기반 UI/UX 설계, 숙소·예약 페이지 구현 |
| 양경빈 | **백엔드 총괄** | 숙소·객실·예약·결제 등 핵심 비즈니스 로직 및 테스트 코드 전면 구현,인증/인가 로직(페어 코딩 참여)                                     |

---

## ⚙️ 기술 스택

| 분류 | 기술                               |
|------|----------------------------------|
| Language | Java 17                          |
| Framework | Spring Boot 3.x, Spring Data JPA |
| Database | H2DB, Redis                      |
| Infra | AWS EC2                          |
| Tools | IntelliJ, GitHub                 |
| Build & Deploy | Gradle, GitHub Actions           |
| Test | JUnit5, Mockito                  |

---

## 🚀 주요 기능

### 🏠 숙소 관리
- 숙소 등록, 수정, 삭제, 상세조회
- Address 임베디드 타입 + Enum(AttributeConverter) 적용

### 🛏 객실 관리 (Rooms)
- 객실 이미지(RoomImages), 편의시설(RoomAmenities) 포함
- UUID 기반 식별자 및 이미지 다중 업로드 처리

### 📅 예약 관리 (Reservation)
- 예약 중복 방지 (비관적 락 적용)
- 예약 취소 및 결제 내역 연동

### 💳 결제 (Payment)
- PortOne 결제 API 연동
- 결제 완료 후 예약 상태 자동 갱신

---