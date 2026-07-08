package com.aitasker.config;

import com.aitasker.audit.entity.AuditLog;
import com.aitasker.audit.repository.AuditLogRepository;
import com.aitasker.common.enums.*;
import com.aitasker.delivery.entity.Delivery;
import com.aitasker.delivery.repository.DeliveryRepository;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.entity.Portfolio;
import com.aitasker.expert.entity.ServicePackage;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.expert.repository.PortfolioRepository;
import com.aitasker.expert.repository.ServicePackageRepository;
import com.aitasker.file.entity.Attachment;
import com.aitasker.file.repository.AttachmentRepository;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.message.entity.Message;
import com.aitasker.message.repository.MessageRepository;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;
import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.payment.enums.TransactionType;
import com.aitasker.payment.enums.WithdrawalStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.payment.repository.WithdrawalRepository;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.review.entity.Review;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Sinh dữ liệu demo cho toàn bộ hệ thống — CHỈ chèn dữ liệu, KHÔNG thay đổi
 * bất kỳ business logic nào ở Service/Controller hiện có.
 *
 * Idempotent: nếu tài khoản demo đầu tiên (client1@aitasker.com) đã tồn tại,
 * seeder coi như dữ liệu demo đã có sẵn và bỏ qua toàn bộ, có thể chạy lại
 * (restart app) bao nhiêu lần cũng không tạo trùng dữ liệu.
 *
 * Bật/tắt qua property: app.seed.demo-data (mặc định: true).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class DemoDataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "Demo@123";

    private final UserRepository userRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final PortfolioRepository portfolioRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final JobPostRepository jobPostRepository;
    private final ProposalRepository proposalRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final ReviewRepository reviewRepository;
    private final MessageRepository messageRepository;
    private final DisputeRepository disputeRepository;
    private final AuditLogRepository auditLogRepository;
    private final AttachmentRepository attachmentRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.demo-data:true}")
    private boolean seedEnabled;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seedEnabled) {
            log.info("Demo data seeding bị tắt (app.seed.demo-data=false)");
            return;
        }
        if (userRepository.existsByEmail("client1@aitasker.com")) {
            log.info("Demo data đã tồn tại, bỏ qua seeding.");
            return;
        }

        log.info("=== Bắt đầu seed demo data cho AITasker ===");

        User admin = getOrCreateAdmin();
        List<User> clients = createClients();
        List<User> experts = createExperts();
        List<ExpertProfile> profiles = createExpertProfiles(experts);
        createPortfolios(experts);
        createServicePackages(experts);

        List<JobPost> jobs = createJobs(clients);
        List<Proposal> proposals = createProposals(jobs, experts);
        List<Project> projects = createProjects(proposals);

        seedProjectWorkflow(projects);

        createWithdrawals(experts);
        createReviews(projects);
        createNotifications(clients, experts);
        createMessages(projects);
        createDisputesAndAudit(projects, admin, clients, experts);
        createAttachmentsWithFiles(clients, experts);

        log.info("=== Seed demo data hoàn tất ===");
        log.info("Admin:  admin@aitasker.com / Admin@123 (hoặc theo app.admin.* nếu đã cấu hình khác)");
        log.info("Client: client1@aitasker.com .. client3@aitasker.com / {}", DEMO_PASSWORD);
        log.info("Expert: expert1@aitasker.com .. expert3@aitasker.com / {}", DEMO_PASSWORD);
    }

    private User getOrCreateAdmin() {
        return userRepository.findByEmail("admin@aitasker.com").orElseGet(() -> {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@aitasker.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            return userRepository.save(admin);
        });
    }

    private List<User> createClients() {
        String[] names = {"Nguyen Van Client", "Tran Thi Client", "Le Hoang Client"};
        return java.util.stream.IntStream.range(0, 3)
                .mapToObj(i -> {
                    User u = new User();
                    u.setName(names[i]);
                    u.setEmail("client" + (i + 1) + "@aitasker.com");
                    u.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
                    u.setRole(Role.CLIENT);
                    u.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(u);
                })
                .toList();
    }

    private List<User> createExperts() {
        String[] names = {"Pham Minh Expert", "Vo Thi Expert", "Dang Quoc Expert"};
        return java.util.stream.IntStream.range(0, 3)
                .mapToObj(i -> {
                    User u = new User();
                    u.setName(names[i]);
                    u.setEmail("expert" + (i + 1) + "@aitasker.com");
                    u.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
                    u.setRole(Role.EXPERT);
                    u.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(u);
                })
                .toList();
    }

    private List<ExpertProfile> createExpertProfiles(List<User> experts) {
        String[] titles = {"Senior AI Engineer", "NLP Specialist", "Computer Vision Engineer"};
        String[] skills = {
                "Python, PyTorch, LLM, LangChain",
                "Python, spaCy, Transformers, NLP",
                "Python, OpenCV, TensorFlow, Computer Vision"
        };
        int[] years = {6, 4, 8};
        String[] rates = {"45.00", "35.00", "60.00"};

        return java.util.stream.IntStream.range(0, experts.size())
                .mapToObj(i -> {
                    ExpertProfile p = new ExpertProfile();
                    p.setUser(experts.get(i));
                    p.setFullName(experts.get(i).getName());
                    p.setTitle(titles[i]);
                    p.setSkills(skills[i]);
                    p.setExperienceYears(years[i]);
                    p.setHourlyRate(new BigDecimal(rates[i]));
                    p.setInternalNotes("Seed data — hồ sơ demo");
                    return expertProfileRepository.save(p);
                })
                .toList();
    }

    private void createPortfolios(List<User> experts) {
        for (User expert : experts) {
            portfolioRepository.save(Portfolio.builder()
                    .expert(expert)
                    .projectName("Chatbot AI cho " + expert.getName())
                    .description("Xây dựng chatbot bán hàng tích hợp OpenAI API và LangChain.")
                    .projectUrl("https://github.com/demo/chatbot-" + expert.getId())
                    .build());
            portfolioRepository.save(Portfolio.builder()
                    .expert(expert)
                    .projectName("Hệ thống Recommendation cho " + expert.getName())
                    .description("Xây dựng hệ thống gợi ý sản phẩm dựa trên collaborative filtering.")
                    .projectUrl("https://github.com/demo/recommendation-" + expert.getId())
                    .build());
        }
    }

    private void createServicePackages(List<User> experts) {
        String[] names = {"AI Chatbot Package - Basic", "AI Resume Parser", "Computer Vision Audit"};
        String[] prices = {"200.00", "150.00", "400.00"};
        int[] days = {5, 3, 10};

        for (int i = 0; i < experts.size(); i++) {
            ServicePackage pkg = new ServicePackage();
            pkg.setExpertId(experts.get(i).getId());
            pkg.setPackageName(names[i]);
            pkg.setPrice(new BigDecimal(prices[i]));
            pkg.setDeliveryDays(days[i]);
            servicePackageRepository.save(pkg);
        }
    }

    private List<JobPost> createJobs(List<User> clients) {
        record JobSeed(String title, String description, String budget, String skills, JobStatus status, int deadlineDays) {}

        List<JobSeed> seeds = List.of(
                new JobSeed("Facebook Sales Chatbot", "Xây dựng chatbot bán hàng tích hợp Facebook Messenger.",
                        "3000.00", "Python, LangChain, OpenAI API", JobStatus.OPEN, 30),
                new JobSeed("AI Resume Parser", "Xây dựng công cụ trích xuất thông tin từ CV bằng NLP.",
                        "1500.00", "Python, NLP, spaCy", JobStatus.OPEN, 20),
                new JobSeed("Expert Recommendation Engine", "Xây dựng hệ thống gợi ý chuyên gia cho marketplace.",
                        "4000.00", "Python, Machine Learning, Recommendation System", JobStatus.IN_PROGRESS, 45),
                new JobSeed("Computer Vision Quality Check", "Kiểm tra chất lượng sản phẩm bằng Computer Vision.",
                        "5000.00", "Python, OpenCV, TensorFlow", JobStatus.IN_PROGRESS, 60),
                new JobSeed("Document OCR System", "Xây dựng hệ thống OCR trích xuất dữ liệu hóa đơn.",
                        "2500.00", "Python, OCR, Tesseract", JobStatus.CLOSED, 15),
                new JobSeed("Chatbot Nội Bộ Doanh Nghiệp", "Chatbot hỗ trợ nhân viên tra cứu quy trình nội bộ.",
                        "1800.00", "Python, LangChain, Vector DB", JobStatus.CANCELLED, 25)
        );

        return java.util.stream.IntStream.range(0, seeds.size())
                .mapToObj(i -> {
                    JobSeed s = seeds.get(i);
                    JobPost job = new JobPost();
                    job.setClient(clients.get(i % clients.size()));
                    job.setTitle(s.title());
                    job.setDescription(s.description());
                    job.setBudget(new BigDecimal(s.budget()));
                    job.setDeadline(LocalDate.now().plusDays(s.deadlineDays()));
                    job.setRequiredSkills(s.skills());
                    job.setStatus(s.status());
                    return jobPostRepository.save(job);
                })
                .toList();
    }

    private List<Proposal> createProposals(List<JobPost> jobs, List<User> experts) {
        record PropSeed(int jobIndex, int expertIndex, String bid, int duration, ProposalStatus status) {}

        List<PropSeed> seeds = List.of(
                new PropSeed(0, 0, "2800.00", 14, ProposalStatus.PENDING),
                new PropSeed(0, 1, "2600.00", 12, ProposalStatus.REJECTED),
                new PropSeed(1, 1, "1400.00", 10, ProposalStatus.ACCEPTED),
                new PropSeed(2, 0, "3800.00", 30, ProposalStatus.ACCEPTED),
                new PropSeed(3, 2, "4800.00", 40, ProposalStatus.ACCEPTED),
                new PropSeed(4, 2, "2400.00", 10, ProposalStatus.ACCEPTED),
                new PropSeed(5, 0, "1700.00", 15, ProposalStatus.WITHDRAWN),
                new PropSeed(0, 2, "1900.00", 20, ProposalStatus.ACCEPTED)
        );

        return seeds.stream()
                .map(s -> proposalRepository.save(Proposal.builder()
                        .job(jobs.get(s.jobIndex()))
                        .expert(experts.get(s.expertIndex()))
                        .bidAmount(new BigDecimal(s.bid()))
                        .duration(s.duration())
                        .coverLetter("Tôi có kinh nghiệm phù hợp và cam kết hoàn thành đúng tiến độ đề xuất.")
                        .submittedAt(LocalDateTime.now().minusDays(5))
                        .status(s.status())
                        .build()))
                .toList();
    }

    /**
     * Tạo Project cho các Proposal đã ACCEPTED, với đa dạng trạng thái Project.
     * seeds[i] tương ứng lần lượt: ACTIVE, ACTIVE, COMPLETED, DISPUTED, CANCELLED
     */
    private List<Project> createProjects(List<Proposal> proposals) {
        List<Proposal> accepted = proposals.stream().filter(Proposal::isAccepted).toList();
        ProjectStatus[] statuses = {
                ProjectStatus.ACTIVE, ProjectStatus.ACTIVE, ProjectStatus.COMPLETED,
                ProjectStatus.DISPUTED, ProjectStatus.CANCELLED
        };

        return java.util.stream.IntStream.range(0, Math.min(accepted.size(), statuses.length))
                .mapToObj(i -> {
                    Proposal p = accepted.get(i);
                    Project project = projectRepository.save(Project.builder()
                            .client(p.getJob().getClient())
                            .expert(p.getExpert())
                            .job(p.getJob())
                            .proposal(p)
                            .startDate(LocalDate.now().minusDays(20))
                            .endDate(LocalDate.now().plusDays(10))
                            .status(statuses[i])
                            .build());
                    p.getJob().setStatus(JobStatus.IN_PROGRESS);
                    jobPostRepository.save(p.getJob());
                    return project;
                })
                .toList();
    }

    /**
     * Với mỗi Project: tạo Milestone + Delivery + Payment/Transaction tương ứng,
     * phủ đủ các trạng thái PENDING/SUBMITTED/APPROVED/REJECTED/PAID và
     * HELD/RELEASED/REFUNDED.
     */
    private void seedProjectWorkflow(List<Project> projects) {
        if (projects.isEmpty()) return;

        // Project[0] (ACTIVE): 1 milestone PENDING (chưa nộp), 1 milestone SUBMITTED
        seedMilestoneWithDelivery(projects.get(0), "Milestone 1 - Khảo sát yêu cầu",
                "1200.00", MilestoneStatus.PENDING, null, null);
        seedMilestoneWithDelivery(projects.get(0), "Milestone 2 - Bản demo đầu tiên",
                "1600.00", MilestoneStatus.SUBMITTED, DeliveryStatus.SUBMITTED, null);

        // Project[1] (ACTIVE): milestone REJECTED (đã nộp bị từ chối) + milestone APPROVED (đã duyệt, chưa giải ngân)
        if (projects.size() > 1) {
            seedMilestoneWithDelivery(projects.get(1), "Milestone 1 - Thiết kế kiến trúc",
                    "1900.00", MilestoneStatus.REJECTED, DeliveryStatus.REJECTED, "Chưa đáp ứng yêu cầu kỹ thuật");
            seedMilestoneWithDelivery(projects.get(1), "Milestone 2 - Triển khai Recommendation Engine",
                    "1900.00", MilestoneStatus.APPROVED, DeliveryStatus.APPROVED, null);
        }

        // Project[2] (COMPLETED): milestone PAID (đã giải ngân) đầy đủ escrow flow
        if (projects.size() > 2) {
            Milestone paidMilestone = seedMilestoneWithDelivery(projects.get(2), "Milestone 1 - Bàn giao hệ thống Computer Vision",
                    "4800.00", MilestoneStatus.PAID, DeliveryStatus.APPROVED, null);
            seedEscrowFlow(projects.get(2), paidMilestone, PaymentStatus.RELEASED);
        }

        // Project[3] (DISPUTED): milestone APPROVED nhưng tiền đang HELD do tranh chấp
        if (projects.size() > 3) {
            Milestone disputedMilestone = seedMilestoneWithDelivery(projects.get(3), "Milestone 1 - OCR Engine",
                    "2400.00", MilestoneStatus.APPROVED, DeliveryStatus.APPROVED, null);
            seedEscrowFlow(projects.get(3), disputedMilestone, PaymentStatus.HELD);
        }

        // Project[4] (CANCELLED): milestone PENDING, escrow đã REFUNDED
        if (projects.size() > 4) {
            Milestone cancelledMilestone = seedMilestoneWithDelivery(projects.get(4), "Milestone 1 - Chatbot nội bộ",
                    "1700.00", MilestoneStatus.PENDING, null, null);
            seedEscrowFlow(projects.get(4), cancelledMilestone, PaymentStatus.REFUNDED);
        }
    }

    private Milestone seedMilestoneWithDelivery(Project project, String title, String amount,
                                                 MilestoneStatus status, DeliveryStatus deliveryStatus, String rejectReason) {
        Milestone milestone = milestoneRepository.save(Milestone.builder()
                .project(project)
                .title(title)
                .description(title + " — mô tả chi tiết công việc cần thực hiện.")
                .amount(new BigDecimal(amount))
                .dueDate(LocalDate.now().plusDays(14))
                .status(status)
                .submittedAt(deliveryStatus != null ? LocalDateTime.now().minusDays(3) : null)
                .approvedAt(status == MilestoneStatus.APPROVED || status == MilestoneStatus.PAID ? LocalDateTime.now().minusDays(1) : null)
                .build());

        if (deliveryStatus != null) {
            deliveryRepository.save(Delivery.builder()
                    .milestone(milestone)
                    .fileUrl("https://drive.google.com/demo-delivery-" + milestone.getId())
                    .note("Bản giao demo cho " + title)
                    .version(1)
                    .submittedBy(project.getExpert())
                    .submittedAt(LocalDateTime.now().minusDays(3))
                    .status(deliveryStatus)
                    .approvedAt(deliveryStatus == DeliveryStatus.APPROVED ? LocalDateTime.now().minusDays(1) : null)
                    .rejectReason(rejectReason)
                    .build());
        }
        return milestone;
    }

    private void seedEscrowFlow(Project project, Milestone milestone, PaymentStatus finalStatus) {
        Payment payment = Payment.builder()
                .project(project)
                .milestone(milestone)
                .amount(milestone.getAmount())
                .status(PaymentStatus.HELD)
                .transactionRef("SEED-DEP-" + milestone.getId())
                .build();
        payment = paymentRepository.save(payment);

        transactionRepository.save(Transaction.builder()
                .payment(payment)
                .type(TransactionType.DEPOSIT)
                .amount(milestone.getAmount())
                .description("Deposit escrow cho Milestone #" + milestone.getId())
                .build());

        if (finalStatus == PaymentStatus.RELEASED) {
            payment.setStatus(PaymentStatus.RELEASED);
            paymentRepository.save(payment);
            transactionRepository.save(Transaction.builder()
                    .payment(payment).type(TransactionType.RELEASE).amount(milestone.getAmount())
                    .description("Release Payment cho Milestone #" + milestone.getId()).build());
        } else if (finalStatus == PaymentStatus.REFUNDED) {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
            transactionRepository.save(Transaction.builder()
                    .payment(payment).type(TransactionType.REFUND).amount(milestone.getAmount())
                    .description("Refund Payment cho Milestone #" + milestone.getId()).build());
        }
        // finalStatus == HELD: giữ nguyên trạng thái HELD vừa tạo.
    }

    /**
     * Withdrawal.@PrePersist luôn ép status=PENDING khi insert -> cần update
     * lại (2 bước) để có đủ 3 trạng thái PENDING/APPROVED/REJECTED trong demo.
     */
    private void createWithdrawals(List<User> experts) {
        Withdrawal w1 = withdrawalRepository.save(Withdrawal.builder()
                .expert(experts.get(0)).amount(new BigDecimal("500.00")).build());

        Withdrawal w2 = withdrawalRepository.save(Withdrawal.builder()
                .expert(experts.get(1)).amount(new BigDecimal("300.00")).build());
        w2.setStatus(WithdrawalStatus.APPROVED);
        w2.setProcessedAt(LocalDateTime.now().minusDays(1));
        withdrawalRepository.save(w2);

        Withdrawal w3 = withdrawalRepository.save(Withdrawal.builder()
                .expert(experts.get(2)).amount(new BigDecimal("1000.00")).build());
        w3.setStatus(WithdrawalStatus.REJECTED);
        w3.setProcessedAt(LocalDateTime.now().minusDays(1));
        withdrawalRepository.save(w3);
    }

    private void createReviews(List<Project> projects) {
        Project completed = projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.COMPLETED)
                .findFirst().orElse(null);
        if (completed == null) return;

        reviewRepository.save(reviewOf(completed.getClient(), completed.getExpert(), completed,
                5, "Chuyên gia làm việc chuyên nghiệp, giao đúng hạn.", ReviewType.CLIENT_TO_EXPERT));
        reviewRepository.save(reviewOf(completed.getExpert(), completed.getClient(), completed,
                5, "Khách hàng phản hồi rõ ràng, thanh toán đúng hạn.", ReviewType.EXPERT_TO_cLIENT));
    }

    private Review reviewOf(User reviewer, User reviewee, Project project, int rating, String comment, ReviewType type) {
        Review r = new Review();
        r.setReviewer(reviewer);
        r.setReviewee(reviewee);
        r.setProject(project);
        r.setRating(rating);
        r.setComment(comment);
        r.setType(type);
        return r;
    }

    private void createNotifications(List<User> clients, List<User> experts) {
        notificationService.createNotification(clients.get(0).getId(), "Có đề xuất mới",
                "Bạn có một đề xuất mới cho công việc đã đăng.", "PROPOSAL_CREATED");
        notificationService.createNotification(experts.get(1).getId(), "Đề xuất được chấp nhận",
                "Đề xuất của bạn đã được chấp nhận, dự án đã được tạo.", "PROPOSAL_ACCEPTED");
        notificationService.createNotification(experts.get(0).getId(), "Đề xuất bị từ chối",
                "Rất tiếc, đề xuất của bạn đã bị từ chối.", "PROPOSAL_REJECTED");
    }

    private void createMessages(List<Project> projects) {
        if (projects.isEmpty()) return;
        Project project = projects.get(0);
        messageRepository.save(Message.builder()
                .project(project).sender(project.getClient()).receiver(project.getExpert())
                .content("Chào bạn, dự án tiến độ thế nào rồi ạ?")
                .sentAt(LocalDateTime.now().minusHours(5)).isRead(true).build());
        messageRepository.save(Message.builder()
                .project(project).sender(project.getExpert()).receiver(project.getClient())
                .content("Chào anh/chị, em đang hoàn thiện Milestone đầu tiên, dự kiến nộp trong 2 ngày tới.")
                .sentAt(LocalDateTime.now().minusHours(4)).isRead(false).build());
    }

    private void createDisputesAndAudit(List<Project> projects, User admin, List<User> clients, List<User> experts) {
        Project disputed = projects.stream().filter(p -> p.getStatus() == ProjectStatus.DISPUTED).findFirst().orElse(null);
        if (disputed != null) {
            disputeRepository.save(Dispute.builder()
                    .project(disputed)
                    .creator(disputed.getClient())
                    .reason("Sản phẩm bàn giao không đúng như thỏa thuận ban đầu.")
                    .status(DisputeStatus.OPEN)
                    .build());
        }

        Project cancelled = projects.stream().filter(p -> p.getStatus() == ProjectStatus.CANCELLED).findFirst().orElse(null);
        if (cancelled != null) {
            disputeRepository.save(Dispute.builder()
                    .project(cancelled)
                    .creator(cancelled.getClient())
                    .reason("Chuyên gia không phản hồi trong thời gian dài.")
                    .status(DisputeStatus.RESOLVED_REFUND)
                    .resolution("Đã hoàn tiền toàn bộ cho Client theo chính sách hoàn tiền.")
                    .build());
        }

        auditLogRepository.save(AuditLog.builder().actor(clients.get(0)).action("LOGIN")
                .description("Đăng nhập thành công (seed data)").ipAddress("127.0.0.1").build());
        auditLogRepository.save(AuditLog.builder().actor(admin).action("ADMIN_BAN_USER")
                .description("Seed: hành động demo cho Audit Log").ipAddress("127.0.0.1").build());
        auditLogRepository.save(AuditLog.builder().actor(experts.get(0)).action("PAYMENT_DEPOSIT")
                .description("Seed: demo giao dịch deposit").ipAddress("127.0.0.1").build());
    }

    private void createAttachmentsWithFiles(List<User> clients, List<User> experts) throws Exception {
        Path dir = Path.of(uploadDir);
        Files.createDirectories(dir);

        writeDemoFile(dir, "demo-portfolio.pdf", buildMinimalPdf());
        writeDemoFile(dir, "demo-contract.docx", buildMinimalDocx());
        writeDemoFile(dir, "demo-avatar.png", buildMinimalPng());
        writeDemoFile(dir, "demo-source.zip", buildMinimalZip());

        attachmentRepository.save(Attachment.builder()
                .originalFileName("portfolio-sample.pdf").storedFileName("demo-portfolio.pdf")
                .contentType("application/pdf").fileSize(Files.size(dir.resolve("demo-portfolio.pdf")))
                .uploadedBy(experts.get(0)).build());

        attachmentRepository.save(Attachment.builder()
                .originalFileName("contract-sample.docx").storedFileName("demo-contract.docx")
                .contentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .fileSize(Files.size(dir.resolve("demo-contract.docx")))
                .uploadedBy(clients.get(0)).build());

        attachmentRepository.save(Attachment.builder()
                .originalFileName("avatar-sample.png").storedFileName("demo-avatar.png")
                .contentType("image/png").fileSize(Files.size(dir.resolve("demo-avatar.png")))
                .uploadedBy(experts.get(1)).build());

        attachmentRepository.save(Attachment.builder()
                .originalFileName("source-sample.zip").storedFileName("demo-source.zip")
                .contentType("application/zip").fileSize(Files.size(dir.resolve("demo-source.zip")))
                .uploadedBy(experts.get(2)).build());
    }

    private void writeDemoFile(Path dir, String name, byte[] content) throws Exception {
        Path target = dir.resolve(name);
        if (!Files.exists(target)) {
            Files.write(target, content);
        }
    }

    private byte[] buildMinimalPdf() {
        String pdf = "%PDF-1.4\n"
                + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
                + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
                + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 200 100]>>endobj\n"
                + "trailer<</Root 1 0 R>>\n%%EOF";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }

    private byte[] buildMinimalDocx() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("[Content_Types].xml"));
            zos.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                    + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                    + "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>"
                    + "</Types>").getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("_rels/.rels"));
            zos.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                    + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>"
                    + "</Relationships>").getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("word/document.xml"));
            zos.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                    + "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                    + "<w:body><w:p><w:r><w:t>AITasker demo contract document.</w:t></w:r></w:p></w:body>"
                    + "</w:document>").getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private byte[] buildMinimalPng() {
        return new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53,
                (byte) 0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
                0x54, 0x08, (byte) 0xD7, 0x63, (byte) 0xF8, (byte) 0xCF, (byte) 0xC0, 0x00,
                0x00, 0x03, 0x01, 0x01, 0x00, 0x18, (byte) 0xDD, (byte) 0x8D,
                (byte) 0xB0, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E,
                0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
    }

    private byte[] buildMinimalZip() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("README.txt"));
            zos.write("AITasker demo source archive.".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}