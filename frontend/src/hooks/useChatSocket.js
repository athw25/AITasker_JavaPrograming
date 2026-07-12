import { useEffect, useRef, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuthStore } from '../store/authStore';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';

export function useChatSocket(projectId, onMessage) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const token = useAuthStore((s) => s.token);

  useEffect(() => {
    if (!projectId || !token) return undefined;

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 4000,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/projects/${projectId}`, (frame) => {
          onMessage(JSON.parse(frame.body));
        });
        client.subscribe('/user/queue/messages', (frame) => {
          onMessage(JSON.parse(frame.body));
        });
      },
      onStompError: () => setConnected(false),
      onWebSocketClose: () => setConnected(false)
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [projectId, token]);

  const sendMessage = useCallback((receiverId, content) => {
    if (!clientRef.current?.connected) return;
    clientRef.current.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ projectId, receiverId, content })
    });
  }, [projectId]);

  return { connected, sendMessage };
}
