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
package org.hellojavaer.ddal.demos.demo1.dao.impl;

import org.hellojavaer.ddal.demos.demo1.dao.RoleDao;
import org.hellojavaer.ddal.demos.demo1.entitry.RoleEntity;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 22/07/2017.
 */
@Repository
public class RoleImpl implements RoleDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Long add(RoleEntity roleEntity) {
        sqlSessionTemplate.insert("role.add", roleEntity);
        return roleEntity.getId();
    }

    @Override
    public int deleteById(Long id) {
        return sqlSessionTemplate.delete("role.deleteById", id);
    }

    @Override
    public int updateById(RoleEntity roleEntity) {
        return sqlSessionTemplate.update("role.updateById", roleEntity);
    }

    @Override
    public RoleEntity getById(Long id) {
        return sqlSessionTemplate.selectOne("role.getById", id);
    }
}
