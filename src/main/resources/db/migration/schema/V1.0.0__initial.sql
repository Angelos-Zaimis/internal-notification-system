CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    sender_id UUID,
    title TEXT,
    body TEXT,
    resource_id UUID,
    action_url TEXT,
    category VARCHAR(266) NOT NULL,
    level VARCHAR(266) NOT NULL,
    delivered BOOLEAN NOT NULL DEFAULT FALSE,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    retry_count INTEGER DEFAULT 3,
    send_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    translation_args JSONB
);
