-- chat tables
CREATE TABLE IF NOT EXISTS `s2_chat_context`
(
    `chat_id`        BIGINT NOT NULL , -- context chat id
    `modified_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP , -- row modify time
    `user`           varchar(64) DEFAULT NULL , -- row modify user
    `query_text`     LONGVARCHAR DEFAULT NULL , -- query text
    `semantic_parse` LONGVARCHAR DEFAULT NULL , -- parse data
    `ext_data`       LONGVARCHAR DEFAULT NULL , -- extend data
    PRIMARY KEY (`chat_id`)
    );

CREATE TABLE IF NOT EXISTS `s2_chat`
(
    `chat_id`       BIGINT auto_increment ,-- AUTO_INCREMENT,
    `agent_id`       INT DEFAULT NULL,
    `chat_name`     varchar(100) DEFAULT NULL,
    `create_time`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    `last_time`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    `creator`       varchar(30)  DEFAULT NULL,
    `last_question` varchar(200) DEFAULT NULL,
    `is_delete`     INT DEFAULT '0' COMMENT 'is deleted',
    `is_top`        INT DEFAULT '0' COMMENT 'is top',
    PRIMARY KEY (`chat_id`)
    ) ;


CREATE TABLE `s2_chat_query`
(
    `question_id`             BIGINT  NOT NULL AUTO_INCREMENT,
    `agent_id`             INT  NULL,
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `query_text`          mediumtext,
    `user_name`         varchar(150)  DEFAULT NULL COMMENT '',
    `query_state`             int(1) DEFAULT NULL,
    `chat_id`           BIGINT NOT NULL , -- context chat id
    `query_result` mediumtext NOT NULL ,
    `score`             int DEFAULT '0',
    `feedback`          varchar(1024) DEFAULT '',
    PRIMARY KEY (`question_id`)
);

CREATE TABLE `s2_chat_parse`
(
    `question_id`             BIGINT  NOT NULL,
    `chat_id`           BIGINT NOT NULL ,
    `parse_id`          INT NOT NULL ,
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `query_text`          varchar(500),
    `user_name`         varchar(150)  DEFAULT NULL COMMENT '',
    `parse_info` mediumtext NOT NULL ,
    `is_candidate` INT DEFAULT 1 COMMENT '1是candidate,0是selected'
);

CREATE TABLE `s2_chat_statistics`
(
    `question_id`             BIGINT  NOT NULL,
    `chat_id`           BIGINT NOT NULL ,
    `user_name`         varchar(150)  DEFAULT NULL COMMENT '',
    `query_text`          varchar(200),
    `interface_name`         varchar(100)  DEFAULT NULL COMMENT '',
    `cost` INT(6) NOT NULL ,
    `type` INT NOT NULL ,
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `s2_chat_config` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `model_id` INT DEFAULT NULL ,
                                                `chat_detail_config` varchar(655) ,
    `chat_agg_config` varchar(655)    ,
    `recommended_questions`  varchar(1500)    ,
    `created_at` TIMESTAMP  NOT NULL   ,
    `updated_at` TIMESTAMP  NOT NULL   ,
    `created_by` varchar(100) NOT NULL   ,
    `updated_by` varchar(100) NOT NULL   ,
    `status` INT NOT NULL  DEFAULT '0' , -- domain extension information status : 0 is normal, 1 is off the shelf, 2 is deleted
    PRIMARY KEY (`id`)
    ) ;
COMMENT ON TABLE s2_chat_config IS 'chat config information table ';


create table s2_user
(
    id       INT AUTO_INCREMENT,
    name     varchar(100) not null,
    display_name varchar(100) null,
    password varchar(100) null,
    email varchar(100) null,
    is_admin INT null,
    PRIMARY KEY (`id`)
);
COMMENT ON TABLE s2_user IS 'user information table';


CREATE TABLE IF NOT EXISTS `s2_semantic_pasre_info` (
                                                        `id` INT NOT NULL AUTO_INCREMENT,
                                                        `trace_id` varchar(200) NOT NULL  ,
    `model_id` INT  NOT NULL ,
    `dimensions`LONGVARCHAR ,
    `metrics`LONGVARCHAR ,
    `orders`LONGVARCHAR ,
    `filters`LONGVARCHAR ,
    `date_info`LONGVARCHAR ,
    `limit` INT NOT NULL ,
    `native_query` TINYINT NOT NULL DEFAULT '0' ,
    `sql`LONGVARCHAR ,
    `created_at` TIMESTAMP  NOT NULL ,
    `created_by` varchar(100) NOT NULL ,
    `status` INT NOT NULL ,
    `elapsed_ms` bigINT DEFAULT NULL ,
    PRIMARY KEY (`id`)
    );
COMMENT ON TABLE s2_semantic_pasre_info IS 'semantic layer sql parsing information table';


CREATE TABLE IF NOT EXISTS `s2_plugin`
(
    `id`         INT AUTO_INCREMENT,
    `type`      varchar(50)   NULL,
    `model`     varchar(100)  NULL,
    `pattern`    varchar(500)  NULL,
    `parse_mode` varchar(100)  NULL,
    `parse_mode_config` LONGVARCHAR  NULL,
    `name`       varchar(100)  NULL,
    `created_at` TIMESTAMP   NULL,
    `created_by` varchar(100) null,
    `updated_at` TIMESTAMP    NULL,
    `updated_by` varchar(100) NULL,
    `config`     LONGVARCHAR  NULL,
    `comment`     LONGVARCHAR  NULL,
    PRIMARY KEY (`id`)
    ); COMMENT ON TABLE s2_plugin IS 'plugin information table';

CREATE TABLE IF NOT EXISTS s2_agent
(
    id          int AUTO_INCREMENT,
    name        varchar(100)  null,
    description varchar(500) null,
    status       int null,
    examples    varchar(500) null,
    config      varchar(2000)  null,
    created_by  varchar(100) null,
    created_at  TIMESTAMP  null,
    updated_by  varchar(100) null,
    updated_at  TIMESTAMP null,
    enable_search int null,
    PRIMARY KEY (`id`)
    ); COMMENT ON TABLE s2_agent IS 'agent information table';


-------demo for semantic and chat
CREATE TABLE IF NOT EXISTS `s2_user_department` (
    `user_name` varchar(200) NOT NULL,
    `department` varchar(200) NOT NULL -- department of user
    );
COMMENT ON TABLE s2_user_department IS 'user_department_info';


CREATE TABLE IF NOT EXISTS `s2_dictionary_task` (
                                                    `id` INT NOT NULL AUTO_INCREMENT,
                                                    `name` varchar(255) NOT NULL , -- task name
    `description` varchar(255) ,
    `command`LONGVARCHAR  NOT NULL , -- task Request Parameters
    `command_md5` varchar(255)  NOT NULL , -- task Request Parameters md5
    `status` INT NOT NULL , -- the final status of the task
    `dimension_ids` varchar(500)  NULL ,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP  ,
    `created_by` varchar(100) NOT NULL ,
    `progress` DOUBLE default 0.00  ,  -- task real-time progress
    `elapsed_ms` bigINT DEFAULT NULL , -- the task takes time in milliseconds
    `message` LONGVARCHAR  , -- remark related information
    PRIMARY KEY (`id`)
    );
COMMENT ON TABLE s2_dictionary_task IS 'dictionary task information table';




