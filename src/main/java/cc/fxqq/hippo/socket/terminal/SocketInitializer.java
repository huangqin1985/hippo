package cc.fxqq.hippo.socket.terminal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Socket 初始化器，每一个Channel进来都会调用这里的 InitChannel 方法
 * @author Gjing
 **/
@Component
public class SocketInitializer extends ChannelInitializer<SocketChannel> {
	
	@Autowired
	private SocketHandler orderHandler;
	
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 添加对byte数组的编解码，netty提供了很多编解码器，你们可以根据需要选择

        pipeline.addLast(new LineBasedFrameDecoder(100000000));
        //pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder());

        // 添加上自己的处理器
        pipeline.addLast(orderHandler);
    }
}