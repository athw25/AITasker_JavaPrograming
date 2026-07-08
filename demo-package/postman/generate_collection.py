import json, uuid

def gid():
    return str(uuid.uuid4())

def item(name, method, path, body=None, params=None, auth_var=None, prerequest=None, tests=None, description=""):
    req = {
        "name": name,
        "request": {
            "method": method,
            "header": [{"key": "Content-Type", "value": "application/json"}] if body else [],
            "url": {
                "raw": "{{baseUrl}}" + path + (("?" + "&".join(f"{k}={v}" for k,v in params.items())) if params else ""),
                "host": ["{{baseUrl}}"],
                "path": [p for p in path.strip("/").split("/") if p],
                "query": [{"key": k, "value": str(v)} for k, v in (params or {}).items()]
            },
            "description": description
        },
        "response": []
    }
    if body is not None:
        req["request"]["body"] = {"mode": "raw", "raw": json.dumps(body, indent=2)}
    if auth_var:
        req["request"]["auth"] = {"type": "bearer", "bearer": [{"key": "token", "value": "{{" + auth_var + "}}", "type": "string"}]}
    events = []
    if prerequest:
        events.append({"listen": "prerequest", "script": {"type": "text/javascript", "exec": prerequest.split("\n")}})
    if tests:
        events.append({"listen": "test", "script": {"type": "text/javascript", "exec": tests.split("\n")}})
    if events:
        req["event"] = events
    return req

def folder(name, items):
    return {"name": name, "item": items}

save_client_test = "\n".join([
    "if (pm.response.code === 200) {",
    "    var data = pm.response.json();",
    "    if (data.id) pm.collectionVariables.set('lastId', data.id);",
    "}"
])

