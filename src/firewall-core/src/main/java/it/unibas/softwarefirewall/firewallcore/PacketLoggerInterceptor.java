package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Singleton
public class PacketLoggerInterceptor implements MethodInterceptor {

    @Inject
    private PacketLogger logger;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (invocation.getMethod().getReturnType() == boolean.class ||
            invocation.getMethod().getReturnType() == Boolean.class) {
            
            boolean allowed = (Boolean) result;
            IPacket packet = (IPacket) invocation.getArguments()[0];
            logger.logPacket(packet, allowed);
        }
        return result;
    }
}
