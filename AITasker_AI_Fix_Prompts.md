# AITasker — AI IDE Fix Prompts
# NOT READY → READY WITH LIMITATIONS → READY
# Dùng với: Cursor Agent / GitHub Copilot Edits / Windsurf
# Thực hiện ĐÚNG THỨ TỰ. Mỗi prompt là một lần chat riêng với Agent.

---

## ══════════════════════════════════════
## PHASE 1 — BUILD FIX (bắt buộc làm trước)
## ══════════════════════════════════════

---

### PROMPT 1A — Xóa file duplicate và stub vô dụng

```
Delete the following files permanently from the project. Do not replace them with anything:

1. src/main/java/com/aitasker/admin/controller/AdminController.java
   Reason: Empty stub (29 bytes). Duplicate of auth/controller/AdminController.java which already has @RequestMapping("/api/admin").

2. src/main/java/com/aitasker/user/repository/ExpertProfileRepository.java
   Reason: Empty stub (33 bytes). Duplicate of expert/repository/ExpertProfileRepository.java.

3. src/main/java/com/aitasker/security/PasswordConfig.java
   Reason: Empty stub (24 bytes). PasswordEncoder bean already declared in SecurityConfig.java.

After deleting, verify no other file imports from these three paths.
```

---

### PROMPT 1B — Sửa 17 stub file còn lại thành Java hợp lệ

```
The following files contain only a comment (e.g. "// AdminService.java") and are NOT valid Java.
Add a minimal valid Java class/interface to each so the project compiles.
Do NOT add any logic. Do NOT add any methods. Just make them valid Java.

FILE 1: src/main/java/com/aitasker/admin/service/AdminService.java
Replace entire content with:
package com.aitasker.admin.service;
public interface AdminService {}

FILE 2: src/main/java/com/aitasker/admin/dashboard/DashboardStatistics.java
Replace entire content with:
package com.aitasker.admin.dashboard;
public class DashboardStatistics {}

FILE 3: src/main/java/com/aitasker/auth/mapper/AuthMapper.java
Replace entire content with:
package com.aitasker.auth.mapper;
public interface AuthMapper {}

FILE 4: src/main/java/com/aitasker/common/util/FileUtils.java
Replace entire content with:
package com.aitasker.common.util;
public class FileUtils { private FileUtils() {} }

FILE 5: src/main/java/com/aitasker/config/CorsConfig.java
Replace entire content with:
package com.aitasker.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class CorsConfig {}

FILE 6: src/main/java/com/aitasker/config/JacksonConfig.java
Replace entire content with:
package com.aitasker.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JacksonConfig {}

FILE 7: src/main/java/com/aitasker/config/ModelMapperConfig.java
Replace entire content with:
package com.aitasker.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ModelMapperConfig {}

FILE 8: src/main/java/com/aitasker/config/OpenApiConfig.java
Replace entire content with:
package com.aitasker.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {}

FILE 9: src/main/java/com/aitasker/config/WebConfig.java
Replace entire content with:
package com.aitasker.config;
import org.springframework.context.annotation.Configuration;
@Configuration
public class WebConfig {}

FILE 10: src/main/java/com/aitasker/user/service/UserService.java
Replace entire content with:
package com.aitasker.user.service;
public interface UserService {}

FILE 11: src/main/java/com/aitasker/user/controller/UserController.java
Replace entire content with:
package com.aitasker.user.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/users-admin")
public class UserController {}

FILE 12: src/main/java/com/aitasker/ai/openai/OpenAiClient.java
Replace entire content with:
package com.aitasker.ai.openai;
import org.springframework.stereotype.Component;
@Component
public class OpenAiClient {}

FILE 13: src/main/java/com/aitasker/ai/openai/OpenAiConfig.java
Replace entire content with:
package com.aitasker.ai.openai;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenAiConfig {}

FILE 14: src/main/java/com/aitasker/ai/assistant/JobAssistantService.java
Replace entire content with:
package com.aitasker.ai.assistant;
import org.springframework.stereotype.Service;
@Service
public class JobAssistantService {}

FILE 15: src/main/java/com/aitasker/ai/assistant/ServiceGeneratorService.java
Replace entire content with:
package com.aitasker.ai.assistant;
import org.springframework.stereotype.Service;
@Service
public class ServiceGeneratorService {}

FILE 16: src/main/java/com/aitasker/ai/recommendation/ExpertRecommendationService.java
Replace entire content with:
package com.aitasker.ai.recommendation;
import org.springframework.stereotype.Service;
@Service
public class ExpertRecommendationService {}

FILE 17: src/main/java/com/aitasker/ai/recommendation/MatchingService.java
Replace entire content with:
package com.aitasker.ai.recommendation;
import org.springframework.stereotype.Service;
@Service
public class MatchingService {}
```

---

### PROMPT 1C — Xóa wildcard import trong ProjectServiceImpl

```
File: src/main/java/com/aitasker/project/service/impl/ProjectServiceImpl.java

Find and DELETE this line (line 30):
    import com.aitasker.*;

Do not add any replacement. If the class still compiles after removal, leave it as is.
If removing this line causes any compile error (symbol not found), report exactly which symbol
is missing so we can add the correct specific import. Do not guess or add random imports.

After the fix, run: ./mvnw compile
Expected: BUILD SUCCESS
```

---

### PROMPT 1D — Sửa UserController mapping conflict

