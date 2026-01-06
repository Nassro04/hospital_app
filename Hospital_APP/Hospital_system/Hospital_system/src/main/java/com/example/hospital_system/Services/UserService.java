package com.example.hospital_system.Services;

import com.example.hospital_system.Dao.UserDao;
import com.example.hospital_system.Models.User;

public class UserService {
    public UserDao userDao = new UserDao();
    public User currentUser;

    public User loginAsAdmin(String username, String password){
        User user = userDao.getAdminAccount(username,password);

        if(user != null){
            user.login();
            return  user;
        }

        return null;
    }


    public User getCurrentUser() {
        return currentUser;
    }


}
