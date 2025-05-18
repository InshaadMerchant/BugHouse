# 📱 BugHouse Mobile – CSE Student Success Center App

## 🧠 Team Name
**BugHouse**

## 🗓 Timeline
**Fall 2024 – Spring 2025**

## 👥 Team Members
- Aniv Surana – Computer Science  
- Araohat Kokate – Computer Science  
- Athrva Arora – Computer Science  
- Inshaad Merchant – Computer Science  
- Milan Singh – Computer Science  
- Prakhyat Chaube – Computer Science  

---

## 📖 Abstract
The **CSE Student Success Center Mobile Application** transforms how students at the University of Texas at Arlington’s Computer Science & Engineering Department access academic support. Designed as a responsive Android application, the app streamlines tutoring session booking, attendance tracking, and performance reporting for students, tutors, and administrators. It aims to increase academic engagement and improve student success outcomes through a centralized, accessible platform.

---

## 🏛 Background
Computer Science students often face challenging coursework requiring personalized, timely support. Traditional methods—manual bookings and walk-ins—lack efficiency and scalability. The BugHouse mobile app addresses these inefficiencies by:
- Enabling real-time scheduling  
- Automating attendance via ID scan  
- Collecting structured feedback  
- Generating performance reports  

This results in a more structured, efficient, and accessible tutoring process.

---

## ✅ Project Requirements
- 🔐 Role-based authentication (Student, Tutor, Admin)  
- 📅 Session booking and scheduling  
- 🆔 Attendance tracking via ID cards  
- ⭐ Session feedback & rating system  
- 📊 Customizable report generation (Admin)  
- 🛠 Admin control of roles and schedules  
- 🔄 Scalable to support concurrent users  
- 🔒 Compliance with security and privacy standards  

---

## ⚙️ Design Constraints
- **Scalability**: Supports peak usage without performance loss  
- **Interoperability**: Integrates with UTA’s ID & auth systems  
- **Offline Support**: Key features available with limited connectivity  
- **Data Integrity**: Accurate actions during network instability  
- **Google Play Compliance**: Meets publishing requirements  
- **Version Management**: Compatible with Android 7.0+  

---

## 🏗 Engineering Standards
- **WCAG 2.1** – Accessibility  
- **ISO/IEC 27001** – Secure data handling  
- **ISO/IEC 25010** – Software product quality  
- **ISO/IEC 25012** – Data quality for reports  
- **ISO/IEC 24760** – Secure identity management  
- **ISO/IEC 27017** – Cloud service security  
- **ISO/IEC 19778** – Mobile performance  
- **ISO 9241-210 & 110** – Usability design  
- **RFC 3339** – Timestamp formatting  
- **IEEE 829** – Software testing documentation  
- **NIST SP 800-63B** – Authentication lifecycle  
- **OAuth 2.0 + MD5** – Secure token management  
- **DRY Principle** – Clean, reusable code  
- **Google Play Compliance** – Store-ready features  

---

## 🧱 System Architecture
Modular six-layered system:
1. **User Authentication Layer** – Login, token handling  
2. **Schedule Management Layer (Admin)** – Sessions & profiles  
3. **User Management Layer (Admin)** – Roles and validation  
4. **Session Management Layer** – Booking, notifications  
5. **Attendance Tracking Layer** – ID-based check-ins  
6. **Report Generation Layer** – Custom reports for admins  

Each layer uses secure interfaces ensuring data integrity across operations.

---

## 🚀 Results
- ✅ Core features (booking, calendar, attendance) are complete  
- 📱 Frontend tested across Android devices and screen sizes  
- 🔐 Auth, session scheduling, and tutor modules fully functional  
- ⚙ Admin backend under active development  
- 🧪 Positive internal testing feedback on UI and functionality  

---

## 🔮 Future Work
- Finish backend admin module  
- Expand to iOS  
- Add AI-powered tutor suggestions  
- Integrate with UTA’s official student systems  
- Launch real-time feedback dashboards  

---

## 📁 Project Files
- 📝 Project Charter  
- 📄 System Requirements Specification  
- 🏗 Architectural Design Specification  
- ⚙️ Detailed Design Specification  
- 🖼 Poster  
- 📦 Closeout Materials  

---

## 📚 References
1. [Microsoft Authentication Library (MSAL)](https://learn.microsoft.com/en-us/azure/active-directory/develop/msal-overview)  
2. [Retrofit - Square Inc.](https://square.github.io/retrofit/)  
3. [PostgreSQL Documentation](https://www.postgresql.org/docs/)  
4. [Express.js API Guide](https://expressjs.com/en/4x/api.html)  
5. [UTA Student Success Center](https://www.uta.edu/academics/schools-colleges/engineering/students/success-center)  

---

> 🏫 Developed as part of the Senior Design Capstone at the University of Texas at Arlington (CSE Dept.)
