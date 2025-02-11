package com.radish.service;

import com.radish.dto.PostRequest;
import com.radish.dto.PostResponse;
import com.radish.exceptions.PostNotFoundException;
import com.radish.exceptions.SubredditNotFoundException;
import com.radish.model.Post;
import com.radish.model.Subreddit;
import com.radish.model.User;
import com.radish.repository.PostRepository;
import com.radish.repository.SubredditRepository;
import com.radish.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    public void save(PostRequest postRequest){
        Subreddit subreddit= subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(()-> new SubredditNotFoundException(postRequest.getSubredditName()));
        Post post= modelMapper.map(postRequest,Post.class);
        post.setSubreddit(subreddit);
        post.setUser(authService.getCurrentUser());
        post.setCreatedDate(Instant.now());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id){
        Post post= postRepository.findById(id)
                .orElseThrow(()-> new PostNotFoundException(id.toString()));
        PostResponse postResponse= modelMapper.map(post,PostResponse.class);
        return postResponse;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(){
//        return postRepository.findAll()
//                .stream()
//                .map(postMapper::mapToDto)
//                .collect(toList());

        List<Post> posts=postRepository.findAll();
        List<PostResponse> postResponses= modelMapper.map(posts,new TypeToken<List<PostResponse>>(){}.getType());
        return postResponses;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId){
        Subreddit subreddit= subredditRepository.findById(subredditId)
                .orElseThrow(()-> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts= postRepository.findAllBySubreddit(subreddit);
        List<PostResponse> postResponses=modelMapper.map(posts,new TypeToken<List<PostResponse>>(){}.getType());
        return postResponses;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username){
        User user=userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));
//        return postRepository.findByUser(user)
//                .stream()
//                .map(postMapper::mapToDto)
//                .collect(toList());
        List<Post> posts= postRepository.findByUser(user);
        List<PostResponse> postResponses=modelMapper.map(posts,new TypeToken<List<PostResponse>>(){}.getType());
        return postResponses;
    }
}
