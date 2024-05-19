package de.jeff_media.replant.jefflib.internal;

import de.jeff_media.replant.jefflib.ReflUtils;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.Contract;

@Internal
public class ServerListPingEventFactory {
    private static final ServerListPingEventConstructorInvoker CONSTRUCTOR_INVOKER;

    @Contract(value="_, _, _, _, _, _ -> new")
    public static ServerListPingEvent createServerListPingEvent(String string, InetAddress inetAddress, String string2, boolean bl, int n, int n2) {
        return CONSTRUCTOR_INVOKER.create(string, inetAddress, string2, bl, n, n2);
    }

    private static Constructor<ServerListPingEvent> getProperConstructor() {
        return ReflUtils.getConstructor(ServerListPingEvent.class, String.class, InetAddress.class, String.class, Integer.TYPE, Integer.TYPE);
    }

    private static Constructor<ServerListPingEvent> getCorruptedConstructor() {
        return ReflUtils.getConstructor(ServerListPingEvent.class, String.class, InetAddress.class, String.class, Boolean.TYPE, Integer.TYPE, Integer.TYPE);
    }

    static {
        Constructor<ServerListPingEvent> constructor = ServerListPingEventFactory.getProperConstructor();
        if (constructor != null) {
            CONSTRUCTOR_INVOKER = new ProperServerListPingEventConstructorInvoker(constructor);
        } else {
            constructor = ServerListPingEventFactory.getCorruptedConstructor();
            if (constructor != null) {
                CONSTRUCTOR_INVOKER = new CorruptedServerListPingEventConstructorInvoker(constructor);
            } else {
                throw new RuntimeException("Couldn't find any constructor for ServerListPingEvent");
            }
        }
    }

    private static interface ServerListPingEventConstructorInvoker {
        public ServerListPingEvent create(String var1, InetAddress var2, String var3, boolean var4, int var5, int var6);
    }

    private static class ProperServerListPingEventConstructorInvoker
    implements ServerListPingEventConstructorInvoker {
        private final Constructor<ServerListPingEvent> constructor;

        @Override
        public ServerListPingEvent create(String string, InetAddress inetAddress, String string2, boolean bl, int n, int n2) {
            try {
                return this.constructor.newInstance(string, inetAddress, string2, n, n2);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                throw new RuntimeException(reflectiveOperationException);
            }
        }

        public ProperServerListPingEventConstructorInvoker(Constructor<ServerListPingEvent> constructor) {
            this.constructor = constructor;
        }
    }

    private static class CorruptedServerListPingEventConstructorInvoker
    implements ServerListPingEventConstructorInvoker {
        private final Constructor<ServerListPingEvent> constructor;

        @Override
        public ServerListPingEvent create(String string, InetAddress inetAddress, String string2, boolean bl, int n, int n2) {
            try {
                return this.constructor.newInstance(string, inetAddress, string2, bl, n, n2);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                throw new RuntimeException(reflectiveOperationException);
            }
        }

        public CorruptedServerListPingEventConstructorInvoker(Constructor<ServerListPingEvent> constructor) {
            this.constructor = constructor;
        }
    }
}

