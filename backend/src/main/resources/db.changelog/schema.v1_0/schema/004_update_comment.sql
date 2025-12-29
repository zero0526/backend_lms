ALTER TABLE post_comments RENAME COLUMN child_comment_id TO parent_comment_id;

ALTER TABLE question_comments RENAME COLUMN child_comment_id TO parent_comment_id;

ALTER TABLE meeting_messages RENAME COLUMN child_comment_id TO parent_comment_id;
