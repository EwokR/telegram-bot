- liquibase formatted sql

-- changeSet: AKorneev:1

-- creating table notification_task

CREATE TABLE notification_task
(
    id SERIAL PRIMARY KEY,
    chat_id integer,
    notification TEXT NOT NULL,
    time TIMESTAMP
);

-- creating table interaction

CREATE TABLE interaction
(
    id SERIAL PRIMARY KEY,
    request TEXT,
    response TEXT NOT NULL
);

-- changeSet: AKorneev: 2

CREATE INDEX request_index ON interaction (request);

CREATE INDEX time_index ON notification_task (time);

-- add some basic requests into table interaction

INSERT INTO interaction (request, response)
VALUES
    ('/holla', 'Bot by Ewokr at your service. Use command /help for further information'),
    ('/help', 'I''m bot senior Hernando. Note that I was made at bench scale, so be patient, please. You can use command /commands for further information about the list of available commands or you can try to type random command that starts with / eldorado.'),
    ('/commands', 'Here is the list of commands: \n /holla \n /help \n /commands \n /notification'),
    ('/notification', 'You can add notification typing line using the next format: dd.MM.yyyy HH:mm yours_text. Example : 23.11.2022 10:00 Pay the bills');