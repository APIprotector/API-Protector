# Use the same base image you had, which includes Java
FROM alpine/java:21-jdk@sha256:366ad7df4fafeca51290ccd7d6f046cf4bf3aa312e59bb191b4be543b39f25e2

# Install Maven, bash (for the script), wget (if needed, mvn might handle downloads),
# coreutils (for mv, ls), and grep.
# Alpine Linux uses 'apk' for package management.
RUN apk update && \
    apk add --no-cache maven bash wget coreutils grep

# Set the working directory inside the container
WORKDIR /app

# Copy the entrypoint script into the container
COPY entrypoint.sh /app/entrypoint.sh

# Make the entrypoint script executable
RUN chmod +x /app/entrypoint.sh

# Set the entrypoint script to be executed when the container starts
ENTRYPOINT ["/app/entrypoint.sh"]