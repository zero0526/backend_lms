-- liquibase formatted sql

-- changeset antigravity:001-create-users-table


CREATE TYPE recurrence_type AS ENUM ('none', 'daily', 'weekly', 'monthly');
CREATE TYPE status_meeting AS ENUM ('upcoming', 'live', 'ended');
CREATE TYPE meeting_role AS ENUM ('participant', 'host');
CREATE TYPE question_type AS ENUM ('choice', 'multiple_choice');


-- ===========================================
-- 1. Roles (Moved up for FK dependency)
-- ===========================================
CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL
);

-- ===========================================
-- 2. Users
-- ==========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT,
    auth_provider VARCHAR(50) NOT NULL DEFAULT 'local',
    provider_user_id VARCHAR(255),
    role_id BIGINT REFERENCES roles(id),
    full_name VARCHAR(255),
    avatar_url TEXT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_provider_user_id ON users(provider_user_id);

-- ===========================================
-- 3. User Profiles (Personal Info)
-- ===========================================
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE,
    gender VARCHAR(20),
    bio TEXT,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    last_login TIMESTAMP,
    preferences JSONB,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP
);


-- ========== USER SESSIONS ==========
CREATE TABLE IF NOT EXISTS user_sessions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  ip_address VARCHAR(255),
  refresh_token VARCHAR(255) UNIQUE NOT NULL,
      revoked BOOLEAN DEFAULT false,
  expires_at TIMESTAMP,
  last_active_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT now()
);
-- ========== OAUTH PROVIDERS ==========
CREATE TABLE IF NOT EXISTS oauth_providers (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL,
  client_id VARCHAR(255) NOT NULL,
  client_secret_key VARCHAR(255) NOT NULL,
  redirect_uri TEXT NOT NULL,
  auth_url TEXT NOT NULL,
  token_url TEXT NOT NULL,
  userinfo_url TEXT NOT NULL,
  scopes TEXT[],
  created_at TIMESTAMP DEFAULT now()
);
CREATE TABLE IF NOT EXISTS verification_tokens(
  id BIGSERIAL PRIMARY KEY,
  token VARCHAR(255),
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  expiry_date TIMESTAMP DEFAULT NOW()
);
-- ========== LOGIN AUDIT LOGS ==========
CREATE TABLE IF NOT EXISTS login_audit_logs (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  session_id BIGINT REFERENCES user_sessions(id) ON DELETE CASCADE,
  ip_address VARCHAR(50),
  event VARCHAR(50),
  message TEXT,
  created_at TIMESTAMP DEFAULT now()
);

-- ========== TOKEN ROTATIONS ==========
CREATE TABLE IF NOT EXISTS token_rotations (
  id BIGSERIAL PRIMARY KEY,
  old_refresh_token VARCHAR(255),
  new_refresh_token VARCHAR(255),
  user_id BIGINT REFERENCES users(id),
  session_id BIGINT REFERENCES user_sessions(id),
  rotated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires_at ON user_sessions(expires_at);

-- ========== SUBSCRIPTIONS ==========
CREATE TABLE IF NOT EXISTS subscriptions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id BIGINT NOT NULL,
  status VARCHAR(20) DEFAULT 'active',
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  auto_renew BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);

-- ========== TRANSACTIONS ==========
CREATE TABLE IF NOT EXISTS transactions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  subscription_id BIGINT REFERENCES subscriptions(id),
  amount NUMERIC(10,2),
  currency VARCHAR(3),
  status VARCHAR(20),
  payment_gateway VARCHAR(50),
  gateway_transaction_id VARCHAR(255),
  created_at TIMESTAMP DEFAULT now()
);

-- ========== PAYMENT METHODS ==========
CREATE TABLE IF NOT EXISTS payment_methods (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100),
  code VARCHAR(50) UNIQUE NOT NULL,
  provider_type VARCHAR(50) DEFAULT 'wallet',
  provider_info JSONB,
  is_active BOOLEAN DEFAULT TRUE,
  is_default BOOLEAN DEFAULT FALSE,
  support_refund BOOLEAN DEFAULT FALSE,
  sandbox_endpoint TEXT,
  production_endpoint TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP
);