```
File: src/main/java/com/aitasker/auth/controller/UserController.java

This file already exists at auth/controller/UserController.java with @RequestMapping("/api/users").
The stub at user/controller/UserController.java was fixed in PROMPT 1B to use "/api/users-admin".

Verify there is no @RequestMapping conflict between:
  - com.aitasker.auth.controller.UserController  → @RequestMapping("/api/users")
  - com.aitasker.user.controller.UserController  → @RequestMapping("/api/users-admin")

If both exist and have different mappings, no action needed.
If they have the same mapping, change user.controller.UserController to @RequestMapping("/api/users-admin").
```

---

## ══════════════════════════════════════
## PHASE 2 — CORE FLOW FIX
## Điều kiện đạt: READY WITH LIMITATIONS
## ══════════════════════════════════════

---

### PROMPT 2A — Fix Role mismatch (CRITICAL)

```
PROBLEM: Role enum has AI_EXPERT but SecurityConfig checks for EXPERT → all Expert users get 403.
Also: ProjectSecurityService hardcodes "AI_EXPERT" string.

CHANGES REQUIRED:

1. File: src/main/java/com/aitasker/common/enums/Role.java
   Change:
     AI_EXPERT
   To:
     EXPERT

2. File: src/main/java/com/aitasker/security/ProjectSecurityService.java
   Find:
     if (hasRole(user, "AI_EXPERT")
   Replace with:
     if (hasRole(user, "EXPERT")

3. File: src/main/java/com/aitasker/auth/service/AuthServiceImpl.java
   The register method uses Role.valueOf(request.getRole().toUpperCase()).
   This is fine as-is — client must now send "EXPERT" instead of "AI_EXPERT".
   No code change needed here.

After changes, search entire codebase for "AI_EXPERT":
  grep -rn "AI_EXPERT" src/
Expected result: 0 occurrences.

VERIFICATION:
POST /api/auth/register with {"role":"EXPERT"} → 200 OK
POST /api/auth/login → 200 OK + JWT token
GET /api/expert/profile with Bearer token → 200 OK (not 403)
```

---

### PROMPT 2B — Fix ProposalRequestDTO missing duration field (CRITICAL)

```
PROBLEM: Proposal entity has @Column(nullable=false) Integer duration, but
ProposalRequestDTO has no duration field → DB constraint error on every submit.

CHANGES REQUIRED:

1. File: src/main/java/com/aitasker/proposal/dto/request/ProposalRequestDTO.java
   Add this field after the coverLetter field:

    @NotNull(message = "Thời gian hoàn thành không được để trống")
    @Min(value = 1, message = "Thời gian hoàn thành phải lớn hơn 0")
    private Integer duration;

   Also add getter and setter for duration (the class uses manual getters/setters pattern):
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

   Add import at top if missing:
    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotNull;

2. File: src/main/java/com/aitasker/proposal/service/ProposalService.java
   In the createProposal() method, after proposal.setCoverLetter(request.getCoverLetter()); add:
    proposal.setDuration(request.getDuration());

VERIFICATION:
POST /api/proposals with body:
  {"jobId":1,"bidAmount":5000000,"coverLetter":"test","duration":30}
→ 200 OK, no DataIntegrityViolationException
```

---

### PROMPT 2C — Fix ExpertProfile — thêm FK tới User (HIGH)

```
PROBLEM: ExpertProfile entity has no reference to User. ExpertServiceImpl.getMyProfile()
calls expertRepository.findById(currentUserId) assuming ExpertProfile.id == User.id (WRONG).

CHANGES REQUIRED:

1. File: src/main/java/com/aitasker/expert/entity/ExpertProfile.java
   Add this field (add imports too):

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_expert_profile_user")
    )
    private com.aitasker.user.entity.User user;

   Add Lombok getter/setter for user field (class already uses @Getter @Setter).
   Add import: import jakarta.persistence.ForeignKey;
   Add import: import jakarta.persistence.OneToOne;

2. File: src/main/java/com/aitasker/expert/repository/ExpertProfileRepository.java
   Add these two methods to the existing interface:

    import java.util.Optional;
    Optional<com.aitasker.expert.entity.ExpertProfile> findByUserId(Long userId);
    Optional<com.aitasker.expert.entity.ExpertProfile> findByUserEmail(String email);

3. File: src/main/java/com/aitasker/expert/service/impl/ExpertServiceImpl.java
   In getMyProfile(Long currentUserId), change:
     ExpertProfile profile = expertRepository.findById(currentUserId)
   To:
     ExpertProfile profile = expertRepository.findByUserId(currentUserId)

   In updateProfile(Long currentUserId, UpdateExpertProfileRequest request), change:
     ExpertProfile profile = expertRepository.findById(currentUserId)
   To:
     ExpertProfile profile = expertRepository.findByUserId(currentUserId)

4. File: src/main/java/com/aitasker/auth/service/AuthServiceImpl.java
   In the register() method, after userRepository.save(user); add:

    if (user.getRole() == com.aitasker.common.enums.Role.EXPERT) {
        com.aitasker.expert.entity.ExpertProfile profile = new com.aitasker.expert.entity.ExpertProfile();
        profile.setUser(user);
        profile.setFullName(request.getFullName());
        expertProfileRepository.save(profile);
    }

   Add field injection at top of AuthServiceImpl:
    private final com.aitasker.expert.repository.ExpertProfileRepository expertProfileRepository;

   (The @RequiredArgsConstructor will auto-inject it.)

VERIFICATION:
POST /api/auth/register {"role":"EXPERT",...} → 200 OK
CHECK DB: SELECT user_id FROM expert_profiles; → must have user_id value (not null)
GET /api/expert/profile (Bearer Expert JWT) → 200 OK with correct profile data
```

