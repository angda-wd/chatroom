package com.angda.client.dao;

import com.angda.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class AccountDao extends BasedDao {
    //注册
    public boolean userReg(User user){
        //insert
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try{
            connection =getConnection();
            String sql = "INSERT INTO user(username, password,breif)" +
                    " VALUES (?,?,?)";
            preparedStatement=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,user.getUsername());
            preparedStatement.setString(2, DigestUtils.md5Hex(user.getPassword()));
            preparedStatement.setString(3,user.getBreif());
            int rows = preparedStatement.executeUpdate();
            if(rows==1){
                return true;
            }
        }catch (SQLException e){
            System.err.println("用户注册失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,preparedStatement);
        }
        return false;
    }
    public User userLogin(String username,String password){
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        try {
            connection=getConnection();
            String sql="select * from user where username=? and password=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,DigestUtils.md5Hex(password));
            resultSet =preparedStatement.executeQuery();
            if(resultSet.next()){
                User user=new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setBreif(resultSet.getString("breif"));
                return user;

            }
        } catch (SQLException e) {
            System.err.println("用户注册失败");

        }finally {
            closeResources(connection,preparedStatement,resultSet);
        }
        return null;
    }
}
