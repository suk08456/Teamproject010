package com.project.Restaurant.Board.Post;


import com.project.Restaurant.Board.PostComment.PostComment;
import com.project.Restaurant.Board.PostComment.PostCommentController;
import com.project.Restaurant.Board.PostComment.PostCommentService;
import com.project.Restaurant.Member.consumer.Customer;
import com.project.Restaurant.Member.consumer.CustomerService;
import com.project.Restaurant.Board.PostComment.PostCommentForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CustomerService customerService;
    private final PostCommentService postCommentService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw){
        Page<Post> paging = this.postService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "Board/post_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id, PostCommentForm postCommentForm){
        Post post = this.postService.getPost(id);
        model.addAttribute("post", post);

        List<PostComment> postCommentList = this.postCommentService.findByPost(id);
        model.addAttribute("postCommentList",postCommentList);

        return "Board/post_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostForm postForm){
        return "Board/post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String postCreate(@Valid PostForm postForm, BindingResult bindingResult,
                             Principal principal) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 에러 메시지를 모델에 추가
            return "Board/post_form";
        }
        Customer customer = this.customerService.findByusername(principal.getName());
        this.postService.create(postForm.getTitle(), postForm.getContent(), customer);
        return "redirect:/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(PostForm postForm, @PathVariable("id") Long id, Principal principal) {
        Post post = this.postService.getPost(id);
        if(!post.getCustomer().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        return "Board/post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String postModify(@Valid PostForm postForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "Board/post_form";
        }
        Post post = this.postService.getPost(id);
        if (!post.getCustomer().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.postService.modify(post, postForm.getTitle(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String postDelete(Principal principal, @PathVariable("id") Long id) {
        Post post = this.postService.getPost(id);
        if (!post.getCustomer().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.postService.delete(post);
        return "redirect:/post/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/likes/{id}")
    public String postLikes(Principal principal, @PathVariable("id") Long id) {
        Post post = this.postService.getPost(id);
        Customer customer = this.customerService.findByusername(principal.getName());
        this.postService.likes(post, customer);
        return String.format("redirect:/post/detail/%s", id);
    }
}
