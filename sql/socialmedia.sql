DROP TABLE Awards;
DROP TABLE Comments;
DROP TABLE HelpTickets;
DROP TABLE Videos;
DROP TABLE Images;
DROP TABLE TextPosts;
DROP TABLE Posts;
DROP TABLE MemberOf;
DROP TABLE Attends;
DROP TABLE Follows;
DROP TABLE Events;
DROP TABLE Users;
DROP TABLE Admins;
DROP TABLE StorageSizes;
DROP TABLE Communities;
DROP TABLE StarSigns;
DROP TABLE Genres;

CREATE TABLE Genres(
                       genre VARCHAR(255) PRIMARY KEY,
                       capacity INTEGER NOT NULL,
                       ageRange VARCHAR(255) NOT NULL
);

CREATE TABLE StarSigns(
                          birthDay INTEGER,
                          birthMonth VARCHAR(16),
                          starSign VARCHAR(255) NOT NULL,
                          PRIMARY KEY(birthDay, birthMonth)
);

CREATE TABLE Communities(
                            name VARCHAR(255) PRIMARY KEY,
                            description VARCHAR(255) NOT NULL,
                            genre VARCHAR(255) DEFAULT 'General'
);

CREATE TABLE StorageSizes(
                             duration INT PRIMARY KEY,
                             size_type VARCHAR(255) NOT NULL
);

CREATE TABLE Admins(
                       adminID VARCHAR(36) PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       experienceLevel VARCHAR(255) NOT NULL
);

CREATE TABLE Users(
                      userName VARCHAR(255) PRIMARY KEY,
                      biography VARCHAR(255),
                      firstName VARCHAR(255) NOT NULL,
                      birthDay INTEGER NOT NULL,
                      birthMonth VARCHAR(16) NOT NULL,
                      birthYear INTEGER NOT NULL,
                      FOREIGN KEY (birthDay, birthMonth) REFERENCES StarSigns(birthDay, birthMonth)
);

CREATE TABLE Events(
                       eventName VARCHAR(255),
                       dateTime TIMESTAMP NOT NULL,
                       location VARCHAR(255),
                       communityName VARCHAR(255) NOT NULL,
                       PRIMARY KEY (eventName, dateTime),
                       FOREIGN KEY (communityName) REFERENCES Communities(name)
);

CREATE TABLE Follows(
                        userName VARCHAR(255),
                        followsUserName VARCHAR(255),
                        PRIMARY KEY(userName, followsUserName),
                        FOREIGN KEY(userName) REFERENCES Users(userName),
                        FOREIGN KEY(followsUserName) REFERENCES Users(userName)
);

CREATE TABLE Attends(
                        userName VARCHAR(255),
                        eventName VARCHAR(255),
                        dateTime TIMESTAMP,
                        PRIMARY KEY(userName, eventName, dateTime),
                        FOREIGN KEY(userName) REFERENCES Users(userName),
                        FOREIGN KEY(eventName, datetime) REFERENCES Events(eventName, dateTime)
);

CREATE TABLE MemberOf(
                         userName VARCHAR(255),
                         communityName VARCHAR(255),
                         PRIMARY KEY(userName, communityName),
                         FOREIGN KEY(userName) REFERENCES Users(userName),
                         FOREIGN KEY(communityName) REFERENCES Communities(name)
);

CREATE TABLE Posts(
                      postId VARCHAR(36) PRIMARY KEY,
                      title VARCHAR(255),
                      userName VARCHAR(255) NOT NULL,
                      communityName VARCHAR(255) NOT NULL,
                      dateTime TIMESTAMP NOT NULL,
                      UNIQUE(userName, dateTime),
                      FOREIGN KEY(userName) REFERENCES Users(userName),
                      FOREIGN KEY(communityName) REFERENCES Communities(name)
);

CREATE TABLE Images(
                       postId VARCHAR(36) PRIMARY KEY,
                       caption VARCHAR(255),
                       FOREIGN KEY(postId) REFERENCES Posts(postId) ON DELETE CASCADE
);

CREATE TABLE Textposts(
                          postId VARCHAR(36) PRIMARY KEY,
                          text VARCHAR(255) NOT NULL,
                          FOREIGN KEY(postId) REFERENCES Posts(postId) ON DELETE CASCADE
);

CREATE TABLE Videos(
                       postId VARCHAR(36) PRIMARY KEY,
                       caption VARCHAR(255),
                       duration INT NOT NULL,
                       FOREIGN KEY (duration) REFERENCES StorageSizes(duration),
                       FOREIGN KEY (postId) REFERENCES Posts(postId) ON DELETE CASCADE
);

