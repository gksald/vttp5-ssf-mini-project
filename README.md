# vttp5-ssf-day19l

### docker command

1. docker --version <br>
   
Clear the docker compilation/build cache <br>
2. docker system prune <br>
   
Building the docker image <br>
3. docker build -t <docker login>/<app name>:<version> <br>
docker build -t darryl1975/vttp5a-ssf-day13l:0.0.1 . <br>
docker build -t me1v1k/vttp5-ssf-day19l:0.0.1 . <br>

Check docker image create in local docker repo <br>
4. docker image ls <br>

Run the image inside the container <br>
5. docker run -d -t -p <exposed public port>:<application server port> <image name> <br>
docker run -d -t -p 8085:3000 me1v1k/vttp5-ssf-day19l:0.0.1 <br>
docker run -d -p 8080:3000 -e API_KEY=e3f7588b728f4f62a9131452240612 me1v1k/vttp5-ssf-day19l:0.0.1 <br>

Check docker container running <br>
6. docker container ls
   
Stop a running container <br>
7. docker stop <container id>  <br>
   
Start a stopped container <br>
8. docker start <container id> <br>

To remove a stopped container<br>
9. docker rm <container id> <br>
    
To delete image <br>
10. docker rmi <image id> <br>
docker rmi 06431f5c8648f5d19fa5d5642b24a12b524b2a322b940ee61426c1e9d90046eb <br>

To push image to docker hub <br>
11. docker push <image-name>
docker push me1v1k/vttp5-ssf-day19l:0.0.1


### Docker compile and push to railway

1. In railway.app, create a service (linked the project) <br>
2. Create the environment variable, SERVER_PORT=3000 <br>
3. In your project root folder (command prompt), execute "railway login --browserless" <br>
4. Link the project, execute "railway link" <br>
5. Deploy the project to railway, execute "railway up" <br>
