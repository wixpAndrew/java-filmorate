import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDateTime;
import java.time.Month;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes= UserController.class)
@WebMvcTest(UserController.class)
public class UserTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserController userController;

    @Test
    public void appendingUser() throws Exception {
        User user = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0));

        String result = objectMapper.writeValueAsString(user);

        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                        .andExpect(status().isOk());

        this.mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(user.getId()))
                        .andExpect(jsonPath("$[0].username").value(user.getUsername()))
                        .andExpect(jsonPath("$[0].login").value(user.getLogin()))
                        .andExpect(jsonPath("$[0].password").value(user.getPassword()))
                        .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                        .andExpect(jsonPath("$[0].birthday").value(user.getBirthday().toString()))
                        .andExpect(status().isOk());
    }

    @Test
    public void updatingFilm() throws Exception {
        User user1 = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0));
    userController.appendUser(user1);

        User user2 = new User(1,
                "got",
                "perfect",
                "dfdfnj",
                "reload@email.com",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0));
                userController.updateUser(user2);

        String result = objectMapper.writeValueAsString(user2);

        this.mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                        .andExpect(status().isOk());

        this.mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(user2.getId()))
                        .andExpect(jsonPath("$[0].username").value(user2.getUsername()))
                        .andExpect(jsonPath("$[0].login").value(user2.getLogin()))
                        .andExpect(jsonPath("$[0].password").value(user2.getPassword()))
                        .andExpect(jsonPath("$[0].email").value(user2.getEmail()))
                        .andExpect(jsonPath("$[0].birthday").value(user2.getBirthday().toString()))
                        .andExpect(status().isOk());
    }
}