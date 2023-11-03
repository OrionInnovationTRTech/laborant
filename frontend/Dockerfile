FROM  node:19-alpine3.17
COPY package.json .
ENV TZ=Europe/Istanbul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]