---

### PROMPT 2D — Fix ProposalController: lấy userId từ SecurityContext (HIGH)

```
PROBLEM: ProposalController receives expertId and clientId from @RequestParam.
This allows identity spoofing. Must read from JWT/SecurityContext per Project Security Rules.

File: src/main/java/com/aitasker/proposal/controller/ProposalController.java

Add this import at the top:
  import com.aitasker.security.userdetails.CustomUserDetails;
  import org.springframework.security.core.Authentication;

Replace the ENTIRE content of ProposalController with:

package com.aitasker.proposal.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import com.aitasker.proposal.dto.request.ProposalRequestDTO;
import com.aitasker.proposal.dto.response.ProposalResponseDTO;
import com.aitasker.proposal.service.ProposalService;
import com.aitasker.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    private Long getCurrentUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }

    @PostMapping
    public ApiResponse<ProposalResponseDTO> createProposal(
            @Valid @RequestBody ProposalRequestDTO request,
            Authentication authentication) {
        Long expertId = getCurrentUserId(authentication);
        return ApiResponse.success("Nộp đề xuất thành công",
                proposalService.createProposal(request, expertId));
    }

    @GetMapping("/job/{jobId}")
    public ApiResponse<PageResponse<ProposalResponseDTO>> getProposalsByJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Lấy danh sách đề xuất thành công",
                proposalService.getProposalsByJob(jobId, page, size));
    }

    @PutMapping("/{id}/accept")
    public ApiResponse<String> acceptProposal(
            @PathVariable Long id,
            Authentication authentication) {
        proposalService.acceptProposal(id, getCurrentUserId(authentication));
        return ApiResponse.success("Chấp nhận đề xuất thành công", null);
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<String> rejectProposal(
            @PathVariable Long id,
            Authentication authentication) {
        proposalService.rejectProposal(id, getCurrentUserId(authentication));
        return ApiResponse.success("Từ chối đề xuất thành công", null);
    }

    @PutMapping("/{id}/withdraw")
    public ApiResponse<String> withdrawProposal(
            @PathVariable Long id,
            Authentication authentication) {
        proposalService.withdrawProposal(id, getCurrentUserId(authentication));
        return ApiResponse.success("Rút đề xuất thành công", null);
    }
}

VERIFICATION:
POST /api/proposals (Bearer Expert A JWT, no expertId in URL)
→ 200 OK, proposal created with expert_id = Expert A's ID
SELECT expert_id FROM proposals ORDER BY created_at DESC; → must match Expert A
```

---

### ✅ CHECKPOINT: READY WITH LIMITATIONS

```
Run all 10 steps in order. ALL must pass before claiming READY WITH LIMITATIONS.

./mvnw clean compile                → BUILD SUCCESS (0 errors)
./mvnw spring-boot:run              → Started AitaskerApplication (no exception on startup)

POST /api/auth/register {"fullName":"Client","email":"c@t.com","password":"Test123!","role":"CLIENT"}  → 200 OK
POST /api/auth/register {"fullName":"Expert","email":"e@t.com","password":"Test123!","role":"EXPERT"}  → 200 OK
POST /api/auth/login {"email":"c@t.com","password":"Test123!"}   → 200 OK + clientToken
POST /api/auth/login {"email":"e@t.com","password":"Test123!"}   → 200 OK + expertToken

GET  /api/expert/profile        (Bearer expertToken)                         → 200 OK (NOT 403)
POST /api/jobs                  (Bearer clientToken, body has title+budget)  → 200 OK + jobId
POST /api/proposals             (Bearer expertToken, body has jobId+duration) → 200 OK + proposalId
PUT  /api/proposals/{id}/accept (Bearer clientToken)                         → 200 OK
GET  /api/projects              (Bearer clientToken)                         → 200 OK, project exists
```

---

## ══════════════════════════════════════
## PHASE 3 — FULL READY
## Thực hiện SAU khi đạt READY WITH LIMITATIONS
## ══════════════════════════════════════

---

### PROMPT 3A — Fix Review.createdAt duplicate column (HIGH)

```
PROBLEM: Review extends BaseEntity (which has createdAt), but Review also declares
its own private LocalDateTime createdAt field → Hibernate duplicate column mapping.

File: src/main/java/com/aitasker/review/entity/Review.java

1. DELETE this field entirely:
     private LocalDateTime createdAt;

2. DELETE this import if it becomes unused:
     import java.time.LocalDateTime;

File: src/main/java/com/aitasker/review/service/ReviewService.java

3. DELETE this line in the create() method:
     review.setCreatedAt(LocalDateTime.now());
   (BaseEntity @PrePersist handles this automatically)

4. In toResponse(), change:
     response.setCreatedAt(review.getCreatedAt());
   This still works because getCreatedAt() is now inherited from BaseEntity.

VERIFICATION:
POST /api/reviews {...} → 200 OK
SELECT id, created_at FROM reviews ORDER BY id DESC; → created_at is populated, no error
```

---

### PROMPT 3B — Fix Message entity: raw IDs → JPA Relationships (HIGH)