CREATE TABLE HelpTickets(
                            ticket VARCHAR(36) PRIMARY KEY,
                            description VARCHAR(255) NOT NULL,
                            type VARCHAR(255) NOT NULL,
                            userName VARCHAR(255) NOT NULL,
                            adminId VARCHAR(36),
                            FOREIGN KEY (userName) REFERENCES Users(userName),
                            FOREIGN KEY (adminId) REFERENCES Admins(adminID) ON DELETE CASCADE
);

CREATE TABLE Comments(
                         commentId VARCHAR(36) PRIMARY KEY,
                         userName VARCHAR(255) NOT NULL,
                         postId VARCHAR(36),
                         text VARCHAR(255) NOT NULL,
                         dateTime TIMESTAMP NOT NULL,
                         FOREIGN KEY (userName) REFERENCES Users(userName),
                         FOREIGN KEY (postId) REFERENCES Posts(postId) ON DELETE CASCADE,
                         UNIQUE (userName, dateTime)
);

CREATE TABLE Awards(
                       type VARCHAR(255),
                       postId VARCHAR(36),
                       cost FLOAT NOT NULL,
                       PRIMARY KEY (postId, type),
                       FOREIGN KEY (postId) REFERENCES Posts(postId) ON DELETE CASCADE
);

INSERT INTO Genres VALUES('Sports', 1000, '12-30');
INSERT INTO Genres VALUES('Health', 200, '19-60');
INSERT INTO Genres VALUES('Gaming', 200, '11-19');
INSERT INTO Genres VALUES('Finance', 200, '20-45');
INSERT INTO Genres VALUES('Tech', 200, '19-30');

INSERT INTO StarSigns VALUES(12, 'January', 'Capricorn');
INSERT INTO StarSigns VALUES(10,'November', 'Scorpio');
INSERT INTO StarSigns VALUES(9,'August', 'Leo');
INSERT INTO StarSigns VALUES(8,'September', 'Virgo');
INSERT INTO StarSigns VALUES(7,'April', 'Aries');

INSERT INTO Communities VALUES('Stocks','The top community for all things stock related.', 'Finance');
INSERT INTO Communities VALUES('Gym','Big gains', 'Health');
INSERT INTO Communities VALUES('Party','Big fun', 'Life');
INSERT INTO Communities VALUES('Minecraft','Minecraft community for building', 'Gaming');
INSERT INTO Communities VALUES('LiverpoolFC','The community for the reds', 'Sports');
INSERT INTO Communities VALUES('Databases','Nerdiest Community in Town', 'Computer Science');

INSERT INTO StorageSizes VALUES(120, 'Medium');
INSERT INTO StorageSizes VALUES(30, 'Small');
INSERT INTO StorageSizes VALUES(200, 'Medium');
INSERT INTO StorageSizes VALUES(600, 'Large');
INSERT INTO StorageSizes VALUES(1200, 'Large');

INSERT INTO Admins VALUES(1, 'james', 'King');
INSERT INTO Admins VALUES(2, 'elizabeth', 'Queen');
INSERT INTO Admins VALUES(3, 'andjill', 'Jack');
INSERT INTO Admins VALUES(4, 'littlemonkeys', 'Ten');
INSERT INTO Admins VALUES(5, 'tails', 'Nine');

INSERT INTO Users VALUES('dman','Cool Bio Alert', 'Daven', 12, 'January', 2003);
INSERT INTO Users VALUES('304ta','im a cool TA!', 'Joe', 10, 'November', 2003);
INSERT INTO Users VALUES('sevaman','Worlds Best Prof', 'Seva', 8, 'September', 1990);
INSERT INTO Users VALUES('olti123','the best group member', 'Olti', 9, 'August', 2003);
INSERT INTO Users VALUES('rickybangbang', 'elite engineer', 'Ricky', 7, 'April', 2003);
INSERT INTO Users VALUES('rickyaa', 'elite', 'Rickyaaaa', 7, 'April', 2004);
INSERT INTO Users VALUES('jamesss', 'isa', 'jamesor', 7, 'April', 1990);

