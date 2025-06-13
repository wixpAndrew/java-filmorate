import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void updatingUser() throws Exception {
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

    @Test
    public void addGetFriend() throws Exception {
        LocalDate localDate = LocalDate.of(2009, 12, 28);
        User user1 = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
                localDate);
        String result1 = objectMapper.writeValueAsString(user1);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result1))
                .andExpect(status().isOk());

        User user2 = new User(2,
                "got",
                "perfect",
                "dfdfnj",
                "reload@email.com",
                localDate);
        String result2 = objectMapper.writeValueAsString(user2);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result2))
                .andExpect(status().isOk());

            this.mvc.perform(put("/users/1/friends/2"))
                    .andExpect(status().isOk());

            // Проверяем, что друг добавился
            this.mvc.perform(get("/users/1/friends"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(user2.getId()))
                    .andExpect(jsonPath("$[0].name").value(user2.getName()))
                    .andExpect(jsonPath("$[0].login").value(user2.getLogin()))
                    .andExpect(jsonPath("$[0].password").value(user2.getPassword()))
                    .andExpect(jsonPath("$[0].email").value(user2.getEmail()))
                    .andExpect(jsonPath("$[0].birthday").value(user2.getBirthday().toString()));
    }

    @Test
    public void deleteFriend() throws Exception {
        LocalDate localDate = LocalDate.of(2009, 12, 28);
        User user1 = new User(1,
                "обливион",
                "nice",
                "dfdf",
                "example@email.com",
                localDate);
        String result1 = objectMapper.writeValueAsString(user1);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result1))
                .andExpect(status().isOk());

        User user2 = new User(2,
                "got",
                "perfect",
                "dfdfnj",
                "reload@email.com",
                localDate);
        String result2 = objectMapper.writeValueAsString(user2);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result2))
                .andExpect(status().isOk());

        this.mvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void commonFriends() throws Exception {
        LocalDate localDate = LocalDate.of(2009, 12, 28);
        User user1 = new User(1,
                "обливион1",
                "nice",
                "dfdf",
                "example@email.com",
                localDate);
        String result1 = objectMapper.writeValueAsString(user1);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result1))
                .andExpect(status().isOk());

        User user2 = new User(2,
                "got2",
                "perfect",
                "dfdfnj",
                "reload@email.com",
                localDate);
        String result2 = objectMapper.writeValueAsString(user2);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result2))
                .andExpect(status().isOk());

        User user3 = new User(3,
                "обливион3",
                "nice",
                "dfdf",
                "exame@email.com",
                localDate);
        String result3 = objectMapper.writeValueAsString(user3);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result3))
                .andExpect(status().isOk());

        User user4 = new User(4,
                "got4",
                "perfect",
                "dfdfnj",
                "rel@email.com",
                localDate);
        String result4 = objectMapper.writeValueAsString(user4);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result4))
                .andExpect(status().isOk());

        User user5 = new User(5,
                "обливион5",
                "nice",
                "dfdf",
                "ele@email.com",
                localDate);
        String result5 = objectMapper.writeValueAsString(user5);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result5))
                .andExpect(status().isOk());

        User user6 = new User(6,
                "got6",
                "perfect",
                "dfdfnj",
                "r@email.com",
                localDate);
        String result6 = objectMapper.writeValueAsString(user6);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result6))
                .andExpect(status().isOk());

        User user7 = new User(7,
                "got7",
                "perfect",
                "dfdfnj",
                "road@email.com",
                localDate);
        String result7 = objectMapper.writeValueAsString(user7);
        this.mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result7))
                .andExpect(status().isOk());

    //-----------------------------------------------------------------
    // ----------для 1
        this.mvc.perform(put("/users/1/friends/3"))
                .andExpect(status().isOk());

        this.mvc.perform(put("/users/1/friends/5"))
                .andExpect(status().isOk());

        this.mvc.perform(put("/users/1/friends/7"))
                .andExpect(status().isOk());
    // -----------------------------------------------------------------
    //--------------- для 2
        this.mvc.perform(put("/users/2/friends/4"))
                .andExpect(status().isOk());

        this.mvc.perform(put("/users/2/friends/6"))
                .andExpect(status().isOk());

        this.mvc.perform(put("/users/2/friends/7"))
                .andExpect(status().isOk());

    //-------- по итогу у 1 - 357, у 2 - 467, общий ->7

        this.mvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user7.getId()))
                .andExpect(jsonPath("$[0].name").value(user7.getName()))
                .andExpect(jsonPath("$[0].login").value(user7.getLogin()))
                .andExpect(jsonPath("$[0].password").value(user7.getPassword()))
                .andExpect(jsonPath("$[0].email").value(user7.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(user7.getBirthday().toString()));
    }
}