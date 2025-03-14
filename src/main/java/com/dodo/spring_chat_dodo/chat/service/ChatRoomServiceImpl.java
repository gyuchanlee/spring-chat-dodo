package com.dodo.spring_chat_dodo.chat.service;

import com.dodo.spring_chat_dodo.chat.dto.*;
import com.dodo.spring_chat_dodo.chat.entity.ChatRoom;
import com.dodo.spring_chat_dodo.chat.entity.ChatRoomUser;
import com.dodo.spring_chat_dodo.chat.entity.ChatRoomUserId;
import com.dodo.spring_chat_dodo.chat.entity.Message;
import com.dodo.spring_chat_dodo.chat.repository.ChatRoomRepository;
import com.dodo.spring_chat_dodo.chat.repository.ChatRoomUserRepository;
import com.dodo.spring_chat_dodo.chat.repository.MessageRepository;
import com.dodo.spring_chat_dodo.user.entity.User;
import com.dodo.spring_chat_dodo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    // 채팅방 개설
    @Override
    public void createChatRoom(ChatRoomCreateDto chatRoomCreateDto) {
        chatRoomRepository.save(
                ChatRoom.builder()
                        .name(chatRoomCreateDto.getName())
                        .description(chatRoomCreateDto.getDescription())
                        .participantsCount(chatRoomCreateDto.getParticipantsCount())
                        .isPrivate(chatRoomCreateDto.getIsPrivate())
                        .latitude(chatRoomCreateDto.getLatitude())
                        .longitude(chatRoomCreateDto.getLongitude())
                        .build()
        );
    }

    // 채팅방 정보 변경
    @Override
    public void updateChatRoom(ChatRoomUpdateDto chatRoomUpdateDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomUpdateDto.getId())
                .orElseThrow(() -> new RuntimeException("chat room does not exist"));
        chatRoom.update(chatRoomUpdateDto);
    }

    // 채팅방 삭제
    @Override
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    // 한 인원의 채팅방 입장
    @Override
    public Long joinChatRoom(Long chatRoomId) {
        // 현재 로그인한 사용자 정보 가져오기
        User currentUser = getCurrentUser();

        // 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("chat room does not exist"));

        // 이미 참여중인지 확인
        Optional<ChatRoomUser> existingChatRoomUser = chatRoomUserRepository.findById(
                ChatRoomUserId.builder()
                        .userId(currentUser.getId())
                        .chatRoomId(chatRoomId).build()
        );

//        log.info("existingChatRoomUser = '{}'",  existingChatRoomUser.get());
        
        if (existingChatRoomUser.isPresent()) {
            // 이미 참여했으므로 그대로 채팅방 ID값만 리턴해주고 그냥 프론트에서 진행하도록 한다
            return chatRoomId;
        }
        
        // 참여자 수 +1
        chatRoom.increaseParticipantsCount();

        // 중계 테이블 연관관계 추가
        ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .id(ChatRoomUserId.builder()
                        .userId(currentUser.getId())
                        .chatRoomId(chatRoomId).build())
                .chatRoom(chatRoom)
                .user(currentUser)
                .isHost(false) // 이미 만든 것에 입장할떄는 항상 false
                .build();

        chatRoomUserRepository.save(chatRoomUser);

        return chatRoom.getId();
    }

    // 한 인원의 채팅방 퇴장
    @Override
    public void leaveChatRoom(Long chatRoomId) {
        // 현재 로그인한 사용자 정보 가져오기
        User currentUser = getCurrentUser();

        // 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("chat room does not exist"));

        // 이미 참여중인지 확인
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findById(
                        ChatRoomUserId.builder()
                                .userId(currentUser.getId())
                                .chatRoomId(chatRoomId).build()
                )
                .orElseThrow(() -> new RuntimeException("이미 참여중인 채팅방입니다."));

        // 호스트가 나가는 경우 처리 (방을 삭제하거나 다른 사람에게 호스트 권한을 넘겨줄 수 있음)
        if (chatRoomUser.isHost()) {
            // 다른 참여자가 있는지 확인
            List<ChatRoomUser> otherParticipants = chatRoomUserRepository.findByChatRoom_IdAndUser_IdNot(
                    chatRoomId, currentUser.getId());

            if (!otherParticipants.isEmpty()) {
                // 다른 참여자 중 한 명에게 호스트 권한 부여 (첫 번째 참여자)
                ChatRoomUser newHost = otherParticipants.getFirst();
                newHost.promoteToHost();
                chatRoomUserRepository.save(newHost);
            } else {
                // 다른 참여자가 없는 경우 채팅방 삭제
                chatRoomRepository.delete(chatRoom);
                // 중계 테이블 레코드 삭제는 CASCADE로 처리됨을 가정
                return;
            }
        }

        // 참여자 수 감소
        chatRoom.decreaseParticipantsCount();

        // 중계 테이블 연관관계 제거
        chatRoomUserRepository.delete(chatRoomUser);
    }


    // 채팅방 리스트 조회
    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomResponseDto> getChatRooms() {
        return chatRoomRepository.findAll()
                .stream()
                .map(chatRoom -> ChatRoomResponseDto.builder()
                        .chatRoomId(chatRoom.getId())
                        .name(chatRoom.getName())
                        .description(chatRoom.getDescription())
                        .participantsCount(chatRoom.getParticipantsCount())
                        .isPrivate(chatRoom.getIsPrivate())
                        .latitude(chatRoom.getLatitude())
                        .longitude(chatRoom.getLongitude())
                        .build())
                .toList();
    }

    // 한 채팅방의 메세지들 조회
    @Transactional(readOnly = true)
    @Override
    public List<MessageDto> getMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("chat room does not exist"));
        List<Message> messages = messageRepository.findAllByChatRoom(chatRoom);

        return messages.stream()
                .map(
                        m -> MessageDto.builder()
                                .messageId(m.getId())
                                .username(m.getUser().getUsername())
                                .content(m.getContent())
                                .build())
                .toList();
    }

    // 메시지 저장
    @Override
    public MessageDto createMessage(MessageCreateDto messageCreateDto) {

        User user = userRepository.findByEmail(messageCreateDto.getEmail())
                .orElseThrow(() -> new RuntimeException("user does not exist"));

        ChatRoom chatRoom = chatRoomRepository.findById(messageCreateDto.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("chat room does not exist"));

        Message message = Message.builder()
                .user(user)
                .chatRoom(chatRoom)
                .content(messageCreateDto.getContent())
                .unreadCount(chatRoom.getParticipantsCount() - 1)
                .build();

        messageRepository.save(message);

        return MessageDto.builder()
                .messageId(message.getId())
                .username(user.getUsername())
                .content(message.getContent())
                .build();
    }

    // 현재 인증된 사용자를 가져오는 유틸리티 메서드
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
