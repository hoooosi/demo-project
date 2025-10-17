export function createSilentAudioTrack(): MediaStreamTrack {
    // 创建一个 AudioContext
    const audioCtx = new AudioContext();
    // 创建一个静音源
    const oscillator = audioCtx.createOscillator();
    const gainNode = audioCtx.createGain();

    // 将增益设置为 0 (静音)
    gainNode.gain.setValueAtTime(0, audioCtx.currentTime);

    // 创建 MediaStreamDestination 并连接源 -> 增益 -> 目的地
    const destination = audioCtx.createMediaStreamDestination();
    oscillator.connect(gainNode).connect(destination);
    oscillator.start();

    // 从 MediaStreamDestination 获取 MediaStreamTrack
    const stream = destination.stream;

    // 返回轨道
    return stream.getAudioTracks()[0] as any;
}


/**
 * 创建一个包含随机动画的视频流轨道。
 * 动画内容为多个随机颜色和大小的彩色小球在画布内反弹。
 * @param width 画布宽度，默认为 640
 * @param height 画布高度，默认为 480
 * @returns MediaStreamTrack 一个包含动画的视频轨道
 */
export function createAnimatedVideoTrack(width = 640, height = 480): MediaStreamTrack {
    const canvas = Object.assign(document.createElement("canvas"), { width, height });
    const ctx = canvas.getContext("2d");

    if (!ctx) {
        throw new Error("Canvas context not available.");
    }

    // --- 动画逻辑修改开始 ---

    // 定义单个小球的数据结构
    interface Ball {
        x: number;      // x 坐标
        y: number;      // y 坐标
        dx: number;     // x 轴速度
        dy: number;     // y 轴速度
        radius: number; // 半径
        color: string;  // 颜色
    }

    // 用于生成随机值的辅助函数
    const random = (min: number, max: number) => Math.random() * (max - min) + min;
    // 生成一个鲜艳的随机 HSL 颜色
    const getRandomColor = () => `hsl(${random(0, 360)}, 70%, 60%)`;

    // 状态管理：创建一个数组来存放所有小球
    const balls: Ball[] = [];
    const numBalls = 5; // 定义小球的数量

    // 初始化所有小球，赋予随机属性
    for (let i = 0; i < numBalls; i++) {
        const radius = random(8, 20); // 随机半径
        balls.push({
            radius: radius,
            // 确保小球初始时完整地出现在画布内
            x: random(radius, width - radius),
            y: random(radius, height - radius),
            // 随机速度（避免速度为0）
            dx: random(-3, 3) || 1,
            dy: random(-3, 3) || 1,
            color: getRandomColor(),
        });
    }

    let requestId: number | null = null;

    // 动画绘制函数
    function draw() {
        // 1. 清除画布 (使用半透明背景制造拖尾效果)
        ctx!.fillStyle = 'rgba(0, 0, 0, 0.2)';
        ctx!.fillRect(0, 0, width, height);

        // 2. 遍历所有小球，更新位置并绘制
        for (const ball of balls) {
            // 绘制小球
            ctx!.beginPath();
            ctx!.arc(ball.x, ball.y, ball.radius, 0, Math.PI * 2);
            ctx!.fillStyle = ball.color;
            ctx!.fill();
            ctx!.closePath();

            // 碰撞检测：如果碰到边缘，反转对应方向的速度
            if (ball.x + ball.radius > width || ball.x - ball.radius < 0) {
                ball.dx = -ball.dx;
            }
            if (ball.y + ball.radius > height || ball.y - ball.radius < 0) {
                ball.dy = -ball.dy;
            }

            // 更新小球的位置
            ball.x += ball.dx;
            ball.y += ball.dy;
        }

        // 3. 循环调用 requestAnimationFrame
        requestId = requestAnimationFrame(draw);
    }

    // --- 动画逻辑修改结束 ---

    // 启动动画
    requestId = requestAnimationFrame(draw);

    // 从 Canvas 捕获视频流，帧率为 30fps
    const stream = canvas.captureStream(30);
    const track = stream.getVideoTracks()[0] as MediaStreamTrack;

    // 重写轨道的 stop 方法，确保在轨道停止时也停止动画循环，防止资源泄露
    const originalStop = track.stop.bind(track);
    track.stop = function () {
        if (requestId) {
            cancelAnimationFrame(requestId);
            requestId = null;
        }
        originalStop();
    };

    return track; // 返回视频轨道
}

export function createMediaStream(): MediaStream {
    const audioTrack = createSilentAudioTrack();
    const videoTrack = createAnimatedVideoTrack();
    return new MediaStream([audioTrack, videoTrack]);
}

export const getScreenStream = async (): Promise<MediaStream> => {
    return await navigator.mediaDevices.getDisplayMedia({
        video: true,
        audio: true,
    })
}

export const getCameraStream = async (): Promise<MediaStream> => {
    return createMediaStream();
    return await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: true,
    })
}