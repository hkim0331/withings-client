FROM clojure:lein

ENV DEBIAN_FRONTEND=noninteractive

# Add npm
RUN apt-get update \
    && apt-get -y install --no-install-recommends \
       apt-utils sudo git \
       npm 2>&1

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*

ENV DEBIAN_FRONTEND=dialog

ARG USERNAME=vscode
ARG USER_ID=1000
RUN useradd -m -U -u ${USER_ID} ${USERNAME}
RUN echo ${USERNAME} ALL=\(ALL\) NOPASSWD:ALL > /etc/sudoers.d/10-vscode

