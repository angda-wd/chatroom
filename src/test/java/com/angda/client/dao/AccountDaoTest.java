package com.angda.client.dao;

import com.angda.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDaoTest {
    private AccountDao accountDao = new AccountDao();
    @Test
    public void userReg() {
        User user=new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setBreif("jsak");
        boolean b=accountDao.userReg(user);
        Assert.assertTrue(b);

    }

    @Test
    public void userLogin() {
        User user =accountDao.userLogin("test","1");
        Assert.assertNotNull(user);
    }
}