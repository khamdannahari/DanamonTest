# Use a newer Java version
FROM openjdk:17-jdk

# Instal Android SDK
RUN apt-get update -qq \
  && apt-get install -qqy --no-install-recommends \
    wget \
    unzip \
    curl \
    && rm -rf /var/lib/apt/lists/* \
  && mkdir -p /opt/android-sdk-linux \
  && wget -q https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip -O /tmp/tools.zip \
  && unzip -qq /tmp/tools.zip -d /opt/android-sdk-linux \
  && rm -f /tmp/tools.zip

# Set PATH
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${PATH}:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Instal SDK
RUN mkdir ~/.android \
  && touch ~/.android/repositories.cfg \
  && yes | sdkmanager --licenses >/dev/null \
  && sdkmanager "platform-tools" "platforms;android-30" >/dev/null

# Set workdir
WORKDIR /app

# Copy project to container
COPY . .

# Build apk
RUN ./gradlew assembleDebug

# Install app
CMD ["./gradlew", "installDebug"]
