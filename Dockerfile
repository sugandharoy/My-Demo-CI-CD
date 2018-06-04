FROM openjdk:8-jre-alpine                                           

ENV VERTICLE_FILE web-examples-3.5.1.jar      

# Set the location of the verticles
ENV VERTICLE_HOME C:\Users\sugandha\git\My-Demo-CI-CD

EXPOSE 8090

# Copy your fat jar to the container
                       

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