INSERT INTO Events VALUES('Workout Day', '2024-10-31 10:00:00', 'UBC', 'Gym');
INSERT INTO Events VALUES('Christmas Stock Celebration', '2024-12-25 11:00:00', 'UBC', 'Stocks');
INSERT INTO Events VALUES('Minecraft Thanksgiving Event', '2024-10-09 12:00:00', 'UBC', 'Minecraft');
INSERT INTO Events VALUES('SQL Party', '2024-01-31 13:00:00', 'UBC', 'Databases');
INSERT INTO Events VALUES('Cardio Hating Party', '2024-03-01 14:00:00', 'UBC', 'Gym');
INSERT INTO Events VALUES('SFU Party', '2023-06-21 11:00:00', 'SFU', 'Party');
INSERT INTO Events VALUES('Cardio Loving Party', '2023-06-20 11:00:00', 'SFU', 'Gym');
INSERT INTO Events VALUES('Push Day', '2023-06-19 10:00:00', 'SFU', 'Gym');
INSERT INTO Events VALUES('Pull Day', '2023-06-19 10:00:00', 'BCIT', 'Gym');

INSERT INTO Follows VALUES('rickybangbang','sevaman');
INSERT INTO Follows VALUES('olti123','sevaman');
INSERT INTO Follows VALUES('304ta','sevaman');
INSERT INTO Follows VALUES('sevaman','olti123');
INSERT INTO Follows VALUES('dman','rickybangbang');

INSERT INTO Attends VALUES('olti123','Workout Day', '2024-10-31 10:00:00');
INSERT INTO Attends VALUES('rickybangbang','Christmas Stock Celebration', '2024-12-25 11:00:00');
INSERT INTO Attends VALUES('304ta','Minecraft Thanksgiving Event', '2024-10-09 12:00:00');
INSERT INTO Attends VALUES('sevaman','SQL Party', '2024-01-31 13:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Workout Day', '2024-10-31 10:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Minecraft Thanksgiving Event', '2024-10-09 12:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'SQL Party', '2024-01-31 13:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'SFU Party', '2023-06-21 11:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Cardio Loving Party', '2023-06-20 11:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Push Day', '2023-06-19 10:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Pull Day', '2023-06-19 10:00:00');
INSERT INTO Attends VALUES('rickybangbang', 'Cardio Hating Party', '2024-03-01 14:00:00');
INSERT INTO Attends VALUES('dman', 'Workout Day', '2024-10-31 10:00:00');
INSERT INTO Attends VALUES('dman', 'Christmas Stock Celebration', '2024-12-25 11:00:00');
INSERT INTO Attends VALUES('dman', 'Minecraft Thanksgiving Event', '2024-10-09 12:00:00');
INSERT INTO Attends VALUES('dman', 'SQL Party', '2024-01-31 13:00:00');
INSERT INTO Attends VALUES('dman', 'SFU Party', '2023-06-21 11:00:00');
INSERT INTO Attends VALUES('dman', 'Cardio Loving Party', '2023-06-20 11:00:00');
INSERT INTO Attends VALUES('dman', 'Push Day', '2023-06-19 10:00:00');
INSERT INTO Attends VALUES('dman', 'Pull Day', '2023-06-19 10:00:00');
INSERT INTO Attends VALUES('dman', 'Cardio Hating Party', '2024-03-01 14:00:00');

INSERT INTO MemberOf VALUES('rickybangbang','Stocks');
INSERT INTO MemberOf VALUES('olti123','Gym');
INSERT INTO MemberOf VALUES('304ta','Minecraft');
INSERT INTO MemberOf VALUES('sevaman','Databases');
INSERT INTO MemberOf VALUES('rickybangbang', 'Gym');
INSERT INTO MemberOf VALUES('rickybangbang', 'Party');

INSERT INTO Posts VALUES(1, 'Hello World', 'sevaman', 'Databases', '2023-10-20 23:59:59');
INSERT INTO Posts VALUES(2, 'Investing is Awesome!', 'rickybangbang', 'Stocks', '2023-08-01 10:30:12');
INSERT INTO Posts VALUES(3, 'How to get strong', 'olti123', 'Gym', '2023-11-01 14:21:00');
INSERT INTO Posts VALUES(4, 'Minecraft Lets Play', '304ta', 'Minecraft', '2023-10-08 03:00:00');
INSERT INTO Posts VALUES(5, 'How to get rich', 'rickybangbang', 'Gym', '2023-12-31 22:05:24');
INSERT INTO Posts VALUES(6, 'Databases 101', 'sevaman', 'Databases', '2023-10-02 20:49:12');
INSERT INTO Posts VALUES(7, 'Stocks for dummies', 'rickybangbang', 'Stocks', '2023-05-01 14:30:20');
INSERT INTO Posts VALUES(8, 'What is the gym', 'olti123', 'Gym', '2023-09-20 07:01:12');
INSERT INTO Posts VALUES(9, 'What is your favourite mob', '304ta', 'Minecraft', '2023-11-10 05:21:28');
INSERT INTO Posts VALUES(10, 'Working out at the gym for 24 hours challenge', 'rickybangbang', 'Gym', '2022-01-01 21:00:21');
INSERT INTO Posts VALUES(11, 'CPSC 304 Is The Best', 'sevaman', 'Databases', '2023-04-02 21:30:21');
INSERT INTO Posts VALUES(12, 'S AND P 500 Is Up!', 'rickybangbang', 'Stocks', '2023-08-01 10:31:12');
INSERT INTO Posts VALUES(13, 'Bicep Curls are for babies', 'olti123', 'Gym', '2023-09-02 08:32:40');
INSERT INTO Posts VALUES(14, 'My fav block review', '304ta', 'Minecraft', '2022-08-08 08:08:08');
INSERT INTO Posts VALUES(15, 'My legs are sore', 'rickybangbang', 'Gym', '2023-11-30 21:08:01');

