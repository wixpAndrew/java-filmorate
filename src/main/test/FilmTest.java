import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes=FilmController.class)
@WebMvcTest(FilmController.class)
public class FilmTest {
    private final static Duration BASIK_DURATION = Duration.parse("PT2H30M");
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FilmController filmController;  // Autowire the actual FilmController

    @Test
    public void appendingFilm() throws Exception {

        Film film = new Film(1,
                "обливион",
                "nice",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));

        filmController.appendFilm(film);

        String result = objectMapper.writeValueAsString(film);

        this.mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                        .andExpect(status().isOk());

        this.mvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(film.getId()))
                .andExpect(jsonPath("$[0].name").value(film.getName()))
                .andExpect(jsonPath("$[0].description").value(film.getDescription()))
                .andExpect(jsonPath("$[0].duration").value(film.getDuration().toString()))
                .andExpect(status().isOk());

    }

    @Test
    public void updatingFilm() throws Exception {
        Film film1 = new Film(1,
                "обливион",
                "nice",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));
        filmController.appendFilm(film1);

        Film film2 = new Film(1,
                "смурфики",
                "круто",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));
        filmController.updateFilm(film2);

        String result = objectMapper.writeValueAsString(film2);

        this.mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                        .andExpect(status().isOk());

        this.mvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(film2.getId()))
                .andExpect(jsonPath("$[0].name").value(film2.getName()))
                .andExpect(jsonPath("$[0].description").value(film2.getDescription()))
                .andExpect(jsonPath("$[0].duration").value(film2.getDuration().toString()))
                .andExpect(status().isOk());
    }
}