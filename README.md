# 🚀 Infinity Payment Router (Hybrid Gateway Engine)

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green?style=for-the-badge&logo=spring)
![System Design](https://img.shields.io/badge/System-Fault_Tolerant-blue?style=for-the-badge)
![Database](https://img.shields.io/badge/H2-In_Memory_DB-purple?style=for-the-badge)

> **A high-performance payment routing engine designed to achieve 100% transaction success rates.**

In the Fintech industry, bank server downtimes (HTTP 500 errors) are the #1 cause of lost revenue. **Infinity Payment Router** solves this by implementing a **Fault-Tolerant Hybrid Architecture**. It automatically detects primary bank failures and instantly reroutes transactions to a backup aggregator (Razorpay/OTP Model) without the user noticing.

---

## 📸 Project Previews

| **1. The "Infinity" UI** | **2. Smart Fault Tolerance** |
|:---:|:---:|
| A modern, glassmorphism-based interface with 3D card animations. | If HDFC fails (500 Error), the system **auto-switches** to the OTP Gateway. |
| ![Home](screenshots/app-home.png) | ![OTP](screenshots/app-otp.png) |

### 3. Live Merchant Dashboard
Real-time traffic monitoring. The **Yellow Badge** indicates a transaction that was "rescued" by the Hybrid Logic.
![Dashboard](screenshots/app-dashboard.png)

---

## 🧠 System Architecture (How it Works)

The system uses a **Failover Routing Strategy** to determine the best path for money movement.

```mermaid
graph TD;
    A[User Clicks Pay] -->|Initiate Transaction| B(Router Controller);
    B --> C{Check HDFC Health};
    C -- Server UP (200 OK) --> D[Route via Direct Bank API];
    C -- Server DOWN (500 Error) --> E[⚠️ Fallback Triggered];
    E --> F[Route via Backup Gateway];
    D --> G[Transaction Success];
    F --> H[OTP Verification];
    H --> G;