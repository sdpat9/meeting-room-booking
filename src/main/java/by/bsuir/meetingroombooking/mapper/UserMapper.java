package by.bsuir.meetingroombooking.mapper;

import by.bsuir.meetingroombooking.dto.UserResponse;
import by.bsuir.meetingroombooking.model.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
        );
    }
}
