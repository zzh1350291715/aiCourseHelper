package com.onlinestudy.service;

import com.onlinestudy.domain.User;
import com.onlinestudy.dto.UserRegistrationDto;
import com.onlinestudy.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User regiserUser(UserRegistrationDto userRegistrationDto);
    User login(UserRegistrationDto userRegistrationDto);
    List<User> getAllUser();
    boolean deleteUser(Long user_id);
}

@Service
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User regiserUser(UserRegistrationDto userRegistrationDto){
        if((userRepository.findByUsername(userRegistrationDto.getUsername())).isPresent()){
            throw new IllegalArgumentException("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(userRegistrationDto.getUsername());
        newUser.setPassword(userRegistrationDto.getPassword());
        newUser.setRole(userRegistrationDto.getRole());
        return userRepository.save(newUser);
    }

    @Override
    public User login(UserRegistrationDto userRegistrationDto){
       Optional<User> userOpt = userRepository.findByUsername(userRegistrationDto.getUsername());
       if(userOpt.isEmpty()){
           throw  new IllegalArgumentException("用户不存在");
       }
       User user = userOpt.get();

       if(!userRegistrationDto.getPassword().equals(user.getPassword())){
           throw  new IllegalArgumentException("密码错误");
       }
       if(!user.getRole().equals(userRegistrationDto.getRole())){
           throw  new IllegalArgumentException("身份不匹配,请切换身份");
       }
       return user;
    }

    @Override
    public List<User> getAllUser()  {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(Long user_id) {
        try {
            userRepository.deleteById(user_id);
            return true;
        }catch (EmptyResultDataAccessException e){
            return false;
        }
    }

}
