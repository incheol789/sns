package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SnsApplicationException;
import com.fast.campus.simplesns.fixture.UserEntityFixture;
import com.fast.campus.simplesns.model.entity.UserEntity;
import com.fast.campus.simplesns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;


    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        // given
        String username = "userName";
        String password = "passWord";

        // when(mocking)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn((UserEntityFixture.get(username, password)));

        // then
        Assertions.assertDoesNotThrow(() -> userService.join(username, password));

    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        // given
        String username = "userName";
        String password = "passWord";
        UserEntity fixture = UserEntityFixture.get(username, password);

        // when(mocking)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        // then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(username, password));
        assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());

    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        // given
        String username = "userName";
        String password = "passWord";

        UserEntity fixture = UserEntityFixture.get(username, password);

        // when(mocking)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of((fixture)));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> userService.login(username, password));

    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_없는경우() {
        // given
        String username = "userName";
        String password = "passWord";

        // when(mocking)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        // then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(username, password));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        // given
        String username = "userName";
        String password = "passWord";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(username, password);

        // when(mocking)
        when(userEntityRepository.findByUserName(username)).thenReturn(Optional.of(fixture));

        // then
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(username, wrongPassword));
    }
}
