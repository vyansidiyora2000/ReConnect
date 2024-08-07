# Use a base image like Ubuntu or Alpine
FROM ubuntu:latest

# Install Java
RUN apt-get update && apt-get install -y default-jdk

# Install Nginx
RUN apt-get install -y nginx

# Copy your Nginx configuration files, Java applications, etc.
COPY frontend/Re-Connect/dist/re-connect /usr/share/nginx/html
COPY backend/target/reconnect-0.0.1-SNAPSHOT.jar reconnect.jar

# Expose ports for Nginx and your Java application
EXPOSE 80
EXPOSE 8080

# Start Nginx and your Java application
CMD service nginx start && java -jar /reconnect.jar
