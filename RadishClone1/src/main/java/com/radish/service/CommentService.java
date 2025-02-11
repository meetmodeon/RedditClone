package com.radish.service;

import com.radish.dto.CommentsDto;
import com.radish.exceptions.PostNotFoundException;
import com.radish.exceptions.SpringRedditException;
import com.radish.model.Comment;
import com.radish.model.NotificationEmail;
import com.radish.model.Post;
import com.radish.model.User;
import com.radish.repository.CommentRepository;
import com.radish.repository.PostRepository;
import com.radish.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL="";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    private final ModelMapper modelMapper;

    public void save(CommentsDto commentsDto){
        Post post=postRepository.findById(commentsDto.getPostId())
                .orElseThrow(()-> new PostNotFoundException(commentsDto.getPostId().toString()));

        User user=userRepository.findByEmail(commentsDto.getUserName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with name : "+commentsDto.getUserName()));
        Comment comment= modelMapper.map(commentsDto,Comment.class);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);

        String message= mailContentBuilder.build(post.getUser().getUsername()+" posted a comment on your post."+POST_URL);
        sendCommentNotification(message,post.getUser());
    }

    private void sendCommentNotification(String message, User user){
        mailService.sendMail(new NotificationEmail(user.getUsername()+" Commented on your post",user.getEmail(),message));

    }

    public List<CommentsDto> getAllCommentsForPost(Long postId){
        Post post= postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId.toString()));

        List<Comment> comments=commentRepository.findByPost(post);
        List<CommentsDto> commentsDtos=modelMapper.map(comments,new TypeToken<List<CommentsDto>>(){}.getType());
        return commentsDtos;
    }

    public List<CommentsDto> getAllCommentsForUser(String userName){
        User user= userRepository.findByEmail(userName)
                .orElseThrow(()-> new UsernameNotFoundException(userName));

        List<Comment> comments= commentRepository.findAllByUser(user);
        List<CommentsDto> commentsDtos=modelMapper.map(comments,new TypeToken<List<CommentsDto>>(){}.getType());
        return commentsDtos;

    }

    public boolean containsSwearWords(String comment){
        if(comment.contains("shit")){
            throw new SpringRedditException("Comments contains unacceptable language");
        }
        return false;
    }
}
