/*
 * MIT License
 *
 * Copyright (c) 2017 hellojavaer
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
package org.hellojavaer.ddal.demos.demo0;

import com.alibaba.fastjson.JSON;
import org.hellojavaer.ddal.ddr.shard.*;
import org.hellojavaer.ddal.demos.demo0.dao.UserDao;
import org.hellojavaer.ddal.demos.demo0.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 19/06/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/dao.xml" })
public class UserDaoTest {

    @Autowired
    private UserDao     userDao;

    @Autowired
    private ShardRouter shardRouter;

    @Test
    @Transactional
    public void baseTestForCRUD() {
        // insert
        UserEntity userEntity = new UserEntity();
        userEntity.setName("allen");
        Long id = userDao.add(userEntity);
        UserEntity userEntityInDb = userDao.getById(id);
        // check insert operation
        Assert.isTrue(userEntityInDb.getId().equals(userEntity.getId()));
        Assert.isTrue(userEntityInDb.getName().equals(userEntity.getName()));

        // update
        UserEntity userEntityForUpdate = new UserEntity();
        userEntityForUpdate.setId(id);
        userEntityForUpdate.setName("allen_well");
        int rows = userDao.updateById(userEntityForUpdate);
        userEntityInDb = userDao.getById(id);
        // check update operation
        Assert.isTrue(rows == 1);
        Assert.isTrue(userEntityInDb.getId().equals(userEntityForUpdate.getId()));
        Assert.isTrue(userEntityInDb.getName().equals(userEntityForUpdate.getName()));

        // delete
        rows = userDao.deleteById(id);
        userEntityInDb = userDao.getById(id);
        // check delete operation
        Assert.isTrue(rows == 1);
        Assert.isTrue(userEntityInDb == null);
    }

    @Test
    @Transactional
    public void scanQueryAll() {
        // step0: make some test data
        List<UserEntity> userEntities = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            UserEntity userEntity = new UserEntity();
            userEntity.setName(String.valueOf(System.currentTimeMillis()));
            userEntities.add(userEntity);
            userDao.add(userEntity);
        }

        // step1: check scan query
        String scName = "base";
        String tbName = "user";
        List<RouteInfo> routeInfos = shardRouter.getRouteInfos(scName, tbName);
        for (RouteInfo routeInfo : routeInfos) {
            // when sql expression doesn't contain shard value, use ShardRouteContext to set route information
            ShardRouteContext.setRouteInfo(scName, tbName, routeInfo);
            List<UserEntity> userEntities0 = userDao.scanQueryAll();
            System.out.println("====== table: '" + routeInfo.toString() + "' contains the following records ==========");
            if (userEntities0 != null) {
                for (UserEntity item : userEntities0) {
                    System.out.println(item);
                }
            }
            ShardRouteContext.clearContext();
        }

        // step2: remove test data
        for (UserEntity userEntity : userEntities) {
            userDao.deleteById(userEntity.getId());
        }
    }

    @Test
    public void groupRouteInfo() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ids.add((long) i);
        }
        Map<RouteInfo, List<Long>> map = ShardRouteUtils.groupSdValuesByRouteInfo(shardRouter, "base", "user", ids);
        for (Map.Entry<RouteInfo, List<Long>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + JSON.toJSONString(entry.getValue()));
        }
    }

}
