package net.flectone.osu.controller;

import lombok.AllArgsConstructor;
import net.flectone.osu.model.News;
import net.flectone.osu.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@AllArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public News createNews(@RequestBody News news) {
        return newsService.createNews(news);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Long id) throws AccessDeniedException {
        newsService.deleteNews(id);
    }

    @PutMapping("/{id}")
    public News updateNews(@PathVariable Long id, @RequestBody News news) throws AccessDeniedException {
        return newsService.updateNews(id, news);
    }
}