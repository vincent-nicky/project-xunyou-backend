/*
 Navicat Premium Data Transfer

 Source Server         : local-win
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : myproject_xunyou

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 04/08/2023 16:21:14
*/

CREATE DATABASE project_xunyou;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

use project_xunyou;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '队伍名称',
                         `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍的头像',
                         `bgImgUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍的背景图',
                         `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                         `maxNum` int NOT NULL DEFAULT 1 COMMENT '最大人数',
                         `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
                         `userId` bigint NULL DEFAULT NULL COMMENT '用户id（队长 id）',
                         `status` int NOT NULL DEFAULT 0 COMMENT '0 - 公开，1 - 私有，2 - 加密',
                         `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '队伍' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, 'Logistics coordinator', 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/team/d35389ce-c4eb-4567-8f79-ae726fa9425c', 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/team/10d1fcb5-3430-4074-abaf-5e7b06d682ba', 'If opportunity doesn’t knock, build a door.', 6, '2023-09-29 05:14:13', 9, 0, '', '2022-10-22 03:38:17', '2023-07-26 23:00:19', 0);
INSERT INTO `team` VALUES (2, 'Database manager', 'https://picsum.photos/480/640/?random', 'http://loremflickr.com/640/480', 'The Navigation pane employs tree structure which allows you to take action upon the                 ', 7, '2023-08-17 02:56:54', 100004, 0, '', '2019-02-05 19:54:28', '2023-07-26 23:00:16', 0);
INSERT INTO `team` VALUES (3, 'Information security analyst', 'https://picsum.photos/seed/picsum/480/640', 'http://loremflickr.com/640/480', 'If the Show objects under schema in navigation pane option is checked at the Preferences            ', 8, '2023-08-07 14:16:19', 8, 2, '12345', '2000-04-15 22:51:48', '2023-07-26 23:01:07', 0);
INSERT INTO `team` VALUES (4, 'Information security analyst', 'https://picsum.photos/480/640/?random', 'http://loremflickr.com/640/480', 'If you wait, all that happens is you get older. To open a query using an external                   ', 10, '2023-10-01 18:46:47', 100006, 0, '', '2016-08-31 20:16:23', '2023-07-26 23:00:30', 0);
INSERT INTO `team` VALUES (5, 'Retail sales associate', 'http://loremflickr.com/480/640', 'http://loremflickr.com/640/480', 'Navicat Monitor can be installed on any local computer or virtual machine and does                  ', 9, '2023-08-05 09:41:32', 4, 2, '12345', '2009-02-16 20:26:02', '2023-07-26 23:01:10', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
                         `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
                         `bgImgUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户首页背景图',
                         `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
                         `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                         `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
                         `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
                         `userStatus` int NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                         `userRole` int NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
                         `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 json 列表',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('柠七', 1, NULL, 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/user/35bb1b7e-0111-41a7-974e-290238b41637', 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/user/6dcf700e-c9a3-401d-9bab-2afbe583d985', 0, '12345678', '18476514394', '1935496654@qq.com', 0, '2023-07-25 16:15:14', '2023-07-26 21:32:15', 0, 0, '[\"篮球\",\"java\",\"c++\",\"react\"]');
INSERT INTO `user` VALUES ('程睿', 2, '011169', 'https://avatars.dicebear.com/api/avataaars/nl.svg', 'https://picsum.photos/640/520/?random', 0, '073814835', '17430950813', 'rcheng117@gmail.com', 0, '2001-07-15 15:54:45', '2023-07-26 18:42:12', 0, 0, '[\"python\"]');
INSERT INTO `user` VALUES ('向安琪', 3, '160110', 'https://avatars.dicebear.com/api/avataaars/76CJs9.svg', 'https://picsum.photos/640/520/?random', 0, '302797998', '11653216445', 'xiang727@icloud.com', 0, '2020-11-26 12:25:29', '2023-07-26 18:42:12', 0, 0, '[\"python\"]');
INSERT INTO `user` VALUES ('傅嘉伦', 4, '026501', 'https://avatars.dicebear.com/api/avataaars/Q4NN.svg', 'https://picsum.photos/640/520/?random', 0, '187636215', '18220293942', 'jialun6@gmail.com', 0, '2007-06-01 14:27:46', '2023-07-26 18:42:12', 0, 0, '[\"python\"]');
INSERT INTO `user` VALUES ('严岚', 5, '180657', 'https://avatars.dicebear.com/api/avataaars/uvKriH.svg', 'https://picsum.photos/640/520/?random', 1, '466498515', '14933597482', 'lanyan@icloud.com', 0, '2018-09-08 08:55:36', '2023-07-26 18:42:12', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('丁睿', 6, '718539', 'https://avatars.dicebear.com/api/avataaars/CHCVw.svg', 'https://picsum.photos/640/520/?random', 1, '560213336', '18482546606', 'ding7@qq.com', 0, '2001-03-30 21:42:12', '2023-07-26 18:42:12', 0, 0, '[\"java\",\"篮球\",\"vue\"]');
INSERT INTO `user` VALUES ('向震南', 7, '756720', 'https://avatars.dicebear.com/api/avataaars/Ha.svg', 'https://picsum.photos/640/520/?random', 0, '908088343', '18235268105', 'zhennanxiang14@outlook.com', 0, '2019-12-09 19:27:36', '2023-07-26 18:42:12', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('叶詩涵', 8, '326408', 'https://avatars.dicebear.com/api/avataaars/09u.svg', 'https://picsum.photos/640/520/?random', 0, '050595585', '12426764186', 'ye00@gmail.com', 0, '2004-11-03 14:58:32', '2023-07-26 18:42:12', 0, 0, '[\"python\"]');
INSERT INTO `user` VALUES ('雷安琪', 9, '913521', 'https://avatars.dicebear.com/api/avataaars/9pPgQ.svg', 'https://picsum.photos/640/520/?random', 1, '942102854', '17449372642', 'leianq@outlook.com', 0, '2013-07-29 18:13:32', '2023-07-26 18:42:12', 0, 0, '[\"c++\"]');
INSERT INTO `user` VALUES ('郑睿', 10, '730101', 'https://avatars.dicebear.com/api/avataaars/eluXR.svg', 'https://picsum.photos/640/520/?random', 0, '326926210', '19443626111', 'ruzheng1107@gmail.com', 0, '2003-07-22 23:10:23', '2023-07-26 18:42:12', 0, 0, '[\"vue\",\"java\"]');
INSERT INTO `user` VALUES ('邵璐', 11, '226007', 'https://avatars.dicebear.com/api/avataaars/DDuTO.svg', 'https://picsum.photos/640/520/?random', 0, '547354168', '13910628224', 'lu5@icloud.com', 0, '2000-10-27 18:49:41', '2023-07-26 18:42:12', 0, 0, '[\"java\",\"篮球\"]');
INSERT INTO `user` VALUES ('Charles Rivera', 100001, '999646', 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/user/5ffd7015-f95c-4dd5-bc03-d3e359baa229', 'http://rxxokggp3.hn-bkt.clouddn.com/myProject-xunyou/user/30084b75-f7b7-4f7d-a77a-a23853480035', 0, '264121117', '12266083833', 'rivera83@icloud.com', 0, '2021-05-15 03:18:34', '2023-07-26 18:42:06', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('Ruby Simpson', 100002, '600184', 'https://randomuser.me/api/portraits/men/10.jpg', 'https://picsum.photos/seed/picsum/640/480', 0, '979023698', '12587756527', 'rubysi4@gmail.com', 0, '2002-05-28 15:09:18', '2008-07-02 08:34:49', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('Kathy Jimenez', 100003, '360967', 'https://randomuser.me/api/portraits/men/8.jpg', 'https://picsum.photos/seed/picsum/640/480', 1, '842128577', '19558251379', 'jimenezkathy@qq.com', 0, '2007-02-05 01:18:24', '2016-09-27 20:33:04', 0, 0, '[\"c++\"]');
INSERT INTO `user` VALUES ('Aaron Long', 100004, '174856', 'https://randomuser.me/api/portraits/women/94.jpg', 'https://picsum.photos/seed/picsum/640/480', 0, '522808703', '11913487493', 'longaaro@outlook.com', 0, '2007-07-31 05:17:54', '2021-02-18 12:17:43', 0, 0, '[\"vue\"]');
INSERT INTO `user` VALUES ('Joel Clark', 100005, '351308', 'https://randomuser.me/api/portraits/men/2.jpg', 'https://picsum.photos/seed/picsum/640/480', 1, '460930570', '11805475742', 'jc5@qq.com', 0, '2023-03-24 03:23:10', '2008-05-23 15:32:54', 0, 0, '[\"c++\"]');
INSERT INTO `user` VALUES ('Carrie Henry', 100006, '787134', 'https://randomuser.me/api/portraits/women/90.jpg', 'http://loremflickr.com/640/480', 1, '469200528', '16768325083', 'henrycarr@outlook.com', 0, '2002-07-26 08:07:39', '2000-07-26 15:45:08', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('Danielle Brooks', 100007, '099922', 'https://randomuser.me/api/portraits/men/24.jpg', 'https://picsum.photos/640/480/?random', 1, '182184344', '18447846837', 'daniellebr104@icloud.com', 0, '2001-02-11 18:08:45', '2008-07-09 05:50:13', 0, 0, '[\"c++\"]');
INSERT INTO `user` VALUES ('Josephine Ramos', 100008, '243986', 'https://randomuser.me/api/portraits/men/69.jpg', 'https://picsum.photos/seed/picsum/640/480', 1, '937825697', '13308935324', 'joseramos@qq.com', 0, '2022-12-03 15:55:44', '2015-01-29 02:01:56', 0, 0, '[\"vue\"]');
INSERT INTO `user` VALUES ('Alan Lopez', 100009, '096819', 'https://randomuser.me/api/portraits/men/3.jpg', 'http://loremflickr.com/640/480', 0, '235767463', '14872337789', 'alanlopez@qq.com', 0, '2019-02-01 06:12:11', '2007-08-13 03:29:50', 0, 0, '[\"react\"]');
INSERT INTO `user` VALUES ('Victor Mills', 100010, '201180', 'https://randomuser.me/api/portraits/women/76.jpg', 'https://picsum.photos/640/480/?random', 0, '842079255', '11042539841', 'victor6@outlook.com', 0, '2009-06-03 21:51:57', '2005-12-19 17:30:23', 0, 0, '[\"java\"]');

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
                              `teamId` bigint NULL DEFAULT NULL COMMENT '队伍id',
                              `joinTime` datetime NULL DEFAULT NULL COMMENT '加入时间',
                              `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户队伍关系' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_team
-- ----------------------------
INSERT INTO `user_team` VALUES (1, 1, 2, '2023-07-26 19:34:59', '2023-07-26 19:34:58', '2023-07-26 19:34:58', 0);
INSERT INTO `user_team` VALUES (2, 8, 3, '2001-06-18 07:30:09', '2008-03-29 09:44:45', '2023-03-05 15:10:46', 0);
INSERT INTO `user_team` VALUES (3, 4, 5, '2016-10-06 14:25:49', '2019-12-30 19:33:27', '2016-02-01 11:17:36', 0);
INSERT INTO `user_team` VALUES (4, 100004, 2, '2001-04-14 02:00:32', '2000-01-15 00:38:51', '2018-06-15 08:16:56', 0);
INSERT INTO `user_team` VALUES (5, 100006, 4, '2000-10-24 06:40:17', '2010-06-21 01:43:27', '2021-10-27 21:08:56', 0);
INSERT INTO `user_team` VALUES (6, 9, 1, '2023-04-07 18:41:30', '2019-03-26 14:38:57', '2012-11-14 21:19:20', 0);

SET FOREIGN_KEY_CHECKS = 1;