INSERT INTO Images VALUES(1, 'This is my first post! Enjoy the photo :D');
INSERT INTO Images VALUES(2, 'Look at this stack of cash I made investing!');
INSERT INTO Images VALUES(9, 'Look at this minecraft pig!');
INSERT INTO Images VALUES(11, '3NF is my favourite');
INSERT INTO Images VALUES(15, 'IM HUUUUGE');

INSERT INTO Textposts VALUES(3, 'Here is a step by step guide on how to get strong. Step 1. Get strong!');
INSERT INTO Textposts VALUES(7, 'For all you dummies out there, listen up');
INSERT INTO Textposts VALUES(8, 'Today I am gonna talk about what is the gym.');
INSERT INTO Textposts VALUES(12, 'Check the market! Invest now!');
INSERT INTO Textposts VALUES(13, 'If you do bicep curls ur the worst!');

INSERT INTO Videos VALUES(4, 'Welcome back to my Minecraft video!', 120);
INSERT INTO Videos VALUES(5, 'Get rich with these quick tips!', 30);
INSERT INTO Videos VALUES(6, 'Watch to learn all about SQL!', 200);
INSERT INTO Videos VALUES(10, 'I almost fainted!', 600);
INSERT INTO Videos VALUES(14, 'Number one will surprise you!', 1200);

INSERT INTO HelpTickets VALUES(1, 'Spammed Craft', 'Spam', 'dman', 1);
INSERT INTO HelpTickets VALUES(2, 'Harassed', 'Harass', 'olti123', 1);
INSERT INTO HelpTickets VALUES(3, 'Racist Post', 'Racism', 'sevaman', 1);
INSERT INTO HelpTickets VALUES(4, 'Broke a Comment', 'Bug', 'sevaman', 1);
INSERT INTO HelpTickets VALUES(5, 'Was rude', 'Rude', 'dman', 1);
INSERT INTO HelpTickets VALUES(6, 'Roasted CPSC', 'Rude', 'olti123', 2);
INSERT INTO HelpTickets VALUES(7, 'Cried in Public', 'Crying', 'rickybangbang', 3);
INSERT INTO HelpTickets VALUES(8, 'Hacked the Mainframe', 'Hacking', 'sevaman', 4);
INSERT INTO HelpTickets VALUES(9, 'Exploited SQL Injection', 'Bug', 'rickybangbang', 2);
INSERT INTO HelpTickets VALUES(10, 'Broke his Computer', 'Broke', 'olti123', 5);

INSERT INTO Comments VALUES(1,  'olti123', 1, 'Daven is smart', '2024-12-24 22:05:24');
INSERT INTO Comments VALUES(2,  'rickybangbang', 1, 'Daven is not smart', '2024-12-25 22:05:25');
INSERT INTO Comments VALUES(3,  'olti123', 1, 'Daven is not smart', '2024-12-26 22:05:26');
INSERT INTO Comments VALUES(4,  'sevaman', 1,'Daven is very smart', '2024-12-27 22:05:27');
INSERT INTO Comments VALUES(5,  'olti123', 1, 'Daven is not so smart', '2024-12-28 22:05:28');

INSERT INTO Awards VALUES('Gold', 1, 1000.00);
INSERT INTO Awards VALUES('Silver', 1, 500.00);
INSERT INTO Awards VALUES('Gold', 2, 1000.00);
INSERT INTO Awards VALUES('Gold', 3, 1000.00);
INSERT INTO Awards VALUES('Platinum', 4, 5000.00);
