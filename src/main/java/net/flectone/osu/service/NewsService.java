package net.flectone.osu.service;

import net.flectone.osu.model.News;
import net.flectone.osu.model.User;
import net.flectone.osu.repository.NewsRepository;
import net.flectone.osu.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public NewsService(UserRepository userRepository,
                       NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News createNews(News news) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        news.setUser(user);
        news.setCreatedDate(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public void deleteNews(Long id) throws AccessDeniedException {
        News news = newsRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("You can't delete this news");
        }

        newsRepository.delete(news);
    }

    public News updateNews(Long id, News updatedNews) throws AccessDeniedException {
        News news = newsRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("You can't edit this news");
        }

        news.setTitle(updatedNews.getTitle());
        news.setContent(updatedNews.getContent());
        return newsRepository.save(news);
    }

    public News getNewsById(Long id) throws AccessDeniedException {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Access denied");
        }

        return news;
    }

}