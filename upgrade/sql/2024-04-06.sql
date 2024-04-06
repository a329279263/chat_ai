ALTER TABLE `chat_ai`.`ai_chat_message_record`
    ADD COLUMN `answerId` bigint(20) NULL COMMENT 'ai回复的消息id' AFTER `deleted`;
