package com.github.dylanz666.service;

import com.github.dylanz666.constant.UserTypeEnum;
import com.github.dylanz666.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : dylanz
 * @since : 10/04/2020
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDetailsImpl userService;
    @Autowired
    private UserDetails userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Spring Security要求必须加密密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //模拟从数据库中取出用户信息，使用的sql如: SELECT * FROM USER WHERE USER_NAME='cherrys'
        List<User> userList = new ArrayList<>();
        User firstUser = new User();
        firstUser.setUsername("cherrys");
        firstUser.setPassword(passwordEncoder.encode("123"));
        firstUser.setUserType(UserTypeEnum.USER.toString());
        userList.add(firstUser);
        User secondUser = new User();
        secondUser.setUsername("randyh");
        secondUser.setPassword(passwordEncoder.encode("456"));
        secondUser.setUserType(UserTypeEnum.USER.toString());
        userList.add(secondUser);

        List<User> mappedUsers = userList.stream().filter(s -> s.getUsername().equals(username)).collect(Collectors.toList());

        //判断用户是否存在
        User user;
        if (CollectionUtils.isEmpty(mappedUsers)) {
            logger.info(String.format("The user %s is not found !", username));
            throw new UsernameNotFoundException(String.format("The user %s is not found !", username));
        }
        user = mappedUsers.get(0);
        return new UserDetailsImpl(user);
    }
}
