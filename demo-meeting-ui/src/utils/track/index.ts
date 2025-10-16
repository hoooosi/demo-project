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

export function createVideoTrack(width = 640, height = 480): MediaStreamTrack {
    // 创建一个 Canvas 元素
    const canvas = Object.assign(document.createElement("canvas"), { width, height });
    const ctx = canvas.getContext("2d");

    // 绘制黑色背景
    if (ctx) {
        ctx.fillStyle = 'blue';
        ctx.fillRect(0, 0, width, height);
    }

    // 从 Canvas 获取 MediaStreamTrack
    // 使用 requestAnimationFrame 持续获取，确保它是“活”的，但对于占位符来说，一次性获取通常足够
    const stream = canvas.captureStream();
    return stream.getVideoTracks()[0] as any;
}


// 跟踪动画状态
interface AnimationState {
    x: number; // 矩形的X坐标
    dx: number; // 矩形的移动速度/方向
    requestId: number | null; // requestAnimationFrame ID
    canvas: HTMLCanvasElement;
}

export function createAnimatedVideoTrack(width = 640, height = 480): MediaStreamTrack {
    const canvas = Object.assign(document.createElement("canvas"), { width, height });
    const ctx = canvas.getContext("2d");

    if (!ctx) {
        throw new Error("Canvas context not available.");
    }

    const state: AnimationState = {
        x: 0,
        dx: 2, // 初始速度
        requestId: null,
        canvas: canvas
    };

    // 动画绘制函数
    function draw() {
        // 1. 清除画布 (设置背景，例如白色)
        ctx!.fillStyle = 'white';
        ctx!.fillRect(0, 0, width, height);

        // 2. 更新矩形位置
        state.x += state.dx;

        // 碰撞检测：如果碰到边缘，反转方向
        const rectWidth = 50;
        if (state.x + rectWidth > width || state.x < 0) {
            state.dx = -state.dx;
        }

        // 3. 绘制动画元素 (例如一个红色矩形)
        ctx!.fillStyle = 'red';
        ctx!.fillRect(state.x, height / 2 - 25, rectWidth, 50);

        // 4. 循环调用 requestAnimationFrame
        state.requestId = requestAnimationFrame(draw);
    }

    // 启动动画
    state.requestId = requestAnimationFrame(draw);

    // 捕获流
    const stream = canvas.captureStream(30);
    const track = stream.getVideoTracks()[0] as MediaStreamTrack;

    const originalStop = track.stop.bind(track);
    track.stop = function () {
        if (state.requestId) {
            cancelAnimationFrame(state.requestId);
        }
        originalStop();
    };

    return track; // 返回轨道
}

export function createMediaStream(): MediaStream {
    const audioTrack = createSilentAudioTrack();
    const videoTrack = createAnimatedVideoTrack();
    return new MediaStream([audioTrack, videoTrack]);
}
