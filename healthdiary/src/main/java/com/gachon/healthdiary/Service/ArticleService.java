package com.gachon.healthdiary.Service;

import com.gachon.healthdiary.DTO.ArticleForm;
import com.gachon.healthdiary.Entity.Article;
import com.gachon.healthdiary.Repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

    public Article findArticle(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleForm dto) {
        // dto -> Entity
        Article newArticle = dto.toEntity();

        // json의 데이터에 id값이 입력되었는지 확인
        if(newArticle.getId() != null){
            return null;
        }

        // Entity -> DB 저장
        return articleRepository.save(newArticle);
    }

    public Article update(Long id, ArticleForm dto) {
        // 1. dto -> Entity
        Article edited = dto.toEntity();
        log.info(edited.toString());

        // 2. target 조회
        Article target = articleRepository.findById(id).orElse(null);

        // 3. 잘못된 요청 처리
        if(id != edited.getId() || target == null){
            log.info("잘못된 요청!! id: {}, edited: {}", id, edited.toString());
            return null;
        }

        // 4. Updated 하기
        target.patch(edited);
        Article updated = articleRepository.save(target);
        return updated;
    }

    public Article delete(Long id) {
        // 1. 대상 article 찾기
        Article target = articleRepository.findById(id).orElse(null);

        // 2. 없는 article인 경우 - null 반환
        if (target == null){
            return null;
        }

        // 3. 있는 article인 경우 - 삭제한 article 반환
        articleRepository.delete(target);
        return target;
    }

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        // 1. dtos -> Entities
        List<Article> articleList = dtos.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());

        // 2. Entities -> save to DB
        articleList.stream()
                .forEach(article -> articleRepository.save(article));

        // 3. 강제 예외 발생시키기
        articleRepository.findById(-1L)
                .orElseThrow(() -> new IllegalArgumentException("데이터 찾기 실패!"));

        // 4. 결과 값 반환하기
        return articleList;
    }
}

