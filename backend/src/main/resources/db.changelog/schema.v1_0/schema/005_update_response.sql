ALTER table question_responses
add column mcp_id bigint references mcq_contents(id);