collection = {
    "info": {
        "_postman_id": gid(),
        "name": "AITasker - Full API Collection",
        "description": "Bo API day du cho AITasker AI Marketplace - covers Auth, Job, Proposal, Project, Milestone, Delivery, Payment, Withdrawal, Expert, Portfolio, Service, Review, Notification, Message, Dispute, Admin, AI, File, Email, Audit Log.",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "variable": [
        {"key": "baseUrl", "value": "{{baseUrl}}"},
        {"key": "lastId", "value": ""}
    ],
    "item": []
}

# ---------- AUTH ----------
auth_items = [
    item("Register Client 1", "POST", "/api/auth/register",
         body={"fullName": "Nguyen Van Client", "email": "client1@aitasker.com", "password": "Demo@123", "role": "CLIENT"}),
    item("Register Expert 1", "POST", "/api/auth/register",
         body={"fullName": "Pham Minh Expert", "email": "expert1@aitasker.com", "password": "Demo@123", "role": "EXPERT"}),
    item("Register Client 2", "POST", "/api/auth/register",
         body={"fullName": "Tran Thi Client", "email": "client2@aitasker.com", "password": "Demo@123", "role": "CLIENT"}),
    item("Login Client 1", "POST", "/api/auth/login",
         body={"email": "client1@aitasker.com", "password": "Demo@123"},
         tests="\n".join([
             "var data = pm.response.json();",
             "pm.collectionVariables.set('clientToken', data.token);",
             "pm.collectionVariables.set('clientRefreshToken', data.refreshToken);"
         ])),
    item("Login Expert 1", "POST", "/api/auth/login",
         body={"email": "expert1@aitasker.com", "password": "Demo@123"},
         tests="\n".join([
             "var data = pm.response.json();",
             "pm.collectionVariables.set('expertToken', data.token);",
             "pm.collectionVariables.set('expertRefreshToken', data.refreshToken);"
         ])),
    item("Login Admin", "POST", "/api/auth/login",
         body={"email": "admin@aitasker.com", "password": "Admin@123"},
         tests="\n".join([
             "var data = pm.response.json();",
             "pm.collectionVariables.set('adminToken', data.token);"
         ])),
    item("Refresh Token (Client)", "POST", "/api/auth/refresh",
         body={"refreshToken": "{{clientRefreshToken}}"}),
    item("Logout (Client)", "POST", "/api/auth/logout",
         body={"refreshToken": "{{clientRefreshToken}}"}),
    item("Get Current User - Client", "GET", "/api/users/me", auth_var="clientToken"),
    item("Get Current User - Expert", "GET", "/api/users/me", auth_var="expertToken"),
]
collection["item"].append(folder("01. Auth & Users", auth_items))


# ---------- JOB ----------
job_items = [
    item("Create Job", "POST", "/api/jobs", auth_var="clientToken",
         body={"title": "Facebook Sales Chatbot", "description": "Xay dung chatbot ban hang tich hop Facebook Messenger.",
               "budget": 3000.00, "deadline": "2026-12-31", "requiredSkills": "Python, LangChain, OpenAI API"},
         tests="\n".join(["var d = pm.response.json();", "pm.collectionVariables.set('jobId', d.id);"])),
    item("Get All Jobs", "GET", "/api/jobs", auth_var="expertToken"),
    item("Get My Jobs (Client)", "GET", "/api/jobs/me", auth_var="clientToken"),
    item("Get Job By Id", "GET", "/api/jobs/{{jobId}}", auth_var="clientToken"),
    item("Search Jobs", "GET", "/api/jobs/search", auth_var="expertToken",
         params={"keyword": "chatbot", "minBudget": 1000, "maxBudget": 5000}),
    item("Update Job", "PUT", "/api/jobs/{{jobId}}", auth_var="clientToken",
         body={"title": "Facebook Sales Chatbot (Updated)", "description": "Mo ta cap nhat.",
               "budget": 3200.00, "deadline": "2026-12-31", "requiredSkills": "Python, LangChain, OpenAI API"}),
    item("Delete Job", "DELETE", "/api/jobs/{{jobId}}", auth_var="clientToken"),
]
collection["item"].append(folder("02. Job Marketplace", job_items))

# ---------- PROPOSAL ----------
proposal_items = [
    item("Create Proposal", "POST", "/api/proposals", auth_var="expertToken",
         body={"jobId": "{{jobId}}", "bidAmount": 2800.00, "duration": 14,
               "coverLetter": "Toi co 5 nam kinh nghiem xay chatbot AI, cam ket dung tien do."},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('proposalId', data.id);"])),
    item("Get Proposals By Job", "GET", "/api/proposals/job/{{jobId}}", auth_var="clientToken",
         params={"page": 0, "size": 10}),
    item("Get My Proposals (Expert)", "GET", "/api/proposals/me", auth_var="expertToken"),
    item("Accept Proposal", "PUT", "/api/proposals/{{proposalId}}/accept", auth_var="clientToken"),
    item("Reject Proposal", "PUT", "/api/proposals/{{proposalId}}/reject", auth_var="clientToken"),
    item("Withdraw Proposal", "PUT", "/api/proposals/{{proposalId}}/withdraw", auth_var="expertToken"),
]
collection["item"].append(folder("03. Proposal", proposal_items))

# ---------- PROJECT ----------
project_items = [
    item("Create Project from Proposal", "POST", "/api/projects", auth_var="clientToken",
         body={"proposalId": "{{proposalId}}", "startDate": "2026-07-10", "endDate": "2026-08-10"},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('projectId', data.id);"])),
    item("Get Project By Id", "GET", "/api/projects/{{projectId}}", auth_var="clientToken"),
    item("Get My Projects (all roles)", "GET", "/api/projects/me", auth_var="clientToken"),
    item("Get Client Projects", "GET", "/api/projects/client", auth_var="clientToken"),
    item("Get Expert Projects", "GET", "/api/projects/expert", auth_var="expertToken"),
    item("Admin Update Project", "PUT", "/api/projects/{{projectId}}", auth_var="adminToken",
         body={"status": "ACTIVE"}),
]
collection["item"].append(folder("04. Project", project_items))

# ---------- MILESTONE & DELIVERY ----------
milestone_items = [
    item("Create Milestone", "POST", "/api/milestones", auth_var="clientToken",
         body={"projectId": "{{projectId}}", "title": "Milestone 1 - Demo", "description": "Ban demo dau tien",
               "amount": 1400.00, "dueDate": "2026-07-20"},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('milestoneId', data.id);"])),
    item("Get Milestones By Project", "GET", "/api/milestones/project/{{projectId}}", auth_var="clientToken"),
    item("Update Milestone", "PUT", "/api/milestones/{{milestoneId}}", auth_var="clientToken",
         body={"title": "Milestone 1 - Updated", "description": "Cap nhat mo ta", "amount": 1500.00, "dueDate": "2026-07-25"}),
    item("Submit Milestone Delivery", "PUT", "/api/milestones/{{milestoneId}}/submit", auth_var="expertToken",
         body={"fileUrl": "https://drive.google.com/demo-v1", "note": "Ban demo lan 1"}),
    item("Reject Milestone", "PUT", "/api/milestones/{{milestoneId}}/reject", auth_var="clientToken",
         params={"reason": "Chua dat yeu cau"}),
    item("Approve Milestone", "PUT", "/api/milestones/{{milestoneId}}/approve", auth_var="clientToken"),
    item("Release Milestone Payment", "PUT", "/api/milestones/{{milestoneId}}/release-payment", auth_var="clientToken"),
    item("Submit Delivery (direct)", "POST", "/api/deliveries", auth_var="expertToken",
         body={"milestoneId": "{{milestoneId}}", "fileUrl": "https://drive.google.com/demo-v2", "note": "Ban demo lan 2"}),
    item("Get Delivery History", "GET", "/api/deliveries/milestone/{{milestoneId}}", auth_var="clientToken"),
]
collection["item"].append(folder("05. Milestone & Delivery", milestone_items))

# ---------- PAYMENT & WITHDRAWAL ----------
payment_items = [
    item("Deposit Escrow", "POST", "/api/payments/deposit", auth_var="clientToken",
         body={"projectId": "{{projectId}}", "amount": 1400.00},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('paymentId', data.id);"])),
    item("Release Payment (by paymentId body)", "PUT", "/api/payments/release", auth_var="clientToken",
         body={"paymentId": "{{paymentId}}"}),
    item("Release Payment (by path)", "PUT", "/api/payments/{{paymentId}}/release", auth_var="clientToken"),
    item("Get Transactions By Payment", "GET", "/api/payments/transactions/{{paymentId}}", auth_var="clientToken"),
    item("Get My Transactions (Expert)", "GET", "/api/payments/transactions/me", auth_var="expertToken"),
    item("Refund Payment (Admin)", "PUT", "/api/payments/{{paymentId}}/refund", auth_var="adminToken",
         params={"reason": "Dispute resolved"}),
    item("Request Withdrawal", "POST", "/api/withdrawals", auth_var="expertToken",
         body={"amount": 500.00}),
    item("Get My Withdrawals", "GET", "/api/withdrawals/me", auth_var="expertToken"),
]
collection["item"].append(folder("06. Payment & Withdrawal", payment_items))

# ---------- EXPERT / PORTFOLIO / SERVICE ----------
expert_items = [
    item("Get My Expert Profile", "GET", "/api/experts/me", auth_var="expertToken"),
    item("Update My Expert Profile", "PUT", "/api/experts/me", auth_var="expertToken",
         body={"fullName": "Pham Minh Expert", "title": "Senior AI Engineer",
               "skills": "Python, PyTorch, LLM", "experienceYears": 5, "hourlyRate": 50.00}),
    item("Add Portfolio", "POST", "/api/experts/portfolio", auth_var="expertToken",
         body={"projectName": "Chatbot cho Shop ABC", "description": "Da trien khai chatbot Facebook Messenger",
               "projectUrl": "https://github.com/example/chatbot"},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('portfolioId', data.id);"])),
    item("Get Portfolio By Expert", "GET", "/api/experts/portfolio/1", auth_var="expertToken"),
    item("Delete Portfolio", "DELETE", "/api/experts/portfolio/{{portfolioId}}", auth_var="expertToken"),
    item("Create Service Package", "POST", "/api/services", auth_var="expertToken",
         body={"packageName": "AI Resume Parser - Basic", "price": 200.00, "deliveryDays": 5},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('serviceId', data.id);"])),
    item("Get All Service Packages", "GET", "/api/services", auth_var="clientToken"),
    item("Update Service Package", "PUT", "/api/services/{{serviceId}}", auth_var="expertToken",
         body={"packageName": "AI Resume Parser - Pro", "price": 250.00, "deliveryDays": 7}),
    item("Delete Service Package", "DELETE", "/api/services/{{serviceId}}", auth_var="expertToken"),
]
collection["item"].append(folder("07. Expert / Portfolio / Service", expert_items))

# ---------- REVIEW ----------
review_items = [
    item("Create Review", "POST", "/api/reviews", auth_var="clientToken",
         body={"revieweeId": "{{expertUserId}}", "projectId": "{{projectId}}", "rating": 5,
               "comment": "Lam viec chuyen nghiep", "type": "CLIENT_TO_EXPERT"}),
    item("Get Reviews By User", "GET", "/api/reviews/user/{{expertUserId}}", auth_var="clientToken"),
]
collection["item"].append(folder("08. Review", review_items))

# ---------- NOTIFICATION & MESSAGE ----------
notif_items = [
    item("Get My Notifications", "GET", "/api/notifications", auth_var="clientToken"),
    item("Mark Notification As Read", "PUT", "/api/notifications/1/read", auth_var="clientToken"),
    item("Get Project Messages", "GET", "/api/messages/project/{{projectId}}", auth_var="clientToken"),
]
collection["item"].append(folder("09. Notification & Message", notif_items))

# ---------- DISPUTE ----------
dispute_items = [
    item("Create Dispute", "POST", "/api/disputes", auth_var="clientToken",
         body={"projectId": "{{projectId}}", "reason": "San pham ban giao khong dung thoa thuan"},
         tests="\n".join(["var d = pm.response.json();", "var data = d.data || d;", "pm.collectionVariables.set('disputeId', data.id);"])),
    item("Get All Disputes (Admin)", "GET", "/api/disputes", auth_var="adminToken", params={"status": "OPEN"}),
    item("Resolve Dispute (Admin)", "PUT", "/api/disputes/{{disputeId}}/resolve", auth_var="adminToken",
         body={"status": "RESOLVED_REFUND", "resolution": "Da hoan tien cho Client"}),
]
collection["item"].append(folder("10. Dispute", dispute_items))

# ---------- AI ----------
ai_items = [
    item("AI Job Assistant", "POST", "/api/ai/job-assistant", auth_var="clientToken",
         body={"prompt": "Toi muon chatbot ban hang Facebook"}),
    item("AI Service Generator", "POST", "/api/ai/service-generator", auth_var="expertToken",
         body={"prompt": "Toi lam AI Resume Parser"}),
    item("AI Recommend Experts", "GET", "/api/ai/recommend-experts/{{jobId}}", auth_var="clientToken"),
]
collection["item"].append(folder("11. AI Modules", ai_items))

# ---------- FILE & EMAIL ----------
file_items = [
    item("Upload File (multipart - configure manually in Postman UI)", "POST", "/api/files/upload", auth_var="expertToken"),
    item("Download File By Id", "GET", "/api/files/1", auth_var="expertToken"),
    item("Delete File", "DELETE", "/api/files/1", auth_var="expertToken"),
    item("Send Test Email (Admin)", "POST", "/api/email/test", auth_var="adminToken",
         body={"to": "test@example.com", "subject": "Test Email", "body": "Noi dung test email tu AITasker."}),
]
collection["item"].append(folder("12. File & Email", file_items))

# ---------- ADMIN ----------
admin_items = [
    item("Get All Users", "GET", "/api/admin/users", auth_var="adminToken"),
    item("Ban User", "PUT", "/api/admin/users/2/ban", auth_var="adminToken"),
    item("Unban User", "PUT", "/api/admin/users/2/unban", auth_var="adminToken"),
    item("Get All Jobs (Admin)", "GET", "/api/admin/jobs", auth_var="adminToken"),
    item("Delete Job (Admin)", "DELETE", "/api/admin/jobs/1", auth_var="adminToken"),
    item("Admin Dashboard", "GET", "/api/admin/dashboard", auth_var="adminToken"),
    item("Admin Analytics", "GET", "/api/admin/analytics", auth_var="adminToken"),
    item("Admin Reports", "GET", "/api/admin/reports", auth_var="adminToken"),
    item("Admin Get All Transactions", "GET", "/api/admin/payments/transactions", auth_var="adminToken"),
    item("Admin Get Withdrawals By Status", "GET", "/api/admin/payments/withdrawals", auth_var="adminToken",
         params={"status": "PENDING"}),
    item("Admin Approve Withdrawal", "PUT", "/api/admin/payments/withdrawals/1/approve", auth_var="adminToken"),
    item("Admin Audit Logs", "GET", "/api/admin/audit-logs", auth_var="adminToken"),
    item("Admin Test Endpoint", "GET", "/api/admin/test", auth_var="adminToken"),
]
collection["item"].append(folder("13. Admin", admin_items))

with open("AITasker.postman_collection.json", "w", encoding="utf-8") as f:
    json.dump(collection, f, indent=2, ensure_ascii=False)

environment = {
    "id": gid(),
    "name": "AITasker - Local",
    "values": [
        {"key": "baseUrl", "value": "http://localhost:8080", "enabled": True},
        {"key": "clientToken", "value": "", "enabled": True},
        {"key": "clientRefreshToken", "value": "", "enabled": True},
        {"key": "expertToken", "value": "", "enabled": True},
        {"key": "expertRefreshToken", "value": "", "enabled": True},
        {"key": "adminToken", "value": "", "enabled": True},
        {"key": "jobId", "value": "", "enabled": True},
        {"key": "proposalId", "value": "", "enabled": True},
        {"key": "projectId", "value": "", "enabled": True},
        {"key": "milestoneId", "value": "", "enabled": True},
        {"key": "paymentId", "value": "", "enabled": True},
        {"key": "portfolioId", "value": "", "enabled": True},
        {"key": "serviceId", "value": "", "enabled": True},
        {"key": "disputeId", "value": "", "enabled": True},
        {"key": "expertUserId", "value": "2", "enabled": True}
    ]
}
with open("AITasker.postman_environment.json", "w", encoding="utf-8") as f:
    json.dump(environment, f, indent=2, ensure_ascii=False)

print("Generated collection with", sum(len(f["item"]) for f in collection["item"]), "requests across", len(collection["item"]), "folders")
