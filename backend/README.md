src
└── main
├── java
│   └── com
│       └── yourcompany
│           └── onlinecourse
│               ├── OnlineCourseSystemApplication.java  // (1) Điểm khởi đầu
│               ├── config/                           // (2) Cấu hình hệ thống
│               │   ├── SecurityConfig.java
│               │   ├── WebConfig.java
│               │   └── ApplicationConfig.java
│               ├── controller/                       // (3) Tầng API (Web Layer)
│               │   ├── AuthController.java
│               │   └── SubscriptionController.java
│               ├── model/ (hoặc entity)              // (4) Tầng Dữ liệu (Domain Layer)
│               │   ├── User.java
│               │   ├── UserSession.java
│               │   └── Subscription.java
│               ├── repository/                       // (5) Tầng Truy cập Dữ liệu
│               │   ├── UserRepository.java
│               │   ├── UserSessionRepository.java
│               │   └── SubscriptionRepository.java
│               ├── service/                          // (6) Tầng Logic nghiệp vụ
│               │   ├── impl/
│               │   │   ├── AuthServiceImpl.java
│               │   │   └── JwtServiceImpl.java
│               │   ├── AuthService.java
│               │   ├── JwtService.java
│               │   └── SubscriptionService.java
│               ├── dto/                              // (7) Đối tượng Truyền dữ liệu
│               │   ├── AuthResponse.java
│               │   ├── LoginRequest.java
│               │   └── RegisterRequest.java
│               └── security/                         // (8) Các thành phần Bảo mật
│                   ├── JwtAuthenticationFilter.java
│                   └── UserDetailsServiceImpl.java
└── resources
├── application.properties (hoặc .yml)          // (9) Cấu hình ứng dụng
├── static/
└── templates/
pom.xml                                               // (10) Quản lý thư viện và dự án


The system allows teachers to grant students assistant privileges, with optional permissions if needed.
@Entity
@Table(name = "user_course_roles")
public class UserCourseRole {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Course course;