```
PROBLEM: Message entity uses raw Long projectId/senderId/receiverId instead of
JPA relationships. Violates Entity Rules.

File: src/main/java/com/aitasker/message/entity/Message.java

Replace the ENTIRE file content with:

package com.aitasker.message.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.project.entity.Project;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_message_project"))
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_message_sender"))
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_message_receiver"))
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime sentAt;

    private Boolean isRead;
}

File: src/main/java/com/aitasker/message/repository/MessageRepository.java
Replace findByProjectIdOrderBySentAtAsc with:
  List<Message> findByProject_IdOrderBySentAtAsc(Long projectId);

File: src/main/java/com/aitasker/message/service/MessageService.java
Update getMessagesByProject() to use project ID for lookup:
  return messageRepository.findByProject_IdOrderBySentAtAsc(projectId);

Update saveMessage() — build Message with proper relationships.
You will need to inject ProjectRepository and UserRepository to load Project and User by ID.

File: src/main/java/com/aitasker/message/controller/MessageController.java and
     src/main/java/com/aitasker/message/controller/ChatWebSocketController.java
Update any references to old raw ID fields (projectId, senderId, receiverId)
to use the new relationship accessors (message.getProject().getId(), etc.)

VERIFICATION:
SQL Server FK check:
SELECT OBJECT_NAME(fk.parent_object_id), col.name
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc ON fk.object_id=fkc.constraint_object_id
JOIN sys.columns col ON fkc.parent_column_id=col.column_id AND fkc.parent_object_id=col.object_id
WHERE OBJECT_NAME(fk.parent_object_id)='messages';
→ Must show: project_id, sender_id, receiver_id
```

---

### PROMPT 3C — Fix duplicate PaymentStatus enum (HIGH)

```
PROBLEM: Two PaymentStatus enums with different values:
- com.aitasker.common.enums.PaymentStatus (4 values: PENDING,HELD,RELEASED,REFUNDED)
- com.aitasker.payment.enums.PaymentStatus (5 values: adds FAILED)
Payment entity uses payment.enums.PaymentStatus. Keep that one.

STEP 1: Delete file
  src/main/java/com/aitasker/common/enums/PaymentStatus.java

STEP 2: Search for any import of the deleted enum:
  grep -rn "com.aitasker.common.enums.PaymentStatus" src/
  For each result found, change import to:
  import com.aitasker.payment.enums.PaymentStatus;

STEP 3: Run compile to confirm:
  ./mvnw compile → BUILD SUCCESS

VERIFICATION:
grep -rn "common.enums.PaymentStatus" src/
→ 0 results
```

---

### PROMPT 3D — Fix RuntimeException → Custom Exception (HIGH)

```
PROBLEM: JobService and ReviewService throw RuntimeException instead of Custom Exceptions.
GlobalExceptionHandler does not catch RuntimeException → returns 500 instead of 404.

File: src/main/java/com/aitasker/job/service/JobService.java

Add import at top:
  import com.aitasker.exception.ResourceNotFoundException;

Find ALL occurrences of:
  throw new RuntimeException("Job not found with id: " + id);
  throw new RuntimeException("User not found");
  throw new RuntimeException("Cannnot update a job that in IN_PROGRESS");

Replace with respectively:
  throw new ResourceNotFoundException("Không tìm thấy Job với id: " + id);
  throw new ResourceNotFoundException("Không tìm thấy người dùng");
  throw new com.aitasker.exception.BadRequestException("Không thể cập nhật Job đang IN_PROGRESS");

File: src/main/java/com/aitasker/review/service/ReviewService.java

Add import:
  import com.aitasker.exception.ResourceNotFoundException;

Find ALL:
  throw new RuntimeException("User not found.");
  throw new RuntimeException("Project not found.");
  throw new RuntimeException("Reviewee not found.");
  throw new RuntimeException("Reviews can only be created after project completion.");
  throw new RuntimeException("Only project participants can leave a review.");
  throw new RuntimeException("You have already reviewed this project.");
  throw new RuntimeException("You cannot review yourself.");
  throw new RuntimeException("Reviewee is not a participant of this project.");

Replace with:
  throw new ResourceNotFoundException("Không tìm thấy người dùng");
  throw new ResourceNotFoundException("Không tìm thấy Project");
  throw new ResourceNotFoundException("Không tìm thấy Reviewee");
  throw new com.aitasker.exception.BadRequestException("Chỉ có thể review khi Project đã hoàn thành");
  throw new com.aitasker.exception.ForbiddenException("Chỉ người tham gia Project mới được review");
  throw new com.aitasker.exception.BusinessException("Bạn đã review Project này rồi");
  throw new com.aitasker.exception.ForbiddenException("Không thể tự review chính mình");
  throw new com.aitasker.exception.ForbiddenException("Reviewee không phải thành viên của Project này");

File: src/main/java/com/aitasker/security/ProjectSecurityService.java
Find:
  throw new RuntimeException("Project not found");
Replace with:
  throw new com.aitasker.exception.ResourceNotFoundException("Không tìm thấy Project");

VERIFICATION:
GET /api/jobs/99999 (Bearer any JWT)
→ 404 Not Found, body: {"success":false,"message":"Không tìm thấy Job với id: 99999"}
NOT 500 Internal Server Error
```

---

### PROMPT 3E — Fix Controller/Service trả Entity trực tiếp → DTO (HIGH)

