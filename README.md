# ddal-demos

- This project lists some useful demos to show how to use DDAL in different scenes. If you want to know more about DDAL see [DDAL wiki](https://github.com/hellojavaer/ddal/wiki).

## Demo0

- 0. design a scene

```
Assume that you need to route 'base' schema and 'user' table,
and the schema's route rule is "{#scName}_{#format('%02d', #sdValue % 2)}",
the table's route rule is "{#tbName}_{#format('%04d', #sdValue % 8)}"

By useing the route rule, we can get the following physical tables:
base_00.user_0000;
base_00.user_0002;
base_00.user_0004;
base_00.user_0005;
base_01.user_0001;
base_01.user_0003;
base_01.user_0005;
base_01.user_0007;
```

- 1. initialize database and table

use [InitDatabaseSqlBuilder.java](https://github.com/hellojavaer/ddal-demos/blob/master/demo0/src/main/java/org/hellojavaer/ddal/demos/demo0/init/InitDatabaseSqlBuilder.java) to build the following sql

```
CREATE DATABASE `base_00` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use base_00;
CREATE TABLE sequence (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    schema_name varchar(32) NOT NULL,
    table_name varchar(64) NOT NULL,
    begin_value bigint(20) NOT NULL,
    next_value bigint(20) NOT NULL,
    end_value bigint(20) DEFAULT NULL,
    select_order int(11) NOT NULL,
    version bigint(20) NOT NULL DEFAULT '0',
    deleted tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (id),
    KEY idx_table_name_schema_name_deleted_select_order (table_name,schema_name,deleted,select_order) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `sequence` (`id`, `schema_name`, `table_name`, `begin_value`, `next_value`, `end_value`, `select_order`, `version`, `deleted`)
VALUES (1, 'base', 'user', 0, 0, NULL, 0, 0, 0);

CREATE TABLE `user_0000`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0002`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0004`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0006`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;

CREATE DATABASE `base_01` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use base_01;
CREATE TABLE `user_0001`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0003`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0005`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
CREATE TABLE `user_0007`(`id` int(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;
```

- 2. modify database config in file 'db.properties'

```
jdbc.url=jdbc:mysql://127.0.0.1:3306/base_00?verifyServerCertificate=false&useSSL=true
jdbc.w.username=w_base
jdbc.w.password=password
jdbc.r0.username=r_base
jdbc.r0.password=password
```

- 3. run and check test cases

```
org.hellojavaer.ddal.demos.demo0.UserDaoTest.baseTestForCRUD
org.hellojavaer.ddal.demos.demo0.UserDaoTest.scanQueryAll
org.hellojavaer.ddal.demos.demo0.UserDaoTest.groupRouteInfo
```

## License

ddal-demos is licensed under **MIT License**.

