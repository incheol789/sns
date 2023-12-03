package com.fast.campus.simplesns.controller;


import com.fast.campus.simplesns.controller.request.PostCreateRequest;
import com.fast.campus.simplesns.controller.response.PostResponse;
import com.fast.campus.simplesns.controller.response.Response;
import com.fast.campus.simplesns.model.User;
import com.fast.campus.simplesns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }
}