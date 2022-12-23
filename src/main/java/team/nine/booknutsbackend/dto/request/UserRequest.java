package team.nine.booknutsbackend.dto.request;

import lombok.Getter;
import team.nine.booknutsbackend.domain.User;

import javax.validation.constraints.NotBlank;
import java.util.Collections;

@Getter
public class UserRequest {

    @NotBlank String loginId;
    @NotBlank String password;
    @NotBlank String username;
    @NotBlank String nickname;
    @NotBlank String email;
    String profileImgUrl;
    String roles;

    public UserRequest(String loginId, String password, String username, String nickname, String email, String profileImgUrl) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }

    public static User userRequest(UserRequest userRequest) {
        User user = new User();
        user.setLoginId(userRequest.getLoginId());
        user.setPassword(userRequest.password);
        user.setUsername(userRequest.getUsername());
        user.setNickname(userRequest.getNickname());
        user.setEmail(userRequest.getEmail());
        user.setProfileImgUrl(userRequest.getProfileImgUrl());
        user.setRoles(Collections.singletonList("ROLE_USER"));

        return user;
    }

}
