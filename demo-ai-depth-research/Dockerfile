# Agent 工作环境镜像
# 基于 Ubuntu 22.04，预装常用开发工具

FROM ubuntu:22.04

# 设置非交互式安装
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Shanghai

# 更新软件源并安装基础工具
RUN apt-get update && apt-get install -y \
    # 基础工具
    curl \
    wget \
    git \
    vim \
    nano \
    unzip \
    zip \
    tar \
    jq \
    ca-certificates \
    gnupg \
    lsb-release \
    # 网络工具
    net-tools \
    iputils-ping \
    dnsutils \
    telnet \
    netcat \
    # 编译工具
    build-essential \
    gcc \
    g++ \
    make \
    cmake \
    # Python 3
    python3 \
    python3-pip \
    python3-venv \
    # Node.js (通过 NodeSource)
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    # Java (OpenJDK)
    && apt-get install -y openjdk-17-jdk \
    # 清理缓存
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 安装常用 Python 包
RUN pip3 install --no-cache-dir \
    requests \
    beautifulsoup4 \
    lxml \
    pandas \
    numpy \
    matplotlib

# 安装常用 Node.js 全局包
RUN npm install -g \
    yarn \
    pm2 \
    http-server

# 设置工作目录
WORKDIR /workspace

# 默认命令
CMD ["/bin/bash"]
