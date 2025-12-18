package com.ctu.bookstore.service.chat;

import com.ctu.bookstore.dto.request.chat.ConversationRequest;
import com.ctu.bookstore.dto.respone.chat.ConversationResponse;
import com.ctu.bookstore.entity.chat.Conversation;
import com.ctu.bookstore.entity.chat.ParticipantInfo;
import com.ctu.bookstore.mapper.chat.ConversationMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.conversation.ConversationRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        List<Conversation> conversations =
                conversationRepository.findAllByParticipantIdsContains(user.getId());

        return conversations.stream()
                .map(this::toConversationResponse)
                .toList();
    }

    public ConversationResponse create(ConversationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        var participant = userRepository.findById(request.getParticipantIds().get(0))
                .orElseThrow(() -> new RuntimeException(
                        "Participant not found with id: " + request.getParticipantIds().get(0)));

        System.out.println("Current user id: " + user.getId());
        System.out.println("Participant id: " + participant.getId());
        List<String> userIds = List.of(user.getId(), participant.getId());
        var sortedIDs = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIDs);

        Conversation conversation = conversationRepository.findByParticipantsHash(userIdHash)
                .orElseGet(() -> {
                    List<ParticipantInfo> participantInfos = List.of(
                            ParticipantInfo.builder()
                                    .userId(user.getId())
                                    .username(user.getUsername())
                                    .avata(user.getAvatar())
                                    .build(),
                            ParticipantInfo.builder()
                                    .userId(participant.getId())
                                    .username(participant.getUsername())
                                    .avata(participant.getAvatar())
                                    .build()
                    );

                    Conversation newConversation = Conversation.builder()
                            .type(request.getType())
                            .participantsHash(userIdHash)
                            .createDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .build();

                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation);
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();

        var userCurrent = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + currentUserName));

        String currentUserId = userCurrent.getId();
        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst()
                .ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvata());
                });

        return conversationResponse;
    }
    public ConversationResponse createDefault() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        var admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("User not found with admin " ));

        var participant = admin;

        System.out.println("Current user id: " + user.getId());
        System.out.println("Participant id: " + participant.getId());
        List<String> userIds = List.of(user.getId(), participant.getId());
        var sortedIDs = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIDs);

        Conversation conversation = conversationRepository.findByParticipantsHash(userIdHash)
                .orElseGet(() -> {
                    List<ParticipantInfo> participantInfos = List.of(
                            ParticipantInfo.builder()
                                    .userId(user.getId())
                                    .username(user.getUsername())
                                    .avata(user.getAvatar())
                                    .build(),
                            ParticipantInfo.builder()
                                    .userId(participant.getId())
                                    .username(participant.getUsername())
                                    .avata(participant.getAvatar())
                                    .build()
                    );

                    Conversation newConversation = Conversation.builder()
                            .type("chat-with-admin")
                            .participantsHash(userIdHash)
                            .createDate(Instant.now())
                            .modifiedDate(Instant.now())
                            .participants(participantInfos)
                            .build();

                    return conversationRepository.save(newConversation);
                });

        return toConversationResponse(conversation);
    }

}
