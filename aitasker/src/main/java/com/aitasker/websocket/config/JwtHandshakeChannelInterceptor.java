package com.aitasker.websocket.config;

import com.aitasker.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Bắt buộc client phải gửi JWT hợp lệ trong header STOMP "Authorization"
 * khi thực hiện CONNECT. Trước bản vá này, endpoint /ws được permitAll()
 * ở tầng HTTP (bắt buộc, vì bắt tay SockJS là HTTP thường) nhưng KHÔNG có
 * bước xác thực nào ở tầng STOMP -> bất kỳ ai cũng subscribe/publish được
 * vào các kênh chat mà không cần đăng nhập.
 *
 * Sau bản vá: CONNECT không có / sai JWT sẽ bị từ chối ngay, và Principal
 * của phiên WebSocket được gắn với user đã xác thực để dùng cho
 * SimpMessagingTemplate.convertAndSendToUser(...).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                log.warn("WebSocket CONNECT rejected: missing Authorization header");
                throw new org.springframework.messaging.simp.stomp.StompConversionException(
                        "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new org.springframework.messaging.simp.stomp.StompConversionException(
                            "Invalid or expired token");
                }

                accessor.setUser(new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()));

            } catch (Exception ex) {
                log.warn("WebSocket CONNECT rejected: {}", ex.getMessage());
                throw new org.springframework.messaging.simp.stomp.StompConversionException(
                        "Authentication failed");
            }
        }

        return message;
    }
}
