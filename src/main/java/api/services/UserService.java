package api.services;

import api.exceptions.AlreadyExistsException;
import api.exceptions.UserNotFoundException;
import api.models.User;
import api.models.requests.CreateUserRequest;
import api.models.responses.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import api.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final String NOT_FOUND_MESSAGE = "User not found";
    private static final String ALREADY_EXISTS_MESSAGE = "Provided username already registered";

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserResponse createUser(CreateUserRequest dto){

        if(userRepository.existsUserByUsername(dto.username())){
            throw new AlreadyExistsException(ALREADY_EXISTS_MESSAGE);
        }

        User newUser = new User(dto.username(), encoder.encode(dto.password()));
        userRepository.save(newUser);

        return new UserResponse(newUser.getId(), newUser.getUsername(), newUser.isVerified());

    }

    public UserResponse findUser(UUID id){

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

        return new UserResponse(user.getId(), user.getUsername(), user.isVerified());

    }

    public List<UserResponse> listAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.isVerified()))
                .toList();
    }

    public void updateUsername(String username, UUID id){

        if (!StringUtils.hasText(username) || username.length() > 16 || username.length() < 4) {
            throw new IllegalArgumentException("Invalid username was provided");
        }

        if (userRepository.existsUserByUsername(username)){
            throw new AlreadyExistsException(ALREADY_EXISTS_MESSAGE);
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

        user.setUsername(username);
        userRepository.save(user);

    }

    public void updatePassword(String password, UUID id) {

        if (!StringUtils.hasText(password) || password.length() > 16 || password.length() < 8) {
            throw new IllegalArgumentException("Invalid password was provided");
        }

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_MESSAGE));

        user.setPasswordHash(encoder.encode(password));
        userRepository.save(user);

    }

    public void deleteUser(UUID id){

        if (!userRepository.existsById(id)){
            throw new UserNotFoundException(NOT_FOUND_MESSAGE);
        }

        userRepository.deleteById(id);

    }
}