-- ========== PAYMENT REQUESTS ==========
CREATE TABLE IF NOT EXISTS payment_requests (
  id BIGSERIAL PRIMARY KEY,
  transaction_id BIGINT REFERENCES transactions(id) ON DELETE CASCADE,
  payment_method_id BIGINT REFERENCES payment_methods(id) ON DELETE RESTRICT,
  order_id VARCHAR(100) UNIQUE,
  request_id VARCHAR(100) UNIQUE,
  amount NUMERIC(10,2),
  pay_url TEXT,
  status VARCHAR(20) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT now()
);

-- ========== PAYMENT CALLBACKS ==========
CREATE TABLE IF NOT EXISTS payment_callbacks (
  id BIGSERIAL PRIMARY KEY,
  payment_request_id BIGINT REFERENCES payment_requests(id) ON DELETE CASCADE,
  raw_data JSONB,
  result_code INT,
  message TEXT,
  callback_time TIMESTAMP DEFAULT now(),
  signature VARCHAR(255)
);


-- ========== COURSES ==========
CREATE TABLE IF NOT EXISTS courses (
  id BIGSERIAL PRIMARY KEY,
  instructor_id BIGINT REFERENCES users(id),
  title VARCHAR(255),
  description TEXT,
  thumbnail_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  last_updated TIMESTAMP,
  precondition  TEXT,
  course_target TEXT
);
CREATE TABLE IF NOT EXISTS tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

--Many-to-Many
CREATE TABLE IF NOT EXISTS course_tag (
    course_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (course_id, tag_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);
-- ========== REVIEWS ==========
CREATE TABLE IF NOT EXISTS review_courses (
  id BIGSERIAL PRIMARY KEY,
  course_id BIGINT REFERENCES courses(id),
  user_id BIGINT REFERENCES users(id),
  rating INT,
  comment TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP
);

-- ========== ENROLLMENTS ==========
CREATE TABLE IF NOT EXISTS enrollments (
  user_id BIGINT REFERENCES users(id),
  course_id BIGINT REFERENCES courses(id),
  enrolled_at TIMESTAMP DEFAULT now(),
  progress_percentage FLOAT,
  PRIMARY KEY (user_id, course_id)
);

-- ========== CHAPTERS ==========
CREATE TABLE IF NOT EXISTS chapters (
  id BIGSERIAL PRIMARY KEY,
  course_id BIGINT REFERENCES courses(id),
  title VARCHAR(255),
  "order" INT
);

-- ========== LESSONS ==========
CREATE TABLE IF NOT EXISTS lessons (
  id BIGSERIAL PRIMARY KEY,
  chapter_id BIGINT REFERENCES chapters(id),
  title VARCHAR(255),
  video_id BIGINT,
  "order" INT,
  description TEXT,
  thumbnail_url TEXT
);

-- ========== VIDEOS ==========
CREATE TABLE IF NOT EXISTS videos (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255),
  video_url VARCHAR(255) NOT NULL,
  duration INT
 );
CREATE TABLE IF NOT EXISTS segments(
id BIGSERIAL PRIMARY KEY,
video_id BIGINT REFERENCES videos(id),
description TEXT,
start_at VARCHAR(20),
end_at VARCHAR(20)
);
ALTER TABLE lessons
ADD FOREIGN KEY (video_id) REFERENCES videos(id);

-- ========== COURSE MATERIALS ==========
CREATE TABLE IF NOT EXISTS course_materials (
  id BIGSERIAL PRIMARY KEY,
  lesson_id BIGINT REFERENCES lessons(id),
  doc_url VARCHAR,
  title VARCHAR,
  file_type VARCHAR
);

-- ========== LESSON PROGRESS ==========
CREATE TABLE IF NOT EXISTS lesson_progress (
  lesson_id BIGINT REFERENCES lessons(id),
  user_id BIGINT REFERENCES users(id),
  course_id BIGINT REFERENCES courses(id),
  progress_video FLOAT,
  progress_quiz FLOAT,
  last_watched_at_second BIGINT,
  first_watched_at TIMESTAMP,
  PRIMARY KEY (lesson_id, user_id)
);

-- ========== QUIZZES ==========
CREATE TABLE IF NOT EXISTS quizzes (
  id BIGSERIAL PRIMARY KEY,
  lesson_id BIGINT REFERENCES lessons(id),
  title VARCHAR,
  precondition VARCHAR,
  description VARCHAR,
  time_limit_minutes INT,
  difficulty_avg VARCHAR,
  total_score INT
);

