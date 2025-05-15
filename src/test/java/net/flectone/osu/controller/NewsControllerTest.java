package net.flectone.osu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.flectone.osu.model.News;
import net.flectone.osu.model.User;
import net.flectone.osu.repository.NewsRepository;
import net.flectone.osu.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private News testNews;

    @BeforeEach
    void setup() {
        User testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);

        testNews = new News();
        testNews.setTitle("Test Title");
        testNews.setContent("Test Content");
        testNews.setUser(testUser);
        testNews = newsRepository.save(testNews);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createNews() throws Exception {
        News newNews = new News();
        newNews.setTitle("New Title");
        newNews.setContent("New Content");

        mockMvc.perform(post("/api/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNews)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteNews() throws Exception {
        mockMvc.perform(delete("/api/news/" + testNews.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(newsRepository.existsById(testNews.getId()));
    }

    @Test
    @WithMockUser(username = "another@example.com")
    void deleteNewsForbidden() throws Exception {
        mockMvc.perform(delete("/api/news/" + testNews.getId()))
                .andExpect(status().isForbidden());
    }
}