```
PROBLEM: NotificationController returns List<Notification> (entity).
AdminUserService returns List<User> (entity with password field).
Violates DTO Rules.

=== PART A: Notification ===

CREATE new file: src/main/java/com/aitasker/notification/dto/NotificationResponse.java

package com.aitasker.notification.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private Long recipientId;
    private String title;
    private String content;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
}

File: src/main/java/com/aitasker/notification/controller/NotificationController.java

Change return types:
  ResponseEntity<List<Notification>>  →  ResponseEntity<List<NotificationResponse>>
  ResponseEntity<Notification>        →  ResponseEntity<NotificationResponse>

In each method, map Notification to NotificationResponse using:
  NotificationResponse.builder()
      .id(n.getId())
      .recipientId(n.getRecipient().getId())
      .title(n.getTitle())
      .content(n.getContent())
      .type(n.getType())
      .read(n.isRead())
      .createdAt(n.getCreatedAt())
      .build()

Remove import of Notification entity from controller.

=== PART B: Admin User ===

CREATE new file: src/main/java/com/aitasker/user/dto/UserSummaryResponse.java

package com.aitasker.user.dto;

import com.aitasker.common.enums.Role;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserSummaryResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}

File: src/main/java/com/aitasker/admin/service/AdminUserService.java

Change getAllUsers() return type from List<User> to List<UserSummaryResponse>:
  public List<UserSummaryResponse> getAllUsers() {
      return userRepository.findAll().stream()
          .map(u -> UserSummaryResponse.builder()
              .id(u.getId())
              .name(u.getName())
              .email(u.getEmail())
              .role(u.getRole())
              .createdAt(u.getCreatedAt())
              .build())
          .toList();
  }

Add import: import com.aitasker.user.dto.UserSummaryResponse;

File: src/main/java/com/aitasker/admin/controller/AdminUserController.java
Update return type to List<UserSummaryResponse> accordingly.

VERIFICATION:
GET /api/notifications (Bearer JWT)
→ Response does NOT contain any "recipient" nested object
→ Response does NOT contain "password" field

GET /api/admin/users (Bearer Admin JWT) — if this endpoint exists
→ Response does NOT contain "password" field
```

---

### PROMPT 3F — Fix Milestone releasePayment: tích hợp Payment thực tế (HIGH)

```
PROBLEM: MilestoneServiceImpl.releasePayment() has TODO at line 137.
It sets milestone status to PAID but never calls PaymentServiceImpl to release escrow funds.
The PaymentRepository already has findByMilestoneId() available.

File: src/main/java/com/aitasker/milestone/service/impl/MilestoneServiceImpl.java

STEP 1: Add these field injections (class uses @RequiredArgsConstructor):
  private final com.aitasker.payment.repository.PaymentRepository paymentRepository;
  private final com.aitasker.payment.service.PaymentServiceImpl paymentServiceImpl;

STEP 2: In releasePayment() method, REPLACE the TODO comment and the code below it:

CURRENT CODE (lines around 137):
    // TODO Integrate the payment provider/escrow transaction before marking the milestone paid.
    milestone.setStatus(MilestoneStatus.PAID);

REPLACE WITH:
    // Release escrow payment for this milestone
    paymentRepository.findByMilestoneId(milestone.getId()).ifPresent(payment -> {
        if (payment.getStatus() == com.aitasker.payment.enums.PaymentStatus.HELD) {
            com.aitasker.payment.dto.ReleaseRequest releaseRequest = new com.aitasker.payment.dto.ReleaseRequest();
            releaseRequest.setPaymentId(payment.getId());
            paymentServiceImpl.release(releaseRequest, currentUser.getId());
        }
    });
    milestone.setStatus(MilestoneStatus.PAID);

The rest of the method (allPaid check, project status COMPLETED) stays unchanged.

STEP 3: The method already has @Transactional — keep it.

Add import:
  import com.aitasker.payment.enums.PaymentStatus;

VERIFICATION:
1. Create a Payment with status HELD for a specific milestone
2. Call PUT /api/milestones/{id}/release-payment (Bearer Client JWT)
3. Check DB:
   SELECT m.status, p.status FROM milestones m
   JOIN payments p ON p.milestone_id = m.id WHERE m.id = ?;
   → m.status = 'PAID', p.status = 'RELEASED'
```

---

### PROMPT 3G — Fix EscrowService.approveWithdrawal: ghi Transaction (HIGH)

```
PROBLEM: EscrowService.approveWithdrawal() approves withdrawal but does not create
a Transaction record → missing financial audit trail.

PROBLEM DETAIL: Transaction.payment has nullable=false currently, which blocks
creating a Transaction for withdrawal (no payment to reference).

File: src/main/java/com/aitasker/payment/entity/Transaction.java

STEP 1: Make payment nullable (withdrawal transactions have no direct payment):
Find:
  @JoinColumn(name = "payment_id", nullable = false)
Replace with:
  @JoinColumn(name = "payment_id", nullable = true)

File: src/main/java/com/aitasker/payment/service/EscrowService.java

STEP 2: In approveWithdrawal(), after withdrawal.setProcessedAt(LocalDateTime.now());
and BEFORE return withdrawalRepository.save(withdrawal); add:

    Transaction transaction = Transaction.builder()
        .payment(null)
        .type(com.aitasker.payment.enums.TransactionType.WITHDRAWAL)
        .amount(withdrawal.getAmount())
        .description("Withdrawal approved for Expert #"
            + withdrawal.getExpert().getId()
            + " — Withdrawal #" + withdrawalId)
        .build();
    transactionRepository.save(transaction);

STEP 3: Verify transactionRepository is already injected (it is, via @RequiredArgsConstructor).
Add import if missing:
  import com.aitasker.payment.enums.TransactionType;

VERIFICATION:
PUT /api/admin/withdrawals/{id}/approve (Bearer Admin JWT)
SELECT w.status, t.type, t.amount FROM withdrawals w
LEFT JOIN transactions t ON t.amount = w.amount AND t.type = 'WITHDRAWAL'
WHERE w.id = ?;
→ w.status = 'APPROVED', t.type = 'WITHDRAWAL'
```

