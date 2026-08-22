package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.exception.UserAlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserManagerImplTest {

    private UserManager userManager;
    private SocketChannel mockChannel1;

    @BeforeEach
    void setUp() {
        userManager = new UserManagerImpl();
        mockChannel1 = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testRegisterNewUserSuccess() throws UserAlreadyExistsException {
        String result = userManager.register("user0", "user0");
        assertEquals("Success", result);
    }

    @Test
    void testRegisterExistingUserThrowsException() throws UserAlreadyExistsException {
        userManager.register("user1", "user1");
        assertThrows(UserAlreadyExistsException.class, () ->
            userManager.register("user1", "differentPassword"));
    }

    @Test
    void testLoginNonExistentUserThrowsException() {
        assertThrows(WrongUserCredentialsException.class, () ->
            userManager.login("nonExistent", "password", mockChannel1));
    }

    @Test
    void testLoginWrongPasswordThrowsException() throws UserAlreadyExistsException {
        userManager.register("user2", "123");
        assertThrows(WrongUserCredentialsException.class, () ->
            userManager.login("user2", "456", mockChannel1));

    }

    @Test
    void testLoginSuccessful() throws UserAlreadyExistsException, WrongUserCredentialsException, UserAlreadyLoggedInException {
        userManager.register("user3", "user3");
        String result = userManager.login("user3", "user3", mockChannel1);
        assertEquals("Success", result);
    }

    @Test
    void testIsLoggedInForLoggedInUser() throws UserAlreadyExistsException, WrongUserCredentialsException, UserAlreadyLoggedInException {
        userManager.register("user4", "user4");
        userManager.login("user4", "user4", mockChannel1);
        assertTrue(userManager.isLoggedIn(mockChannel1));
    }

    @Test
    void testIsLoggedInForNonLoggedInUser() {
        assertFalse(userManager.isLoggedIn(Mockito.mock(SocketChannel.class)));
    }

    @Test
    void testGetUsernameForLoggedInUser() throws UserAlreadyExistsException, WrongUserCredentialsException, UserNotLoggedInException, UserAlreadyLoggedInException {
        userManager.register("user5", "user5");
        userManager.login("user5", "user5", mockChannel1);
        assertEquals("user5", userManager.getUsername(mockChannel1));
    }

    @Test
    void testGetUsernameForNonLoggedInUser() throws UserNotLoggedInException {
        assertThrows( UserNotLoggedInException.class , () -> userManager.getUsername(Mockito.mock(SocketChannel.class)));
    }

    @Test
    void testLogoutTerminatesSession() throws UserAlreadyExistsException, WrongUserCredentialsException, UserNotLoggedInException, UserAlreadyLoggedInException {
        userManager.register("user6", "user6");
        userManager.login("user6", "user6", mockChannel1);
        assertTrue(userManager.isLoggedIn(mockChannel1));

        userManager.logout(mockChannel1);
        assertFalse(userManager.isLoggedIn(mockChannel1));
        assertThrows(  UserNotLoggedInException.class , () -> userManager.getUsername(mockChannel1));
    }
}
