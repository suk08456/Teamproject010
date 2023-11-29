package com.project.Restaurant.Board.PostComment;

import com.project.Restaurant.Board.Post.Post;
import com.project.Restaurant.DataNotFoundException;
import com.project.Restaurant.Member.consumer.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public PostComment create(Post post, String content, Customer customer) {


        PostComment postComment = new PostComment();
        postComment.setContent(content);
        postComment.setLocalDateTime(LocalDateTime.now());
        postComment.setPost(post);
        postComment.setCustomer(customer);
        postComment.setParent(0L);
        postComment.setIsDeleted(false);
        this.postCommentRepository.save(postComment);
        return postComment;
    }

    public PostComment getpostComment(Long id) {
        Optional<PostComment> answer = this.postCommentRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(PostComment postComment, String content) {
        postComment.setContent(content);
        postComment.setModifyDate(LocalDateTime.now());
        this.postCommentRepository.save(postComment);
    }

    public void delete(PostComment postComment) {
        this.postCommentRepository.delete(postComment);
    }

    public void likes(PostComment postComment, Customer customer) {
        postComment.getLikes().add(customer);
        this.postCommentRepository.save(postComment);
    }


    public void newsubcommet(Post post, Customer customer, String content, Long contentid) {
        PostComment pmt = new PostComment();
        pmt.setContent(content);
        pmt.setCustomer(customer);
        pmt.setPost(post);
        pmt.setLocalDateTime(LocalDateTime.now());
        pmt.setParent(contentid);
        pmt.setIsDeleted(false);
        this.postCommentRepository.save(pmt);
    }

    public List<PostComment> findByPost(Long id){
        List<PostComment>postCommentList = this.postCommentRepository.findByPost_id(id);
        return postCommentList;
    }
}