---

### PROMPT 3H — Fix Money Rules: double/Double → BigDecimal (MEDIUM)

```
PROBLEM: Project Money Rules require BigDecimal for all monetary values.
Currently double/Double used in: ExpertProfile.hourlyRate, JobPostRequest.budget,
JobPostResponse.budget, ExpertProfileResponse.hourlyRate.

FILE 1: src/main/java/com/aitasker/expert/entity/ExpertProfile.java
Change:
  private double hourlyRate;
To:
  private java.math.BigDecimal hourlyRate;

FILE 2: src/main/java/com/aitasker/expert/dto/response/ExpertProfileResponse.java
Change:
  private double hourlyRate;
  public double getHourlyRate() { return hourlyRate; }
  public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
To:
  private java.math.BigDecimal hourlyRate;
  public java.math.BigDecimal getHourlyRate() { return hourlyRate; }
  public void setHourlyRate(java.math.BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

FILE 3: src/main/java/com/aitasker/expert/mapper/ExpertMapper.java
In toDto() method, change:
  response.setHourlyRate(entity.getHourlyRate());
No change needed if both sides are now BigDecimal.

In toEntity() method, update similarly if it sets hourlyRate.

FILE 4: src/main/java/com/aitasker/expert/dto/request/UpdateExpertProfileRequest.java
Find any hourlyRate field declared as double and change to BigDecimal.

FILE 5: src/main/java/com/aitasker/job/dto/JobPostRequest.java
Change:
  private Double budget;
To:
  private java.math.BigDecimal budget;
Remove manual BigDecimal conversion in JobService.

FILE 6: src/main/java/com/aitasker/job/dto/JobPostResponse.java
Change:
  private Double budget;
  private Long ClientId;   ← ALSO fix naming: ClientId → clientId
To:
  private java.math.BigDecimal budget;
  private Long clientId;

FILE 7: src/main/java/com/aitasker/job/service/JobService.java
In create() method, REMOVE:
  job.setBudget(request.getBudget() != null ? BigDecimal.valueOf(request.getBudget()) : null);
REPLACE WITH:
  job.setBudget(request.getBudget());

In update() method, similarly remove the BigDecimal.valueOf() conversion.

In toResponse() method, REMOVE:
  res.setBudget(job.getBudget() != null ? job.getBudget().doubleValue() : null);
REPLACE WITH:
  res.setBudget(job.getBudget());

VERIFICATION:
grep -rn "double\|Double" src/main/java/com/aitasker/ | grep -i "budget\|price\|amount\|rate\|fee"
→ 0 results (or only non-monetary uses of Double)
```

---

### PROMPT 3I — Fix Payment/Transaction/Withdrawal: kế thừa BaseEntity (MEDIUM)

```
PROBLEM: Payment, Transaction, Withdrawal manage their own id/@PrePersist
instead of extending BaseEntity. Violates Entity Rules.

FILE 1: src/main/java/com/aitasker/payment/entity/Payment.java
- Add: extends com.aitasker.common.entity.BaseEntity
- REMOVE these fields (already in BaseEntity):
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
- REMOVE @PrePersist and @PreUpdate methods
- REMOVE import jakarta.persistence.PrePersist, PreUpdate if unused
- Keep all other fields and relationships unchanged.

FILE 2: src/main/java/com/aitasker/payment/entity/Transaction.java
- Add: extends com.aitasker.common.entity.BaseEntity
- REMOVE:
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @PrePersist public void prePersist() { createdAt = LocalDateTime.now(); }
- Keep all other fields unchanged.

FILE 3: src/main/java/com/aitasker/payment/entity/Withdrawal.java
- Add: extends com.aitasker.common.entity.BaseEntity
- REMOVE:
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime requestedAt;
    @PrePersist public void prePersist() { requestedAt=...; status=...; }
- KEEP the requestedAt field BUT rename concept: BaseEntity has createdAt.
  For requestedAt: you can add a separate column or map createdAt as requestedAt.
  Simplest: add @Column(name="requested_at") private LocalDateTime requestedAt;
  and remove only the @PrePersist. Set requestedAt manually in EscrowService before save:
    withdrawal.setRequestedAt(java.time.LocalDateTime.now());
  Also set status before save in requestWithdrawal():
    withdrawal.setStatus(WithdrawalStatus.PENDING);
- Add import: import com.aitasker.common.entity.BaseEntity;

VERIFICATION:
./mvnw compile → BUILD SUCCESS
Check no entity has @Id or @GeneratedValue declared locally (BaseEntity provides them).
```

---

### PROMPT 3J — Fix Logger: System.out.println → SLF4J (MEDIUM)