-- ========== QUESTIONS ==========
CREATE TABLE IF NOT EXISTS questions (
  id BIGSERIAL PRIMARY KEY,
  quiz_id BIGINT REFERENCES quizzes(id),
  question_text VARCHAR,
  question_image VARCHAR(255),
  explanation TEXT,
  level VARCHAR,

  score FLOAT,
  "order" INT
);

-- ========== MCQ CONTENTS ==========
CREATE TABLE IF NOT EXISTS mcq_contents (
  id BIGSERIAL PRIMARY KEY,
  question_id BIGINT REFERENCES questions(id),
  choice_text TEXT,
  choice_image VARCHAR(255),
  is_correct BOOLEAN
);

-- ========== QUIZ ATTEMPTS ==========
CREATE TABLE IF NOT EXISTS quiz_attempts (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  quiz_id BIGINT REFERENCES quizzes(id),
  started_at TIMESTAMP,
  submitted_at TIMESTAMP,
  total_score FLOAT,
  is_completed BOOLEAN
);

-- ========== QUESTION RESPONSES ==========
CREATE TABLE IF NOT EXISTS question_responses (
  id BIGSERIAL PRIMARY KEY,
  attempt_id BIGINT REFERENCES quiz_attempts(id),
  question_id BIGINT REFERENCES questions(id),
  is_correct BOOLEAN,
  is_selected BOOLEAN,
  score_awarded FLOAT,
  answered_at TIMESTAMP
);

-- ========== FORUM ==========
CREATE TABLE IF NOT EXISTS posts (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  course_id BIGINT REFERENCES courses(id),
  title TEXT,
  content TEXT,
  is_pinned BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  last_updated TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post_comments (
  id BIGSERIAL PRIMARY KEY,
  post_id BIGINT REFERENCES posts(id),
  user_id BIGINT REFERENCES users(id),
  child_comment_id BIGINT REFERENCES post_comments(id),
  content TEXT,
  is_edited BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS question_comments (
  id BIGSERIAL PRIMARY KEY,
  question_id BIGINT REFERENCES questions(id),
  user_id BIGINT REFERENCES users(id),
  child_comment_id BIGINT REFERENCES question_comments(id),
  content TEXT,
  is_edited BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP
);

-- ========== MEETINGS ==========
CREATE TABLE IF NOT EXISTS meetings (
  id BIGSERIAL PRIMARY KEY,
  course_id BIGINT REFERENCES courses(id),
  originator_id BIGINT REFERENCES users(id),
  title VARCHAR(255),
  recurrence BYTEA,
  record_option BOOLEAN DEFAULT false,
  default_duration_minutes INT DEFAULT 60,
  join_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  last_updated TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meeting_sessions (
  id BIGSERIAL PRIMARY KEY,
  meeting_id BIGINT REFERENCES meetings(id),
  scheduled_at TIMESTAMP,
  started_at TIMESTAMP,
  ended_at TIMESTAMP,
  actual_duration NUMERIC(21,0),
  status VARCHAR(20) DEFAULT 'upcoming'
);

CREATE TABLE IF NOT EXISTS meeting_participants (
  id BIGSERIAL PRIMARY KEY,
  session_id BIGINT REFERENCES meeting_sessions(id),
  user_id BIGINT REFERENCES users(id),
  joined_at TIMESTAMP,
  left_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS meeting_recordings (
  id BIGSERIAL PRIMARY KEY,
  session_id BIGINT REFERENCES meeting_sessions(id),
  url VARCHAR(255),
  duration NUMERIC(21,0),
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS meeting_messages (
  id BIGSERIAL PRIMARY KEY,
  session_id BIGINT REFERENCES meeting_sessions(id),
  sender_id BIGINT REFERENCES users(id),
  content TEXT,
  child_comment_id BIGINT REFERENCES meeting_messages(id),
  sent_at TIMESTAMP DEFAULT now()
);

-- ========== NOTIFICATIONS ==========
CREATE TABLE IF NOT EXISTS notifications (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(id),
  title TEXT,
  message TEXT,
  link_url VARCHAR(255),
  is_read BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT now()
);

