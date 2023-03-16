FROM  node:19-alpine3.17
COPY package.json .
RUN npm install
COPY . .
RUN npm run build
CMD ["npm", "start"]