This system manages a capstone project workflow.
Students submit proposals, supervisors approve and create projects, milestones and comments track progress, and admins oversee users and projects.

Mention:

MVC architecture

Role-based access

JDBC persistence


“The system enforces role‑based access and follows real academic workflows.”



One‑Page System Flow
🔹 1. Authentication
User registers & logs in

Session stores User object (id, role)

🔹 2. Student
Submits proposal (summary + supervisorId)*********************

Proposal status = PENDING

Proposal assigned to supervisor

🔹 3. Supervisor
Sees only assigned proposals

Approves proposal

System:

Marks proposal approved

Creates project

Assigns student + supervisor

🔹 4. Project Work
Supervisor adds milestones

Supervisor comments

Student uploads final project file

🔹 5. Senior Supervisor
Views approved projects (oversight only)

🔹 6. Admin
Views users & projects

System monitoring (no daily interaction)

