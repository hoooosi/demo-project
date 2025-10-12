package io.github.hoooosi.meeting.websocket.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (io.netty.handler.timeout.IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Reader idle, closing connection");
                ctx.channel().close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("ping");
            }
        }
    }
}
