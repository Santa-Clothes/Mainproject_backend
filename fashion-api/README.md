# 👗 Fashion API - AI 기반 패션 추천 시스템

AI 이미지 분석(FastAPI)과 벡터 유사도 검색(PostgreSQL pgvector)을 활용하여 사용자의 취향에 맞는 패션 아이템을 추천해 주는 Spring Boot 기반 백엔드 서비스입니다.

---

## 🚀 핵심 기능 (Core Features)

### 1. 비주얼 서치 (Visual Search)
- 사용자가 업로드한 의류 이미지를 분석하여 디자인과 스타일이 유사한 상품을 추천합니다.
- **FastAPI**를 연동하여 이미지 벡터(Embedding)를 추출합니다.
- **pgvector (Cosine Similarity)**를 사용하여 100만 건 이상의 데이터 중 가장 유사한 상위 상품을 실시간으로 검색합니다.

### 2. 하이브리드 추천 엔진
- **외부 상품(Naver)**: 네이버 쇼핑 데이터 기반의 대중적인 아이템 추천.
- **자사 상품(Internal)**: 서비스 자체 DB(NineOunce) 내 보유 상품 추천.

### 3. 지능형 회원 서비스
- **OAuth2 통합 로그인**: Google, Naver, Kakao 소셜 로그인 연단.
- **JWT 기반 보안**: 데이터 무결성을 보장하는 토큰 기반 인증 체계.
- **관심 상품(Save Products)**: 마음에 드는 상품 저장 및 관리 (N+1 성능 최적화 완료).

---

## 🛠 Tech Stack & Technical Decisions

### Backend Framework & Language
- **Java 21**: 최신 LTS 버전인 Java 21의 **Virtual Threads**를 활용하여 수천 개의 동시 요청을 가볍게 처리하며, 고성능 동시성 프로그래밍을 구현했습니다.
- **Spring Boot 3.4.1**: 안정적인 아키텍처와 다양한 스타터 의존성을 통해 생산성을 극대화하고, 레벨 3 성숙도의 RESTful API를 구축했습니다.

### Security & Authentication
- **Spring Security (Stateless)**: 서버의 확장성을 위해 세션을 사용하지 않는 Stateless 아키텍처를 채택했습니다.
- **OAuth2 & JWT**: 
  - **OAuth2**: Google, Naver, Kakao API를 연동하여 사용자 접근성을 높였습니다.
  - **JWT (JSON Web Token)**: 인증된 요청을 검증하기 위한 토큰 기반 인증을 직접 구현했으며, 필터 수준에서 보안을 강화했습니다.

### AI & Vector Search Engine
- **FastAPI (Python)**: PyTorch/TensorFlow 기반의 AI 모델을 서빙하기 위해 비동기 처리에 특화된 FastAPI를 별도 엔진으로 두었습니다.
- **PostgreSQL & pgvector**: 
  - 정형 데이터와 벡터 데이터를 하나의 DB에서 관리하여 시스템 복잡도를 낮췄습니다.
  - **HNSW(Hierarchical Navigable Small World)** 인덱스를 적용하여 1,000차원 이상의 고차원 벡터 검색 시에도 밀리초(ms) 단위의 응답 속도를 확보했습니다.

### External Integration & Optimization
- **Spring WebFlux (WebClient)**:
  - 기존의 동기식 `RestTemplate` 대신 **Non-blocking** 방식의 `WebClient`를 사용하여 FastAPI와의 통신 중 스레드 차단을 방지했습니다.
  - 커넥션 풀링(Reactor Netty) 설정을 세밀하게 튜닝하여 리소스 소모를 최소화했습니다.
- **Supabase Storage**: 이미지 파일을 안전하게 보관하고 전역 CDN을 통해 빠르게 서빙하기 위해 Supabase의 클라우드 스토리지를 연동했습니다.

### Productivity Tools
- **Project Lombok**: 보일러플레이트 코드를 줄여 도메인 모델의 가독성을 높였습니다.
- **SpringDoc (Swagger UI)**: 협업을 위해 `OpenAPI 3.1` 스펙의 인터랙티브 API 문서를 자동 생성합니다.

---

## ⚡ 주요 성능 최적화 사항

최근 업데이트를 통해 시스템의 응답 속도와 안정성을 대폭 개선했습니다.

- **병렬 처리 (Parallelism)**: `CompletableFuture`를 활용하여 외부 상품과 자사 상품 검색을 동시에 수행 (응답 시간 40% 단축).
- **커넥션 풀링 (Connection Pooling)**: `Reactor Netty` 커넥션 풀 설정을 통해 FastAPI/Supabase 통신 오버헤드 최소화.
- **N+1 쿼리 해결**: 관심 상품 목록 조회 시 `findAllById`를 활용하여 DB 접근 횟수를 1/N로 절감.

---

## 📖 API Documentation
서버 구동 후 다음 주소에서 Swagger UI를 통해 상세한 API 명세를 확인할 수 있습니다.
- `http://localhost:8080/swagger-ui.html`

---

## 🏗️ Getting Started

### Prerequisites
- JDK 21
- Gradle
- PostgreSQL 15+ (with pgvector)

### Build & Run
```bash
./gradlew clean build
./gradlew bootRun
```
