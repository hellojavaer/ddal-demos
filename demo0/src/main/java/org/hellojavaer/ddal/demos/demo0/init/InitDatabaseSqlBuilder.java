/*
 * MIT License
 *
 * Copyright (c) 2017 hellojavaer@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.hellojavaer.ddal.demos.demo0.init;

import org.hellojavaer.ddal.ddr.shard.RouteInfo;
import org.hellojavaer.ddal.ddr.shard.ShardRouteUtils;
import org.hellojavaer.ddal.ddr.shard.ShardRouter;
import org.hellojavaer.ddal.ddr.shard.simple.SimpleShardParser;
import org.hellojavaer.ddal.ddr.shard.simple.SimpleShardRouteRuleBinding;
import org.hellojavaer.ddal.ddr.shard.simple.SimpleShardRouter;
import org.hellojavaer.ddal.ddr.shard.simple.SpelShardRouteRule;
import org.hellojavaer.ddal.jsqlparser.JSQLParser;

import java.util.*;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 12/07/2017.
 */
public class InitDatabaseSqlBuilder {

    // run this method
    public static void main(String[] args) {
        ShardRouter shardRouter = buildShardRouter();
        String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";
        String createTableSql = "CREATE TABLE `%s`(`id` bigint(20) NOT NULL, `name` varchar(32) NOT NULL, PRIMARY KEY (`id`), KEY `idx_name` (`name`) USING BTREE ) ENGINE=InnoDB;";
        build(shardRouter, createDatabaseSql, createTableSql, "base", "user");
    }

    public static void build(ShardRouter shardRouter, String createDatabaseSql, String createTableSql, String scName,
                             String tbName) {
        Map<String, List<RouteInfo>> groupedRouteInfos = ShardRouteUtils.groupRouteInfosByScName(shardRouter.getRouteInfos(scName,
                                                                                                                           tbName));
        boolean first = true;
        for (Map.Entry<String, List<RouteInfo>> entry : groupedRouteInfos.entrySet()) {
            System.out.println();
            System.out.println(String.format(createDatabaseSql, entry.getKey()));// create db
            System.out.println("use " + entry.getKey() + ";");// create db
            if (first) {
                // create sequence table
                first = false;
                System.out.println("CREATE TABLE sequence (\n"
                                   + "  id bigint(20) NOT NULL AUTO_INCREMENT,\n"
                                   + "  schema_name varchar(32) NOT NULL,\n"
                                   + "  table_name varchar(64) NOT NULL,\n"
                                   + "  begin_value bigint(20) NOT NULL,\n"
                                   + "  next_value bigint(20) NOT NULL,\n"
                                   + "  end_value bigint(20) DEFAULT NULL,\n"
                                   + "  step int(11),\n"
                                   + "  skip_n_steps int(11),\n"
                                   + "  select_order int(11) NOT NULL,\n"
                                   + "  version bigint(20) NOT NULL DEFAULT '0',\n"
                                   + "  deleted tinyint(1) NOT NULL DEFAULT '0',\n"
                                   + "  PRIMARY KEY (id),\n"
                                   + "  KEY idx_table_name_schema_name_deleted_select_order (table_name,schema_name,deleted,select_order) USING BTREE\n"
                                   + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                System.out.println("INSERT INTO sequence(id,schema_name,table_name,begin_value,next_value,end_value,step,skip_n_steps,select_order,version,deleted)"
                                   + "\n  VALUES(1, 'base', 'user', 0, 0, NULL, NULL, NULL, 0, 0, 0);");
            }
            for (RouteInfo routeInfo : entry.getValue()) {
                System.out.println(String.format(createTableSql, routeInfo.getTbName()));// create db
            }
        }
    }

    private static ShardRouter buildShardRouter() {
        SimpleShardParser parser = new SimpleShardParser();
        SimpleShardRouter shardRouter = new SimpleShardRouter();
        List<SimpleShardRouteRuleBinding> bindings = new ArrayList<>();
        // def route rule
        SpelShardRouteRule idRouteRule = new SpelShardRouteRule();
        idRouteRule.setScRouteRule("{scName}_{format('%02d', sdValue % 2)}");
        idRouteRule.setTbRouteRule("{tbName}_{format('%04d', sdValue % 8)}");

        // bind route rule to logical table
        SimpleShardRouteRuleBinding user = new SimpleShardRouteRuleBinding();
        user.setScName("base");
        user.setTbName("user");
        user.setSdKey("id");
        user.setSdValues("[0~7]");
        user.setRule(idRouteRule);
        bindings.add(user);
        // TODO: build other tables to the rule
        // ...

        shardRouter.setRouteRuleBindings(bindings);
        parser.setShardRouter(shardRouter);
        parser.setSqlParser(new JSQLParser());
        return shardRouter;
    }
}