```
PROBLEM: JwtFilter uses System.out.println for debug output. Not production-safe.

File: src/main/java/com/aitasker/security/jwt/JwtFilter.java

STEP 1: Add Lombok @Slf4j annotation to class:
  @Component
  @RequiredArgsConstructor
  @Slf4j    ← ADD THIS
  public class JwtFilter extends OncePerRequestFilter {

STEP 2: Replace ALL System.out.println calls:
  System.out.println("\n========== JWT FILTER ==========");   → log.debug("========== JWT FILTER ==========");
  System.out.println("URI: " + request.getRequestURI());      → log.debug("JWT Filter - URI: {}", request.getRequestURI());
  System.out.println("UserDetailsService NOT FOUND");         → log.warn("UserDetailsService NOT FOUND");
  System.out.println("Authorization Header: " + authHeader);  → log.debug("Authorization Header: {}", authHeader);
  System.out.println("No Bearer Token");                      → log.debug("No Bearer Token found");
  System.out.println("Token: " + token);                      → log.debug("Token received (first 10 chars): {}...", token.substring(0, Math.min(10, token.length())));
  System.out.println("Username from token: " + username);     → log.debug("Username from token: {}", username);
  System.out.println("UserDetails username: " + ...);         → log.debug("UserDetails username: {}", userDetails.getUsername());
  System.out.println("Authorities: " + ...);                  → log.debug("Authorities: {}", userDetails.getAuthorities());
  System.out.println("Token valid: " + valid);                → log.debug("Token valid: {}", valid);
  System.out.println("Authentication SET SUCCESS");            → log.debug("Authentication set for: {}", username);
  System.out.println("Token INVALID");                        → log.warn("Token INVALID for URI: {}", request.getRequestURI());
  System.out.println("Authentication already exists...");      → log.debug("Authentication already exists or username null");
  System.out.println("JWT Exception:");                       → log.error("JWT Exception occurred: {}", ex.getMessage());
  ex.printStackTrace();                                        → (delete this line — log.error above is enough)
  System.out.println("Authentication after filter: " + ...);  → log.debug("Auth after filter: {}", SecurityContextHolder.getContext().getAuthentication());
  System.out.println("========== END JWT FILTER ==========");  → log.debug("========== END JWT FILTER ==========");

Add import if not present: import lombok.extern.slf4j.Slf4j;

VERIFICATION:
grep -n "System.out.println" src/main/java/com/aitasker/security/jwt/JwtFilter.java
→ 0 results
```

---

### PROMPT 3K — Fix application.yaml: environment variables (MEDIUM)

```
PROBLEM: DB password and JWT secret are hardcoded in application.yaml.

File: src/main/resources/application.yaml

Replace the datasource and jwt sections with:

spring:
  application:
    name: aitasker
  datasource:
    url: jdbc:sqlserver://${DB_HOST:127.0.0.1}:${DB_PORT:1433};databaseName=${DB_NAME:AITasker};encrypt=false;trustServerCertificate=true
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

app:
  jwt:
    secret: ${JWT_SECRET:aitasker-jwt-secret-key-must-be-at-least-32-characters}
    expiration-ms: ${JWT_EXPIRATION_MS:86400000}

CREATE file: .env.example (in project root, next to pom.xml)
Contents:
DB_HOST=127.0.0.1
DB_PORT=1433
DB_NAME=AITasker
DB_USERNAME=sa
DB_PASSWORD=YourPassword
JWT_SECRET=your-secret-key-minimum-32-characters-long
JWT_EXPIRATION_MS=86400000

Verify .env is in .gitignore. If not, add it.

TEAM NOTE: Each member creates their own .env file locally (not committed).
For dev convenience, keep defaults pointing to local SQL Server.
```

---

### PROMPT 3L — Fix JobPostRequest validation + UserStatus field (MEDIUM)

```
=== PART A: JobPostRequest validation ===

File: src/main/java/com/aitasker/job/dto/JobPostRequest.java

Add validation annotations and change budget to BigDecimal:

package com.aitasker.job.dto;

import com.aitasker.common.enums.JobStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobPostRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotNull(message = "Ngân sách không được để trống")
    @DecimalMin(value = "0.01", message = "Ngân sách phải lớn hơn 0")
    private BigDecimal budget;

    @NotNull(message = "Hạn chót không được để trống")
    private LocalDate deadline;

    private String requiredSkills;
}

File: src/main/java/com/aitasker/job/controller/JobController.java
Ensure @Valid is present on all @RequestBody parameters. If missing, add it.

=== PART B: UserStatus field in User entity ===

File: src/main/java/com/aitasker/user/entity/User.java

Add this field:
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private com.aitasker.common.enums.UserStatus status = com.aitasker.common.enums.UserStatus.ACTIVE;

(UserStatus enum already exists in com.aitasker.common.enums.UserStatus)
Add import: import com.aitasker.common.enums.UserStatus;

File: src/main/java/com/aitasker/auth/service/AuthServiceImpl.java
In register() method, after user.setRole(...); add:
    user.setStatus(com.aitasker.common.enums.UserStatus.ACTIVE);

VERIFICATION:
./mvnw compile → BUILD SUCCESS
POST /api/jobs without title → 400 Bad Request with validation error message
SELECT status FROM users; → shows 'ACTIVE' for all registered users
```

---

## ══════════════════════════════════════
## FINAL VERIFICATION — READY CHECKLIST
## ══════════════════════════════════════

