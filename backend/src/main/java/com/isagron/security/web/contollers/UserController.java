package com.isagron.security.web.contollers;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import com.isagron.security.domain.dtos.UpdateUserRequest;
import com.isagron.security.domain.dtos.UserDto;
import com.isagron.security.domain.dtos.UserPropertiesRequest;
import com.isagron.security.services.resources.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private ModelMapper modelMapper;

    private UserService userService;


    @Autowired
    public UserController(ModelMapper modelMapper, UserService userService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserPropertiesRequest userPropertiesRequest) {
        return this.modelMapper.map(userService.addNewUser(userPropertiesRequest), UserDto.class);
    }

    @PutMapping("/{userName}")
    public UserDto updateUser(@PathVariable String userName, UserPropertiesRequest userPropertiesRequest) {
        return this.modelMapper.map(
                userService.updateUser(
                        UpdateUserRequest.builder()
                                .userName(userName)
                                .userPropertiesRequest(userPropertiesRequest)
                                .build()),
                UserDto.class
        );
    }

    @PutMapping("/{userName}/updateProfileImage")
    public UserDto updateProfileImage(@PathVariable String userName, @RequestParam MultipartFile profileImage) {
        return this.modelMapper.map(
                userService.updateProfileImage(userName, profileImage),
                UserDto.class
        );
    }

    @GetMapping("/{userName}")
    public UserDto findUser(@PathVariable String userName) {
        return this.modelMapper.map(
                this.userService.getOrElseThrow(userName),
                UserDto.class
        );
    }

    @GetMapping()
    public List<UserDto> getAll() {
        return this.userService.getUsers()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/page")
    public Page<UserDto> getAll(@RequestParam(required = false) String userName, @RequestParam(required = false) String role, @RequestParam(name = "page") int pageIndex,
                                @RequestParam(name = "size") int pageSize) {
        return this.userService.getUsers(userName, role, PageRequest.of(pageIndex, pageSize))
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        this.userService.deleteUser(userId);
    }


    @GetMapping(path = "/{userName}/profile-image", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable String userName){
        return this.userService.getProfileImage(userName);
    }


}
