package io.github.hoooosi.meeting.websocket.netty;

import io.github.hoooosi.meeting.common.model.dto.TokenDTO;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.websocket.ChannelContextUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChannelHandler.Sharable
@Slf4j
public class TokenValidationHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final RedisUtils redisUtils;
    private final ChannelContextUtils channelContextUtils;

    public TokenValidationHandler(RedisUtils redisUtils, ChannelContextUtils channelContextUtils) {
        this.redisUtils = redisUtils;
        this.channelContextUtils = channelContextUtils;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        List<String> tokens = queryStringDecoder.parameters().get("token");

        if (tokens == null || tokens.isEmpty()) {
            this.sendErrorResp(ctx);
            return;
        }

        String tokenKey = tokens.getFirst();
        TokenDTO tokenDTO = redisUtils.getTokenDTO(tokenKey);
        if (tokenDTO == null) {
            this.sendErrorResp(ctx);
            return;
        }

        channelContextUtils.addContext(tokenDTO.getUserId(), ctx.channel());
        ctx.fireChannelRead(request.retain());
    }

    private void sendErrorResp(ChannelHandlerContext ctx) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, Unpooled.copiedBuffer("Forbidden", CharsetUtil.UTF_8));
        resp.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, resp.content().readableBytes());
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