```
Run after ALL prompts completed.

=== BUILD ===
□ ./mvnw clean compile    → BUILD SUCCESS, 0 errors, 0 warnings about stubs

=== STARTUP ===
□ ./mvnw spring-boot:run  → Started AitaskerApplication in Xs
□ No: NoUniqueBeanDefinitionException
□ No: IllegalStateException Ambiguous mapping
□ No: MappingException duplicate column

=== SECURITY ===
□ grep -rn "AI_EXPERT" src/                                      → 0 results
□ grep -rn "System.out.println" src/main/java/                   → 0 results
□ grep -rn "common.enums.PaymentStatus" src/                     → 0 results
□ grep -rn "double\|Double" src/ | grep -i "budget\|rate\|fee"   → 0 results

=== CORE FLOW (all 10 steps) ===
□ POST /api/auth/register CLIENT           → 200 OK
□ POST /api/auth/register EXPERT           → 200 OK
□ POST /api/auth/login CLIENT              → 200 OK + JWT
□ POST /api/auth/login EXPERT              → 200 OK + JWT
□ GET  /api/expert/profile (expertJWT)     → 200 OK (NOT 403)
□ POST /api/jobs (clientJWT)               → 200 OK + jobId
□ POST /api/proposals (expertJWT+duration) → 200 OK + proposalId
□ PUT  /api/proposals/{id}/accept (cJWT)   → 200 OK
□ GET  /api/projects (clientJWT)           → 200 OK, project exists
□ Identity spoofing: no @RequestParam userId in ProposalController

=== DATA INTEGRITY ===
□ SELECT created_at FROM reviews; → no null, no hibernate error
□ DESCRIBE messages (SQL Server: sp_help 'messages'); → FK on project_id, sender_id, receiver_id
□ SELECT * FROM expert_profiles; → user_id column exists and not null
□ SELECT * FROM users; → status column exists, value = 'ACTIVE'

=== BUSINESS LOGIC ===
□ GET  /api/jobs/99999                     → 404 (NOT 500)
□ approve milestone + check payment status → payment.status = 'RELEASED'
□ approve withdrawal + check transaction   → transaction record exists

=== API CONTRACT ===
□ GET /api/notifications                   → no Notification entity in response, no password
□ GET /swagger-ui/index.html               → loads, shows all endpoint groups
```

---

## QUICK REFERENCE — File Change Summary

| File | Change Type | Prompt |
|---|---|---|
| admin/controller/AdminController.java | DELETE | 1A |
| user/repository/ExpertProfileRepository.java | DELETE | 1A |
| security/PasswordConfig.java | DELETE | 1A |
| 17 stub files | ADD valid Java skeleton | 1B |
| project/service/impl/ProjectServiceImpl.java | REMOVE wildcard import | 1C |
| common/enums/Role.java | AI_EXPERT → EXPERT | 2A |
| security/ProjectSecurityService.java | "AI_EXPERT" → "EXPERT" | 2A |
| proposal/dto/request/ProposalRequestDTO.java | ADD duration field | 2B |
| proposal/service/ProposalService.java | SET duration in createProposal | 2B |
| expert/entity/ExpertProfile.java | ADD User @OneToOne | 2C |
| expert/repository/ExpertProfileRepository.java | ADD findByUserId | 2C |
| expert/service/impl/ExpertServiceImpl.java | USE findByUserId | 2C |
| auth/service/AuthServiceImpl.java | CREATE ExpertProfile on register | 2C |
| proposal/controller/ProposalController.java | REPLACE @RequestParam with Authentication | 2D |
| review/entity/Review.java | REMOVE duplicate createdAt | 3A |
| review/service/ReviewService.java | REMOVE setCreatedAt() call | 3A |
| message/entity/Message.java | RAW IDs → JPA Relationships | 3B |
| message/repository/MessageRepository.java | UPDATE method signatures | 3B |
| common/enums/PaymentStatus.java | DELETE (keep payment/enums/PaymentStatus) | 3C |
| job/service/JobService.java | RuntimeException → ResourceNotFoundException | 3D |
| review/service/ReviewService.java | RuntimeException → Custom Exceptions | 3D |
| notification/controller/NotificationController.java | Entity → NotificationResponse DTO | 3E |
| admin/service/AdminUserService.java | List<User> → List<UserSummaryResponse> | 3E |
| milestone/service/impl/MilestoneServiceImpl.java | ADD payment release in releasePayment() | 3F |
| payment/entity/Transaction.java | payment FK nullable=true | 3G |
| payment/service/EscrowService.java | ADD Transaction on withdrawal approve | 3G |
| expert/entity/ExpertProfile.java | double → BigDecimal hourlyRate | 3H |
| expert/dto/response/ExpertProfileResponse.java | double → BigDecimal | 3H |
| job/dto/JobPostRequest.java | Double → BigDecimal + @Valid annotations | 3H, 3L |
| job/dto/JobPostResponse.java | Double → BigDecimal, ClientId → clientId | 3H |
| job/service/JobService.java | Remove BigDecimal.valueOf() conversion | 3H |
| payment/entity/Payment.java | extends BaseEntity, remove manual id/@PrePersist | 3I |
| payment/entity/Transaction.java | extends BaseEntity, remove manual id | 3I |
| payment/entity/Withdrawal.java | extends BaseEntity, remove manual id | 3I |
| security/jwt/JwtFilter.java | System.out.println → @Slf4j log | 3J |
| src/main/resources/application.yaml | Hardcoded → env vars | 3K |
| job/dto/JobPostRequest.java | ADD @NotBlank, @NotNull, @DecimalMin | 3L |
| user/entity/User.java | ADD UserStatus field | 3L |
| auth/service/AuthServiceImpl.java | SET status=ACTIVE on register | 3L |
```
