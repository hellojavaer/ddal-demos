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
package org.hellojavaer.ddal.demos.demo0.dao.impl;

import org.hellojavaer.ddal.demos.demo0.dao.UserDao;
import org.hellojavaer.ddal.demos.demo0.entity.UserEntity;
import org.hellojavaer.ddal.sequence.Sequence;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author <a href="mailto:hellojavaer@gmail.com">Kaiming Zou</a>,created on 15/06/2017.
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private Sequence           sequence;

    @Override
    public Long add(UserEntity userEntity) {
        Long id = sequence.nextValue(100, TimeUnit.MILLISECONDS);
        userEntity.setId(id);
        sqlSessionTemplate.insert("user.add", userEntity);
        return id;
    }

    @Override
    public int deleteById(Long id) {
        return sqlSessionTemplate.delete("user.deleteById", id);
    }

    @Override
    public int updateById(UserEntity userEntity) {
        return sqlSessionTemplate.update("user.updateById", userEntity);
    }

    @Override
    public UserEntity getById(Long id) {
        return sqlSessionTemplate.selectOne("user.getById", id);
    }

    @Override
    public List<UserEntity> scanQueryAll() {
        return sqlSessionTemplate.selectList("user.scanQueryAll");
    }

}
