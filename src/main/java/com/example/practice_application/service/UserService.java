package com.example.practice_application.service;

import com.example.practice_application.dto.AddressDto;
import com.example.practice_application.dto.UserRequestDto;
import com.example.practice_application.dto.UserResponseDto;
import com.example.practice_application.model.Address;
import com.example.practice_application.model.User;
import com.example.practice_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public List<UserResponseDto> getUsers() {
       return userRepository.findAll().stream().map(this::getUserDto).collect(Collectors.toList());
    }

    public Optional<UserResponseDto> getUser(int id) {
        return userRepository.findById(id).map(this::getUserDto);
    }

    public User createUser(UserRequestDto user) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userCreationRequestDto(newUser, user);
        return userRepository.save(newUser);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public List<UserResponseDto> searchUsers(String search) {
        return userRepository.searchUsers(search).stream().map(this::getUserDto).collect(Collectors.toList());
    }

    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }


    private UserResponseDto getUserDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setAge(user.getAge());
        userResponseDto.setPhone(user.getPhone());
        userResponseDto.setRole(user.getRole());
        userResponseDto.setDetails(user.getDetails());
        userResponseDto.setUpdatedAt(user.getUpdatedAt());

        if(user.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setCountry(user.getAddress().getCountry());
            addressDto.setState(user.getAddress().getState());
            addressDto.setZip(user.getAddress().getZip());
            addressDto.setStreet(user.getAddress().getStreet());
            userResponseDto.setAddress(addressDto);
        }

        return userResponseDto;
    }


    private void userCreationRequestDto(User newUser, UserRequestDto userRequestDto) {
        newUser.setName(userRequestDto.getName());
        newUser.setUsername(userRequestDto.getUsername());
        newUser.setEmail(userRequestDto.getEmail());
        newUser.setAge(userRequestDto.getAge());
        newUser.setPhone(userRequestDto.getPhone());
        newUser.setDetails(userRequestDto.getDetails());

        if(userRequestDto.getAddress() != null) {
            Address address = new Address();
            address.setCity(userRequestDto.getAddress().getCity());
            address.setCountry(userRequestDto.getAddress().getCountry());
            address.setState(userRequestDto.getAddress().getState());
            address.setZip(userRequestDto.getAddress().getZip());
            address.setStreet(userRequestDto.getAddress().getStreet());
            newUser.setAddress(address);
        }
    }

}
