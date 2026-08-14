package bg.sofia.uni.fmi.mjt.server.user;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
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
        userManager = UserManagerImpl.getInstance();
        mockChannel1 = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testRegisterNewUserSuccess() throws UserAlreadyExistsException {
        String result = userManager.register("testUser", "password123");
        assertEquals("Success", result);
    }

    @Test
    void testRegisterExistingUserThrowsException() throws UserAlreadyExistsException {
        userManager.register("testUser", "password123");
        assertThrows(UserAlreadyExistsException.class, () ->
            userManager.register("testUser", "differentPassword"));
    }

    @Test
    void testLoginNonExistentUserThrowsException() {
        assertThrows(WrongUserCredentialsException.class, () ->
            userManager.login("nonExistent", "password", mockChannel1));
    }

    @Test
    void testLoginWrongPasswordThrowsException() throws UserAlreadyExistsException {
        userManager.register("testUser", "correctPassword");
        assertThrows(WrongUserCredentialsException.class, () ->
            userManager.login("testUser", "wrongPassword", mockChannel1));

    }

    @Test
    void testLoginSuccessful() throws UserAlreadyExistsException, WrongUserCredentialsException {
        userManager.register("testUser", "correctPassword");
        String result = userManager.login("testUser", "correctPassword", mockChannel1);
        assertEquals("Success", result);
    }

    @Test
    void testIsLoggedInForLoggedInUser() throws UserAlreadyExistsException, WrongUserCredentialsException {
        userManager.register("testUser", "correctPassword");
        userManager.login("testUser", "correctPassword", mockChannel1);
        assertTrue(userManager.isLoggedIn(mockChannel1));
    }

    @Test
    void testIsLoggedInForNonLoggedInUser() {
        assertFalse(userManager.isLoggedIn(mockChannel1));
    }

    @Test
    void testGetUsernameForLoggedInUser() throws UserAlreadyExistsException, WrongUserCredentialsException, UserNotLoggedInException {
        userManager.register("testUser", "correctPassword");
        userManager.login("testUser", "correctPassword", mockChannel1);
        assertEquals("testUser", userManager.getUsername(mockChannel1));
    }

    @Test
    void testGetUsernameForNonLoggedInUser() throws UserNotLoggedInException {
        assertThrows( UserNotLoggedInException.class , () -> userManager.getUsername(mockChannel1));
    }

    @Test
    void testLogoutTerminatesSession() throws UserAlreadyExistsException, WrongUserCredentialsException, UserNotLoggedInException {
        userManager.register("testUser", "correctPassword");
        userManager.login("testUser", "correctPassword", mockChannel1);
        assertTrue(userManager.isLoggedIn(mockChannel1));

        userManager.logout(mockChannel1);
        assertFalse(userManager.isLoggedIn(mockChannel1));
        assertThrows(  UserNotLoggedInException.class , () -> userManager.getUsername(mockChannel1));
    }
}