    @ManyToOne(optional = false)
    private Role role; // e.g. TEACHER, ASSISTANT, STUDENT

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_course_permissions",
        joinColumns = @JoinColumn(name = "user_course_role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> extraPermissions = new HashSet<>();

    private LocalDateTime assignedAt = LocalDateTime.now();
}
@PostMapping("/{courseId}/assistants")

[//]: # (authentication:user info , #courseId get from @PathVariable Long courseId)
@PreAuthorize("@courseSecurity.hasPermission(authentication, #courseId, 'ASSIGN_ASSISTANT')")
public ResponseEntity<?> addAssistant(
@PathVariable Long courseId,
@RequestBody AssignAssistantRequest req
) {
courseRoleService.assignAssistant(courseId, req.getUserId(), req.getPermissions());
return ResponseEntity.ok("Assistant assigned successfully");
}

@Component("courseSecurity")
public class CourseSecurity {

    private final UserCourseRoleRepository userCourseRoleRepository;

    public CourseSecurity(UserCourseRoleRepository repo) {
        this.userCourseRoleRepository = repo;
    }

    public boolean hasPermission(Authentication auth, Long courseId, String permissionName) {
        String email = auth.getName();

        return userCourseRoleRepository.findByUserEmailAndCourseId(email, courseId)
                .flatMap(ucr -> ucr.getAllPermissions().stream()
                        .filter(p -> p.getName().equals(permissionName))
                        .findFirst()
                ).isPresent();
    }
    public Set<Permission> getAllPermissions() {
        Set<Permission> all = new HashSet<>(role.getPermissions());
        all.addAll(extraPermissions);
        return all;
    }
}

Quiz Attempts, Grading & Question Discussion Implementation Walkthrough
Summary
I have successfully implemented comprehensive quiz and discussion features including:

Quiz Attempt Management: Start quiz, submit answers, view attempt history
Automatic Grading System: Score calculation and storage in quiz_attempts
Question Discussions: Comment on questions, nested replies, view discussions
Notification System: Automatic notifications for comment replies and question activity
Changes Made
Repositories Created
QuizAttemptRepository.java
- Query quiz attempts by user and quiz
  QuestionResponseRepository.java
- Store individual question responses
  QuestionCommentRepository.java
- Manage comments and replies
  NotificationRepository.java
- Handle user notifications
  DTOs Created
  QuizAttemptDTO.java
- Start quiz attempt
  AnswerSubmissionDTO.java
- Submit answer for a question
  QuizSubmissionDTO.java
- Submit entire quiz
  QuestionCommentDTO.java
- Add comment or reply
  Quiz Attempt Features
  Service Layer
  QuizAttemptService.java
  Interface defining quiz attempt operations.

QuizAttemptServiceImpl.java
Key Features:

startAttempt()
- Creates new quiz attempt with start time
  submitAttempt()
- Processes answers and calculates score automatically
  Automatic Grading Logic:
  Compares selected choices with correct answers
  Awards points only if ALL correct choices are selected
  Stores individual question responses
  Calculates and saves total score
  getAttemptById()
- Retrieve attempt details
  getUserAttempts()
- Get user's attempt history
  Controller Layer
  QuizAttemptController.java
  Endpoints:

POST /api/quiz-attempt/start - Start a new quiz attempt
POST /api/quiz-attempt/submit - Submit quiz and get automatic grade
GET /api/quiz-attempt/{attemptId} - Get attempt details with score
GET /api/quiz-attempt/user/{userId}?quizId={quizId} - Get user's attempts
Question Discussion Features
Service Layer
QuestionCommentService.java
Interface for comment operations.

QuestionCommentServiceImpl.java
Key Features:

addComment()
- Add top-level comment or reply
  Notification Triggers:
  Reply to comment → Notifies parent comment author
  New comment on question → Notifies all users who previously commented (implicit follow)
  getQuestionComments()
- Get all top-level comments for a question
  updateComment()
- Edit own comment (marks as edited)
  deleteComment()
- Delete own comment
  Controller Layer
  QuestionCommentController.java
  Endpoints:

POST /api/question-comment?userId={userId} - Add comment or reply
GET /api/question-comment/question/{questionId} - Get all comments
PUT /api/question-comment/{commentId}?content={content}&userId={userId} - Update comment
DELETE /api/question-comment/{commentId}?userId={userId} - Delete comment
Notification System
Service Layer
NotificationService.java
Interface for notification operations.

NotificationServiceImpl.java
Key Features:

createNotification()
- Create notification for user
  getUserNotifications()
- Get all notifications
  getUnreadNotifications()
- Get unread only
  markAsRead()
- Mark single notification as read
  markAllAsRead()
- Mark all user's notifications as read
  getUnreadCount()
- Get count of unread notifications
  Controller Layer
  NotificationController.java
  Endpoints:

GET /api/notification/user/{userId} - Get all notifications
GET /api/notification/user/{userId}/unread - Get unread notifications
GET /api/notification/user/{userId}/unread-count - Get unread count
PUT /api/notification/{notificationId}/read - Mark as read
PUT /api/notification/user/{userId}/read-all - Mark all as read
Implementation Highlights
Automatic Grading Algorithm
// Compares user's selected choices with correct answers
// Awards full points only if exact match (all correct, no incorrect)
boolean isCorrect = selectedChoiceIds.containsAll(correctChoiceIds) &&
correctChoiceIds.containsAll(selectedChoiceIds);
Notification Strategy (Implicit Follow)
Reply Notifications: When user B replies to user A's comment, user A gets notified
Question Activity: When user B comments on a question that user A has commented on, user A gets notified
This creates an "implicit follow" - users automatically follow questions they interact with
Data Persistence
Quiz attempts stored in quiz_attempts table with scores
Individual answers stored in question_responses table
Comments support nested structure via child_comment_id
Notifications track read/unread status
Testing Guide
1. Quiz Attempt Flow
# Start quiz
POST /api/quiz-attempt/start
Body: {"quizId": 1, "userId": 1}
# Submit quiz
POST /api/quiz-attempt/submit
Body: {
"attemptId": 1,
"answers": [
{"questionId": 1, "selectedChoiceIds": [1, 2]},
{"questionId": 2, "selectedChoiceIds": [3]}
]
}
# View attempt with score
GET /api/quiz-attempt/1
2. Discussion Flow
# Add comment
POST /api/question-comment?userId=1
Body: {"questionId": 1, "content": "Great question!"}
# Reply to comment
POST /api/question-comment?userId=2
Body: {"questionId": 1, "parentCommentId": 1, "content": "I agree!"}
# View comments
GET /api/question-comment/question/1
3. Notification Flow
# Check notifications
GET /api/notification/user/1/unread
# Mark as read
PUT /api/notification/5/read
Next Steps
Add pagination for comments and notifications
Implement comment voting/likes
Add email notifications
Create WebSocket support for real-time notifications
Add quiz time limit enforcement

