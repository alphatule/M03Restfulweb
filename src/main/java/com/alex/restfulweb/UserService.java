package com.alex.restfulweb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> updateUser(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            return userRepository.save(existingUser);
        });
    }

    public void applyPatchToUsers(JsonPatch patch) {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode patched = patch.apply(JsonLoader.fromString(objectMapper.writeValueAsString(user)));
                User updatedUser = objectMapper.treeToValue(patched, User.class);
                userRepository.save(updatedUser);
            } catch (JsonPatchException | IOException e) {
                throw new RuntimeException("Error applying patch", e);
            }
        });
    }
}
