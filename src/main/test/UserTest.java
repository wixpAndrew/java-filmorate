import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = UserController.class)
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
        LocalDate localDate = LocalDate.of(2009, 12, 28);
        User user = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
                localDate);

        String result = objectMapper.writeValueAsString(user);

        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                .andExpect(status().isOk());

        this.mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].login").value(user.getLogin()))
                .andExpect(jsonPath("$[0].password").value(user.getPassword()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(user.getBirthday().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void updatingFilm() throws Exception {
        LocalDate localDate = LocalDate.of(2009, 12, 28);
        User user1 = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
               localDate);
        userController.appendUser(user1);

        User user2 = new User(1,
                "got",
                "perfect",
                "dfdfnj",
                "reload@email.com",
                localDate);
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
                .andExpect(jsonPath("$[0].name").value(user2.getName()))
                .andExpect(jsonPath("$[0].login").value(user2.getLogin()))
                .andExpect(jsonPath("$[0].password").value(user2.getPassword()))
                .andExpect(jsonPath("$[0].email").value(user2.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(user2.getBirthday().toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void validationException() throws Exception {
        LocalDate localDate = LocalDate.of(2009,12,28);
            User userEmailNoSimvol = new User(1,
                    "обливион",
                    "dfdfdffd",
                    "dfdf",
                    "exampleemail.com",
                   localDate);
            userController.appendUser(userEmailNoSimvol);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEmailNoSimvol))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("ошибка в почте !"))
                .andExpect(status().is5xxServerError());
//----------------------------------------------------------------------------------------------
        User userLoginEmpty = new User(1,
                "обливион",
                "",
                "dfdf",
                "exampleemail.com",
              localDate);
        userController.appendUser(userLoginEmpty);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginEmpty))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("ошибка в логине !"))
                .andExpect(status().is5xxServerError());
//-----------------------------------------------------------------------------------------
        User userEmailNull = new User(1,
                "обливион",
                "dfdfdfd",
                "dfdf",
                "",
            localDate);
        userController.appendUser(userEmailNull);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEmailNull))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("ошибка в почте !"))
                .andExpect(status().is5xxServerError());
//-----------------------------------------------------------------------------------------
    }
}