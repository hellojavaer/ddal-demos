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
package org.hellojavaer.ddal.demos.demo1;

import org.hellojavaer.ddal.demos.demo1.dao.RoleDao;
import org.hellojavaer.ddal.demos.demo1.entitry.RoleEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 22/07/2017.
 */
@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/dao.xml" })
public class RoleDaoTest {

    @Autowired
    private RoleDao            roleDao;

    @Autowired
    private ApplicationContext context;

    @Test
    @Transactional
    public void testForCRUD() {
        // 1. do insert
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("role_name");
        Long id = roleDao.add(roleEntity);

        // check insert
        RoleEntity roleInDB = roleDao.getById(id);
        Assert.isTrue(roleInDB != null && roleInDB.getName().equals(roleEntity.getName()));

        // 2. do update
        RoleEntity roleForUpdate = new RoleEntity();
        roleForUpdate.setId(id);
        roleForUpdate.setName("updated_role_name");
        int rows = roleDao.updateById(roleForUpdate);
        // check update
        Assert.isTrue(rows == 1);
        roleInDB = roleDao.getById(id);
        Assert.isTrue(roleInDB != null && roleInDB.getName().equals(roleForUpdate.getName()));

        // 3. do delete
        rows = roleDao.deleteById(id);
        // check delete
        Assert.isTrue(rows == 1);
        roleInDB = roleDao.getById(id);
        Assert.isTrue(roleInDB == null);
    }

    /**
     * after you run this method, you can see the log in the console to check database invoking record
     */
    @Test
    public void testLoadBalance() {
        for (int i = 0; i < 20; i++) {
            context.getBean(RoleDaoTest.class).getById(1L);
        }
    }

    @Transactional(readOnly = true)
    public RoleEntity getById(Long id) {
        return roleDao.getById(id);
    }

}
