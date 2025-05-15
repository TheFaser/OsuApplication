package net.flectone.osu.service;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class NewsService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News createNews(News news) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        news.setUser(user);
        news.setCreatedDate(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public void deleteNews(Long id) throws AccessDeniedException {
        News news = newsRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Ты не можешь удалить эту новость");
        }

        newsRepository.delete(news);
    }

    public News updateNews(Long id, News updatedNews) throws AccessDeniedException {
        News news = newsRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Ты не можешь изменять эту новость");
        }

        news.setTitle(updatedNews.getTitle());
        news.setContent(updatedNews.getContent());
        return newsRepository.save(news);
    }

    public News getNewsById(Long id) throws AccessDeniedException {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Новость не найдена"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!news.getUser().getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Доступ запрещён");
        }

        return news;
    }

}