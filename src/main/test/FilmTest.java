import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes=FilmController.class)
@WebMvcTest(FilmController.class)
public class FilmTest {
    private final  Duration BASIK_DURATION = Duration.parse("PT2H30M");

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FilmController filmController;

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

    @Test
    public void validationExceptions() throws Exception {
        Duration negativeDuration = Duration.ofHours(-5).plusMinutes(-30);
        Film filmEmptyName = new Film(1,
                "",
                "nice",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));

        Film filmLimitSimvols = new Film(1,
                "обливион",
                "dsmfokdfowforewofjnerojfnerojfoernfojnernfjnojfoejnfrenjrfenjoeorjonjejnnernfjernjefrofonernjernfernnfefernfernefrojnfernojfrenfjnoefrjnrfenjfrjofrenjfrjnorefjnefrnjoefrjnofrejnofrjnofjjonrefjnorefjnorefjnoefrjnoferjnoefrjnoferjonfernjoferjnofernfrejnoerfnefrnferjnoferojnerfnjoerjnoferojnfejnorfejnorfjnoerfjnoerfojerfnjoerfonjerojnfeorjnfojernfojnerfojnerojfnerojngfoerjngoerjgoierjmgoierngoiernpogierogesojgopejglekrwgm[oeirg[oerwjgpjewrwgojwertngeirwjtnglkewrtgm[ojrtngo[rtujgojrtwgpijetngpoiwetug[uetwjg[oewtgkoan[go[uogrhgj[oatgno[jaetngajtgnotoaugtanoeugjnatjngoajnotrjno[grjnojngfjnfgjnofa[nfgnjognfngjoo",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));

        Film filmLimitlocalDateTime = new Film(1,
                "обливион",
                "nice",
                LocalDateTime.of(1700, Month.DECEMBER, 28, 0, 0 ),
                Duration.parse("PT2H30M"));

        Film filmBadDuratiom = new Film(1,
                "обливион",
                "nice",
                LocalDateTime.of(2009, Month.DECEMBER, 28, 0, 0 ),
                negativeDuration);

        //-----------------------------------------------------------------------------------------
        // -------------------------------------POST---------------------------
        this.mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmEmptyName))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Имя не может быть пустым!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmLimitSimvols))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Описание не может превышать 200 символов!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmLimitlocalDateTime))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Дата релиза не может быть раньше 28 декабря 1895 года!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmBadDuratiom))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Продолжительность фильма должна быть положительным числом!"))
                .andExpect(status().is4xxClientError());
        //--------------------------------------------------------------------------------------------------
        //------------ PUT---------------------------------
        this.mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmEmptyName))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Имя не может быть пустым!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmLimitSimvols))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Описание не может превышать 200 символов!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmLimitlocalDateTime))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Дата релиза не может быть раньше 28 декабря 1895 года!"))
                .andExpect(status().is4xxClientError());

        this.mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmBadDuratiom))
                ).andExpect(r -> r.getResponse().getContentAsString().equals("Продолжительность фильма должна быть положительным числом!"))
                .andExpect(status().is4xxClientError());
        //---------------------------------------------------------------------------------------------------
    }
}